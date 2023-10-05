package cn.foxtech.manager.system.service;

import cn.foxtech.common.domain.constant.ServiceVOFieldConstant;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.process.ProcessUtils;
import cn.foxtech.common.utils.Maps;
import cn.foxtech.common.utils.file.FileCompareUtils;
import cn.foxtech.common.utils.file.FileNameUtils;
import cn.foxtech.common.utils.file.FileTextUtils;
import cn.foxtech.common.utils.http.DownLoadUtil;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.md5.MD5Utils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.osinfo.OSInfo;
import cn.foxtech.common.utils.shell.ShellUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.manager.system.constants.RepoComponentConstant;
import cn.foxtech.manager.system.constants.RepositoryConfigConstant;
import cn.foxtech.manager.system.constants.RepositoryStatusConstant;
import cn.foxtech.manager.system.utils.ServiceIniFilesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 仓库管理服务：它简化仓库的管理
 */
@Component
public class RepoComponentService {
    private final String siteUri = "http://www.fox-tech.cn";
    /**
     * 本地组件的安装状态列表：通过缓存安装状态，用于优化磁盘扫描安装包的长时间卡顿问题
     */
    private final Map<String, Object> statusMap = new ConcurrentHashMap<>();

    @Autowired
    private RedisConsoleService logger;
    /**
     * 配置管理查询
     */
    @Autowired
    private ManageConfigService configService;

    @Autowired
    private ServerPortService serverPortService;

    @Autowired
    private CloudRemoteService cloudRemoteService;

    @Value("${spring.fox-service.service.type}")
    private String foxServiceType = "undefinedServiceType";

    @Value("${spring.fox-service.service.name}")
    private String foxServiceName = "undefinedServiceName";

