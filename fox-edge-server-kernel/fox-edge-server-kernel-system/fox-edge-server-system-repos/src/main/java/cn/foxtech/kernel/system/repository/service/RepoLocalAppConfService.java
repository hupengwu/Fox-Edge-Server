/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.domain.constant.ServiceVOFieldConstant;
import cn.foxtech.common.entity.manager.InitialConfigService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.file.FileTextUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * 获得各服务组件的启动service.conf 信息
 * 这些service.conf文件是标准化的配置格式，里面有服务的关键信息
 */
@Component
public class RepoLocalAppConfService {

    @Autowired
    private RedisConsoleService console;

    @Autowired
    private RepoLocalPathNameService pathNameService;

    /**
     * 初始化配置：需要感知运行期的用户动态输入的配置，所以直接使用这个组件
     */
    @Autowired
    private InitialConfigService configService;

    /**
     * 读取配置文件内容
     *
     * @param filePath \opt\fox-edge\shell\kernel\gateway-service\service.conf
     * @return 配置文件的内容
     * @throws IOException 读取异常
     */
    public Map<String, Object> readConfFile(String filePath) throws IOException {
        List<String> lineList = FileTextUtils.readTextFileLines(filePath);
        if (lineList.isEmpty()) {
            throw new ServiceException("读取文件内容失败!");
        }

        // 读取文件内容
        String appEngine = getParam(lineList, "appEngine");
        String appType = getParam(lineList, "appType");
        String appName = getParam(lineList, "appName");
        String jarName = getParam(lineList, "jarName");
        String loaderName = getParam(lineList, "loaderName");
        String springParam = getParam(lineList, "springParam");
        String confFiles = getParam(lineList, "confFiles");
        String pyName = getParam(lineList, "pyName");
        String pythonParam = getParam(lineList, "pythonParam");
        String nativeName = getParam(lineList, "nativeName");
        String nativeParam = getParam(lineList, "nativeParam");

        // 验证数据
        if (MethodUtils.hasEmpty(appType) || MethodUtils.hasEmpty(appName)) {
            throw new ServiceException("配置文件内容，缺失配置项: appType, appName");
        }

        // 检测：如果没有填写，就是旧版本的conf文件，那么默认为java程序
        if (MethodUtils.hasEmpty(appEngine)) {
            appEngine = "java";
        }

        Map<String, Object> data = new HashMap<>();
        data.put(ServiceVOFieldConstant.field_app_engine, appEngine);
        data.put(ServiceVOFieldConstant.field_app_name, appName);
        data.put(ServiceVOFieldConstant.field_app_type, appType);
        data.put(ServiceVOFieldConstant.field_loader_name, loaderName);


        if (appEngine.equals("java")) {
            // java程序：必填项目jarName
            if (MethodUtils.hasEmpty(jarName)) {
                throw new ServiceException("配置文件内容，缺失配置项: jarName");
            }
            String pathName = this.pathNameService.getPathName4LocalBin2MainFile(appType, appName, jarName);

            data.put(ServiceVOFieldConstant.field_path_name, pathName);
            data.put(ServiceVOFieldConstant.field_file_name, jarName);
            data.put(ServiceVOFieldConstant.field_user_param, springParam);
        } else if (appEngine.equals("python") || appEngine.equals("python3")) {
            // python程序：必填项目pyName
            if (MethodUtils.hasEmpty(pyName)) {
                throw new ServiceException("配置文件内容，缺失配置项: pyName");
            }
            String pathName = this.pathNameService.getPathName4LocalBin2MainFile(appType, appName, pyName);

            data.put(ServiceVOFieldConstant.field_path_name, pathName);
            data.put(ServiceVOFieldConstant.field_file_name, pyName);
            data.put(ServiceVOFieldConstant.field_user_param, pythonParam);
        } else if (appEngine.equals("native")) {
            // native程序：必填项目nativeName, nativeParam
            if (MethodUtils.hasEmpty(nativeName, nativeParam)) {
                throw new ServiceException("配置文件内容，缺失配置项: nativeName, nativeParam");
            }
            String pathName = this.pathNameService.getPathName4LocalBin2MainFile(appType, appName, nativeName);

            data.put(ServiceVOFieldConstant.field_path_name, pathName);
            data.put(ServiceVOFieldConstant.field_file_name, nativeName);
            data.put(ServiceVOFieldConstant.field_user_param, nativeParam);
        } else {
            throw new ServiceException("配置文件内容，尚未支持的程序类型: " + appEngine);
        }


        // conf文件列表
        List<String> list = new ArrayList<>();
        if (!MethodUtils.hasEmpty()) {
            String[] confFileArr = confFiles.split(";");
            for (String confFile : confFileArr) {
                if (confFile.isEmpty()) {
                    continue;
                }

                list.add(confFile);
            }
        }
        data.put(ServiceVOFieldConstant.field_conf_files, list);

        return data;
    }

