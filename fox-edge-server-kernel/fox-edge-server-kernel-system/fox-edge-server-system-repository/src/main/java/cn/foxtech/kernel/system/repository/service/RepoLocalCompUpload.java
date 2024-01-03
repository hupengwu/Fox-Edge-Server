package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.entity.constant.OperateVOFieldConstant;
import cn.foxtech.common.entity.constant.RepoCompVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
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
            return this.uploadJspDecoderEntity(entity.getCompParam(), commitKey, description);
        }


        throw new ServiceException("该组件类型，不支持本地上传");
    }

    private Map<String, Object> uploadJarDecoderEntity(Map<String, Object> compParam, String commitKey) throws IOException {
        String modelName = (String) compParam.get(RepoCompConstant.filed_model_name);
        String modelVersion = (String) compParam.get(RepoCompConstant.filed_model_version);
        String deviceType = (String) compParam.get(OperateVOFieldConstant.field_device_type);
        String manufacturer = (String) compParam.get(OperateVOFieldConstant.field_manufacturer);
        String fileName = (String) compParam.get("fileName");
        if (MethodUtils.hasEmpty(modelName, modelVersion, deviceType, manufacturer, fileName, commitKey)) {
            throw new ServiceException("缺少参数： modelName, modelVersion, deviceType, manufacturer, fileName, commitKey");
        }

        String filePath = this.pathNameService.getPathName4LocalJarDecoder2file(modelName, modelVersion);
        File file = new File(filePath);
        if (!file.exists() || !file.isFile()) {
            throw new ServiceException("文件不存在！");
        }


        Map<String, Object> formData = new HashMap<>();
        formData.put(RepoCompConstant.filed_model_type, RepoCompConstant.repository_type_decoder);
        formData.put(RepoCompConstant.filed_model_name, modelName);
        formData.put(RepoCompConstant.filed_model_version, modelVersion);
        formData.put(RepoCompConstant.filed_component, "service");
        formData.put("file", file);
        formData.put(RepoCompConstant.filed_commit_key, commitKey);

        return this.remoteService.executeUpload("/manager/repository/component/upload", formData);

    }

    private Map<String, Object> uploadJspDecoderEntity(Map<String, Object> compParam, String commitKey, String description) throws IOException {
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

        return this.remoteService.executePost("/manager/repository/component/script/version/entity", body);

    }

    private Map<String, Object> uploadCsvTemplateEntity(Map<String, Object> compParam, String commitKey) throws IOException, InterruptedException {
        String modelName = (String) compParam.get(RepoCompConstant.filed_model_name);
        String modelVersion = (String) compParam.get(RepoCompConstant.filed_model_version);
        String deviceType = (String) compParam.get(OperateVOFieldConstant.field_device_type);
        String manufacturer = (String) compParam.get(OperateVOFieldConstant.field_manufacturer);
        if (MethodUtils.hasEmpty(modelName, modelVersion, deviceType, manufacturer, commitKey)) {
            throw new ServiceException("缺少参数： modelName, modelVersion, deviceType, manufacturer, commitKey");
        }

        File file = null;
        try {
            // 打包成tar文件
            String tarFileName = modelName + ".tar";
            this.shellService.packCsvTemplate2TarFile(tarFileName, modelName, modelVersion);


            // 打开tar文件
            String pathName = this.pathNameService.getPathName4LocalTemplate2version(modelName, modelVersion);
            file = new File(pathName + "\\" + tarFileName);
            if (!file.exists() || !file.isFile()) {
                throw new ServiceException("文件不存在！");
            }

            Map<String, Object> formData = new HashMap<>();
            formData.put(RepoCompConstant.filed_model_type, RepoCompConstant.repository_type_template);
            formData.put(RepoCompConstant.filed_model_name, modelName);
            formData.put(RepoCompConstant.filed_model_version, modelVersion);
            formData.put(RepoCompConstant.filed_component, "service");
            formData.put("file", file);
            formData.put(RepoCompConstant.filed_commit_key, commitKey);


            return this.remoteService.executeUpload("/manager/repository/component/upload", formData);
        } finally {
            if (file != null && file.exists()) {
                file.delete();
            }

        }
    }
}