    /**
     * 扫描仓库状态
     */
    public void scanRepositoryStatus(String modelType) {
        try {
            // 扫描本地目录，初步判定有哪些组件，构造出组件列表
            List<Map<String, Object>> localList = this.findLocalModel(modelType);

            // 清空原有的数据
            Maps.setValue(this.statusMap, modelType, new ConcurrentHashMap<>());

            // 扫描本地组件的完整状态，扫描出组件的状态
            this.scanLocalStatus(modelType, localList);

            // 本地存在的组件特征
            Set<String> localKeys = new HashSet<>();
            for (Map<String, Object> map : localList) {
                String modelName = (String) map.get(RepoComponentConstant.filed_model_name);
                String modelVersion = (String) map.get(RepoComponentConstant.filed_model_version);
                String version = (String) map.get(RepoComponentConstant.filed_version);
                String stage = (String) map.get(RepoComponentConstant.filed_stage);
                String component = (String) map.get(RepoComponentConstant.filed_component);
                localKeys.add(modelType + ":" + modelName + ":" + modelVersion + ":" + version + ":" + stage + ":" + component);

            }

            // 获得云端组件列表
            List<Map<String, Object>> cloudList = this.queryLocalListFile(modelType);

            // 尚未下载云端组件，标识未未下载状态
            for (Map<String, Object> map : cloudList) {
                String modelName = (String) map.get(RepoComponentConstant.filed_model_name);
                String modelVersion = (String) map.get(RepoComponentConstant.filed_model_version);
                Map<String, Object> lastVersion = (Map<String, Object>) map.get(RepoComponentConstant.filed_last_version);
                List<Map<String, Object>> versions = (List<Map<String, Object>>) map.get(RepoComponentConstant.filed_versions);

                // 合并lastVersion和Versions
                List<Map<String, Object>> verEntityList = new ArrayList<>();
                if (lastVersion != null) {
                    verEntityList.add(lastVersion);
                }
                if (versions != null) {
                    for (Map<String, Object> verEntity : versions) {
                        verEntityList.add(verEntity);
                    }
                }

                for (Map<String, Object> verEntity : verEntityList) {
                    String version = (String) verEntity.get(RepoComponentConstant.filed_version);
                    String stage = (String) verEntity.get(RepoComponentConstant.filed_stage);
                    String component = (String) verEntity.get(RepoComponentConstant.filed_component);
                    if (MethodUtils.hasEmpty(modelName, modelVersion, version, component)) {
                        continue;
                    }


                    // 检查：该组件是否存在本地
                    String key = modelType + ":" + modelName + ":" + modelVersion + ":" + version + ":" + stage + ":" + component;
                    if (localKeys.contains(key)) {
                        continue;
                    }

                    // 标识为未下载状态
                    Maps.setValue(this.statusMap, modelType, modelName, modelVersion, version, stage, component, RepoComponentConstant.filed_status, RepositoryStatusConstant.status_not_downloaded);
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }


    /**
     * 查找本地已经下载的模块列表
     * 目录结构 \opt\fox-edge\repository\decoder\anno\1.0.0\bin
     *
     * @param modelType 组件类型
     * @return 已经下载的本地组件列表
     */
    public List<Map<String, Object>> findLocalModel(String modelType) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        // 仓库目录
        File file = new File("");
        String repoDirName = file.getAbsolutePath() + "/repository";

        // 类型级别的目录：\opt\fox-edge\repository\decoder
        File typeDir = new File(repoDirName + "/" + modelType);
        if (!typeDir.exists() || !typeDir.isDirectory()) {
            return resultList;
        }

        String[] modelFiles = typeDir.list();
        for (String modelFile : modelFiles) {
            // 模块级别的目录：\opt\fox-edge\repository\decoder\anno\
            File modelDir = new File(typeDir, modelFile);
            if (modelDir.isFile()) {
                continue;
            }

            // \opt\fox-edge\repository\decoder\fox-edge-server-protocol-bass260zj\v1\1.0.0\
            String[] modelVersionFiles = modelDir.list();
            for (String modelVersionFile : modelVersionFiles) {
                File modelVersionDir = new File(modelDir, modelVersionFile);
                if (modelVersionDir.isFile()) {
                    continue;
                }

                String[] versionFiles = modelVersionDir.list();
                for (String versionFile : versionFiles) {
                    File versionDir = new File(modelVersionDir, versionFile);
                    if (versionDir.isFile()) {
                        continue;
                    }

                    // \opt\fox-edge\repository\decoder\fox-edge-server-protocol-bass260zj\v1\1.0.0\
                    String[] stageFiles = versionDir.list();
                    for (String stageFile : stageFiles) {
                        File stageDir = new File(versionDir, stageFile);
                        if (stageDir.isFile()) {
                            continue;
                        }

                        String[] componentFiles = stageDir.list();
                        for (String componentFile : componentFiles) {
                            File componentDir = new File(stageDir, componentFile);
                            if (componentDir.isFile()) {
                                continue;
                            }

                            // 提取业务参数
                            String modelName = modelDir.getName();
                            String modelVersion = modelVersionDir.getName();
                            String version = versionDir.getName();
                            String stage = stageDir.getName();
                            String component = componentDir.getName();

                            Map<String, Object> map = new HashMap<>();
                            map.put(RepoComponentConstant.filed_model_name, modelName);
                            map.put(RepoComponentConstant.filed_model_version, modelVersion);
                            map.put(RepoComponentConstant.filed_version, version);
                            map.put(RepoComponentConstant.filed_stage, stage);
                            map.put(RepoComponentConstant.filed_component, component);

                            resultList.add(map);
                        }
                    }
                }
            }

        }

        return resultList;
    }

    /**
     * 从云端查询仓库列表，并保存到本地
     *
     * @param modelType
     * @return
     * @throws IOException
     */
    public List<Map<String, Object>> queryUriListFile(String modelType) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put(RepoComponentConstant.filed_model_type, modelType);
        Map<String, Object> respond = this.cloudRemoteService.executePost("/manager/repository/component/entities", body);

        List<Map<String, Object>> list = (List<Map<String, Object>>) respond.get("data");
        if (list == null) {
            throw new ServiceException("云端数据仓库返回的数据为空！");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);

        String json = JsonUtils.buildJson(data);

        String listFileName;
        if (RepoComponentConstant.repository_type_decoder.equals(modelType)) {
            listFileName = "decoderList.jsn";
        } else if (RepoComponentConstant.repository_type_webpack.equals(modelType)) {
            listFileName = "webpackList.jsn";
        } else if (RepoComponentConstant.repository_type_service.equals(modelType)) {
            listFileName = "serviceList.jsn";
        } else if (RepoComponentConstant.repository_type_template.equals(modelType)) {
            listFileName = "templateList.jsn";
        } else {
            listFileName = "serviceList.jsn";
        }

        // 确认目录
        File file = new File("");
        String localPath = file.getAbsolutePath() + "/repository/" + modelType;

        // 创建目录
        File pathDir = new File(localPath);
        if (!pathDir.exists()) {
            pathDir.mkdirs();
        }

        // 将获得的list信息保存到本地
        FileTextUtils.writeTextFile(localPath + "/" + listFileName, json);

        return list;
    }

    /**
     * 查询本地模块列表列表
     *
     * @return
     * @throws IOException
     */
    public List<Map<String, Object>> queryLocalListFile(String modelType) throws IOException {
        String listFileName = "";
        if (RepoComponentConstant.repository_type_decoder.equals(modelType)) {
            listFileName = "decoderList.jsn";
        } else if (RepoComponentConstant.repository_type_webpack.equals(modelType)) {
            listFileName = "webpackList.jsn";
        } else if (RepoComponentConstant.repository_type_service.equals(modelType)) {
            listFileName = "serviceList.jsn";
        } else if (RepoComponentConstant.repository_type_template.equals(modelType)) {
            listFileName = "templateList.jsn";
        } else {
            listFileName = "serviceList.jsn";
        }

        // 下载list文件
        File file = new File("");
        String localPath = file.getAbsolutePath() + "/repository/" + modelType;

        return this.queryLocalListFile(localPath, listFileName);
    }


    /**
     * 从本地列表文件中，取得模块列表信息
     *
     * @param localPath
     * @param listFileName
     * @return
     * @throws IOException
     */
    private List<Map<String, Object>> queryLocalListFile(String localPath, String listFileName) throws IOException {
        String pathName = localPath + "/" + listFileName;

        // 检查文件是否已经下载
        File exist = new File(pathName);
        if (!exist.exists()) {
            return new ArrayList<>();
        }

        // 解析json格式的list文件
        String jsn = FileTextUtils.readTextFile(localPath + "/" + listFileName);
        Map<String, Object> param = JsonUtils.buildObject(jsn, Map.class);

        return (List<Map<String, Object>>) param.get("list");
    }


    public void extendLocalStatus(Map<String, Object> entity) {
        {
            String modelType = (String) entity.getOrDefault(RepoComponentConstant.filed_model_type, "");
            String modelName = (String) entity.getOrDefault(RepoComponentConstant.filed_model_name, "");
            String modelVersion = (String) entity.getOrDefault(RepoComponentConstant.filed_model_version, RepoComponentConstant.filed_value_model_version_default);
            Map<String, Object> lastVersion = (Map<String, Object>) entity.getOrDefault(RepoComponentConstant.filed_last_version, new HashMap<>());
            List<Map<String, Object>> versions = (List<Map<String, Object>>) entity.getOrDefault(RepoComponentConstant.filed_versions, "");
            String component = (String) entity.getOrDefault(RepoComponentConstant.filed_component, "");


            // 验证last版本的破损状态
            int status = this.verifyMd5Status(modelType, modelName, modelVersion, component, lastVersion);
            if (RepositoryStatusConstant.status_damaged_package == status) {
                lastVersion.put(RepoComponentConstant.filed_status, status);
            } else {
                if (this.verifyUpgradeStatus(modelType, modelName, modelVersion, lastVersion, versions)) {
                    lastVersion.put(RepoComponentConstant.filed_status, RepositoryStatusConstant.status_need_upgrade);
                }
            }


            // 验证明细包的破损状态
            for (Map<String, Object> verEntity : versions) {
                status = this.verifyMd5Status(modelType, modelName, modelVersion, component, verEntity);
                verEntity.put(RepoComponentConstant.filed_status, status);
            }
        }
    }

    /**
     * 计算本地下载包的MD5
     *
     * @param verEntity
     * @param status
     * @param localMd5
     */
    private int verifyMd5Status(Map<String, Object> verEntity, Integer status, String localMd5) {
        // 检查：是否已经下载
        if (status < RepositoryStatusConstant.status_downloaded) {
            return status;
        }

        if (localMd5 == null) {
            localMd5 = "";
        }

        // 在查询阶段，计算破损状态：本地和云端的MD5是否一致
        String md5 = (String) verEntity.getOrDefault(RepoComponentConstant.filed_md5, "");
        if (!localMd5.equalsIgnoreCase(md5)) {
            return RepositoryStatusConstant.status_damaged_package;
        }

        return status;
    }

    private boolean verifyUpgradeStatus(String modelType, String modelName, String modelVersion, Map<String, Object> lastVersion, List<Map<String, Object>> versions) {
        // 检查最新版本
        if (lastVersion == null) {
            return false;
        }

        String lastVer = (String) lastVersion.get(RepoComponentConstant.filed_version);
        String lastStage = (String) lastVersion.get(RepoComponentConstant.filed_stage);
        String lastComponent = (String) lastVersion.get(RepoComponentConstant.filed_component);

        // 如果最新版本就是安装版本，那么不需要升级
        Integer lastStatus = (Integer) Maps.getOrDefault(this.statusMap, modelType, modelName, modelVersion, lastVer, lastStage, lastComponent, RepoComponentConstant.filed_status, RepositoryStatusConstant.status_not_scanned);
        if (RepositoryStatusConstant.status_installed == lastStatus) {
            return false;
        }

        // 明细版本
        for (Map<String, Object> verEntity : versions) {
            String version = (String) verEntity.get(RepoComponentConstant.filed_version);
            String stage = (String) verEntity.get(RepoComponentConstant.filed_stage);
            String component = (String) verEntity.get(RepoComponentConstant.filed_component);

            // 排除last版本
            if (version.equals(lastVer) && stage.equals(lastStage) && component.equals(lastComponent)) {
                continue;
            }

            // 检查：低版本是否处于安装状态，如果是安装状态，那么就是需要用最新版本来升级
            Integer status = (Integer) Maps.getOrDefault(this.statusMap, modelType, modelName, modelVersion, version, stage, component, RepoComponentConstant.filed_status, RepositoryStatusConstant.status_not_scanned);
            if (RepositoryStatusConstant.status_installed == status) {
                return true;
            }
        }


        return false;
    }

    /**
     * 根据MD5重新计算本地和云端的一致性状态
     *
     * @param modelType
     * @param modelName
     * @param component
     * @param verEntity
     */
    private int verifyMd5Status(String modelType, String modelName, String modelVersion, String component, Map<String, Object> verEntity) {
        String version = (String) verEntity.get(RepoComponentConstant.filed_version);
        String stage = (String) verEntity.get(RepoComponentConstant.filed_stage);
        if (MethodUtils.hasEmpty(version, stage)) {
            return -1;
        }

        Integer status = (Integer) Maps.getOrDefault(this.statusMap, modelType, modelName, modelVersion, version, stage, component, RepoComponentConstant.filed_status, RepositoryStatusConstant.status_not_scanned);
        String localMd5 = (String) Maps.getOrDefault(this.statusMap, modelType, modelName, modelVersion, version, stage, component, RepoComponentConstant.filed_local_md5, "");
        verEntity.put(RepoComponentConstant.filed_status, status);
        verEntity.put(RepoComponentConstant.filed_local_md5, localMd5);

        return this.verifyMd5Status(verEntity, status, localMd5);
    }


    /**
     * 扫描本地模块的安装状态
     *
     * @param modelType 模块类型
     * @param list      模块列表
     */
    public void scanLocalStatus(String modelType, List<Map<String, Object>> list) {
        for (Map<String, Object> map : list) {
            if (!modelType.equals(map.get(RepoComponentConstant.filed_model_type))) {
                continue;
            }

            this.scanLocalStatus(map);
        }
    }

    private void scanLocalStatus(Map<String, Object> comp) {
        // 提取业务参数
        String modelName = (String) comp.get(RepoComponentConstant.filed_model_name);
        String modelType = (String) comp.get(RepoComponentConstant.filed_model_type);
        String modelVersion = (String) comp.get(RepoComponentConstant.filed_model_version);
        String version = (String) comp.get(RepoComponentConstant.filed_version);
        String stage = (String) comp.get(RepoComponentConstant.filed_stage);
        String component = (String) comp.get(RepoComponentConstant.filed_component);

        // 本地的状态
        this.scanLocalStatus(modelType, modelName, modelVersion, version, stage, component);
        this.scanLocalMd5(modelType, modelName, modelVersion, version, stage, component);
    }


    private String getRepoLocalTarFileName(String modelName, String modelVersion, String version) {
        return modelName + "-" + modelVersion + "-" + version + ".tar";
    }

    private String getRepoLocalPathTarFileName(String absolutePath, String modelType, String modelName, String modelVersion, String version, String stage, String component) {
        return absolutePath + "/repository/" + modelType + "/" + modelName + "/" + modelVersion + "/" + version + "/" + stage + "/" + component + "/" + "tar";
    }

    private void deleteEmptyDir(String fileDir) throws IOException, InterruptedException {
        Path path = Paths.get(fileDir);
        File file = path.toFile();
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
            return;
        }

        // 是否为空目录
        if (file.list().length != 0) {
            return;
        }

        // 切换目录
        fileDir = file.getAbsolutePath();

        if (OSInfo.isWindows()) {
            // 删除可能存在的目录
            fileDir = fileDir.replace("/", "\\");
            ShellUtils.executeCmd("rd /s /q " + fileDir);
        }
        if (OSInfo.isLinux()) {
            ShellUtils.executeShell("rm -rf '" + fileDir + "'");
        }
    }

