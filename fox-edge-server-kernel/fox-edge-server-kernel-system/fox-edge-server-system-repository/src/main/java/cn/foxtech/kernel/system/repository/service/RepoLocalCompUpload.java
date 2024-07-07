package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.entity.constant.OperateVOFieldConstant;
import cn.foxtech.common.entity.constant.RepoCompVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceModelEntity;
import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将本地仓库的组件，上传到fox-cloud云端仓库
 */
@Component
public class RepoLocalCompUpload {
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private RepoLocalPathNameService pathNameService;

    @Autowired
    private CloudRemoteService remoteService;

    @Autowired
    private RepoLocalShellService shellService;

    public Map<String, Object> uploadEntity(Long compId, String commitKey, String description) throws IOException, InterruptedException {
        RepoCompEntity entity = this.entityManageService.getEntity(compId, RepoCompEntity.class);
        if (entity == null) {
            throw new ServiceException("实体不存在");
        }

        if (entity.getCompRepo().equals(RepoCompVOFieldConstant.value_comp_repo_local) && entity.getCompType().equals(RepoCompVOFieldConstant.value_comp_type_file_template)) {
            return this.uploadCsvTemplateEntity(entity.getCompParam(), commitKey);
        }
        if (entity.getCompRepo().equals(RepoCompVOFieldConstant.value_comp_repo_local) && entity.getCompType().equals(RepoCompVOFieldConstant.value_comp_type_jar_decoder)) {
            return this.uploadJarDecoderEntity(entity.getCompParam(), commitKey);
        }
        if (entity.getCompRepo().equals(RepoCompVOFieldConstant.value_comp_repo_local) && entity.getCompType().equals(RepoCompVOFieldConstant.value_comp_type_jsp_decoder)) {
            return this.uploadJspDecoderEntity(entity, commitKey, description);
        }
        if (entity.getCompRepo().equals(RepoCompVOFieldConstant.value_comp_repo_local) && entity.getCompType().equals(RepoCompVOFieldConstant.value_comp_type_jsn_decoder)) {
            return this.uploadJsnDecoderEntity(entity, commitKey, description);
        }


        throw new ServiceException("该组件类型，不支持本地上传");
    }

    private Map<String, Object> uploadJarDecoderEntity(Map<String, Object> compParam, String commitKey) throws IOException {
        String modelName = (String) compParam.get(RepoCompConstant.filed_model_name);
        String deviceType = (String) compParam.get(OperateVOFieldConstant.field_device_type);
        String manufacturer = (String) compParam.get(OperateVOFieldConstant.field_manufacturer);
        String fileName = (String) compParam.get("fileName");
        if (MethodUtils.hasEmpty(modelName, deviceType, manufacturer, fileName, commitKey)) {
            throw new ServiceException("缺少参数： modelName, deviceType, manufacturer, fileName, commitKey");
        }

        String filePath = this.pathNameService.getPathName4LocalJarDecoder2file(modelName);
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new ServiceException("文件不存在！");
        }


        Map<String, Object> formData = new HashMap<>();
        formData.put(RepoCompConstant.filed_model_type, RepoCompConstant.repository_type_decoder);
        formData.put(RepoCompConstant.filed_model_name, modelName);
        formData.put(RepoCompConstant.filed_model_version, RepoCompConstant.filed_value_model_version_default);
        formData.put(RepoCompConstant.filed_component, "service");
        formData.put(RepoCompConstant.filed_work_mode, "");
        formData.put("file", file);
        formData.put(RepoCompConstant.filed_commit_key, commitKey);

