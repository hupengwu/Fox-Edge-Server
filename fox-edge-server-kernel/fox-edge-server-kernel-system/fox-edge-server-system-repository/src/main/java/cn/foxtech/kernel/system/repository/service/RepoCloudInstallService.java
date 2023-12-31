package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.domain.constant.ServiceVOFieldConstant;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.process.ProcessUtils;
import cn.foxtech.common.utils.file.FileCompareUtils;
import cn.foxtech.common.utils.file.FileNameUtils;
import cn.foxtech.common.utils.file.FileTextUtils;
import cn.foxtech.common.utils.http.DownLoadUtil;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.shell.ShellUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import cn.foxtech.kernel.system.common.service.ManageConfigService;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import cn.foxtech.kernel.system.repository.constants.RepoConfigConstant;
import cn.foxtech.kernel.system.repository.constants.RepoStatusConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sun.awt.OSInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 云端组件的安装
 */
@Component
public class RepoCloudInstallService {
    private final String siteUri = "http://www.fox-tech.cn";

    /**
     * 配置管理查询
     */
    @Autowired
    private ManageConfigService configService;

    @Autowired
    private EntityManageService entityManageService;

    /**
     * 本地端口分配
     */
    @Autowired
    private RepoLocalAppPortService appPortService;


    /**
     * jar文件信息
     */
    @Autowired
    private RepoLocalJarFileInfoService jarFileService;

    /**
     * app服务的conf配置文件
     */
    @Autowired
    private RepoLocalAppConfService appConfService;

    /**
     * 路径名称
     */
    @Autowired
    private RepoLocalPathNameService pathNameService;

    /**
     * 组件安装状态
     */
    @Autowired
    private RepoCloudInstallStatus installStatus;

    @Autowired
    private RepoCloudRemoteService cloudRemoteService;

    @Autowired
    private RepoLocalCompService localCompService;


    /**
     * 从云端查询仓库列表，并保存到本地
     *
     * @param modelType 模块类型
     * @return uri信息
     * @throws IOException 异常信息
     */
    public List<Map<String, Object>> queryUriListFile(String modelType) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put(RepoCompConstant.filed_model_type, modelType);
        Map<String, Object> respond = this.cloudRemoteService.queryCloudCompFileList(body);

        List<Map<String, Object>> list = (List<Map<String, Object>>) respond.get("data");
        if (list == null) {
            throw new ServiceException("云端数据仓库返回的数据为空！");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("list", list);

        String json = JsonUtils.buildJson(data);

        String listFileName;
        if (RepoCompConstant.repository_type_decoder.equals(modelType)) {
            listFileName = "decoderList.jsn";
        } else if (RepoCompConstant.repository_type_webpack.equals(modelType)) {
            listFileName = "webpackList.jsn";
        } else if (RepoCompConstant.repository_type_service.equals(modelType)) {
            listFileName = "serviceList.jsn";
        } else if (RepoCompConstant.repository_type_template.equals(modelType)) {
            listFileName = "templateList.jsn";
        } else {
            listFileName = "serviceList.jsn";
        }

        // 确认目录
        String localPath = this.pathNameService.getPathName4LocalRepo2modelType(modelType);

        // 创建目录
        File pathDir = new File(localPath);
        if (!pathDir.exists()) {
            pathDir.mkdirs();
        }

        // 将获得的list信息保存到本地
        FileTextUtils.writeTextFile(localPath + "/" + listFileName, json, "UTF-8");

        return list;
    }


