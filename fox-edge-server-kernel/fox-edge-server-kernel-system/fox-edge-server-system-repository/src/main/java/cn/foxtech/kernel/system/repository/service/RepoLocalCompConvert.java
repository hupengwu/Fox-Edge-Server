package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.domain.constant.ServiceVOFieldConstant;
import cn.foxtech.common.entity.constant.OperateVOFieldConstant;
import cn.foxtech.common.entity.constant.RepoCompVOFieldConstant;
import cn.foxtech.common.entity.entity.OperateMethodEntity;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.device.domain.constant.DeviceMethodVOFieldConstant;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 参数转换器：将各种场景下的不规范的特定格式参数，转化为RepoLocalCompBuilder需要的规范化参数
 */
@Component
public class RepoLocalCompConvert {
    @Autowired
    private RepoLocalJarFileNameService jarFileNameService;

    /**
     * 用户场景：fox-cloud的云端仓库对象参数，保存为本地仓库对象时，将云端仓库格式的参数转换为本地仓库格式的参数
     * <p>
     * 将云端仓库返回的参数，转为Builder需要的参数
     *
     * @param cloud
     * @return
     */
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

    /**
     * 用户场景：fox-edge在启动的时候，从本地app们的conf文件中，构造本地仓库需要的规范化参数
     *
     * @param confMap
     * @return
     */
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
        compParam.put(ServiceVOFieldConstant.field_app_engine, confMap.get(ServiceVOFieldConstant.field_app_engine));
        compParam.put(ServiceVOFieldConstant.field_app_name, confMap.get(ServiceVOFieldConstant.field_app_name));
        compParam.put(ServiceVOFieldConstant.field_app_type, confMap.get(ServiceVOFieldConstant.field_app_type));
        compParam.put(ServiceVOFieldConstant.field_file_name, confMap.get(ServiceVOFieldConstant.field_file_name));
        compParam.put(ServiceVOFieldConstant.field_loader_name, confMap.get(ServiceVOFieldConstant.field_loader_name));
        compParam.put(ServiceVOFieldConstant.field_conf_files, confMap.get(ServiceVOFieldConstant.field_conf_files));
        compParam.put(ServiceVOFieldConstant.field_user_param, confMap.get(ServiceVOFieldConstant.field_user_param));
        return localMap;
    }

    /**
     * 场景3：fox-edge启动的时候，从本地的jar文件，根据文件名称，生成一个缺省的本地仓库参数
     * 注意：该信息是缺省的，并不完整，它包含一个Fox-Edge/public
     * 它的作用：主要是在没有对应的JAR解码器组件的时候，先创建一个组件对象，形成组件/操作的结构化关系
     * 后面，在通过其他场景，更新替换该Fox-Edge/public信息
     *
     * @param fileName
     * @return
     */
    public Map<String, Object> convertFileName2Local(String fileName) {
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
     * 场景4：从operate中，构造一个本地仓库对象需要的参数
     * 注意：该信息是缺省的，并不完整，它主要是能够从JAR解码器的注解中，获得准确的组件信息
     * 它的作用：主要是用来更新JAR组件的manufacturer和deviceType信息
     *
     * @param operateEntity
     * @return
     */
    public Map<String, Object> convertOperateEntity2Local(OperateMethodEntity operateEntity) {
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
}
