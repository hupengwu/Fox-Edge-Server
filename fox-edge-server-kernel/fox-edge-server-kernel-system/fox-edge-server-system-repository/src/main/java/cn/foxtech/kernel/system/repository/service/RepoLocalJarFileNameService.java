package cn.foxtech.kernel.system.repository.service;

import org.springframework.stereotype.Component;

@Component
public class RepoLocalJarFileNameService {
    /**
     * 将jar文件名称，拆分为包名称和版本号，两个部分
     * 例如：fox-edge-server-protocol-core.v1.jar，拆分为fox-edge-server-protocol-core和v1
     *
     * @param jarFileName jar文件名称，例如fox-edge-server-protocol-core.v1.jar
     * @return decoder
     */
    public String getModelName(String jarFileName) {
        // 检查：是否为。jar文件
        if (!jarFileName.toLowerCase().endsWith(".jar")) {
            return null;
        }

        // 去除.jar的后缀，为拆分数据做准备
        return jarFileName.substring(0, jarFileName.length() - ".jar".length());
    }


}