    public void scanLocalStatusAndMd5(String modelType, String modelName, String modelVersion, Map<String, Object> versionMap) {
        String version = (String) versionMap.get(RepoComponentConstant.filed_version);
        String stage = (String) versionMap.get(RepoComponentConstant.filed_stage);
        String component = (String) versionMap.get(RepoComponentConstant.filed_component);

        // 扫描本地的状态
        this.scanLocalStatus(modelType, modelName, modelVersion, version, stage, component);
        this.scanLocalMd5(modelType, modelName, modelVersion, version, stage, component);
    }

    /**
     * 扫描本地的组件安装状态，已经MD5文件状态
     *
     * @param modelType    模块类型
     * @param modelName    模块名称
     * @param modelVersion 模块版本
     * @param lastVersion  最新的文件版本
     * @param versions     最新的文件状态
     */
    public void scanLocalStatusAndMd5(String modelType, String modelName, String modelVersion, Map<String, Object> lastVersion, List<Map<String, Object>> versions) {
        // 取出最新的文件版本信息
        if (!MethodUtils.hasEmpty(lastVersion)) {
            this.scanLocalStatusAndMd5(modelType, modelName, modelVersion, lastVersion);
        }

        // 各明细版本
        if (!MethodUtils.hasEmpty(versions)) {
            // 每一个版本的状态
            for (Map<String, Object> version : versions) {
                this.scanLocalStatusAndMd5(modelType, modelName, modelVersion, version);
            }
        }
    }

