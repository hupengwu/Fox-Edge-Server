/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.jar.info;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * JAR静态信息工具
 */
public class JarInfoUtils {
    /**
     * 扫描JAR包中的Class
     *
     * @param jarFile jar文件
     * @return Class名称列表
     * @throws IOException 文件异常
     */
    public static Set<String> scanClassNames(String path, String jarFile) throws IOException {
        Set<String> classNames = new HashSet<>();
        ZipInputStream zip = new ZipInputStream(Files.newInputStream(Paths.get(path + jarFile)));
        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                // This ZipEntry represents a class. Now, what class does it represent?
                String className = entry.getName().replace('/', '.'); // including ".class"
                classNames.add(className.substring(0, className.length() - ".class".length()));
            }
        }

        return classNames;
    }

    /**
     * 扫描类名称
     *
     * @param jarFiles jar文件列表
     * @return 文件名为key，文件中的class为value
     * @throws IOException 文件异常
     */
    public static Map<String, Set<String>> scanFile2Class(String path, List<String> jarFiles) throws IOException {
        // 类名称集合
        Map<String, Set<String>> map = new HashMap<>();
        for (String jarFile : jarFiles) {
            Set<String> classNames = scanClassNames(path, jarFile);
            map.put(jarFile, classNames);
        }

        return map;
    }

    /**
     * 扫描类名称
     *
     * @param jarFiles jar文件列表
     * @return class为key，文件名为value
     * @throws IOException 文件异常
     */
    public static Map<String, Set<String>> scanClass2File(String path, List<String> jarFiles) throws IOException {
        Map<String, Set<String>> class2file = new HashMap<>();

        Map<String, Set<String>> file2class = scanFile2Class(path, jarFiles);
        for (String fileName : file2class.keySet()) {
            Set<String> classNames = file2class.get(fileName);

            for (String className : classNames) {
                Set<String> files = class2file.computeIfAbsent(className, k -> new HashSet<>());
                files.add(fileName);
            }
        }

        return class2file;
    }

    /**
     * 冲突检查：检查是否有重复的类名称在不同的jar里面，并列出冲突的信息
     *
     * @param jarFiles jar文件列表
     * @return class为key，文件名为value
     * @throws IOException 读取异常
     */
    public static Map<String, Set<String>> checkConflictClass2Files(String path, List<String> jarFiles) throws IOException {
        Map<String, Set<String>> class2file4result = new HashMap<>();
        Map<String, Set<String>> class2file = scanClass2File(path, jarFiles);
        for (String className : class2file.keySet()) {
            Set<String> files = class2file.get(className);
            if (files.size() > 1) {
                Set<String> sets = class2file4result.computeIfAbsent(className, k -> new HashSet<>());
                sets.addAll(files);
            }
        }

        return class2file4result;
    }

    /**
     * 冲突检查：检查是否有重复的类名称在不同的jar里面，并列出冲突的信息
     *
     * @param jarFiles jar文件列表
     * @return file为key，冲突的class为value
     * @throws IOException 读取异常
     */
    public static Map<String, Set<String>> checkConflictFile2Class(String path, List<String> jarFiles) throws IOException {
        Map<String, Set<String>> result = new HashMap<>();

        Map<String, Set<String>> class2file4conflict = checkConflictClass2Files(path, jarFiles);
        for (String className : class2file4conflict.keySet()) {
            Set<String> files = class2file4conflict.get(className);
            for (String file : files) {
                Set<String> sets = result.computeIfAbsent(file, k -> new HashSet<>());
                sets.add(className);
            }
        }

        return result;
    }
}
