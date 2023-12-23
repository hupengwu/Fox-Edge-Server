package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.entity.constant.BaseVOFieldConstant;
import cn.foxtech.common.entity.constant.OperateVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.utils.bean.BeanMapUtils;
import cn.foxtech.common.utils.file.FileAttributesUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CSV文件模板
 */
@Component
public class RepoLocalCsvFileService {
    private static final Logger logger = Logger.getLogger(RepoLocalCsvFileService.class);

    @Autowired
    private EntityManageService entityManageService;

    public List<Map<String, Object>> extendCompFileCount(List<BaseEntity> compEntityList) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (BaseEntity entity : compEntityList) {
            RepoCompEntity compEntity = (RepoCompEntity) entity;

            int count;
            try {
                List<Map<String, Object>> list = this.queryFileList(compEntity);
                count = list.size();
            } catch (Exception e) {
                count = 0;
            }

            Map<String, Object> map = BeanMapUtils.objectToMap(compEntity);
            map.put("fileCount", count);

            mapList.add(map);
        }

        return mapList;

    }

    public List<Map<String, Object>> queryFileList(RepoCompEntity compEntity) {
        String modelName = (String) compEntity.getCompParam().get(RepoCompConstant.filed_model_name);
        if (MethodUtils.hasEmpty(modelName)) {
            throw new ServiceException("找不到对应的模块名称:" + modelName);
        }

        String modelVersion = (String) compEntity.getCompParam().get(RepoCompConstant.filed_model_version);
        if (MethodUtils.hasEmpty(modelVersion)) {
            throw new ServiceException("找不到对应的模块版本:" + modelVersion);
        }

        String version = (String) compEntity.getCompParam().get(RepoCompConstant.filed_version);
        if (MethodUtils.hasEmpty(version)) {
            throw new ServiceException("找不到对应的版本:" + version);
        }

        String manufacturer = (String) compEntity.getCompParam().get(OperateVOFieldConstant.field_manufacturer);
        String deviceType = (String) compEntity.getCompParam().get(OperateVOFieldConstant.field_device_type);


        List<Map<String, Object>> resultList = this.queryTemplateFileList(modelName, modelVersion);
        for (Map<String, Object> map : resultList) {
            if (modelName != null) {
                map.put(RepoCompConstant.filed_model_name, modelName);
            }

            if (modelVersion != null) {
                map.put(RepoCompConstant.filed_model_version, modelVersion);
            }

            if (version != null) {
                map.put(RepoCompConstant.filed_version, version);
            }

            if (manufacturer != null) {
                map.put(OperateVOFieldConstant.field_manufacturer, manufacturer);
            }

            if (deviceType != null) {
                map.put(OperateVOFieldConstant.field_device_type, deviceType);
            }
        }

        return resultList;
    }

    public String queryTemplateFilePath(Long compId) {
        // 简单验证
        if (MethodUtils.hasEmpty(compId)) {
            throw new ServiceException("参数不能为空: compId");
        }

        RepoCompEntity compEntity = this.entityManageService.getEntity(compId, RepoCompEntity.class);
        if (compEntity == null) {
            throw new ServiceException("找不到对应的组件:" + compId);
        }

        String modelName = (String) compEntity.getCompParam().get(RepoCompConstant.filed_model_name);
        String modelVersion = (String) compEntity.getCompParam().get(RepoCompConstant.filed_model_version);
        String version = (String) compEntity.getCompParam().get(RepoCompConstant.filed_version);
        if (MethodUtils.hasEmpty(modelName, modelVersion, version)) {
            throw new ServiceException("找不到对应的模块信息: modelName, modelVersion, version");
        }


        return "template/" + modelName + "/" + modelVersion + "/" + version;
    }

    public void deleteFileTemplate(Long compId, String fileName) {
        // 获得路径名称
        String path = this.queryTemplateFilePath(compId);

        File dir = new File("");

        File delete = new File(dir.getAbsolutePath() + "/template/" + path + "/" + fileName);
        delete.delete();
    }

    private List<Map<String, Object>> queryTemplateFileList(String modelName, String modelVersion) {
        List<Map<String, Object>> result = new ArrayList<>();

        File file = new File("");

        File templateDir = new File(file.getAbsolutePath() + "/template/" + modelName + "/" + modelVersion);
        if (!templateDir.exists() || !templateDir.isDirectory()) {
            return result;
        }

        for (File childFile : templateDir.listFiles()) {
            Map<String, Object> data = this.getFileNameInfo(childFile);
            result.add(data);
        }

        return result;
    }

    /**
     * 查询模板列表
     *
     * @return
     */
    public List<Map<String, Object>> queryTemplateList() {
        List<Map<String, Object>> result = new ArrayList<>();

        File file = new File("");

        File templateDir = new File(file.getAbsolutePath() + "/template/");
        if (!templateDir.exists() || !templateDir.isDirectory()) {
            return result;
        }

        for (String modelName : templateDir.list()) {
            File modelDir = new File(templateDir, modelName);
            if (!modelDir.isDirectory()) {
                continue;
            }

            for (String modelVer : modelDir.list()) {
                File modelDirDir = new File(modelDir, modelVer);
                if (!modelDirDir.isDirectory()) {
                    continue;
                }

                for (String version : modelDirDir.list()) {
                    File versionDir = new File(modelDirDir, version);
                    if (!versionDir.isDirectory()) {
                        continue;
                    }

                    for (File childFile : versionDir.listFiles()) {
                        Map<String, Object> data = this.getFileNameInfo(modelName, modelVer, version, childFile);
                        result.add(data);
                    }
                }
            }
        }

        return result;
    }

    private Map<String, Object> getFileNameInfo(String modelName, String modelVer, String version, File childFile) {
        Map<String, Object> data = new HashMap<>();

        data.put(RepoCompConstant.filed_model_name, modelName);
        data.put(RepoCompConstant.filed_model_version, modelVer);
        data.put(RepoCompConstant.filed_version, version);
        data.put(RepoCompConstant.filed_component, RepoCompConstant.repository_type_template);
        data.put("fileName", childFile.getName());

        // 乱码文件的异常处理
        try {
            BasicFileAttributes attributes = FileAttributesUtils.getAttributes(childFile);
            data.put(BaseVOFieldConstant.field_create_time, attributes.creationTime().toMillis());
            data.put(BaseVOFieldConstant.field_update_time, attributes.lastModifiedTime().toMillis());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return data;
    }

    private Map<String, Object> getFileNameInfo(File childFile) {
        Map<String, Object> data = new HashMap<>();

        data.put(RepoCompConstant.filed_component, RepoCompConstant.repository_type_template);
        data.put("fileName", childFile.getName());

        // 乱码文件的异常处理
        try {
            BasicFileAttributes attributes = FileAttributesUtils.getAttributes(childFile);
            data.put(BaseVOFieldConstant.field_create_time, attributes.creationTime().toMillis());
            data.put(BaseVOFieldConstant.field_update_time, attributes.lastModifiedTime().toMillis());
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return data;
    }
}