    public void extendLocalStatus(Map<String, Object> entity) {
        {
            String modelType = (String) entity.getOrDefault(RepoCompConstant.filed_model_type, "");
            String modelName = (String) entity.getOrDefault(RepoCompConstant.filed_model_name, "");
            String modelVersion = (String) entity.getOrDefault(RepoCompConstant.filed_model_version, RepoCompConstant.filed_value_model_version_default);
            Map<String, Object> lastVersion = (Map<String, Object>) entity.getOrDefault(RepoCompConstant.filed_last_version, new HashMap<>());
            List<Map<String, Object>> versions = (List<Map<String, Object>>) entity.getOrDefault(RepoCompConstant.filed_versions, "");
            String component = (String) entity.getOrDefault(RepoCompConstant.filed_component, "");


            // 验证last版本的破损状态
            int status = this.installStatus.verifyMd5Status(modelType, modelName, modelVersion, component, lastVersion);
            if (RepoStatusConstant.status_damaged_package == status) {
                lastVersion.put(RepoCompConstant.filed_status, status);
            } else {
                if (this.installStatus.verifyUpgradeStatus(modelType, modelName, modelVersion, lastVersion, versions)) {
                    lastVersion.put(RepoCompConstant.filed_status, RepoStatusConstant.status_need_upgrade);
                }
            }


            // 验证明细包的破损状态
            for (Map<String, Object> verEntity : versions) {
                status = this.installStatus.verifyMd5Status(modelType, modelName, modelVersion, component, verEntity);
                verEntity.put(RepoCompConstant.filed_status, status);

                // 检查：该版本是否为【已安装】版本，如果是，则该版本为当前版本，因为【已安装】版本为正在使用的唯一版本
                if (RepoStatusConstant.status_installed == status) {
                    entity.put(RepoCompConstant.filed_used_version, verEntity);
                }
            }
        }
    }

    public void insertRepoCompEntity(String modelType, String modelName, String modelVersion) throws IOException {
        // 向云端查询组件信息
        Map<String, Object> body = new HashMap<>();
        body.put(RepoCompConstant.filed_model_type, modelType);
        body.put(RepoCompConstant.filed_model_name, modelName);
        body.put(RepoCompConstant.filed_model_version, modelVersion);
        Map<String, Object> respond = this.cloudRemoteService.queryCloudCompFileList(body);

        // 取出组件信息
        List<Map<String, Object>> list = (List<Map<String, Object>>) respond.get("data");
        if (MethodUtils.hasEmpty(list)) {
            throw new ServiceException("云端数据仓库返回的数据为空！");
        }
        Map<String, Object> data = list.get(0);

        // 构造本地仓库组件实体需要的参数
        Map<String, Object> localMap = this.localCompService.convertCloud2Local(data);
        if (localMap==null){
            return;
        }

        // 构造本地仓库实体
        RepoCompEntity repoCompEntity = this.localCompService.buildCompEntity(localMap);
        if (repoCompEntity == null) {
            throw new ServiceException("构造的本地仓库组件实体为null");
        }

        // 插入、更新数据
        RepoCompEntity exist = this.entityManageService.getEntity(repoCompEntity.makeServiceKey(), RepoCompEntity.class);
        if (exist == null) {
            this.entityManageService.insertEntity(repoCompEntity);
        } else {
            RepoCompEntity clone = JsonUtils.clone(exist);
            clone.getCompParam().putAll(repoCompEntity.getCompParam());
            this.entityManageService.updateEntity(clone);
        }
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
        fileDir = FileNameUtils.getOsFilePath(file.getAbsolutePath());

        if (OSInfo.getOSType().equals(OSInfo.OSType.WINDOWS)) {
            // 删除可能存在的目录
            ShellUtils.executeCmd("rd /s /q " + fileDir);
        }
        if (OSInfo.getOSType().equals(OSInfo.OSType.LINUX)) {
            ShellUtils.executeShell("rm -rf '" + fileDir + "'");
        }
    }

