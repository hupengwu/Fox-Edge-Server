package cn.foxtech.common.utils.jar.info;

import cn.foxtech.common.utils.Maps;
import cn.foxtech.common.utils.xml.XmlReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarInfoReader {
    /**
     * 读取类信息
     *
     * @param jarFilePath
     * @return
     * @throws IOException
     */
    public static Set<String> readClassName(String jarFilePath) throws IOException {
        Map<String, Object> jarInfo = readJarInfo(jarFilePath);

        return (Set<String>) jarInfo.get("classFileName");
    }

    public static Set<String> readDirectoryName(String jarFilePath) throws IOException {
        Map<String, Object> jarInfo = readJarInfo(jarFilePath);

        return (Set<String>) jarInfo.get("directoryName");
    }

    public static JarInfoEntity readJarInfoEntity(String jarFilePath) throws IOException {
        // 读取jar文件的静态信息
        Map<String, Object> jarInfo = readJarInfo(jarFilePath);

        // 提取dir和class信息
        JarInfoEntity entity = new JarInfoEntity();
        entity.getClassFileName().addAll((Set<String>) jarInfo.get("classFileName"));
        entity.getDirectoryName().addAll((Set<String>) jarInfo.get("directoryName"));

        // 读取POM.XML文件信息
        String xml = readPomXml(jarFilePath, (String) jarInfo.get("pom.xml"));
        Map<String, Object> jarXml = XmlReader.parse(xml);
        Object xmlEl = Maps.getValue(jarXml, "project", "dependencies", "dependency");

        // XML解释器，在单个元素的时候会返回MAP，在多个数据的时候，会返回LIST
        List<Map<String, Object>> dependencies = new ArrayList<>();
        if (xmlEl != null) {
            if (xmlEl instanceof Map) {
                dependencies.add((Map) xmlEl);
            }
            if (xmlEl instanceof List) {
                dependencies = (List) xmlEl;
            }
        }


        for (Map<String, Object> dependency : dependencies) {
            JarInfoItem item = new JarInfoItem();
            item.setGroupId((String) dependency.get("groupId"));
            item.setArtifactId((String) dependency.get("artifactId"));
            item.setVersion((String) dependency.get("version"));

            entity.getDependencies().add(item);
        }

        // 读取pom.properties文件信息
        byte[] bytes = getFileFromJar(jarFilePath, "pom.properties");
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(bytes));
        entity.getProperties().setGroupId(properties.getProperty("groupId"));
        entity.getProperties().setArtifactId(properties.getProperty("artifactId"));
        entity.getProperties().setVersion(properties.getProperty("version"));

        return entity;
    }

    public static Map<String, Object> readJarInfo(String jarFilePath) throws IOException {
        Map<String, Object> result = new HashMap<>();

        Set<String> dirNames = new HashSet<>();
        Set<String> classNames = new HashSet<>();

        result.put("directoryName", dirNames);
        result.put("classFileName", classNames);

        ZipInputStream zip = new ZipInputStream(Files.newInputStream(Paths.get(jarFilePath)));
        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
            if (entry.isDirectory()) {
                // 目录名称
                dirNames.add(entry.getName());
            } else {
                // class文件名称
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName().replace('/', '.');
                    classNames.add(className.substring(0, className.length() - ".class".length()));
                }

                if (entry.getName().startsWith("META-INF/maven") && entry.getName().endsWith("pom.xml")) {
                    result.put("pom.xml", entry.getName());
                    continue;
                }

                // META-INF.maven.cn.fox-tech.fox-edge-server-protocol-zxdu58.pom.properties
                if (entry.getName().startsWith("META-INF/maven") && entry.getName().endsWith("pom.properties")) {
                    result.put("pom.properties", entry.getName());
                    continue;
                }
            }
        }

        return result;
    }

    /**
     * 读取jar中的某个文件的内容
     *
     * @param jarFilePath jar文件的名称
     * @param filename    jar中某个文件的明长城
     * @return byte[]
     */
    public static byte[] getFileFromJar(String jarFilePath, String filename) {
        try (JarFile jarFile = new JarFile(jarFilePath)) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                if (jarEntry.getName().endsWith(filename)) {

                    // 读取jar包中某个文件的内容
                    InputStream inputStream = null;
                    try {
                        inputStream = jarFile.getInputStream(jarEntry);
                        byte[] bytes = new byte[inputStream.available()];
                        inputStream.read(bytes);
                        return bytes;
                    } finally {
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        throw new RuntimeException(String.format("从 %s 中获取 %s 失败", jarFilePath, filename));
    }

    public static Properties readPomProperties(String jarFilePath, String fileName) throws IOException {
        try {
            byte[] bytes = getFileFromJar(jarFilePath, fileName);
            Properties properties = new Properties();
            properties.load(new ByteArrayInputStream(bytes));
            return properties;
        } catch (IOException e) {
            throw e;
        }
    }

    public static Properties readPomProperties(String jarFilePath) throws IOException {
        try {
            Map<String, Object> jarInfo = readJarInfo(jarFilePath);

            byte[] bytes = getFileFromJar(jarFilePath, "pom.properties");
            Properties properties = new Properties();
            properties.load(new ByteArrayInputStream(bytes));
            return properties;
        } catch (IOException e) {
            throw e;
        }
    }

    public static String readPomXml(String jarFilePath) throws IOException {
        Map<String, Object> jarInfo = readJarInfo(jarFilePath);

        return readPomXml(jarFilePath, (String) jarInfo.get("pom.xml"));
    }

    public static String readPomXml(String jarFilePath, String fileName) throws IOException {
        try {
            StringBuilder sb = new StringBuilder();

            byte[] bytes = getFileFromJar(jarFilePath, fileName);
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);

            BufferedInputStream in = new BufferedInputStream(bi);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            bi.close();
            in.close();
            br.close();
            return sb.toString();
        } catch (IOException e) {
            throw e;
        }
    }
}