    /**
     * 扫描某个文件版本包的状态
     *
     * @param modelType 仓库类型
     * @param modelName 模块名称
     * @param version   模块版本
     * @param component 组件
     */
    public void scanLocalStatus(String modelType, String modelName, String modelVersion, String version, String stage, String component) {
        // 简单验证
        if (MethodUtils.hasEmpty(modelType, modelName, modelVersion, version, stage, component)) {
            throw new ServiceException("参数不能为空: modelType, modelName, modelVersion, version, stage, component");
        }


        // 阶段1：未下载
        int status = RepositoryStatusConstant.status_not_downloaded;

        // 检查：是否已经下载
        File file = new File("");
        String absolutePath = file.getAbsolutePath();
        String tarFile = absolutePath + "/repository/" + modelType + "/" + modelName + "/" + modelVersion + "/" + version + "/" + stage + "/" + component + "/" + this.getRepoLocalTarFileName(modelName, modelVersion, version);
        File check = new File(tarFile);
        if (!check.exists()) {
            Maps.setValue(this.statusMap, modelType, modelName, modelVersion, version, stage, component, RepoComponentConstant.filed_status, status);
            return;
        }
        // 阶段2："已经下载，待安装!
        status = RepositoryStatusConstant.status_downloaded;

        // 检查：解压后的目录是否存在，至少包含一个文件
        String tarDir = this.getRepoLocalPathTarFileName(absolutePath, modelType, modelName, modelVersion, version, stage, component);
        List<String> tarFileNames = FileNameUtils.findFileList(tarDir, true, true);
        if (tarFileNames.isEmpty()) {
            Maps.setValue(this.statusMap, modelType, modelName, modelVersion, version, stage, component, RepoComponentConstant.filed_status, status);
            return;
        }

        // 阶段3：已经下载，已解压!
        status = RepositoryStatusConstant.status_decompressed;

        // 阶段3：已经安装的文件
        for (String name : tarFileNames) {
            String tarFileName = name.substring(tarDir.length() + 1);

            String srcFileName = "";
            String jarFileName = "";

            if (RepoComponentConstant.repository_type_decoder.equals(modelType)) {
                srcFileName = tarDir + "/" + tarFileName;
                jarFileName = absolutePath + "/jar/decoder/" + tarFileName;
            }
            if (RepoComponentConstant.repository_type_template.equals(modelType)) {
                srcFileName = tarDir + "/" + tarFileName;
                jarFileName = absolutePath + "/template/" + modelName + "/" + modelVersion + "/" + version + "/" + tarFileName;
            }
            if (RepoComponentConstant.repository_type_service.equals(modelType)) {
                srcFileName = tarDir + "/" + tarFileName;
                jarFileName = absolutePath + "/" + tarFileName;
            }
            if (RepoComponentConstant.repository_type_webpack.equals(modelType)) {
                srcFileName = tarDir + "/" + tarFileName;
                jarFileName = absolutePath + "/" + tarFileName;
            }


            // 检查：目标文件是否存在，如果存在缺失文件，则未安装
            check = new File(jarFileName);
            if (!check.exists()) {
                // 阶段4："已下载，未安装!"
                status = RepositoryStatusConstant.status_not_installed;
                break;
            }

            // 检查：内容是否一致，如果存在不一致，则未安装
            boolean same = FileCompareUtils.isSameFile(jarFileName, srcFileName);
            if (!same) {
                // 阶段4："已下载，未安装!"
                status = RepositoryStatusConstant.status_not_installed;
                break;
            }

            // 阶段5："已下载，已安装!"
            status = RepositoryStatusConstant.status_installed;
        }

        Maps.setValue(this.statusMap, modelType, modelName, modelVersion, version, stage, component, RepoComponentConstant.filed_status, status);
    }

