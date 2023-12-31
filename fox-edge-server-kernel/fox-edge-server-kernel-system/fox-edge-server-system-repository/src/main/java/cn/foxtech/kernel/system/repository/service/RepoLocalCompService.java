package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.constant.HttpStatus;
import cn.foxtech.common.domain.constant.ServiceVOFieldConstant;
import cn.foxtech.common.entity.constant.OperateVOFieldConstant;
import cn.foxtech.common.entity.constant.RepoCompVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.entity.entity.OperateMethodEntity;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.utils.ContainerUtils;
import cn.foxtech.common.utils.DifferUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.device.domain.constant.DeviceMethodVOFieldConstant;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * JAVA版本的JAR解码器服务
 */
@Component
public class RepoLocalCompService {
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private RepoLocalOperateService operateService;


    @Autowired
    private RepoLocalPathNameService pathNameService;

    @Autowired
    private CloudRemoteService remoteService;

    @Autowired
    private RepoLocalShellService shellService;

    @Autowired
    private RepoLocalJarFileNameService jarFileNameService;


    public List<BaseEntity> getCompEntityList(Map<String, Object> body) {
        String compRepo = (String) body.get(RepoCompVOFieldConstant.field_comp_repo);
        String compType = (String) body.get(RepoCompVOFieldConstant.field_comp_type);
        String keyWord = (String) body.get(RepoCompVOFieldConstant.field_key_word);

        // 简单验证
        if (MethodUtils.hasEmpty(compRepo, compType)) {
            throw new ServiceException("参数不能为空: compRepo, compType");
        }

        return this.entityManageService.getEntityList(RepoCompEntity.class, (Object value) -> {
            RepoCompEntity compEntity = (RepoCompEntity) value;

            if (!compRepo.equals(compEntity.getCompRepo())) {
                return false;
            }

            if (!compType.equals(compEntity.getCompType())) {
                return false;
            }


            if (RepoCompVOFieldConstant.value_comp_type_jar_decoder.equals(compType) // jar-decoder
                    || RepoCompVOFieldConstant.value_comp_type_jsp_decoder.equals(compType) // jsp-decoder
                    || RepoCompVOFieldConstant.value_comp_type_file_template.equals(compType)// file-template
            ) {
                String manufacturer = (String) compEntity.getCompParam().getOrDefault(OperateVOFieldConstant.field_manufacturer, "");
                String deviceType = (String) compEntity.getCompParam().getOrDefault(OperateVOFieldConstant.field_device_type, "");

                if (MethodUtils.hasEmpty(keyWord)) {
                    return true;
                }

                if (manufacturer.toLowerCase().contains(keyWord.toLowerCase())) {
                    return true;
                }
                if (deviceType.toLowerCase().contains(keyWord.toLowerCase())) {
                    return true;
                }
                return compEntity.getCompName().toLowerCase().contains(keyWord.toLowerCase());
            }

            if (RepoCompVOFieldConstant.value_comp_type_app_service.equals(compType)) {
                String appName = (String) compEntity.getCompParam().getOrDefault(ServiceVOFieldConstant.field_app_name, "");
                String appType = (String) compEntity.getCompParam().getOrDefault(ServiceVOFieldConstant.field_app_type, "");

                if (MethodUtils.hasEmpty(keyWord)) {
                    return true;
                }

                if (appName.toLowerCase().contains(keyWord.toLowerCase())) {
                    return true;
                }
                return appType.toLowerCase().contains(keyWord.toLowerCase());
            }

            return true;
        });
    }

    public void deleteCompEntity(Long compId) {
        // 简单验证
        if (MethodUtils.hasEmpty(compId)) {
            throw new ServiceException("参数不能为空: compId");
        }

        // 获得实体
        RepoCompEntity compEntity = this.entityManageService.getEntity(compId, RepoCompEntity.class);
        if (compEntity == null) {
            throw new ServiceException("找不到对应的组件:" + compId);
        }

        // 检查：实体中的依赖关系，避免数据之间依赖关系失效
        if (RepoCompVOFieldConstant.value_comp_type_jsp_decoder.equals(compEntity.getCompType())) {
            List<BaseEntity> operateList = this.operateService.getOperateEntityList(compEntity);
            if (!operateList.isEmpty()) {
                throw new ServiceException("该组件下面，已经定义了操作方法，请先删除这些操作方法后，再删除组件!");
            }
        }

        // 删除数据
        this.entityManageService.deleteEntity(compId, RepoCompEntity.class);
    }

