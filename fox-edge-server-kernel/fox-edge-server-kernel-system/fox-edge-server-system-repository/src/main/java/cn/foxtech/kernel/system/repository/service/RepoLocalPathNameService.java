package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.utils.file.FileNameUtils;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RepoLocalPathNameService {
    private final String absolutePath = (new File("")).getAbsolutePath();

    /**
     * 查找本地已经下载的模块列表
     * 目录结构 \opt\fox-edge\repository\decoder\anno\1.0.0\bin
     *
     * @param modelType 组件类型
     * @return 已经下载的本地组件列表
     */
    public List<Map<String, Object>> findRepoLocalModel(String modelType) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        // 仓库目录
        String repoDirName = this.absolutePath + "/repository";

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
                // 查找一个版本级别的信息
                List<Map<String, Object>> versions = this.findRepoLocalModel(modelType, modelFile, modelVersionFile);
                resultList.addAll(versions);
            }

        }

        return resultList;
    }

    public List<Map<String, Object>> findRepoLocalModel(String modelType, String modelName, String modelVersion) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        // 程序目录
        File file = new File("");

        // 组件级别的目录：\opt\fox-edge\repository\decoder\fox-edge-server-protocol-dlt645-1997\v1
        File modelVersionDir = new File(file.getAbsolutePath() + "/repository/" + modelType + "/" + modelName + "/" + modelVersion);
        if (!modelVersionDir.exists() || !modelVersionDir.isDirectory()) {
            return resultList;
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
                    String version = versionDir.getName();
                    String stage = stageDir.getName();
                    String component = componentDir.getName();

                    Map<String, Object> map = new HashMap<>();
                    map.put(RepoCompConstant.filed_model_type, modelType);
                    map.put(RepoCompConstant.filed_model_name, modelName);
                    map.put(RepoCompConstant.filed_model_version, modelVersion);
                    map.put(RepoCompConstant.filed_version, version);
                    map.put(RepoCompConstant.filed_stage, stage);
                    map.put(RepoCompConstant.filed_component, component);

                    resultList.add(map);
                }
            }

        }

        return resultList;
    }

    /**
     * 获得路径
     *
     * @return \opt\fox-edge\shell\kernel\gateway-service\service.conf
     */
    public String getPathName4LocalShell2confFile(String appType, String appName) {
        return FileNameUtils.getOsFilePath(this.absolutePath + "/shell/" + appType + "/" + appName + "/" + "service.conf");
    }

    /**
     * 获得路径
     *
     * @return \opt\fox-edge\bin\kernel\gateway-service\fox-edge-server-manager-gateway.jar
     */
    public String getPathName4LocalBin2jarFile(String appType, String appName, String jarFileName) {
        return FileNameUtils.getOsFilePath(this.absolutePath + "/bin/" + appType + "/" + appName + "/" + jarFileName);
    }

    /**
     * 获得路径
     *
     * @return \opt\fox-edge\bin\service\python-server-demo\python-server-demo-20240312.py
     */
    public String getPathName4LocalBin2pyFile(String appType, String appName, String pyFileName) {
        return FileNameUtils.getOsFilePath(this.absolutePath + "/bin/" + appType + "/" + appName + "/" + pyFileName);
    }

    public String getPathName4LocalBin2MainFile(String appType, String appName, String mainFileName) {
        return FileNameUtils.getOsFilePath(this.absolutePath + "/bin/" + appType + "/" + appName + "/" + mainFileName);
    }

    /**
     * 获得路径
     *
     * @return \opt\fox-edge\repository\decoder\fox-edge-server-protocol-s7plc
     */
    public String getPathName4LocalRepo2modelName(String modelType, String modelName) {
        return FileNameUtils.getOsFilePath(this.absolutePath + "/repository/" + modelType + "/" + modelName);
    }

    /**
     * 获得路径
     *
     * @return \opt\fox-edge\repository\decoder
     */
    public String getPathName4LocalRepo2modelType(String modelType) {
        return FileNameUtils.getOsFilePath(this.absolutePath + "/repository/" + modelType);
    }

    /**
     * 获得路径
     *
     * @return \opt\fox-edge\jar\decoder\service\fox-edge-server-protocol-cjt188.v1.jar
     */
    public String getPathName4LocalJarDecoder2file(String modelName, String modelVersion) {
        return FileNameUtils.getOsFilePath(this.absolutePath + "/jar/decoder/" + modelName + "." + modelVersion + ".jar");
    }

    /**
     * 获得路径
     *
     * @return \opt\fox-edge\repository\decoder\fox-edge-server-protocol-s7plc\v1\1.0.4\master\service
     */
    public String getPathName4LocalRepo2component(String modelType, String modelName, String modelVersion, String version, String stage, String component) {
        return FileNameUtils.getOsFilePath(this.absolutePath + "/repository/" + modelType + "/" + modelName + "/" + modelVersion + "/" + version + "/" + stage + "/" + component);
    }

    /**
     * 获得路径
     *
     * @return \opt\fox-edge\repository\decoder\fox-edge-server-protocol-s7plc\v1\1.0.4\master\service\fox-edge-server-protocol-s7plc-v1-1.0.4.tar
     */
    public String getPathName4LocalRepo2tarFile(String modelType, String modelName, String modelVersion, String version, String stage, String component) {
        return FileNameUtils.getOsFilePath(this.getPathName4LocalRepo2component(modelType, modelName, modelVersion, version, stage, component) + "/" + this.getFileName4LocalRepoTarFile(modelName, modelVersion, version));
    }


    /**
     * 获得文件名：
     *
     * @return fox-edge-server-protocol-s7plc-v1-1.0.4.tar
     */
    public String getFileName4LocalRepoTarFile(String modelName, String modelVersion, String version) {
        return modelName + "-" + modelVersion + "-" + version + ".tar";
    }

    /**
     * 获得路径
     *
     * @return \opt\fox-edge\repository\decoder\fox-edge-server-protocol-cjt188-core\v1\1.0.4\master\service\tar
     */
    public String getPathName4LocalRepo2tar(String modelType, String modelName, String modelVersion, String version, String stage, String component) {
        return FileNameUtils.getOsFilePath(this.absolutePath + "/repository/" + modelType + "/" + modelName + "/" + modelVersion + "/" + version + "/" + stage + "/" + component + "/" + "tar");
    }

    /**
     * 获得路径
     *
     * @return \opt\fox-edge\template\dobot-mg400\v1
     */
    public String getPathName4LocalTemplate2modelVersion(String modelName, String modelVersion) {
        return FileNameUtils.getOsFilePath(this.absolutePath + "/template/" + modelName + "/" + modelVersion);
    }

    /**
     * 获得路径
     *
     * @return \opt\fox-edge\template\dobot-mg400\v1\1.0.0
     */
    public String getPathName4LocalTemplate2version(String modelName, String modelVersion) {
        return FileNameUtils.getOsFilePath(this.absolutePath + "/template/" + modelName + "/" + modelVersion);
    }
}
