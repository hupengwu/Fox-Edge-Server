package cn.foxtech.manager.system.service;

import cn.foxtech.common.entity.constant.DeviceDecoderVOFieldConstant;
import cn.foxtech.common.utils.Maps;
import cn.foxtech.common.utils.file.FileAttributesUtils;
import cn.foxtech.common.utils.file.FileNameUtils;
import cn.foxtech.common.utils.jar.info.JarInfoEntity;
import cn.foxtech.common.utils.jar.info.JarInfoReader;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.number.NumberUtils;
import cn.foxtech.device.protocol.RootLocation;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

@Component
public class JarFileInfoService {
    private Map<String, Object> buildDescription(List<Map<String, Object>> repoList) {
        Map<String, Object> result = new HashMap<>();
        for (Map<String, Object> map : repoList) {
            String modelName = (String) map.getOrDefault("modelName", "");
            String modelVersion = (String) map.getOrDefault("modelVersion", "");
            String description = (String) map.getOrDefault("description", "");
            result.put(modelName + ":" + modelVersion, description);
        }

        return result;
    }

    public List<Map<String, Object>> findJarInfo(Map<String, Object> configValue, List<Map<String, Object>> repoList) throws IOException {
        // 查找目录下所有的jar文件
        List<String> jarNameList = this.findJarFiles();

        // 取出需要装载的数据
        Set<String> loadJars = this.getLoads(configValue);

        // 分组：按解码器进行分组
        Map<String, Object> decoderMap = this.readJarFiles(jarNameList);

        // 云端仓库的描述信息
        Map<String, Object> descriptionMap = this.buildDescription(repoList);

        // 读取各文件的时间信息
        Map<String, Long> createTime = new HashMap<>();
        Map<String, Long> updateTime = new HashMap<>();
        Map<String, Long> size = new HashMap<>();
        readFileTime(jarNameList, createTime, updateTime, size);

        List<Map<String, Object>> resultList = new ArrayList<>();

        // 将三级目录：packName/verNum/version：HashMap
        // 以二级列表返回：packName/verNum：HashMap
        for (String packName : decoderMap.keySet()) {
            // 第1级：packName
            Map<String, Object> verNumMap = (Map<String, Object>) decoderMap.get(packName);

            for (String verNum : verNumMap.keySet()) {
                // 第2级：verNum
                Map<String, Object> versionMap = (Map<String, Object>) verNumMap.get(verNum);

                List<String> versionIds = new ArrayList<>();
                versionIds.addAll(versionMap.keySet());
                Collections.sort(versionIds);
                Collections.reverse(versionIds);

                // 待会用它从下面的列表中，找出默认的选择加载项目
                Map<String, Object> first = (Map<String, Object>) versionMap.get(versionIds.get(0));

                // 组成列表
                List<Map<String, Object>> versions = new ArrayList<>();
                for (String version : versionIds) {
                    // 第3级：version
                    Map<String, Object> map = (Map<String, Object>) versionMap.get(version);

                    String jarFileName = packName + "-" + version + ".jar";
                    map.put("fileName", jarFileName);
                    map.put("createTime", createTime.get(jarFileName));
                    map.put("updateTime", updateTime.get(jarFileName));
                    map.put("size", size.get(jarFileName));
                    map.put("load", loadJars.contains(jarFileName));

                    // 如果已经被用户配置为加载项目，那么优先选择它
                    if (loadJars.contains(jarFileName)) {
                        first = map;
                    }

                    versions.add(map);
                }


                // 以二级列表返回：packName/verNum：HashMap
                Map<String, Object> result = new HashMap<>();
                result.put("packName", packName);
                result.put("jarVer", verNum);
                result.put("versions", versions);
                result.put("first", first);
                result.put("description", descriptionMap.get(packName + ":" + verNum));

                resultList.add(result);
            }
        }

        return resultList;
    }

    private Set<String> getLoads(Map<String, Object> configValue) {
        Set<String> result = new HashSet<>();
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) configValue.getOrDefault(DeviceDecoderVOFieldConstant.field_list, new ArrayList<>());
        for (Map<String, Object> data : dataList) {
            String fileName = (String) data.get("fileName");
            if (fileName == null) {
                continue;
            }

            Boolean load = (Boolean) data.get("load");
            if (!Boolean.TRUE.equals(load)) {
                continue;
            }


            result.add(fileName);
        }