    public RepoCompEntity getRepoCompEntity(Integer compId) {
        return this.entityManageService.getEntity(Long.parseLong(compId.toString()), RepoCompEntity.class);
    }


    public Map<String, Object> convertCloud2Local(Map<String, Object> cloud) {
        String modelType = (String) cloud.get(RepoCompConstant.filed_model_type);
        String modelName = (String) cloud.get(RepoCompConstant.filed_model_name);
        String modelVersion = (String) cloud.get(RepoCompConstant.filed_model_version);
        String manufacturer = (String) cloud.get(OperateVOFieldConstant.field_manufacturer);
        String deviceType = (String) cloud.get(OperateVOFieldConstant.field_device_type);
        String compId = (String) cloud.get(RepoCompVOFieldConstant.field_id);


        // 必选参数
        if (MethodUtils.hasEmpty(modelType, modelName, modelVersion)) {
            throw new ServiceException("cloud参数不能为空: modelType, modelName, modelVersion");
        }

        Map<String, Object> localMap = new HashMap<>();


        if (modelType.equals(RepoCompConstant.repository_type_decoder)) {
            localMap.put(RepoCompVOFieldConstant.field_comp_repo, RepoCompVOFieldConstant.value_comp_repo_local);
            localMap.put(RepoCompVOFieldConstant.field_comp_type, RepoCompVOFieldConstant.value_comp_type_jar_decoder);
            localMap.put(RepoCompVOFieldConstant.field_comp_param, new HashMap<>());

            // 必选参数
            if (MethodUtils.hasEmpty(modelName, modelVersion, manufacturer, deviceType)) {
                throw new ServiceException("参数不能为空: modelName, modelVersion, manufacturer, deviceType");
            }

            String fileName = modelName + "." + modelVersion + ".jar";

            // 可选参数
            Map<String, Object> compParam = (Map<String, Object>) localMap.get(RepoCompVOFieldConstant.field_comp_param);
            compParam.put(OperateVOFieldConstant.field_manufacturer, manufacturer);
            compParam.put(OperateVOFieldConstant.field_device_type, deviceType);
            compParam.put(RepoCompVOFieldConstant.field_comp_id, compId);
            compParam.put(RepoCompConstant.filed_model_name, modelName);
            compParam.put(RepoCompConstant.filed_model_version, modelVersion);
            compParam.put(RepoCompConstant.filed_file_name, fileName);

            return localMap;
        }
        if (modelType.equals(RepoCompConstant.repository_type_template)) {
            localMap.put(RepoCompVOFieldConstant.field_comp_repo, RepoCompVOFieldConstant.value_comp_repo_local);
            localMap.put(RepoCompVOFieldConstant.field_comp_type, RepoCompVOFieldConstant.value_comp_type_file_template);
            localMap.put(RepoCompVOFieldConstant.field_comp_param, new HashMap<>());

            // 必选参数
            if (MethodUtils.hasEmpty(modelName, modelVersion, manufacturer, deviceType)) {
                throw new ServiceException("参数不能为空: modelName, modelVersion, manufacturer, deviceType");
            }

            // 可选参数
            Map<String, Object> compParam = (Map<String, Object>) localMap.get(RepoCompVOFieldConstant.field_comp_param);
            compParam.put(OperateVOFieldConstant.field_manufacturer, manufacturer);
            compParam.put(OperateVOFieldConstant.field_device_type, deviceType);
            compParam.put(RepoCompVOFieldConstant.field_comp_id, compId);
            compParam.put(RepoCompConstant.filed_model_name, modelName);
            compParam.put(RepoCompConstant.filed_model_version, modelVersion);

            return localMap;
        }

        return null;
    }