    /**
     * 扫描本地下载包的MD5
     *
     * @param modelType
     * @param modelName
     * @param version
     * @param component
     */
    public void scanLocalMd5(String modelType, String modelName, String modelVersion, String version, String stage, String component) {
        // 简单验证
        if (MethodUtils.hasEmpty(modelName, modelVersion, version, component)) {
            throw new ServiceException("参数不能为空: modelName, modelVersion, version, component");
        }


        String md5 = "";

        // 检查：是否已经下载
        File file = new File("");
        String absolutePath = file.getAbsolutePath();
        String tarFile = absolutePath + "/repository/" + modelType + "/" + modelName + "/" + modelVersion + "/" + version + "/" + stage + "/" + component + "/" + this.getRepoLocalTarFileName(modelName, modelVersion, version);
        File check = new File(tarFile);
        if (!check.exists()) {
            Maps.setValue(this.statusMap, modelType, modelName, modelVersion, version, stage, component, RepoComponentConstant.filed_local_md5, md5);
            return;
        }

        // 计算MD5
        md5 = MD5Utils.getMD5Txt(check);

        Maps.setValue(this.statusMap, modelType, modelName, modelVersion, version, stage, component, RepoComponentConstant.filed_local_md5, md5);
    }


    /**
     * 测试云端的文件能否被下载
     *
     * @param modelType
     * @param modelName
     * @param version
     * @param pathName
     * @return
     */
    public boolean testUrlFileCanBeOpen(String modelType, String modelName, String modelVersion, String version, String pathName) {
        String host = (String) this.configService.getConfigValue(this.foxServiceName, this.foxServiceType, RepositoryConfigConstant.filed_config_name, RepositoryConfigConstant.filed_config_file, this.siteUri);
        if (MethodUtils.hasEmpty(host)) {
            throw new ServiceException("尚未配置仓库的uri，请先配置仓库的uri");
        }

        String urlStr = host + "/" + modelType + "/" + modelName + "/" + modelVersion + "/" + version + "/" + pathName;

        return DownLoadUtil.testUrlFileCanBeOpen(urlStr, "");
    }

