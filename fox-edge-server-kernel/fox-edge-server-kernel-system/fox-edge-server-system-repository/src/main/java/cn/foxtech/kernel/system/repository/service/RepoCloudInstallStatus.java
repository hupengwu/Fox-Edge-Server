package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.Maps;
import cn.foxtech.common.utils.file.FileCompareUtils;
import cn.foxtech.common.utils.file.FileNameUtils;
import cn.foxtech.common.utils.md5.MD5Utils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import cn.foxtech.kernel.system.repository.constants.RepoStatusConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 安装状态的管理
 */
@Component
public class RepoCloudInstallStatus {
    /**
     * 本地组件的安装状态列表：通过缓存安装状态，用于优化磁盘扫描安装包的长时间卡顿问题
     */
    private final Map<String, Object> statusMap = new ConcurrentHashMap<>();

    @Autowired
    private RedisConsoleService logger;

    @Autowired
    private RepoLocalPathNameService pathNameService;

    /**
     * 扫描仓库状态
     */
    public void scanRepositoryStatus(String modelType) {
        try {
            // 扫描本地目录，初步判定有哪些组件，构造出组件列表
            List<Map<String, Object>> localList = this.pathNameService.findRepoLocalModel(modelType);

            // 清空原有的数据
            Maps.setValue(this.statusMap, modelType, new ConcurrentHashMap<>());

            // 扫描本地组件的完整状态，扫描出组件的状态
            this.scanLocalStatus(modelType, localList);

            // 本地存在的组件特征
            Set<String> localKeys = new HashSet<>();
            for (Map<String, Object> map : localList) {
                String modelName = (String) map.get(RepoCompConstant.filed_model_name);
                String modelVersion = (String) map.get(RepoCompConstant.filed_model_version);
                String version = (String) map.get(RepoCompConstant.filed_version);
                String stage = (String) map.get(RepoCompConstant.filed_stage);
                String component = (String) map.get(RepoCompConstant.filed_component);
                localKeys.add(modelType + ":" + modelName + ":" + modelVersion + ":" + version + ":" + stage + ":" + component);

            }

            // 获得云端组件列表
            List<Map<String, Object>> cloudList = this.pathNameService.queryLocalListFile(modelType);

            // 尚未下载云端组件，标识未未下载状态
            for (Map<String, Object> map : cloudList) {
                String modelName = (String) map.get(RepoCompConstant.filed_model_name);
                String modelVersion = (String) map.get(RepoCompConstant.filed_model_version);
                Map<String, Object> lastVersion = (Map<String, Object>) map.get(RepoCompConstant.filed_last_version);
                List<Map<String, Object>> versions = (List<Map<String, Object>>) map.get(RepoCompConstant.filed_versions);

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
                    String version = (String) verEntity.get(RepoCompConstant.filed_version);
                    String stage = (String) verEntity.get(RepoCompConstant.filed_stage);
                    String component = (String) verEntity.get(RepoCompConstant.filed_component);
                    if (MethodUtils.hasEmpty(modelName, modelVersion, version, component)) {
                        continue;
                    }


                    // 检查：该组件是否存在本地
                    String key = modelType + ":" + modelName + ":" + modelVersion + ":" + version + ":" + stage + ":" + component;
                    if (localKeys.contains(key)) {
                        continue;
                    }

                    // 标识为未下载状态
                    Maps.setValue(this.statusMap, modelType, modelName, modelVersion, version, stage, component, RepoCompConstant.filed_status, RepoStatusConstant.status_not_downloaded);
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
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
        String tarFile = this.pathNameService.getPathName4LocalRepo2tarFile(modelType, modelName, modelVersion, version, stage, component);
        File check = new File(tarFile);
        if (!check.exists()) {
            Maps.setValue(this.statusMap, modelType, modelName, modelVersion, version, stage, component, RepoCompConstant.filed_local_md5, md5);
            return;
        }

        // 计算MD5
        md5 = MD5Utils.getMD5Txt(check);

        Maps.setValue(this.statusMap, modelType, modelName, modelVersion, version, stage, component, RepoCompConstant.filed_local_md5, md5);
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
        int status = RepoStatusConstant.status_not_downloaded;

        // 检查：是否已经下载
        File file = new File("");
        String absolutePath = file.getAbsolutePath();
        String tarFile = this.pathNameService.getPathName4LocalRepo2tarFile(modelType, modelName, modelVersion, version, stage, component);
        File check = new File(tarFile);
        if (!check.exists()) {
            Maps.setValue(this.statusMap, modelType, modelName, modelVersion, version, stage, component, RepoCompConstant.filed_status, status);
            return;
        }
        // 阶段2："已经下载，待安装!
        status = RepoStatusConstant.status_downloaded;

        // 检查：解压后的目录是否存在，至少包含一个文件
        String tarDir = this.pathNameService.getPathName4LocalRepo2tar(modelType, modelName, modelVersion, version, stage, component);
        List<String> tarFileNames = FileNameUtils.findFileList(tarDir, true, true);
        if (tarFileNames.isEmpty()) {
            Maps.setValue(this.statusMap, modelType, modelName, modelVersion, version, stage, component, RepoCompConstant.filed_status, status);
            return;
        }

        // 阶段3：已经下载，已解压!
        status = RepoStatusConstant.status_decompressed;

        // 阶段3：已经安装的文件
        for (String name : tarFileNames) {
            String tarFileName = name.substring(tarDir.length() + 1);

            String srcFileName = "";
            String jarFileName = "";

            if (RepoCompConstant.repository_type_decoder.equals(modelType)) {
                srcFileName = tarDir + "/" + tarFileName;
                jarFileName = absolutePath + "/jar/decoder/" + modelName + "." + modelVersion + ".jar";
            }
            if (RepoCompConstant.repository_type_template.equals(modelType)) {
                srcFileName = tarDir + "/" + tarFileName;
                jarFileName = absolutePath + "/template/" + modelName + "/" + modelVersion + "/" + tarFileName;
            }
            if (RepoCompConstant.repository_type_service.equals(modelType)) {
                srcFileName = tarDir + "/" + tarFileName;
                jarFileName = absolutePath + "/" + tarFileName;
            }
            if (RepoCompConstant.repository_type_webpack.equals(modelType)) {
                srcFileName = tarDir + "/" + tarFileName;
                jarFileName = absolutePath + "/" + tarFileName;
            }


            // 检查：目标文件是否存在，如果存在缺失文件，则未安装
            check = new File(jarFileName);
            if (!check.exists()) {
                // 阶段4："已下载，未安装!"
                status = RepoStatusConstant.status_not_installed;
                break;
            }

            // 检查：内容是否一致，如果存在不一致，则未安装
            boolean same = FileCompareUtils.isSameFile(jarFileName, srcFileName);
            if (!same) {
                // 阶段4："已下载，未安装!"
                status = RepoStatusConstant.status_not_installed;
                break;
            }

            // 阶段5："已下载，已安装!"
            status = RepoStatusConstant.status_installed;
        }

        Maps.setValue(this.statusMap, modelType, modelName, modelVersion, version, stage, component, RepoCompConstant.filed_status, status);
    }

    /**
     * 扫描本地模块的安装状态
     *
     * @param modelType 模块类型
     * @param list      模块列表
     */
    public void scanLocalStatus(String modelType, List<Map<String, Object>> list) {
        for (Map<String, Object> map : list) {
            if (!modelType.equals(map.get(RepoCompConstant.filed_model_type))) {
                continue;
            }

            this.scanLocalStatus(map);
        }
    }

    private void scanLocalStatus(Map<String, Object> comp) {
        // 提取业务参数
        String modelName = (String) comp.get(RepoCompConstant.filed_model_name);
        String modelType = (String) comp.get(RepoCompConstant.filed_model_type);
        String modelVersion = (String) comp.get(RepoCompConstant.filed_model_version);
        String version = (String) comp.get(RepoCompConstant.filed_version);
        String stage = (String) comp.get(RepoCompConstant.filed_stage);
        String component = (String) comp.get(RepoCompConstant.filed_component);

        // 本地的状态
        this.scanLocalStatus(modelType, modelName, modelVersion, version, stage, component);
        this.scanLocalMd5(modelType, modelName, modelVersion, version, stage, component);
    }

    public boolean verifyUpgradeStatus(String modelType, String modelName, String modelVersion, Map<String, Object> lastVersion, List<Map<String, Object>> versions) {
        // 检查最新版本
        if (lastVersion == null) {
            return false;
        }

        String lastVer = (String) lastVersion.get(RepoCompConstant.filed_version);
        String lastStage = (String) lastVersion.get(RepoCompConstant.filed_stage);
        String lastComponent = (String) lastVersion.get(RepoCompConstant.filed_component);

        // 如果最新版本就是安装版本，那么不需要升级
        Integer lastStatus = (Integer) Maps.getOrDefault(this.statusMap, modelType, modelName, modelVersion, lastVer, lastStage, lastComponent, RepoCompConstant.filed_status, RepoStatusConstant.status_not_scanned);
        if (RepoStatusConstant.status_installed == lastStatus) {
            return false;
        }

        // 明细版本
        for (Map<String, Object> verEntity : versions) {
            String version = (String) verEntity.get(RepoCompConstant.filed_version);
            String stage = (String) verEntity.get(RepoCompConstant.filed_stage);
            String component = (String) verEntity.get(RepoCompConstant.filed_component);

            // 排除last版本
            if (version.equals(lastVer) && stage.equals(lastStage) && component.equals(lastComponent)) {
                continue;
            }

            // 检查：低版本是否处于安装状态，如果是安装状态，那么就是需要用最新版本来升级
            Integer status = (Integer) Maps.getOrDefault(this.statusMap, modelType, modelName, modelVersion, version, stage, component, RepoCompConstant.filed_status, RepoStatusConstant.status_not_scanned);
            if (RepoStatusConstant.status_installed == status) {
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
    public int verifyMd5Status(String modelType, String modelName, String modelVersion, String component, Map<String, Object> verEntity) {
        String version = (String) verEntity.get(RepoCompConstant.filed_version);
        String stage = (String) verEntity.get(RepoCompConstant.filed_stage);
        if (MethodUtils.hasEmpty(version, stage)) {
            return -1;
        }

        Integer status = (Integer) Maps.getOrDefault(this.statusMap, modelType, modelName, modelVersion, version, stage, component, RepoCompConstant.filed_status, RepoStatusConstant.status_not_scanned);
        String localMd5 = (String) Maps.getOrDefault(this.statusMap, modelType, modelName, modelVersion, version, stage, component, RepoCompConstant.filed_local_md5, "");
        verEntity.put(RepoCompConstant.filed_status, status);
        verEntity.put(RepoCompConstant.filed_local_md5, localMd5);

        return this.verifyMd5Status(verEntity, status, localMd5);
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
        if (status < RepoStatusConstant.status_downloaded) {
            return status;
        }

        if (localMd5 == null) {
            localMd5 = "";
        }

        // 在查询阶段，计算破损状态：本地和云端的MD5是否一致
        String md5 = (String) verEntity.getOrDefault(RepoCompConstant.filed_md5, "");
        if (!localMd5.equalsIgnoreCase(md5)) {
            return RepoStatusConstant.status_damaged_package;
        }

        return status;
    }

    /**
     * 获得跟该应用相关的各版本信息
     *
     * @param modelType
     * @param modelName
     * @return
     */
    public Map<String, Object> getModelStatus(String modelType, String modelName) {
        return (Map<String, Object>) Maps.getOrDefault(this.statusMap, modelType, modelName, new HashMap<>());
    }

}