    public Map<String, Object> convertConf2Local(Map<String, Object> confMap) {
        Map<String, Object> localMap = new HashMap<>();
        localMap.put(RepoCompVOFieldConstant.field_comp_repo, RepoCompVOFieldConstant.value_comp_repo_local);
        localMap.put(RepoCompVOFieldConstant.field_comp_type, RepoCompVOFieldConstant.value_comp_type_app_service);
        localMap.put(RepoCompVOFieldConstant.field_comp_param, new HashMap<>());

        String appName = (String) confMap.get(ServiceVOFieldConstant.field_app_name);
        String appType = (String) confMap.get(ServiceVOFieldConstant.field_app_type);
        if (MethodUtils.hasEmpty(appType, appName)) {
            throw new ServiceException("参数不能为空: appName,appType");
        }

        Map<String, Object> compParam = (Map<String, Object>) localMap.get(RepoCompVOFieldConstant.field_comp_param);
        compParam.put(ServiceVOFieldConstant.field_app_name, confMap.get(ServiceVOFieldConstant.field_app_name));
        compParam.put(ServiceVOFieldConstant.field_app_type, confMap.get(ServiceVOFieldConstant.field_app_type));
        compParam.put(ServiceVOFieldConstant.field_file_name, confMap.get(ServiceVOFieldConstant.field_file_name));
        compParam.put(ServiceVOFieldConstant.field_loader_name, confMap.get(ServiceVOFieldConstant.field_loader_name));
        compParam.put(ServiceVOFieldConstant.field_spring_param, confMap.get(ServiceVOFieldConstant.field_spring_param));
        compParam.put(ServiceVOFieldConstant.field_conf_files, confMap.get(ServiceVOFieldConstant.field_conf_files));

        return localMap;
    }


    /**
     * 将JAR解码器的文件名，转换为一个缺省的本地组件结构
     * 注意：该信息是缺省的，并不完整，它包含一个Fox-Edge/public
     * 它的作用：主要是在没有对应的JAR解码器组件的时候，先创建一个组件对象，形成组件/操作的结构化关系
     * 后面，在通过其他场景，更新替换该Fox-Edge/public信息
     *
     * @param fileName 文件名
     * @return 一个低优先级的JAR本地组件对象
     */
    public Map<String, Object> convertFileName2Local(String fileName){
        Map<String, Object> localMap = new HashMap<>();
        localMap.put(RepoCompVOFieldConstant.field_comp_repo, RepoCompVOFieldConstant.value_comp_repo_local);
        localMap.put(RepoCompVOFieldConstant.field_comp_type, RepoCompVOFieldConstant.value_comp_type_jar_decoder);
        localMap.put(RepoCompVOFieldConstant.field_comp_param, new HashMap<>());


        // 取出文件名和JAR文件版本信息
        String manufacturer = RepoCompConstant.value_default_manufacturer;
        String deviceType = RepoCompConstant.value_default_device_type;
        if (MethodUtils.hasEmpty(fileName)) {
            throw new ServiceException("文件名称不能为空!");
        }

        // 从结构化的文件名中，取出信息
        Map<String, String> map = this.jarFileNameService.splitJarFileName(fileName);
        if (map == null) {
            throw new ServiceException("文件名称格式不正确!");
        }

        String modelName = map.get(RepoCompConstant.filed_model_name);
        String modelVersion = map.get(RepoCompConstant.filed_model_version);
        if (MethodUtils.hasEmpty(modelName, modelVersion)) {
            throw new ServiceException("参数不能为空： modelName, modelVersion");
        }

        Map<String, Object> compParam = (Map<String, Object>) localMap.get(RepoCompVOFieldConstant.field_comp_param);
        compParam.put(OperateVOFieldConstant.field_manufacturer, manufacturer);
        compParam.put(OperateVOFieldConstant.field_device_type, deviceType);
        compParam.put(RepoCompConstant.filed_model_name, modelName);
        compParam.put(RepoCompConstant.filed_model_version, modelVersion);
        compParam.put(RepoCompConstant.filed_file_name, fileName);

        return localMap;
    }

