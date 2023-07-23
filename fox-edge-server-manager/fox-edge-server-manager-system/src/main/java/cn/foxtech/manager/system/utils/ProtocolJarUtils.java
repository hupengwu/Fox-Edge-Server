package cn.foxtech.manager.system.utils;

import cn.foxtech.common.entity.utils.ExtendUtils;
import cn.foxtech.common.entity.constant.DeviceDecoderVOFieldConstant;
import cn.foxtech.common.utils.ContainerUtils;
import cn.foxtech.common.utils.file.FileAttributesUtils;
import cn.foxtech.common.utils.file.FileNameUtils;
import cn.foxtech.common.utils.jar.info.JarInfoUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class ProtocolJarUtils {
    /**
     * 获得jar的配置信息
     *
     * @param dataList     已经存在的配置信息
     * @param defaultValue 不存在的时候，缺省值作为配置信息
     * @return jar文件的配置信息
     */
    public static List<Map<String, Object>> findJarConfig(List<Map<String, Object>> dataList, Boolean defaultValue) throws IOException {
        // 查找目录下所有的jar文件
        List<String> jarNameList = findJarFiles();

        // 将list转换成map方式
        Map<String, Map<String, Object>> dataMap = ContainerUtils.buildMapByKey(dataList, DeviceDecoderVOFieldConstant.field_file_name, String.class);

        // 转换成为实体格式的数据
        List<Map<String, Object>> result = makeEntityList(jarNameList, dataMap, defaultValue);

        // 筛选处理load的jar文件，并检查彼此的冲突关系
        List<String> loadList = filterByLoad(jarNameList, dataMap);
        Map<String, Set<String>> file2class = JarInfoUtils.checkConflictFile2Class("./jar/decoder/", loadList);

        // 获得文件的创建时间/更新时间
        Map<String, Long> createTime = new HashMap<>();
        Map<String, Long> updateTime = new HashMap<>();
        readFileTime(jarNameList, createTime, updateTime);

        // 将扩展信息扩展到实体列表上
        ExtendUtils.extend(result, file2class, DeviceDecoderVOFieldConstant.field_file_name, DeviceDecoderVOFieldConstant.field_conflict_file_name);
        ExtendUtils.extend(result, createTime, DeviceDecoderVOFieldConstant.field_file_name, DeviceDecoderVOFieldConstant.field_create_time);
        ExtendUtils.extend(result, updateTime, DeviceDecoderVOFieldConstant.field_file_name, DeviceDecoderVOFieldConstant.field_update_time);
        return result;
    }

    public static void readFileTime(List<String> jarNameList, Map<String, Long> createTime, Map<String, Long> updateTime) throws IOException {
        for (String fileName : jarNameList) {
            BasicFileAttributes attributes = FileAttributesUtils.getAttributes("./jar/decoder/" + fileName);
            createTime.put(fileName, attributes.creationTime().toMillis());
            updateTime.put(fileName, attributes.lastModifiedTime().toMillis());
        }
    }

    public static List<Map<String, Object>> makeEntityList(List<String> jarNameList, Map<String, Map<String, Object>> dataMap, Boolean defaultValue) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        for (String jarName : jarNameList) {
            // 缺省值
            Map<String, Object> fileConfig = new HashMap<>();
            fileConfig.put(DeviceDecoderVOFieldConstant.field_file_name, jarName);
            fileConfig.put(DeviceDecoderVOFieldConstant.field_load, defaultValue);

            // 继承已经配置过的数据
            Map<String, Object> map = dataMap.get(jarName);
            if (map != null) {
                Boolean load = (Boolean) map.get(DeviceDecoderVOFieldConstant.field_load);
                if (load == null) {
                    load = defaultValue;
                }

                fileConfig.put(DeviceDecoderVOFieldConstant.field_load, load);
            }

            resultList.add(fileConfig);
        }

        return resultList;
    }

    public static List<String> findJarFiles() {
        List<String> jarNameList = new ArrayList<>();
        List<String> fileNameList = new ArrayList<>();
        FileNameUtils.findFileList("./jar/decoder", false, false, fileNameList);
        for (String fileName : fileNameList) {
            if (fileName.toLowerCase().endsWith(".jar")) {
                jarNameList.add(fileName);
            }
        }

        return jarNameList;
    }

    public static List<String> filterByLoad(List<String> jarNameList, Map<String, Map<String, Object>> dataMap) {
        List<String> resultList = new ArrayList<>();

        for (String file : jarNameList) {
            Map<String, Object> map = dataMap.get(file);
            if (map == null) {
                continue;
            }

            Object load = map.get(DeviceDecoderVOFieldConstant.field_load);
            if (load == null) {
                continue;
            }

            if (Boolean.TRUE.equals(load)) {
                resultList.add(file);
            }
        }

        return resultList;
    }

    public static boolean deleteFile(String fileName) {
        File file = new File("./jar/decoder/" + fileName);
        return file.delete();
    }

    public static void WriteConfigFile(List<Map<String, Object>> dataList) throws IOException {
        // 查找目录下所有的jar文件
        List<String> jarNameList = findJarFiles();

        // 将list转换成map方式
        Map<String, Map<String, Object>> dataMap = ContainerUtils.buildMapByKey(dataList, DeviceDecoderVOFieldConstant.field_file_name, String.class);


        // 筛选处理load的jar文件
        List<String> loadList = filterByLoad(jarNameList, dataMap);

        // 打开文件
        File file = new File("");
        String fileName = file.getAbsolutePath() + "/conf/fox-edge-server-device.conf";

        // 逐行写入数据
        FileWriter fileWriter = new FileWriter(fileName);
        for (String loadFile : loadList) {
            fileWriter.write("jar/decoder/" + loadFile + "\n");
        }


        fileWriter.close();
    }
}