    public void saveConf(String filePath, Map<String, Object> data) throws IOException {
        String appEngine = (String) data.getOrDefault(ServiceVOFieldConstant.field_app_engine, "java");
        String appType = (String) data.getOrDefault(ServiceVOFieldConstant.field_app_type, "");
        String appName = (String) data.getOrDefault(ServiceVOFieldConstant.field_app_name, "");
        String fileName = (String) data.getOrDefault(ServiceVOFieldConstant.field_file_name, "");
        String loaderName = (String) data.getOrDefault(ServiceVOFieldConstant.field_loader_name, "");
        String userParam = (String) data.getOrDefault(ServiceVOFieldConstant.field_user_param, "");
        List<String> confFiles = (List<String>) data.getOrDefault(ServiceVOFieldConstant.field_conf_files, new ArrayList<>());

        if (MethodUtils.hasEmpty(appType, appName, fileName)) {
            throw new ServiceException("缺失配置参数：appType, appName, fileName");
        }


        StringBuilder sb = new StringBuilder();
        sb.append("appEngine=" + appEngine + "\r\n");
        sb.append("appType=" + appType + "\r\n");
        sb.append("appName=" + appName + "\r\n");

        if (appEngine.equals("java")) {
            sb.append("jarName=" + fileName + "\r\n");
            sb.append("springParam=\"" + userParam + "\"" + "\r\n");
            sb.append("loaderName=" + loaderName + "\r\n");
            sb.append("confFiles=\"");
            for (String line : confFiles) {
                sb.append(line + ";");
            }
            sb.append("\"");
        }
        if (appEngine.equals("python") || appEngine.equals("python3")) {
            sb.append("pyName=" + fileName + "\r\n");
            sb.append("pythonParam=\"" + userParam + "\"" + "\r\n");
        }
        if (appEngine.equals("native")) {
            sb.append("nativeName=" + fileName + "\r\n");
            sb.append("nativeParam=\"" + userParam + "\"" + "\r\n");
        }

        sb.append("\r\n");

        // 把文本内容写入文件
        FileTextUtils.writeTextFile(filePath, sb.toString(), "");
    }

    private String getParam(List<String> lineList, String paramName) {
        for (String line : lineList) {
            if (!line.startsWith(paramName + "=")) {
                continue;
            }

            String value = line.substring(paramName.length() + 1);
            if (value.startsWith("\"")) {
                value = value.substring(1);
            }
            if (value.endsWith("\"")) {
                value = value.substring(0, value.length() - 1);
            }

            return value;
        }

        return "";
    }

    public List<Map<String, Object>> getConfFileInfoList() throws IOException {
        File file = new File("");

        List<Map<String, Object>> result = new ArrayList<>();
        result.addAll(readConfFile(file.getAbsolutePath(), ServiceVOFieldConstant.field_type_kernel));
        result.addAll(readConfFile(file.getAbsolutePath(), ServiceVOFieldConstant.field_type_system));
        result.addAll(readConfFile(file.getAbsolutePath(), ServiceVOFieldConstant.field_type_service));

        return result;
    }

    public List<Map<String, Object>> filterAppConfFile(List<Map<String, Object>> appList) {
        Set<String> disables = this.getKernelAppEnable(false);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> map : appList) {
            String appType = (String) map.getOrDefault(ServiceVOFieldConstant.field_app_type, "");
            String appName = (String) map.getOrDefault(ServiceVOFieldConstant.field_app_name, "");
            String key = appType + ":" + appName;

            if (disables.contains(key)) {
                continue;
            }

            result.add(map);
        }

        return result;
    }

    public List<Map<String, Object>> filterModelList(List<Map<String, Object>> appList) {
        Set<String> disables = this.getKernelAppEnable(false);

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> map : appList) {
            String component = (String) map.getOrDefault(RepoCompConstant.field_component, "");
            String modelName = (String) map.getOrDefault(RepoCompConstant.field_model_name, "");
            String key = component + ":" + modelName;

            if (disables.contains(key)) {
                continue;
            }

            result.add(map);
        }

        return result;
    }

    public Set<String> getKernelAppEnable(boolean enable) {
        Map<String, Object> kernelEnableConfig = this.configService.getConfigParam("kernelEnableConfig");
        Map<String, Object> kernelConfig = (Map<String, Object>) kernelEnableConfig.getOrDefault("kernel", new HashMap<>());

        String key = "disable";
        if (enable) {
            key = "enable";
        }
        List<Map<String, Object>> list = (List<Map<String, Object>>) kernelConfig.getOrDefault(key, new ArrayList<>());

        Set<String> result = new HashSet<>();
        for (Map<String, Object> map : list) {
            String appType = (String) map.getOrDefault(ServiceVOFieldConstant.field_app_type, "");
            String appName = (String) map.getOrDefault(ServiceVOFieldConstant.field_app_name, "");
            result.add(appType + ":" + appName);
        }

        return result;
    }

    public List<Map<String, Object>> readConfFile(String absolutePath, String appType) throws IOException {
        List<Map<String, Object>> result = new ArrayList<>();

        File shellDir = new File(absolutePath + "/shell/" + appType);
        if (!shellDir.exists() || !shellDir.isDirectory()) {
            return result;
        }

        String[] dirs = shellDir.list();
        for (String appName : dirs) {
            // 读取模块的service.conf信息
            Map<String, Object> data = this.readConfFile(absolutePath, appType, appName);
            if (data == null) {
                continue;
            }

            result.add(data);

        }

        return result;
    }

    public Map<String, Object> readConfFile(String absolutePath, String appType, String appName) {
        try {
            File file = new File(absolutePath + "/shell/" + appType + "/" + appName + "/service.conf");
            if (!file.isFile()) {
                throw new ServiceException("指定的文件不存在:" + file.getAbsolutePath());
            }


            Map<String, Object> data = this.readConfFile(file.getAbsolutePath());
            if (!appType.equals(data.get("appType"))) {
                throw new ServiceException("service.conf文件种的appType与模块的service不匹配！");
            }

            return data;
        } catch (Exception e) {
            this.console.error("读取配置文件内容失败:" + e.getMessage());
            return null;
        }
    }
}