    /**
     * 将JAR解码器的操作对象，转换为一个缺省的本地组件结构
     *
     * 注意：该信息是缺省的，并不完整，它主要是能够从JAR解码器的注解中，获得准确的组件信息
     *
     * 它的作用：主要是用来更新JAR组件的manufacturer和deviceType信息
     *
     * @param operateEntity 文件名
     * @return 一个低优先级的JAR本地组件对象
     */
    public Map<String, Object> convertOperateEntity2Local(OperateMethodEntity operateEntity){
        Map<String, Object> localMap = new HashMap<>();
        localMap.put(RepoCompVOFieldConstant.field_comp_repo, RepoCompVOFieldConstant.value_comp_repo_local);
        localMap.put(RepoCompVOFieldConstant.field_comp_type, RepoCompVOFieldConstant.value_comp_type_jar_decoder);
        localMap.put(RepoCompVOFieldConstant.field_comp_param, new HashMap<>());

        String fileName = (String) operateEntity.getEngineParam().getOrDefault(DeviceMethodVOFieldConstant.field_file, "");

        // 从结构化的文件名中，取出信息
        Map<String, String> map = this.jarFileNameService.splitJarFileName(fileName);
        if (map == null) {
            throw new ServiceException("文件名称格式不正确!");
        }

        String modelName = map.get(RepoCompConstant.filed_model_name);
        String modelVersion = map.get(RepoCompConstant.filed_model_version);
        if (MethodUtils.hasEmpty(modelName, modelVersion)) {
            throw new ServiceException("参数不能为空： modelName, modelVersion");
        }


        Map<String, Object> compParam = (Map<String, Object>) localMap.get(RepoCompVOFieldConstant.field_comp_param);
        compParam.put(OperateVOFieldConstant.field_manufacturer, operateEntity.getManufacturer());
        compParam.put(OperateVOFieldConstant.field_device_type, operateEntity.getDeviceType());
        compParam.put(RepoCompConstant.filed_model_name, modelName);
        compParam.put(RepoCompConstant.filed_model_version, modelVersion);
        compParam.put(RepoCompConstant.filed_file_name, fileName);

        return localMap;
    }

    public RepoCompEntity buildCompEntity(Map<String, Object> params) {
        // 提取业务参数
        String compType = (String) params.get(RepoCompVOFieldConstant.field_comp_type);
        String compRepo = (String) params.get(RepoCompVOFieldConstant.field_comp_repo);
        Map<String, Object> compParam = (Map<String, Object>) params.get(RepoCompVOFieldConstant.field_comp_param);


        // 简单校验参数
        if (MethodUtils.hasNull(compType, compRepo, compParam)) {
            throw new ServiceException("参数不能为空: compType, compRepo, compParam");
        }

        if (compRepo.equals(RepoCompVOFieldConstant.value_comp_repo_local) && compType.equals(RepoCompVOFieldConstant.value_comp_type_jar_decoder)) {
            String manufacturer = (String) compParam.get(OperateVOFieldConstant.field_manufacturer);
            String deviceType = (String) compParam.get(OperateVOFieldConstant.field_device_type);
            String fileName = (String) compParam.get(RepoCompConstant.filed_file_name);
            if (MethodUtils.hasNull(manufacturer, deviceType, fileName)) {
                throw new ServiceException("参数不能为空: manufacturer, deviceType, fileName");
            }

            // 构造作为参数的实体
            RepoCompEntity entity = new RepoCompEntity();
            entity.setCompRepo(compRepo);
            entity.setCompType(compType);
            entity.setCompName(fileName);
            entity.setCompParam(compParam);

            return entity;
        }

        if (compRepo.equals(RepoCompVOFieldConstant.value_comp_repo_local) && compType.equals(RepoCompVOFieldConstant.value_comp_type_jsp_decoder)) {
            String manufacturer = (String) compParam.get(OperateVOFieldConstant.field_manufacturer);
            String deviceType = (String) compParam.get(OperateVOFieldConstant.field_device_type);
            if (MethodUtils.hasNull(manufacturer, deviceType)) {
                throw new ServiceException("参数不能为空: manufacturer, deviceType");
            }

            // 构造作为参数的实体
            RepoCompEntity entity = new RepoCompEntity();
            entity.setCompRepo(compRepo);
            entity.setCompType(compType);
            entity.setCompName(manufacturer + ":" + deviceType);
            entity.setCompParam(compParam);

            return entity;
        }

        if (compRepo.equals(RepoCompVOFieldConstant.value_comp_repo_local) && compType.equals(RepoCompVOFieldConstant.value_comp_type_app_service)) {
            String appName = (String) compParam.get(ServiceVOFieldConstant.field_app_name);
            String appType = (String) compParam.get(ServiceVOFieldConstant.field_app_type);
            if (MethodUtils.hasNull(appName, appType)) {
                throw new ServiceException("参数不能为空: appName, appType");
            }

            // 构造作为参数的实体
            RepoCompEntity entity = new RepoCompEntity();
            entity.setCompRepo(compRepo);
            entity.setCompType(compType);
            entity.setCompName(appType + ":" + appName);
            entity.setCompParam(compParam);

            return entity;
        }

        if (compRepo.equals(RepoCompVOFieldConstant.value_comp_repo_local) && compType.equals(RepoCompVOFieldConstant.value_comp_type_file_template)) {
            String manufacturer = (String) compParam.get(OperateVOFieldConstant.field_manufacturer);
            String deviceType = (String) compParam.get(OperateVOFieldConstant.field_device_type);
            String modelName = (String) compParam.get(RepoCompConstant.filed_model_name);
            if (MethodUtils.hasNull(manufacturer, deviceType, modelName)) {
                throw new ServiceException("参数不能为空: manufacturer, deviceType, modelName");
            }

            // 构造作为参数的实体
            RepoCompEntity entity = new RepoCompEntity();
            entity.setCompRepo(compRepo);
            entity.setCompType(compType);
            entity.setCompName(manufacturer + ":" + deviceType);
            entity.setCompParam(compParam);

            // 填写固定参数
            entity.getCompParam().put(RepoCompConstant.filed_model_version, "v1");
            entity.getCompParam().put(RepoCompConstant.filed_version, "1.0.0");

            return entity;
        }

        throw new ServiceException("不支持的类型:" + compRepo + "," + compType);

    }

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

