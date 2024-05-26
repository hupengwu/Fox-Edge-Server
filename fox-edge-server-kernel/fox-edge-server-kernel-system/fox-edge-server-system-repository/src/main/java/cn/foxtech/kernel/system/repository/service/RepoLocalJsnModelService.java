package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.entity.constant.DeviceModelVOFieldConstant;
import cn.foxtech.common.entity.constant.RepoCompVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceModelEntity;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RepoLocalJsnModelService {
    @Autowired
    private EntityManageService entityManageService;


    public List<BaseEntity> selectEntityList(Map<String, Object> body) {
        return this.entityManageService.getEntityList(DeviceModelEntity.class, (Object value) -> {
            DeviceModelEntity entity = (DeviceModelEntity) value;

            boolean result = true;


            if (body.containsKey(DeviceModelVOFieldConstant.field_device_type)) {
                result &= entity.getDeviceType().contains((String) body.get(DeviceModelVOFieldConstant.field_device_type));
            }
            if (body.containsKey(DeviceModelVOFieldConstant.field_manufacturer)) {
                result &= entity.getManufacturer().equals(body.get(DeviceModelVOFieldConstant.field_manufacturer));
            }
            if (body.containsKey(DeviceModelVOFieldConstant.field_model_name)) {
                result &= entity.getModelName().equals(body.get(DeviceModelVOFieldConstant.field_model_name));
            }

            return result;
        });
    }

    public List<BaseEntity> getDeviceTemplateEntityList(RepoCompEntity compEntity) {
        return this.entityManageService.getEntityList(DeviceModelEntity.class, (Object value) -> {
            DeviceModelEntity entity = (DeviceModelEntity) value;

            String manufacturer = (String) compEntity.getCompParam().get(DeviceModelVOFieldConstant.field_manufacturer);
            String deviceType = (String) compEntity.getCompParam().get(DeviceModelVOFieldConstant.field_device_type);

            if (!entity.getManufacturer().equals(manufacturer)) {
                return false;
            }
            return entity.getDeviceType().equals(deviceType);

        });
    }

    public void insertOrUpdate(Map<String, Object> params) {
        // 提取业务参数
        Integer compId = (Integer) params.get(DeviceModelVOFieldConstant.field_comp_id);
        String modelName = (String) params.get(DeviceModelVOFieldConstant.field_model_name);
        Map<String, Object> modelParam = (Map<String, Object>) params.get(DeviceModelVOFieldConstant.field_model_param);
        Map<String, Object> extendParam = (Map<String, Object>) params.get(DeviceModelVOFieldConstant.field_extend_param);

        // 简单校验参数
        if (MethodUtils.hasEmpty(compId, modelName)) {
            throw new ServiceException("参数不能为空: compId, templateName, manufacturer, deviceType");
        }

        // 验证参数格式
        verifyModelParam(modelParam);

        RepoCompEntity compEntity = this.entityManageService.getEntity(Long.valueOf(compId), RepoCompEntity.class);
        if (compEntity == null) {
            throw new ServiceException("找不到对应的组件:" + compId);
        }

        String manufacturer = (String) compEntity.getCompParam().get(DeviceModelVOFieldConstant.field_manufacturer);
        String deviceType = (String) compEntity.getCompParam().get(DeviceModelVOFieldConstant.field_device_type);
        if (MethodUtils.hasEmpty(manufacturer, deviceType)) {
            throw new ServiceException("参数不能为空: manufacturer, deviceType");
        }

        // 构造作为参数的实体
        DeviceModelEntity entity = new DeviceModelEntity();
        entity.setModelName(modelName);
        entity.setDeviceType(deviceType);
        entity.setManufacturer(manufacturer);
        entity.setModelParam(modelParam);
        entity.setExtendParam(extendParam);

        // 简单验证实体的合法性
        if (entity.hasNullServiceKey()) {
            throw new ServiceException("具有null的service key！");
        }

        // 新增/修改实体：参数不包含id为新增，包含为修改
        if (params.get("id") == null) {
            if (entity.getModelParam() == null) {
                entity.setModelParam(new HashMap<>());
            }
            if (entity.getExtendParam() == null) {
                entity.setExtendParam(new HashMap<>());
            }

            DeviceModelEntity exist = this.entityManageService.getEntity(entity.makeServiceKey(), DeviceModelEntity.class);
            if (exist != null) {
                throw new ServiceException("实体已存在");
            }

            this.entityManageService.insertEntity(entity);
        } else {
            Long id = Long.parseLong(params.get("id").toString());
            DeviceModelEntity exist = this.entityManageService.getEntity(id, DeviceModelEntity.class);
            if (exist == null) {
                throw new ServiceException("实体不存在");
            }

            // 如果没有填写，说明只是修改标题，那么填入原来的数值
            if (MethodUtils.hasEmpty(entity.getModelParam())) {
                entity.setModelParam(exist.getModelParam());
            }
            // 如果为空，那么说明用户不想修改该数据
            if (MethodUtils.hasNull(entity.getExtendParam())) {
                entity.setExtendParam(exist.getExtendParam());
            }


            if (!exist.getManufacturer().equals(manufacturer) // 不允许修改
                    || !exist.getDeviceType().equals(deviceType) // 不允许修改
            ) {
                throw new ServiceException("不允许修改，否则影响关联关系：manufacturer, deviceType, engineType, operateName");
            }

            // 修改数据
            entity.setId(id);
            this.entityManageService.updateEntity(entity);
        }
    }

    private void verifyModelParam(Map<String, Object> modelParam) {
        try {
            List<Map<String, Object>> list = (List<Map<String, Object>>) modelParam.get("list");
            Map<String, Object> engine = (Map<String, Object>) modelParam.get("engine");
            String manufacturer = (String) engine.get("manufacturer");
            String deviceType = (String) engine.get("deviceType");
        } catch (Exception e) {
            throw new ServiceException("modelParam的格式不合法");
        }

    }

    public void deleteEntity(Long id) {
        this.entityManageService.deleteEntity(id, DeviceModelEntity.class);
    }

    public DeviceModelEntity queryEntity(Long id) {
        return this.entityManageService.getEntity(id, DeviceModelEntity.class);
    }

    public RepoCompEntity getCompEntity(String manufacturer,String deviceType){
        RepoCompEntity compEntity = this.entityManageService.getEntity(RepoCompEntity.class, (Object value) -> {
            RepoCompEntity entity = (RepoCompEntity) value;

            if (!entity.getCompType().equals(RepoCompVOFieldConstant.value_comp_type_jsn_decoder)) {
                return false;
            }

            if (!manufacturer.equals(entity.getCompParam().get(RepoCompConstant.filed_manufacturer))) {
                return false;
            }

            return deviceType.equals(entity.getCompParam().get(RepoCompConstant.filed_device_type));
        });

        return compEntity;
    }
}