        return this.remoteService.executeUpload("/manager/repository/component/upload", formData);

    }

    private Map<String, Object> uploadJspDecoderEntity(RepoCompEntity repoCompEntity, String commitKey, String description) throws IOException {
        Map<String, Object> compParam = repoCompEntity.getCompParam();

        String compId = (String) compParam.get(RepoCompVOFieldConstant.field_comp_id);
        String deviceType = (String) compParam.get(OperateVOFieldConstant.field_device_type);
        String manufacturer = (String) compParam.get(OperateVOFieldConstant.field_manufacturer);
        if (MethodUtils.hasEmpty(compId, deviceType, manufacturer)) {
            throw new ServiceException("缺少参数： compId, deviceType, manufacturer");
        }

        List<BaseEntity> entityList = this.entityManageService.getEntityList(OperateEntity.class, (Object value) -> {
            OperateEntity entity = (OperateEntity) value;

            if (!entity.getEngineType().equals(OperateVOFieldConstant.value_engine_javascript)) {
                return false;
            }

            if (!entity.getManufacturer().equals(manufacturer)) {
                return false;
            }

            return entity.getDeviceType().equals(deviceType);
        });


        Map<String, Object> body = JsonUtils.clone(compParam);
        body.put(RepoCompVOFieldConstant.field_comp_id, compId);
        body.put(RepoCompVOFieldConstant.field_commit_key, commitKey);
        body.put(RepoCompVOFieldConstant.field_description, description);
        body.put("operates", entityList);

        Map<String, Object> respond = this.remoteService.executePost("/manager/repository/component/script/version/entity", body);

        // 更新版本信息
        this.updateVersion(repoCompEntity, respond);

        return respond;
    }


    private Map<String, Object> uploadJsnDecoderEntity(RepoCompEntity repoCompEntity, String commitKey, String description) throws IOException {
        Map<String, Object> compParam = repoCompEntity.getCompParam();

        String compId = (String) compParam.get(RepoCompVOFieldConstant.field_comp_id);
        String deviceType = (String) compParam.get(OperateVOFieldConstant.field_device_type);
        String manufacturer = (String) compParam.get(OperateVOFieldConstant.field_manufacturer);
        if (MethodUtils.hasEmpty(compId, deviceType, manufacturer)) {
            throw new ServiceException("缺少参数： compId, deviceType, manufacturer");
        }

        List<BaseEntity> entityList = this.entityManageService.getEntityList(DeviceModelEntity.class, (Object value) -> {
            DeviceModelEntity entity = (DeviceModelEntity) value;

            if (!entity.getManufacturer().equals(manufacturer)) {
                return false;
            }

            return entity.getDeviceType().equals(deviceType);
        });


        Map<String, Object> body = JsonUtils.clone(compParam);
        body.put(RepoCompVOFieldConstant.field_comp_id, compId);
        body.put(RepoCompVOFieldConstant.field_commit_key, commitKey);
        body.put(RepoCompVOFieldConstant.field_description, description);
        body.put("objects", entityList);

        Map<String, Object> respond = this.remoteService.executePost("/manager/repository/component/model/version/entity", body);

        // 更新版本信息
        this.updateVersion(repoCompEntity, respond);

        return respond;
    }

    private Map<String, Object> uploadCsvTemplateEntity(Map<String, Object> compParam, String commitKey) throws IOException, InterruptedException {
        String modelName = (String) compParam.get(RepoCompConstant.filed_model_name);
        String deviceType = (String) compParam.get(OperateVOFieldConstant.field_device_type);
        String manufacturer = (String) compParam.get(OperateVOFieldConstant.field_manufacturer);
        if (MethodUtils.hasEmpty(modelName, deviceType, manufacturer, commitKey)) {
            throw new ServiceException("缺少参数： modelName, deviceType, manufacturer, commitKey");
        }

        File file = null;
        try {
            // 打包成tar文件
            String tarFileName = modelName + ".tar";
            this.shellService.packCsvTemplate2TarFile(tarFileName, modelName);


            // 打开tar文件
            String pathName = this.pathNameService.getPathName4LocalTemplate2version(modelName);
            file = new File(pathName + "\\" + tarFileName);
            if (!file.exists() || !file.isFile()) {
                throw new ServiceException("文件不存在！");
            }

            Map<String, Object> formData = new HashMap<>();
            formData.put(RepoCompConstant.filed_model_type, RepoCompConstant.repository_type_template);
            formData.put(RepoCompConstant.filed_model_name, modelName);
            formData.put(RepoCompConstant.filed_model_version, RepoCompConstant.filed_value_model_version_default);
            formData.put(RepoCompConstant.filed_component, "service");
            formData.put(RepoCompConstant.filed_work_mode, "");
            formData.put("file", file);
            formData.put(RepoCompConstant.filed_commit_key, commitKey);


            return this.remoteService.executeUpload("/manager/repository/component/upload", formData);
        } finally {
            if (file != null && file.exists()) {
                file.delete();
            }

        }
    }

    private void updateVersion(RepoCompEntity repoCompEntity, Map<String, Object> respond) {
        // 读取对象信息
        RepoCompEntity entity = this.entityManageService.getEntity(repoCompEntity.getId(), RepoCompEntity.class);
        if (entity == null) {
            return;
        }

        Map<String, Object> data = (Map<String, Object>) respond.get(AjaxResult.DATA_TAG);

        Map<String, Object> installVersion = new HashMap<>();
        installVersion.put("id", data.get("id"));
        installVersion.put("updateTime", data.get("updateTime"));
        installVersion.put("description", data.get("description"));

        Map<String, Object> compParam = entity.getCompParam();
        compParam.put("installVersion", installVersion);

        // 保存修改
        this.entityManageService.updateEntity(entity);
    }
}