    public Map<String, Object> syncEntity(Long compId) throws IOException, InterruptedException {
        RepoCompEntity entity = this.entityManageService.getEntity(compId, RepoCompEntity.class);
        if (entity == null) {
            throw new ServiceException("实体不存在");
        }


        if (entity.getCompRepo().equals(RepoCompVOFieldConstant.value_comp_repo_local) && entity.getCompType().equals(RepoCompVOFieldConstant.value_comp_type_file_template)) {
            return this.syncCsvTemplateEntity(entity);
        }
        if (entity.getCompRepo().equals(RepoCompVOFieldConstant.value_comp_repo_local) && entity.getCompType().equals(RepoCompVOFieldConstant.value_comp_type_jar_decoder)) {
            return this.syncJarDecoderEntity(entity);
        }
        if (entity.getCompRepo().equals(RepoCompVOFieldConstant.value_comp_repo_local) && entity.getCompType().equals(RepoCompVOFieldConstant.value_comp_type_jsp_decoder)) {
            return this.syncJspDecoderEntity(entity);
        }

        throw new ServiceException("该组件类型，不支持从云端同步！");
    }

    private Map<String, Object> syncCsvTemplateEntity(RepoCompEntity entity) throws IOException {
        Map<String, Object> compParam = entity.getCompParam();

        String modelName = (String) compParam.get(RepoCompConstant.filed_model_name);
        String modelVersion = (String) compParam.get(RepoCompConstant.filed_model_version);
        if (MethodUtils.hasEmpty(modelName, modelVersion)) {
            throw new ServiceException("缺少参数： modelName, modelVersion");
        }


        Map<String, Object> body = new HashMap<>();
        body.put(RepoCompConstant.filed_model_type, "template");
        body.put(RepoCompConstant.filed_model_name, modelName);
        body.put(RepoCompConstant.filed_model_version, modelVersion);

        // 获得云端的信息
        Map<String, Object> respond = this.remoteService.executePost("/manager/repository/component/groupName", body);
        Object code = respond.get(AjaxResult.CODE_TAG);
        Object data = respond.get(AjaxResult.DATA_TAG);
        if (!HttpStatus.SUCCESS.equals(code)) {
            throw new ServiceException("从云端查询信息失败！" + respond);
        }
        if (data == null) {
            throw new ServiceException("云端没有这个组件，请先去云端仓库为本账号:" + this.remoteService.getUsername() + "归属的群组，注册这个组件:" + modelName);
        }

        // 找到了云端的组件信息
        Map<String, Object> map = (Map<String, Object>) data;

        // 克隆一个副本，防止修改影响到了原本
        entity = JsonUtils.clone(entity);
        entity.getCompParam().put(RepoCompVOFieldConstant.field_group_name, map.get("groupName"));
        entity.getCompParam().put(RepoCompVOFieldConstant.field_comp_id, map.get("id"));

        // 保存数据
        this.entityManageService.updateEntity(entity);

        return respond;
    }