    /**
     * 下载文件
     *
     * @param modelType 仓库类型
     * @throws IOException 异常信息
     */
    public void downloadFile(String modelType, String modelName, String modelVersion, String version, String stage, String pathName, String component) throws IOException, InterruptedException {
        // 简单验证
        if (MethodUtils.hasEmpty(modelType, modelName, modelVersion, version, stage, pathName, component)) {
            throw new ServiceException("参数不能为空:modelType, modelName, modelVersion, version, stage, pathName, component");
        }

        String fileName = this.getRepoLocalTarFileName(modelName, modelVersion, version);

        String host = (String) this.configService.getConfigValue(this.foxServiceName, this.foxServiceType, RepositoryConfigConstant.filed_config_name, RepositoryConfigConstant.filed_config_file, this.siteUri);
        if (MethodUtils.hasEmpty(host)) {
            throw new ServiceException("尚未配置仓库的uri，请先配置仓库的uri");
        }

        if (!fileName.endsWith(".tar")) {
            throw new ServiceException("文件必须为tar格式!");
        }
        Set<String> components = new HashSet<>();
        components.add("bin");
        components.add(RepoComponentConstant.repository_type_template);
        components.add(ServiceVOFieldConstant.field_type_service);
        components.add(ServiceVOFieldConstant.field_type_system);
        components.add(ServiceVOFieldConstant.field_type_kernel);
        components.add(RepoComponentConstant.repository_type_webpack);
        if (!components.contains(component)) {
            throw new ServiceException("component必须为：" + components);
        }

        // 下载tar文件
        File file = new File("");
        String url = host + "/" + modelType + "/" + modelName + "/" + modelVersion + "/" + version + "/" + pathName;
        String localPath = file.getAbsolutePath() + "/repository/" + modelType + "/" + modelName + "/" + modelVersion + "/" + version + "/" + stage + "/" + component;
        DownLoadUtil.downLoadFromHttpUrl(url, fileName, localPath, "");


        String tarDir = localPath + "/" + "tar";
        String localFile = localPath + "/" + fileName;

        if (OSInfo.isWindows()) {
            tarDir = tarDir.replace("/", "\\");
            localFile = localFile.replace("/", "\\");

            // 删除已经存在的解压目录
            ShellUtils.executeCmd("rd /s /q " + tarDir);

            // 新建一个目解压录
            ShellUtils.executeCmd("mkdir " + tarDir);

            // 解压文件到解压目录:tar -xf 静默解压，windows下不能返回太多内容，否则会死锁
            ShellUtils.executeCmd("tar -xf " + localFile + " -C " + tarDir);
        }
        if (OSInfo.isLinux()) {
            // 删除已经存在的解压目录
            ShellUtils.executeShell("rm -r  '" + tarDir + "'");

            // 新建一个目解压录
            ShellUtils.executeShell("mkdir '" + tarDir + "'");

            // 解压文件到解压目录
            ShellUtils.executeShell("tar -xvf '" + localFile + "' -C '" + tarDir + "'");
        }


        // 将状态保存起来
        this.scanLocalStatus(modelType, modelName, modelVersion, version, stage, component);
    }

