package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RepoLocalJarFileNameService {
    /**
     * 将jar文件名称，拆分为包名称和版本号，两个部分
     * 例如：fox-edge-server-protocol-core.v1.jar，拆分为fox-edge-server-protocol-core和v1
     *
     * @param jarFileName jar文件名称，例如fox-edge-server-protocol-core.v1.jar
     * @return decoder，v1
     */
    public Map<String, String> splitJarFileName(String jarFileName) {
        // 检查：是否为。jar文件
        if (!jarFileName.toLowerCase().endsWith(".jar")) {
            return null;
        }

        // 去除.jar的后缀，为拆分数据做准备
        jarFileName = jarFileName.substring(0, jarFileName.length() - ".jar".length());
        String[] items = jarFileName.split("\\.");
        if (items.length < 2) {
            return null;
        }

        String modelVersion = items[items.length - 1];
        String modelName = jarFileName.substring(0, jarFileName.length() - modelVersion.length() - 1);

        Map<String, String> result = new HashMap<>();
        result.put(RepoCompConstant.filed_model_name, modelName);
        result.put(RepoCompConstant.filed_model_version, modelVersion);

        return result;
    }


}