    private Map<String, Object> syncJarDecoderEntity(RepoCompEntity entity) throws IOException {
        Map<String, Object> compParam = entity.getCompParam();

        String modelName = (String) compParam.get(RepoCompConstant.filed_model_name);
        String modelVersion = (String) compParam.get(RepoCompConstant.filed_model_version);
        if (MethodUtils.hasEmpty(modelName, modelVersion)) {
            throw new ServiceException("缺少参数： modelName, modelVersion");
        }


        Map<String, Object> body = new HashMap<>();
        body.put(RepoCompConstant.filed_model_type, "decoder");
        body.put(RepoCompConstant.filed_model_name, modelName);
        body.put(RepoCompConstant.filed_model_version, modelVersion);

        // 获得云端的信息
        Map<String, Object> respond = this.remoteService.executePost("/manager/repository/component/groupName", body);
        Object code = respond.get(AjaxResult.CODE_TAG);
        Object data = respond.get(AjaxResult.DATA_TAG);
        if (!HttpStatus.SUCCESS.equals(code)) {
            throw new ServiceException("从云端查询信息失败！" + respond);
        }
        if (data == null) {
            throw new ServiceException("云端没有这个组件，请先去云端仓库为本账号:" + this.remoteService.getUsername() + "归属的群组，注册这个组件:" + modelName);
        }

        // 找到了云端的组件信息
        Map<String, Object> map = (Map<String, Object>) data;

        // 克隆一个副本，防止修改影响到了原本
        entity = JsonUtils.clone(entity);
        entity.getCompParam().put(RepoCompVOFieldConstant.field_group_name, map.get("groupName"));
        entity.getCompParam().put(RepoCompVOFieldConstant.field_comp_id, map.get("id"));

        // 保存数据
        this.entityManageService.updateEntity(entity);

        return respond;
    }

    private Map<String, Object> syncJspDecoderEntity(RepoCompEntity entity) throws IOException {
        Map<String, Object> compParam = entity.getCompParam();

        String deviceType = (String) compParam.get(OperateVOFieldConstant.field_device_type);
        String manufacturer = (String) compParam.get(OperateVOFieldConstant.field_manufacturer);
        if (MethodUtils.hasEmpty(deviceType, manufacturer)) {
            throw new ServiceException("缺少参数： deviceType, manufacturer");
        }


        Map<String, Object> body = new HashMap<>();
        body.put(OperateVOFieldConstant.field_manufacturer, manufacturer);
        body.put(OperateVOFieldConstant.field_device_type, deviceType);

        // 获得云端的信息
        Map<String, Object> respond = this.remoteService.executePost("/manager/repository/component/script/title/list", body);
        Object code = respond.get(AjaxResult.CODE_TAG);
        Object data = respond.get(AjaxResult.DATA_TAG);
        if (!HttpStatus.SUCCESS.equals(code) || data == null) {
            throw new ServiceException("从云端查询信息失败！" + respond);
        }

        // 如果空列表：云端没有这个组件
        List<Map<String, Object>> list = (List<Map<String, Object>>) data;
        if (list.isEmpty()) {
            throw new ServiceException("云端没有这个组件，请先去云端仓库为本账号:" + this.remoteService.getUsername() + "归属的群组，注册这个组件:" + deviceType);
        }

        // 找到了云端的组件信息
        Map<String, Object> map = list.get(0);

        // 克隆一个副本，防止修改影响到了原本
        entity = JsonUtils.clone(entity);
        entity.getCompParam().put(RepoCompVOFieldConstant.field_group_name, map.get("groupName"));
        entity.getCompParam().put(RepoCompVOFieldConstant.field_comp_id, map.get("id"));

        // 保存数据
        this.entityManageService.updateEntity(entity);

        return respond;
    }

    public Map<String, Object> installVersion(String compType, Map<String, Object> data) throws IOException, InterruptedException {

        if (compType.equals(RepoCompVOFieldConstant.value_comp_type_jsp_decoder)) {
            return this.installJspDecoderEntity(data);
        }

        throw new ServiceException("该组件类型，不支持本地上传");
    }

