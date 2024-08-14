/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class TempDirManageService {
    @Value("${spring.fox-service.service.type}")
    private String foxServiceType = "undefinedServiceType";

    @Value("${spring.fox-service.service.name}")
    private String foxServiceName = "undefinedServiceName";

    private String absolutePath;

    public void createTempDir() throws IOException {
        if (this.absolutePath == null) {
            File file = new File("");
            this.absolutePath = file.getAbsolutePath();
        }


        String pathName = this.absolutePath + "/temp/" + this.foxServiceType + "/" + this.foxServiceName;
        Path path = Paths.get(pathName);

        Files.createDirectories(path);
        if (!Files.exists(path)) {
            throw new IOException("unable to create dir: " + path);
        }
    }

    /**
     * 获得临时目录
     *
     * @return 服务拥有的临时目录
     */
    public String getTempDir() {
        if (this.absolutePath == null) {
            File file = new File("");
            this.absolutePath = file.getAbsolutePath();
        }

        return this.absolutePath + "/temp/" + this.foxServiceType + "/" + this.foxServiceName;
    }
}