    /**
     * 删除本地安装包以及解压后的目录文件
     *
     * @param modelType
     * @param modelName
     * @param version
     * @param component
     */
    public void deletePackageFile(String modelType, String modelName, String modelVersion, String version, String stage, String component) {
        try {
            // 简单验证
            if (MethodUtils.hasEmpty(modelType, modelName, modelVersion, version, component)) {
                throw new ServiceException("参数不能为空: modelType, modelName, modelVersion, version, component");
            }

            File file = new File("");
            String modelNameDir = file.getAbsolutePath() + "/repository/" + modelType + "/" + modelName;
            String modelVersionDir = modelNameDir + "/" + modelVersion;
            String packageDir = modelVersionDir + "/" + version;

            //  删除安装包文件
            if (OSInfo.isWindows()) {
                packageDir = packageDir.replace("/", "\\");
                ShellUtils.executeCmd("rd /s /q " + packageDir);
            }
            if (OSInfo.isLinux()) {
                ShellUtils.executeShell("rm -rf '" + packageDir + "'");
            }


            // 检查：是否整个模块都清空了，如果是就删除残余目录
            this.deleteEmptyDir(modelVersionDir);
            this.deleteEmptyDir(modelNameDir);

            // 将状态保存起来
            this.scanLocalStatus(modelType, modelName, modelVersion, version, stage, component);
            this.scanLocalMd5(modelType, modelName, modelVersion, version, stage, component);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 安装模块
     */
    public void installFile(String modelType, String modelName, String modelVersion, String version, String stage, String component) {
        try {
            // 简单验证
            if (MethodUtils.hasEmpty(modelType, modelName, modelVersion, version, stage, component)) {
                throw new ServiceException("参数不能为空:modelType, modelName, modelVersion, version, stage, component");
            }

            File file = new File("");
            String tarDir = this.getRepoLocalPathTarFileName(file.getAbsolutePath(), modelType, modelName, modelVersion, version, stage, component);

            // 备份目标文件，然后将解压文件复制复制到目标目录
            if (modelType.equals(RepoComponentConstant.repository_type_decoder)) {
                this.installDecoderFile(tarDir, file.getAbsolutePath() + "/jar/decoder");
            }
            if (modelType.equals(RepoComponentConstant.repository_type_template)) {
                this.installTemplateFile(tarDir, file.getAbsolutePath() + "/template/" + modelName + "/" + modelVersion + "/" + version);
            }
            if (modelType.equals(RepoComponentConstant.repository_type_service)) {
                if (ServiceVOFieldConstant.field_type_kernel.equals(component) || ServiceVOFieldConstant.field_type_system.equals(component) || ServiceVOFieldConstant.field_type_service.equals(component)) {
                    this.installServiceFile(modelName, modelVersion, component, version, stage);
                } else {
                    throw new ServiceException("component必须为service或者system或者kernel!");
                }
            }
            if (modelType.equals(RepoComponentConstant.repository_type_webpack)) {
                this.installWebpackFile(tarDir, file.getAbsolutePath());
            }

            // 将状态保存起来
            this.scanLocalStatus(modelType, modelName, modelVersion, version, stage, component);
            this.scanLocalMd5(modelType, modelName, modelVersion, version, stage, component);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    private void mkdirDir(String destFileDir) throws IOException, InterruptedException {
        if (OSInfo.isWindows()) {
            destFileDir = destFileDir.replace("/", "\\");
            ShellUtils.executeCmd("mkdir " + destFileDir);
        }
        if (OSInfo.isLinux()) {
            ShellUtils.executeShell("mkdir -p '" + destFileDir + "'");
        }
    }

    private void copyFile(String installFileDir, String fileName, String destFileDir) throws IOException, InterruptedException {
        if (OSInfo.isWindows()) {
            installFileDir = installFileDir.replace("/", "\\");
            destFileDir = destFileDir.replace("/", "\\");

            ShellUtils.executeCmd("xcopy /r/Y/F \"" + installFileDir + "\\" + fileName + "\" \"" + destFileDir + "\"");
        }
        if (OSInfo.isLinux()) {
            ShellUtils.executeShell("cp -f '" + installFileDir + "/" + fileName + "' '" + destFileDir + "'");
        }
    }

    private void installTemplateFile(String installFileDir, String destFileDir) throws IOException, InterruptedException {
        // 获得所有bin下的所有文件名
        List<String> fileList = FileNameUtils.findFileList(installFileDir, false, false);
        if (fileList.isEmpty()) {
            throw new ServiceException(installFileDir + " 没有文件");
        }

        // 预创建目录
        this.mkdirDir(destFileDir);

        // 拷贝文件
        for (String fileName : fileList) {
            this.copyFile(installFileDir, fileName, destFileDir);
        }
    }

    /**
     * 安装设备解码器的bin文件
     *
     * @param installFileDir 安装文件目录 \opt\fox-edge\repository\decoder\BASS260ZJ\1.0.0\bin
     * @param destFileDir    目标文件目录 \opt\fox-edge\jar\decoder
     * @throws IOException          异常信息
     * @throws InterruptedException 异常信息
     */
    private void installDecoderFile(String installFileDir, String destFileDir) throws IOException, InterruptedException {
        // 获得所有bin下的所有文件名
        List<String> fileList = FileNameUtils.findFileList(installFileDir, false, false);
        if (fileList.isEmpty()) {
            throw new ServiceException(installFileDir + " 没有文件");
        }

        // 预创建目录
        this.mkdirDir(destFileDir);


        // 复制文件
        for (String fileName : fileList) {
            if (!fileName.endsWith(".jar")) {
                continue;
            }

            // 检查：两个文件是否一致，如果一致旧不需重复替换
            if (FileCompareUtils.isSameFile(installFileDir + "/" + fileName, destFileDir + "/" + fileName)) {
                continue;
            }

            // 拷贝文件
            this.copyFile(installFileDir, fileName, destFileDir);
        }
    }


    /**
     * 安装service
     * 通过shell来调用upgrade.sh脚本来安装，这样才能避免manage-service直接覆盖自己的时候，程序的跑飞现象
     *
     * @param modelName 模块名称
     * @param component 组件名称
     * @param version   版本信息
     * @throws IOException          异常信息
     * @throws InterruptedException 异常信息
     */
    private void installServiceFile(String modelName, String modelVersion, String component, String version, String stage) throws IOException, InterruptedException {
        // 管理服务默认固定9000端口，其他服务按动态端口分配
        Integer serverPort = ServiceVOFieldConstant.field_port_gateway;
        if (!ServiceVOFieldConstant.field_app_gateway.equals(modelName) || !ServiceVOFieldConstant.field_type_kernel.equals(component)) {
            serverPort = this.serverPortService.getServicePort(modelName, component);
        }

        File file = new File("");
        ShellUtils.executeShell(file.getAbsolutePath() + "/shell/upgrade.sh " + component + " " + modelName + " " + modelVersion + " " + version + " " + stage + " " + serverPort);
    }

    private void installWebpackFile(String installFileDir, String destFileDir) throws IOException, InterruptedException {
        if (OSInfo.isWindows()) {
            installFileDir = installFileDir.replace("/", "\\");
            destFileDir = destFileDir.replace("/", "\\");

            // 删除旧文件
            ShellUtils.executeCmd("rd /s /q " + destFileDir + "\\dist");
            // 复制新文件
            ShellUtils.executeCmd("xcopy /s/r/Y/F/q " + installFileDir + " " + destFileDir);
        }
        if (OSInfo.isLinux()) {
            ShellUtils.executeShell("cp -rf '" + installFileDir + "/dist' '" + destFileDir + "'");
        }
    }

    public void uninstallServiceFile(String appName, String appType) throws IOException, InterruptedException {
        // 简单校验参数
        if (MethodUtils.hasEmpty(appName, appType)) {
            throw new ServiceException("参数不能为空:appName, appType");
        }

        List<Map<String, Object>> shellFileInfoList = ServiceIniFilesUtils.getConfFileInfoList();
        ProcessUtils.extendAppStatus(shellFileInfoList);

        for (Map<String, Object> map : shellFileInfoList) {
            if (!appName.equals(map.get(ServiceVOFieldConstant.field_app_name))) {
                continue;
            }
            if (!appType.equals(map.get(ServiceVOFieldConstant.field_app_type))) {
                continue;
            }
            if (appType.equals(map.get(ServiceVOFieldConstant.field_type_kernel))) {
                continue;
            }


            // 获得进程的PID，如果为空，则说明该进程并没有启动
            Object pid = map.get("pid");
            if (pid != null) {
                // kill掉该进程
                ShellUtils.executeShell("kill -9 " + pid);
            }

            // 下载tar文件
            File file = new File("");
            String fileName = (String) map.get(ServiceVOFieldConstant.field_file_name);

            // 检查：文件的合法性
            if (MethodUtils.hasEmpty(fileName)) {
                continue;
            }

            // 删除bin目录
            String binDir = file.getAbsolutePath() + "/bin/" + appType + "/" + appName;
            ShellUtils.executeShell("rm -r  '" + binDir + "'");

            // 删除shell目录
            String shellDir = file.getAbsolutePath() + "/shell/" + appType + "/" + appName;
            ShellUtils.executeShell("rm -r  '" + shellDir + "'");

            // 删除temp目录
            String tempDir = file.getAbsolutePath() + "/temp/" + appType + "/" + appName;
            ShellUtils.executeShell("rm -r  '" + tempDir + "'");
        }

        // 获得跟该应用相关的各版本信息
        Map<String, Object> versions = (Map<String, Object>) Maps.getOrDefault(this.statusMap, RepoComponentConstant.repository_type_service, appName, new HashMap<>());
        if (MethodUtils.hasEmpty(versions)) {
            return;
        }

        // 重新扫描各版本的安装状态：因为安装后是不知道具体哪个版本，所以要扫描各个版本
        for (String version : versions.keySet()) {
            Map<String, Object> stages = (Map<String, Object>) versions.get(version);
            for (String stage : stages.keySet()) {
                Map<String, Object> components = (Map<String, Object>) stages.get(stage);
                for (String component : components.keySet()) {
                    this.scanLocalStatus(RepoComponentConstant.repository_type_service, appName, RepoComponentConstant.filed_value_model_version_default, version, stage, component);
                    this.scanLocalMd5(RepoComponentConstant.repository_type_service, appName, RepoComponentConstant.filed_value_model_version_default, version, stage, component);
                }
            }
        }
    }

}