    private Map<String, Object> installJspDecoderEntity(Map<String, Object> data) throws IOException {
        String deviceType = (String) data.get(OperateVOFieldConstant.field_device_type);
        String manufacturer = (String) data.get(OperateVOFieldConstant.field_manufacturer);
        String scriptId = (String) data.get(OperateVOFieldConstant.field_script_id);
        String groupName = (String) data.get(OperateVOFieldConstant.field_group_name);
        List<Map<String, Object>> operates = (List<Map<String, Object>>) data.get("operates");
        if (MethodUtils.hasEmpty(deviceType, manufacturer, scriptId, groupName, operates)) {
            throw new ServiceException("缺少参数： deviceType, manufacturer, scriptId, groupName, operates");
        }

        RepoCompEntity repoCompEntity = new RepoCompEntity();
        repoCompEntity.setCompRepo(RepoCompVOFieldConstant.value_comp_repo_local);
        repoCompEntity.setCompType(RepoCompVOFieldConstant.value_comp_type_jsp_decoder);
        repoCompEntity.setCompName(manufacturer + ":" + deviceType);

        // 如果组件对象不存在，那么就创建一个新的组件对象
        RepoCompEntity existCompEntity = this.entityManageService.getEntity(repoCompEntity.makeServiceKey(), RepoCompEntity.class);
        if (existCompEntity == null) {
            Map<String, Object> compParam = repoCompEntity.getCompParam();
            compParam.put(RepoCompVOFieldConstant.field_comp_id, scriptId);
            compParam.put(RepoCompVOFieldConstant.field_group_name, groupName);
            compParam.put(OperateVOFieldConstant.field_manufacturer, manufacturer);
            compParam.put(OperateVOFieldConstant.field_device_type, deviceType);

            this.entityManageService.insertEntity(repoCompEntity);
        } else {
            repoCompEntity = existCompEntity;
        }


        // 获得已经存在的操作列表
        List<BaseEntity> operateList = this.entityManageService.getEntityList(OperateEntity.class, (Object value) -> {
            OperateEntity entity = (OperateEntity) value;

            if (!entity.getEngineType().equals(OperateVOFieldConstant.value_engine_javascript)) {
                return false;
            }
            if (!entity.getManufacturer().equals(manufacturer)) {
                return false;
            }
            return entity.getDeviceType().equals(deviceType);
        });

        // 组织成天MAP关系
        Map<String, OperateEntity> dstOperateMap = new HashMap<>();
        for (Map<String, Object> operate : operates) {
            OperateEntity operateEntity = new OperateEntity();
            operateEntity.bind(operate);
            operateEntity.setManufacturer(manufacturer);
            operateEntity.setDeviceType(deviceType);

            dstOperateMap.put(operateEntity.getOperateName(), operateEntity);
        }

        Map<String, BaseEntity> srcOperateMap = ContainerUtils.buildMapByKey(operateList, OperateEntity::getOperateName);

        Set<String> addList = new HashSet<>();
        Set<String> delList = new HashSet<>();
        Set<String> eqlList = new HashSet<>();
        DifferUtils.differByValue(srcOperateMap.keySet(), dstOperateMap.keySet(), addList, delList, eqlList);

        for (String key : addList) {
            OperateEntity operateEntity = dstOperateMap.get(key);
            operateEntity.setId(null);
            this.entityManageService.insertEntity(operateEntity);
        }
        for (String key : delList) {
            BaseEntity operateEntity = srcOperateMap.get(key);
            this.entityManageService.deleteEntity(operateEntity);
        }
        for (String key : eqlList) {
            BaseEntity dstEntity = dstOperateMap.get(key);
            BaseEntity srcEntity = srcOperateMap.get(key);
            if (dstEntity.makeServiceValue().equals(srcEntity.makeServiceValue())) {
                continue;
            }

            dstEntity.setId(srcEntity.getId());
            this.entityManageService.updateEntity(dstEntity);
        }

        // 获得版本日期
        Long updateTime = Long.valueOf(data.getOrDefault("updateTime", "0").toString());
        String format = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat SDF = new SimpleDateFormat(format);
        String timer = SDF.format(new Date(updateTime));

        // 更新：安装版本的信息
        Map<String, Object> install = new HashMap<>();
        install.put("updateTime", timer);
        install.put("description", data.get("description"));
        install.put("id", data.get("id"));

        // 更新版本信息
        repoCompEntity.getCompParam().put("installVersion", install);
        this.entityManageService.updateEntity(repoCompEntity);

        return null;
    }


}
