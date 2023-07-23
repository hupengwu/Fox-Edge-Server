package cn.foxtech.manager.system.utils;

import cn.foxtech.common.domain.constant.ServiceVOFieldConstant;
import cn.foxtech.common.utils.file.FileTextUtils;
import cn.foxtech.common.utils.method.MethodUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获得各服务的启动service.ini信息
 * 这些service.ini文件是标准化的配置卫生间，里面有服务的关键信息
 */
public class ServiceIniFilesUtils {
    public static List<Map<String, Object>> getConfFileInfoList() throws IOException {
        File file = new File("");

        List<Map<String, Object>> result = new ArrayList<>();
        result.addAll(readConfFile(file.getAbsolutePath(), ServiceVOFieldConstant.field_type_kernel));
        result.addAll(readConfFile(file.getAbsolutePath(), ServiceVOFieldConstant.field_type_system));
        result.addAll(readConfFile(file.getAbsolutePath(), ServiceVOFieldConstant.field_type_service));

        return result;
    }

    private static List<Map<String, Object>> readConfFile(String absolutePath, String appType) throws IOException {
        List<Map<String, Object>> result = new ArrayList<>();

        File shellDir = new File(absolutePath + "/shell/" + appType);
        if (!shellDir.exists() || !shellDir.isDirectory()) {
            return result;
        }

        String[] dirs = shellDir.list();
        for (String s : dirs) {
            File dir = new File(shellDir, s);
            if (dir.isFile()) {
                continue;
            }
            File file = new File(dir, "service.conf");
            if (!file.isFile()) {
                continue;
            }

            List<String> lineList = FileTextUtils.readTextFileLines(file.getAbsolutePath());
            if (lineList.isEmpty()) {
                continue;
            }

            // 检查：配置文件中的appType是否正确
            if (!appType.equals(getParam(lineList, "appType"))) {
                continue;
            }

            // 读取文件内容
            String appName = getParam(lineList, "appName");
            String jarName = getParam(lineList, "jarName");
            String loaderName = getParam(lineList, "loaderName");
            String springParam = getParam(lineList, "springParam");


            // 验证数据
            if (MethodUtils.hasEmpty(appName) || MethodUtils.hasEmpty(jarName)) {
                continue;
            }


            Map<String, Object> data = new HashMap<>();
            data.put(ServiceVOFieldConstant.field_app_name, appName);
            data.put(ServiceVOFieldConstant.field_app_type, appType);
            data.put(ServiceVOFieldConstant.field_file_name, jarName);
            data.put(ServiceVOFieldConstant.field_loader_name, loaderName);
            data.put(ServiceVOFieldConstant.field_spring_param, springParam);
            data.put(ServiceVOFieldConstant.field_path_name, absolutePath + "/bin/" + appType + "/" + appName + "/" + jarName);

            result.add(data);
        }

        return result;
    }

    private static String getParam(List<String> lineList, String paramName) {
        for (String line : lineList) {
            if (!line.startsWith(paramName + "=")) {
                continue;
            }

            String value = line.substring(paramName.length() + 1);
            if (value.startsWith("\"")) {
                value = value.substring(1);
            }
            if (value.endsWith("\"")) {
                value = value.substring(0,value.length()-1);
            }

            return value;
        }

        return "";
    }
}
