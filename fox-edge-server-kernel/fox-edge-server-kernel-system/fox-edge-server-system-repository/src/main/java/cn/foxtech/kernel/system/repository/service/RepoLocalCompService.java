package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.domain.constant.ServiceVOFieldConstant;
import cn.foxtech.common.entity.constant.DeviceModelVOFieldConstant;
import cn.foxtech.common.entity.constant.OperateVOFieldConstant;
import cn.foxtech.common.entity.constant.RepoCompVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceModelEntity;
import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.utils.ContainerUtils;
import cn.foxtech.common.utils.DifferUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private RepoLocalJsnModelService modelService;

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

        // 检查：实体中的依赖关系，避免数据之间依赖关系失效
        if (RepoCompVOFieldConstant.value_comp_type_jsn_decoder.equals(compEntity.getCompType())) {
            List<BaseEntity> operateList = this.modelService.getDeviceTemplateEntityList(compEntity);
            if (!operateList.isEmpty()) {
                throw new ServiceException("该组件下面，已经定义了操作方法，请先删除这些操作方法后，再删除组件!");
            }
        }

        // 删除数据
        this.entityManageService.deleteEntity(compId, RepoCompEntity.class);
    }

    public RepoCompEntity getRepoCompEntity(Long compId) {
        return this.entityManageService.getEntity(Long.parseLong(compId.toString()), RepoCompEntity.class);
    }


    public Map<String, Object> installVersion(String compType, Map<String, Object> data) throws IOException, InterruptedException {

        if (compType.equals(RepoCompVOFieldConstant.value_comp_type_jsp_decoder)) {
            return this.installJspDecoderEntity(data);
        }
        if (compType.equals(RepoCompVOFieldConstant.value_comp_type_jsn_decoder)) {
            return this.installJsnDecoderEntity(data);
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

    private Map<String, Object> installJsnDecoderEntity(Map<String, Object> data) throws IOException {
        String deviceType = (String) data.get(DeviceModelVOFieldConstant.field_device_type);
        String manufacturer = (String) data.get(DeviceModelVOFieldConstant.field_manufacturer);
        String modelId = (String) data.get(DeviceModelVOFieldConstant.field_model_id);
        String groupName = (String) data.get(DeviceModelVOFieldConstant.field_group_name);
        List<Map<String, Object>> objects = (List<Map<String, Object>>) data.get("objects");
        if (MethodUtils.hasEmpty(deviceType, manufacturer, modelId, groupName, objects)) {
            throw new ServiceException("缺少参数： deviceType, manufacturer, modelId, groupName, objects");
        }

        RepoCompEntity repoCompEntity = new RepoCompEntity();
        repoCompEntity.setCompRepo(RepoCompVOFieldConstant.value_comp_repo_local);
        repoCompEntity.setCompType(RepoCompVOFieldConstant.value_comp_type_jsn_decoder);
        repoCompEntity.setCompName(manufacturer + ":" + deviceType);

        // 如果组件对象不存在，那么就创建一个新的组件对象
        RepoCompEntity existCompEntity = this.entityManageService.getEntity(repoCompEntity.makeServiceKey(), RepoCompEntity.class);
        if (existCompEntity == null) {
            Map<String, Object> compParam = repoCompEntity.getCompParam();
            compParam.put(DeviceModelVOFieldConstant.field_comp_id, modelId);
            compParam.put(DeviceModelVOFieldConstant.field_group_name, groupName);
            compParam.put(DeviceModelVOFieldConstant.field_manufacturer, manufacturer);
            compParam.put(DeviceModelVOFieldConstant.field_device_type, deviceType);

            this.entityManageService.insertEntity(repoCompEntity);
        } else {
            repoCompEntity = existCompEntity;
        }


        // 获得已经存在的操作列表
        List<BaseEntity> objectList = this.entityManageService.getEntityList(DeviceModelEntity.class, (Object value) -> {
            DeviceModelEntity entity = (DeviceModelEntity) value;

            if (!entity.getManufacturer().equals(manufacturer)) {
                return false;
            }
            return entity.getDeviceType().equals(deviceType);
        });

        // 组织成天MAP关系
        Map<String, DeviceModelEntity> dstOperateMap = new HashMap<>();
        for (Map<String, Object> object : objects) {
            DeviceModelEntity modelEntity = new DeviceModelEntity();
            modelEntity.bind(object);
            modelEntity.setManufacturer(manufacturer);
            modelEntity.setDeviceType(deviceType);

            dstOperateMap.put(modelEntity.getModelName(), modelEntity);
        }

        Map<String, BaseEntity> srcOperateMap = ContainerUtils.buildMapByKey(objectList, DeviceModelEntity::getModelName);

        Set<String> addList = new HashSet<>();
        Set<String> delList = new HashSet<>();
        Set<String> eqlList = new HashSet<>();
        DifferUtils.differByValue(srcOperateMap.keySet(), dstOperateMap.keySet(), addList, delList, eqlList);

        for (String key : addList) {
            DeviceModelEntity modelEntity = dstOperateMap.get(key);
            modelEntity.setId(null);
            this.entityManageService.insertEntity(modelEntity);
        }
        for (String key : delList) {
            BaseEntity modelEntity = srcOperateMap.get(key);
            this.entityManageService.deleteEntity(modelEntity);
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
