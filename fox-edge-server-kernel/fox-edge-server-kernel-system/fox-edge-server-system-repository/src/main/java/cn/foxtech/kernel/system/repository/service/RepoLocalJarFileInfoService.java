package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.entity.constant.DeviceDecoderVOFieldConstant;
import cn.foxtech.common.entity.constant.RepoCompVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.utils.MapUtils;
import cn.foxtech.common.utils.bean.BeanMapUtils;
import cn.foxtech.common.utils.file.FileAttributesUtils;
import cn.foxtech.common.utils.file.FileNameUtils;
import cn.foxtech.common.utils.jar.info.JarInfoEntity;
import cn.foxtech.common.utils.jar.info.JarInfoReader;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.device.protocol.RootLocation;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * 本地JAR文件的JAR包文件信息
 */
@Component
public class RepoLocalJarFileInfoService {
    @Autowired
    private RepoLocalJarFileNameService fileNameService;

    @Autowired
    private RepoLocalJarFileConfigService configService;

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

    public List<Map<String, Object>> findJarInfo(Set<String> loadJars, List<Map<String, Object>> repoList) throws IOException {
        // 查找目录下所有的jar文件
        List<String> jarNameList = this.findLocalJarFiles();


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

                // 以二级列表返回：packName/verNum：HashMap
                Map<String, Object> result = new HashMap<>();
                result.putAll(versionMap);
                result.put("packName", packName);
                result.put("jarVer", verNum);
                result.put("description", descriptionMap.get(packName + ":" + verNum));

                String jarFileName = packName + "." + verNum + ".jar";
                result.put("fileName", jarFileName);
                result.put("createTime", createTime.get(jarFileName));
                result.put("updateTime", updateTime.get(jarFileName));
                result.put("size", size.get(jarFileName));
                result.put(DeviceDecoderVOFieldConstant.field_load, loadJars.contains(jarFileName));

                resultList.add(result);
            }
        }

        return resultList;
    }

    private Map<String, Object> readJarFiles(List<String> jarNameList) {
        Map<String, Object> result = new HashMap<>();
        for (String jarFileName : jarNameList) {
            // 读取jar文件信息
            String modelName = this.fileNameService.getModelName(jarFileName);
            if (MethodUtils.hasEmpty(modelName)) {
                continue;
            }

            Map<String, Object> jarFileInfo = this.readJarFileInfo(jarFileName);
            if (jarFileInfo == null) {
                continue;
            }

            MapUtils.setValue(result, modelName, jarFileInfo.get("jarVer"), jarFileInfo);
        }


        return result;
    }

    public Map<String, Object> readJarFiles(String jarFileName) {
        // 读取jar文件信息
        String modelName = this.fileNameService.getModelName(jarFileName);
        if (MethodUtils.hasEmpty(modelName)) {
            return null;
        }

        Map<String, Object> jarFileInfo = this.readJarFileInfo(jarFileName);
        if (jarFileInfo == null) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        result.putAll(jarFileInfo);
        result.put(RepoCompConstant.filed_model_name, modelName);

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

    public Map<String, Object> readJarFileInfo(String jarFileName) {
        try {
            File dir = new File("");

            File jarFile = new File(dir.getAbsolutePath() + "/jar/decoder/" + jarFileName);

            // 文件大小
            long fileSize = FileAttributesUtils.getAttributes(jarFile).size();

            // 从jar文件中，读取jar信息
            JarInfoEntity jarInfoEntity = JarInfoReader.readJarInfoEntity(jarFile.getAbsolutePath());

            // 获得名空间信息
            String jarSpace = getJarSpace(jarInfoEntity.getClassFileName());

            Map<String, Object> jarInfo = new HashMap<>();
            jarInfo.put("jarSpace", jarSpace);
            jarInfo.put("groupId", jarInfoEntity.getProperties().getGroupId());
            jarInfo.put("artifactId", jarInfoEntity.getProperties().getArtifactId());
            jarInfo.put("version", jarInfoEntity.getProperties().getVersion());
            jarInfo.put("dependencies", jarInfoEntity.getDependencies());
            jarInfo.put("classFileName", jarInfoEntity.getClassFileName());
            jarInfo.put("fileName", jarFileName);
            jarInfo.put("size", fileSize);


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

    private List<String> findLocalJarFiles() {
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

    public List<Map<String, Object>> findJarNameList() {
        List<Map<String, Object>> mapList = new ArrayList<>();
        List<String> jarFileNameList = this.findLocalJarFiles();
        for (String jarFileName : jarFileNameList) {
            String modelName = this.fileNameService.getModelName(jarFileName);
            if (MethodUtils.hasEmpty(modelName)) {
                continue;
            }

            Map<String, Object> map = new HashMap<>();
            map.put(RepoCompConstant.filed_model_name, modelName);
            map.put(RepoCompConstant.filed_file_name, jarFileName);
            mapList.add(map);
        }

        return mapList;
    }

    public List<Map<String, Object>> extendCompJarInfo(List<BaseEntity> compEntityList) {
        // 取出需要装载的数据
        Set<String> loadJars = this.configService.getLoads();

        List<Map<String, Object>> mapList = new ArrayList<>();
        for (BaseEntity entity : compEntityList) {
            RepoCompEntity compEntity = (RepoCompEntity) entity;

            String fileName = (String) compEntity.getCompParam().get(RepoCompConstant.filed_file_name);
            if (fileName == null) {
                continue;
            }

            // 读取JAR文件中的POM信息
            Map<String, Object> jarInfo = this.readJarFileInfo(fileName);
            if (jarInfo == null) {
                continue;
            }

            Map<String, Object> map = BeanMapUtils.objectToMap(compEntity);

            Map<String, Object> compParam = (Map<String, Object>) map.get(RepoCompVOFieldConstant.field_comp_param);
            compParam.put(RepoCompConstant.filed_version, jarInfo.get(RepoCompConstant.filed_version));
            compParam.put("size", jarInfo.get("size"));
            compParam.put(DeviceDecoderVOFieldConstant.field_load, loadJars.contains(fileName));

            mapList.add(map);
        }

        return mapList;

    }
}