        return result;
    }

    private Map<String, Object> readJarFiles(List<String> jarNameList) {
        Map<String, Object> result = new HashMap<>();
        for (String jarFileName : jarNameList) {
            // 读取jar文件信息
            Map<String, String> jarNameInfo = this.split(jarFileName);
            if (jarNameInfo == null) {
                continue;
            }

            Map<String, Object> jarFileInfo = this.readJarFileInfo(jarFileName);
            if (jarFileInfo == null) {
                continue;
            }

            Maps.setValue(result, jarNameInfo.get("decoder"), jarFileInfo.get("jarVer"), jarNameInfo.get("version"), jarFileInfo);
        }


        return result;
    }

    private void readFileTime(List<String> jarNameList, Map<String, Long> createTime, Map<String, Long> updateTime, Map<String, Long> size) throws IOException {
        for (String fileName : jarNameList) {
            BasicFileAttributes attributes = FileAttributesUtils.getAttributes("./jar/decoder/" + fileName);
            createTime.put(fileName, attributes.creationTime().toMillis());
            updateTime.put(fileName, attributes.lastModifiedTime().toMillis());
            size.put(fileName, attributes.size());
        }
    }

    public String getPackName(String jarFileName) {
        Map<String, String> map = this.split(jarFileName);
        if (map == null) {
            return "";
        }

        return map.get("decoder");
    }

    /**
     * 将jar文件名称，拆分为包名称和版本号，两个部分
     * 例如：fox-edge-server-protocol-utils-1.0.3.jar，拆分为fox-edge-server-protocol-utils和1.0.3
     *
     * @param jarFileName jar文件名称，例如fox-edge-server-protocol-utils-1.0.3.jar
     * @return decoder，version的map
     */
    private Map<String, String> split(String jarFileName) {
        // 检查：是否为。jar文件
        if (!jarFileName.toLowerCase().endsWith(".jar")) {
            return null;
        }

        // 去除.jar的后缀，为拆分数据做准备
        jarFileName = jarFileName.substring(0, jarFileName.length() - ".jar".length());
        String[] items = jarFileName.split("-");
        if (items.length < 2) {
            return null;
        }

        String version = items[items.length - 1];
        String decoder = jarFileName.substring(0, jarFileName.length() - version.length() - 1);

        Map<String, String> result = new HashMap<>();
        result.put("decoder", decoder);
        result.put("version", version);

        return result;
    }

    private Map<String, Object> readJarFileInfo(String jarFileName) {
        try {
            File dir = new File("");

            // 从jar文件中，读取jar信息
            JarInfoEntity jarInfoEntity = JarInfoReader.readJarInfoEntity(dir.getAbsolutePath() + "\\jar\\decoder\\" + jarFileName);


            // 获得版本名称
            String jarVer = getJarVer(jarInfoEntity.getClassFileName());
            if (MethodUtils.hasEmpty(jarVer)) {
                jarVer = "v1";
            }
            // 剔除掉非规范化命名的解码器
            if (!jarVer.startsWith("v") || NumberUtils.parseLong(jarVer.substring(1)) == null) {
                return null;
            }


            // 获得名空间信息
            String jarSpace = getJarSpace(jarInfoEntity.getClassFileName());

            Map<String, Object> jarInfo = new HashMap<>();
            jarInfo.put("jarSpace", jarSpace);
            jarInfo.put("jarVer", jarVer);
            jarInfo.put("groupId", jarInfoEntity.getProperties().getGroupId());
            jarInfo.put("artifactId", jarInfoEntity.getProperties().getArtifactId());
            jarInfo.put("version", jarInfoEntity.getProperties().getVersion());
            jarInfo.put("dependencies", jarInfoEntity.getDependencies());
            jarInfo.put("classFileName", jarInfoEntity.getClassFileName());

            return jarInfo;

        } catch (Exception e) {
            return null;
        }
    }

    public boolean deleteFile(String fileName) {
        File file = new File("./jar/decoder/" + fileName);
        return file.delete();
    }

    private boolean checkValidity(List<String> classNames) {
        String pack = RootLocation.class.getPackage().getName();
        String rootLocationName = RootLocation.class.getName();

        for (String className : classNames) {
            if (!className.startsWith(pack + ".")) {
                return false;
            }

            // 检查：是否为RootLocation类
            if (className.equals(rootLocationName)) {
                continue;
            }


            String subName = className.substring(pack.length() + 1);
            String[] items = subName.split("\\.");
            if (items.length < 3) {
                return false;
            }

            // 是否以v打头
            if (!items[0].startsWith("v")) {
                return false;
            }
        }

        return true;
    }

    private String tryGetJarVer(List<String> classNames) {
        String pack = RootLocation.class.getPackage().getName();
        String rootLocationName = RootLocation.class.getName();

        for (String className : classNames) {
            // 检查：是否为RootLocation类
            if (className.equals(rootLocationName)) {
                continue;
            }


            String subName = className.substring(pack.length() + 1);
            String[] items = subName.split("\\.");
            return items[0];
        }

        return "";
    }

    private String getJarVer(List<String> classNames) {
        String pack = RootLocation.class.getPackage().getName();
        String rootLocationName = RootLocation.class.getName();

        String first = tryGetJarVer(classNames);
        if (first.isEmpty()) {
            return first;
        }

        for (String className : classNames) {
            // 检查：是否为RootLocation类
            if (className.equals(rootLocationName)) {
                continue;
            }


            String subName = className.substring(pack.length() + 1);
            String[] items = subName.split("\\.");
            if (!items[0].equals(first)) {
                return "";
            }
        }

        return first;
    }

    private String getJarSpace(List<String> classNames) {
        try {
            String pack = RootLocation.class.getPackage().getName();
            String rootLocationName = RootLocation.class.getName();

            String jarVer = tryGetJarVer(classNames);

            int minLength = Integer.MAX_VALUE;
            List<String[]> list = new ArrayList<>();
            for (String className : classNames) {
                // 检查：是否为RootLocation类
                if (className.equals(rootLocationName)) {
                    continue;
                }


                String subName = className.substring(pack.length() + jarVer.length() + 2);
                String[] items = subName.split("\\.");
                if (items.length < 2) {
                    continue;
                }

                if (minLength > items.length - 1) {
                    minLength = items.length - 1;
                }

                list.add(items);
            }

            if (list.isEmpty()) {
                return "";
            }

            String nameSpace = "";
            for (int i = 0; i < minLength; i++) {

                // 檢查：是否相同
                boolean same = true;
                String name = list.get(0)[i];
                for (String[] items : list) {
                    if (!name.equals(items[i])) {
                        same = false;
                        break;
                    }
                }

                if (same) {
                    nameSpace += "." + name;
                    continue;
                }

                break;
            }

            if (nameSpace.isEmpty()) {
                return "";
            }

            nameSpace = nameSpace.substring(1);
            return nameSpace;
        } catch (Exception e) {
            return "";
        }
    }

    private List<String> findJarFiles() {
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

}