    public void scanLocalStatusAndMd5(String modelType, String modelName, String modelVersion, Map<String, Object> versionMap) {
        String version = (String) versionMap.get(RepoCompConstant.filed_version);
        String stage = (String) versionMap.get(RepoCompConstant.filed_stage);
        String component = (String) versionMap.get(RepoCompConstant.filed_component);

        // 扫描本地的状态
        this.installStatus.scanLocalStatus(modelType, modelName, modelVersion, version, stage, component);
        this.installStatus.scanLocalMd5(modelType, modelName, modelVersion, version, stage, component);
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
     * 扫码本地已安装模块的状态
     * 主要场景：用户在安装完成之后，删除了本地仓库中的安装包，此时只有安装文件，就涉及到它们的安装状态问题
     *
     * @param modelType    模块类型
     * @param modelName    模块名称
     * @param modelVersion
     */
    public Map<String, Object> scanModelStatus(String modelType, String modelName, String modelVersion) {
        // 简单验证
        if (MethodUtils.hasEmpty(modelType, modelName, modelVersion)) {
            throw new ServiceException("参数不能为空: modelType, modelName, modelVersion");
        }

        if (RepoCompConstant.repository_type_decoder.equals(modelType)) {
            String fileName = modelName + "." + modelVersion + ".jar";
            Map<String, Object> jarInfoMap = this.jarFileService.readJarFiles(fileName);
            return jarInfoMap;
        }
        if (RepoCompConstant.repository_type_template.equals(modelType)) {
            String modelPathName = this.pathNameService.getPathName4LocalTemplate2modelVersion(modelName, modelVersion);
            File file = new File(modelPathName);
            if (!file.exists() || file.isFile()) {
                return null;
            }

            List<String> fileNames = Arrays.asList(file.list());
            Collections.sort(fileNames);
            if (fileNames.isEmpty()) {
                return null;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("version", fileNames.get(fileNames.size() - 1));
            return result;
        }

        return null;
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
        String host = (String) this.configService.getConfigValueOrDefault(RepoConfigConstant.filed_config_name, RepoConfigConstant.filed_config_file, this.siteUri);
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

        String fileName = this.pathNameService.getFileName4LocalRepoTarFile(modelName, modelVersion, version);

        String host = (String) this.configService.getConfigValueOrDefault(RepoConfigConstant.filed_config_name, RepoConfigConstant.filed_config_file, this.siteUri);
        if (MethodUtils.hasEmpty(host)) {
            throw new ServiceException("尚未配置仓库的uri，请先配置仓库的uri");
        }

        if (!fileName.endsWith(".tar")) {
            throw new ServiceException("文件必须为tar格式!");
        }
        Set<String> components = new HashSet<>();
        components.add("bin");
        components.add(RepoCompConstant.repository_type_template);
        components.add(ServiceVOFieldConstant.field_type_service);
        components.add(ServiceVOFieldConstant.field_type_system);
        components.add(ServiceVOFieldConstant.field_type_kernel);
        components.add(RepoCompConstant.repository_type_webpack);
        if (!components.contains(component)) {
            throw new ServiceException("component必须为：" + components);
        }

        // 下载tar文件
        String url = host + "/" + modelType + "/" + modelName + "/" + modelVersion + "/" + version + "/" + pathName;
        String localPath = this.pathNameService.getPathName4LocalRepo2component(modelType, modelName, modelVersion, version, stage, component);
        DownLoadUtil.downLoadFromHttpUrl(url, fileName, localPath, "");


        String tarDir = localPath + "/" + "tar";
        String localFile = localPath + "/" + fileName;

        tarDir = FileNameUtils.getOsFilePath(tarDir);
        localFile = FileNameUtils.getOsFilePath(localFile);

        if (OSInfo.getOSType().equals(OSInfo.OSType.WINDOWS)) {
            // 删除已经存在的解压目录
            ShellUtils.executeCmd("rd /s /q " + tarDir);

            // 新建一个目解压录
            ShellUtils.executeCmd("mkdir " + tarDir);

            // 解压文件到解压目录:tar -xf 静默解压，windows下不能返回太多内容，否则会死锁
            ShellUtils.executeCmd("tar -xf " + localFile + " -C " + tarDir);
        }
        if (OSInfo.getOSType().equals(OSInfo.OSType.LINUX)) {
            // 删除已经存在的解压目录
            ShellUtils.executeShell("rm -r  '" + tarDir + "'");

            // 新建一个目解压录
            ShellUtils.executeShell("mkdir '" + tarDir + "'");

            // 解压文件到解压目录
            ShellUtils.executeShell("tar -xvf '" + localFile + "' -C '" + tarDir + "'");
        }


        // 将状态保存起来
        this.installStatus.scanLocalStatus(modelType, modelName, modelVersion, version, stage, component);
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

            String modelNameDir = this.pathNameService.getPathName4LocalRepo2modelName(modelType, modelName);
            String modelVersionDir = modelNameDir + "/" + modelVersion;
            String packageDir = modelVersionDir + "/" + version;

            packageDir = FileNameUtils.getOsFilePath(packageDir);

            //  删除安装包文件
            if (OSInfo.getOSType().equals(OSInfo.OSType.WINDOWS)) {
                ShellUtils.executeCmd("rd /s /q " + packageDir);
            }
            if (OSInfo.getOSType().equals(OSInfo.OSType.LINUX)) {
                ShellUtils.executeShell("rm -rf '" + packageDir + "'");
            }


            // 检查：是否整个模块都清空了，如果是就删除残余目录
            this.deleteEmptyDir(modelVersionDir);
            this.deleteEmptyDir(modelNameDir);

            // 将状态保存起来
            this.installStatus.scanLocalStatus(modelType, modelName, modelVersion, version, stage, component);
            this.installStatus.scanLocalMd5(modelType, modelName, modelVersion, version, stage, component);
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 安装模块
     *
     * @param modelType    模块类型
     * @param modelName    模块名称
     * @param modelVersion 模块版本
     * @param version      jar版本
     * @param stage        发布状态
     * @param component    组件类型
     */
    public void installFile(String modelType, String modelName, String modelVersion, String version, String stage, String component) {
        try {
            // 简单验证
            if (MethodUtils.hasEmpty(modelType, modelName, modelVersion, version, stage, component)) {
                throw new ServiceException("参数不能为空:modelType, modelName, modelVersion, version, stage, component");
            }

            File file = new File("");
            String tarDir = this.pathNameService.getPathName4LocalRepo2tar(modelType, modelName, modelVersion, version, stage, component);

            // 备份目标文件，然后将解压文件复制复制到目标目录
            if (modelType.equals(RepoCompConstant.repository_type_decoder)) {
                this.installDecoderFile(tarDir, file.getAbsolutePath() + "/jar/decoder", modelName, modelVersion);
            }
            if (modelType.equals(RepoCompConstant.repository_type_template)) {
                this.installTemplateFile(tarDir, file.getAbsolutePath() + "/template/" + modelName + "/" + modelVersion);
            }
            if (modelType.equals(RepoCompConstant.repository_type_service)) {
                if (ServiceVOFieldConstant.field_type_kernel.equals(component) || ServiceVOFieldConstant.field_type_system.equals(component) || ServiceVOFieldConstant.field_type_service.equals(component)) {
                    this.installServiceFile(modelName, modelVersion, component, version, stage);
                } else {
                    throw new ServiceException("component必须为service或者system或者kernel!");
                }
            }
            if (modelType.equals(RepoCompConstant.repository_type_webpack)) {
                this.installWebpackFile(tarDir, file.getAbsolutePath());
            }

            // 安装某个文件版本后，其他文件版本的状态，都会联动变化，所以要扫描整个大版本
            List<Map<String, Object>> versions = this.pathNameService.findRepoLocalModel(modelType, modelName, modelVersion);
            for (Map<String, Object> map : versions) {
                String subVersion = (String) map.get(RepoCompConstant.filed_version);
                String subStage = (String) map.get(RepoCompConstant.filed_stage);
                String subComponent = (String) map.get(RepoCompConstant.filed_component);

                // 将状态保存起来
                this.installStatus.scanLocalStatus(modelType, modelName, modelVersion, subVersion, subStage, subComponent);
                this.installStatus.scanLocalMd5(modelType, modelName, modelVersion, subVersion, subStage, subComponent);
            }

        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    private void mkdirDir(String destFileDir) throws IOException, InterruptedException {
        destFileDir = FileNameUtils.getOsFilePath(destFileDir);

        if (OSInfo.getOSType().equals(OSInfo.OSType.WINDOWS)) {
            ShellUtils.executeCmd("mkdir " + destFileDir);
        }
        if (OSInfo.getOSType().equals(OSInfo.OSType.LINUX)) {
            ShellUtils.executeShell("mkdir -p '" + destFileDir + "'");
        }
    }

    private void copyFile(String installFileDir, String fileName, String destFileDir) throws IOException, InterruptedException {
        installFileDir = FileNameUtils.getOsFilePath(installFileDir);
        destFileDir = FileNameUtils.getOsFilePath(destFileDir);

        if (OSInfo.getOSType().equals(OSInfo.OSType.WINDOWS)) {
            ShellUtils.executeCmd("xcopy /r/Y/F \"" + installFileDir + "\\" + fileName + "\" \"" + destFileDir + "\"");
        }
        if (OSInfo.getOSType().equals(OSInfo.OSType.LINUX)) {
            ShellUtils.executeShell("cp -f '" + installFileDir + "/" + fileName + "' '" + destFileDir + "'");
        }
    }

    private void copyFile(String installFileDir, String fileName, String destFileDir, String destFileName) throws IOException, InterruptedException {
        installFileDir = FileNameUtils.getOsFilePath(installFileDir);
        destFileDir = FileNameUtils.getOsFilePath(destFileDir);

        if (OSInfo.getOSType().equals(OSInfo.OSType.WINDOWS)) {
            ShellUtils.executeCmd("copy /y \"" + installFileDir + "\\" + fileName + "\" \"" + destFileDir + "\\" + destFileName + "\"");
        }
        if (OSInfo.getOSType().equals(OSInfo.OSType.LINUX)) {
            ShellUtils.executeShell("cp -f '" + installFileDir + "/" + fileName + "' '" + destFileDir + "/" + destFileName + "'");
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
    private void installDecoderFile(String installFileDir, String destFileDir, String modelName, String modelVersion) throws IOException, InterruptedException {
        // 获得repository目录下所有下的可安装所有文件名
        List<String> fileList = FileNameUtils.findFileList(installFileDir, false, false);
        if (fileList.isEmpty()) {
            throw new ServiceException(installFileDir + " 没有文件");
        }

        // 预创建目录
        this.mkdirDir(destFileDir);


        // 复制文件：该目录实际上有且只有一个jar文件
        for (String fileName : fileList) {
            if (!fileName.endsWith(".jar")) {
                continue;
            }

            String destFileName = modelName + "." + modelVersion + ".jar";

            // 检查：该文件是否已经存在，如果存在，那么就检查文件内容是否相同
            File exist = new File(destFileDir + "/" + destFileName);
            if (exist.isFile() && exist.exists()) {
                // 检查：两个文件是否一致，如果一致旧不需重复替换
                if (FileCompareUtils.isSameFile(installFileDir + "/" + fileName, destFileDir + "/" + destFileName)) {
                    continue;
                }
            }


            // 拷贝文件
            this.copyFile(installFileDir, fileName, destFileDir, destFileName);
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
            serverPort = this.appPortService.getServicePort(modelName, component);
        }

        File file = new File("");
        ShellUtils.executeShell(file.getAbsolutePath() + "/shell/upgrade.sh " + component + " " + modelName + " " + modelVersion + " " + version + " " + stage + " " + serverPort);
    }

    private void installWebpackFile(String installFileDir, String destFileDir) throws IOException, InterruptedException {
        installFileDir = FileNameUtils.getOsFilePath(installFileDir);
        destFileDir = FileNameUtils.getOsFilePath(destFileDir);

        if (OSInfo.getOSType().equals(OSInfo.OSType.WINDOWS)) {
            // 删除旧文件
            ShellUtils.executeCmd("rd /s /q " + destFileDir + "\\dist");
            // 复制新文件
            ShellUtils.executeCmd("xcopy /s/r/Y/F/q " + installFileDir + " " + destFileDir);
        }
        if (OSInfo.getOSType().equals(OSInfo.OSType.LINUX)) {
            ShellUtils.executeShell("cp -rf '" + installFileDir + "/dist' '" + destFileDir + "'");
        }
    }

    public void uninstallServiceFile(String appName, String appType) throws IOException, InterruptedException {
        // 简单校验参数
        if (MethodUtils.hasEmpty(appName, appType)) {
            throw new ServiceException("参数不能为空:appName, appType");
        }

        List<Map<String, Object>> shellFileInfoList = this.appConfService.getConfFileInfoList();
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
        Map<String, Object> versions = this.installStatus.getModelStatus(RepoCompConstant.repository_type_service, appName);
        if (MethodUtils.hasEmpty(versions)) {
            return;
        }

        // 重新扫描各版本的安装状态：因为安装后是不知道具体哪个版本，所以要扫描各个版本
        for (String version : versions.keySet()) {
            Map<String, Object> stages = (Map<String, Object>) versions.get(version);
            for (String stage : stages.keySet()) {
                Map<String, Object> components = (Map<String, Object>) stages.get(stage);
                for (String component : components.keySet()) {
                    this.installStatus.scanLocalStatus(RepoCompConstant.repository_type_service, appName, RepoCompConstant.filed_value_model_version_default, version, stage, component);
                    this.installStatus.scanLocalMd5(RepoCompConstant.repository_type_service, appName, RepoCompConstant.filed_value_model_version_default, version, stage, component);
                }
            }
        }
    }

}
