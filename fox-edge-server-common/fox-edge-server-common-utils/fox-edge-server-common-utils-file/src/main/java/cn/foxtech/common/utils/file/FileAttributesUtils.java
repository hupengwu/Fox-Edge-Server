/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class FileAttributesUtils {
    public static BasicFileAttributes getAttributes(File file) throws IOException {
        Path path = file.toPath();
        return Files.readAttributes(path, BasicFileAttributes.class);
    }

    public static BasicFileAttributes getAttributes(String fileName) throws IOException {
        File file = new File(fileName);
        return getAttributes(file);
    }
}
