package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.entity.constant.Constants;
import cn.foxtech.common.entity.constant.OperateVOFieldConstant;
import cn.foxtech.common.entity.constant.RepoCompVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RepoLocalOperateService {
    @Autowired
    private EntityManageService entityManageService;


    @Autowired
    private EngineParamService engineParamService;

    public List<BaseEntity> selectEntityList(Map<String, Object> body) {
        return this.entityManageService.getEntityList(OperateEntity.class, (Object value) -> {
            OperateEntity entity = (OperateEntity) value;

            boolean result = true;

            if (body.containsKey(OperateVOFieldConstant.field_device_type)) {
                result = entity.getDeviceType().contains((String) body.get(OperateVOFieldConstant.field_device_type));
            }
            if (body.containsKey(OperateVOFieldConstant.field_manufacturer)) {
                result &= entity.getManufacturer().equals(body.get(OperateVOFieldConstant.field_manufacturer));
            }
            if (body.containsKey(OperateVOFieldConstant.field_operate_name)) {
                result &= entity.getOperateName().equals(body.get(OperateVOFieldConstant.field_operate_name));
            }
            if (body.containsKey(OperateVOFieldConstant.field_operate_mode)) {
                result &= entity.getOperateMode().equals(body.get(OperateVOFieldConstant.field_operate_mode));
            }
            if (body.containsKey(OperateVOFieldConstant.field_operate_modes)) {
                Set<String> operateModes = (Set<String>) body.get(OperateVOFieldConstant.field_operate_modes);
                result &= operateModes.contains(entity.getOperateMode());
            }
            if (body.containsKey(OperateVOFieldConstant.field_data_type)) {
                result &= entity.getDataType().equals(body.get(OperateVOFieldConstant.field_data_type));
            }
            if (body.containsKey(OperateVOFieldConstant.field_service_type)) {
                result &= entity.getServiceType().equals(body.get(OperateVOFieldConstant.field_service_type));
            }
            if (body.containsKey(OperateVOFieldConstant.field_engine_type)) {
                result &= entity.getEngineType().equals(body.get(OperateVOFieldConstant.field_engine_type));
            }
            if (body.containsKey(OperateVOFieldConstant.field_polling)) {
                result &= entity.getPolling().equals(body.get(OperateVOFieldConstant.field_polling));
            }

            return result;
        });
    }

    public List<BaseEntity> getOperateEntityList(RepoCompEntity compEntity) {
        return this.entityManageService.getEntityList(OperateEntity.class, (Object value) -> {
            OperateEntity entity = (OperateEntity) value;

            String manufacturer = (String) compEntity.getCompParam().get(OperateVOFieldConstant.field_manufacturer);
            String deviceType = (String) compEntity.getCompParam().get(OperateVOFieldConstant.field_device_type);

            if (!entity.getManufacturer().equals(manufacturer)) {
                return false;
            }
            if (!entity.getDeviceType().equals(deviceType)) {
                return false;
            }

            if (RepoCompVOFieldConstant.value_comp_type_jar_decoder.equals(compEntity.getCompType())) {
                return entity.getEngineType().equals(OperateVOFieldConstant.value_engine_java);
            }
            if (RepoCompVOFieldConstant.value_comp_type_jsp_decoder.equals(compEntity.getCompType())) {
                return entity.getEngineType().equals(OperateVOFieldConstant.value_engine_javascript);
            }

            return false;

        });
    }

    public void insertOrUpdate(Map<String, Object> params) {
        // 提取业务参数
        Integer compId = (Integer) params.get(OperateVOFieldConstant.field_comp_id);
        String operateName = (String) params.get(OperateVOFieldConstant.field_operate_name);
        String operateMode = (String) params.get(OperateVOFieldConstant.field_operate_mode);
        String dataType = (String) params.get(OperateVOFieldConstant.field_data_type);
        String serviceType = (String) params.get(OperateVOFieldConstant.field_service_type);
        String engineType = (String) params.get(OperateVOFieldConstant.field_engine_type);
        Boolean polling = (Boolean) params.get(OperateVOFieldConstant.field_polling);
        Integer timeout = (Integer) params.get(OperateVOFieldConstant.field_timeout);
        Map<String, Object> engineParam = (Map<String, Object>) params.get(OperateVOFieldConstant.field_engine_param);


        // 简单校验参数
        if (MethodUtils.hasEmpty(compId, operateName, operateMode, dataType, serviceType, engineType, polling, timeout)) {
            throw new ServiceException("参数不能为空: compId, operateName, operateMode, dataType, serviceType, engineType, polling, timeout");
        }

        RepoCompEntity compEntity = this.entityManageService.getEntity(Long.valueOf(compId.toString()), RepoCompEntity.class);
        if (compEntity == null) {
            throw new ServiceException("找不到对应的组件:" + compId);
        }

        String manufacturer = (String) compEntity.getCompParam().get(OperateVOFieldConstant.field_manufacturer);
        String deviceType = (String) compEntity.getCompParam().get(OperateVOFieldConstant.field_device_type);
        if (MethodUtils.hasEmpty(manufacturer, deviceType)) {
            throw new ServiceException("参数不能为空: manufacturer, deviceType");
        }

        // 构造作为参数的实体
        OperateEntity entity = new OperateEntity();
        entity.setDeviceType(deviceType);
        entity.setManufacturer(manufacturer);
        entity.setEngineType(engineType);
        entity.setEngineParam(engineParam);
        entity.setOperateName(operateName);
        entity.setOperateMode(operateMode);
        entity.setDataType(dataType);
        entity.setServiceType(serviceType);
        entity.setPolling(polling);
        entity.setTimeout(timeout);

        // 简单验证实体的合法性
        if (entity.hasNullServiceKey()) {
            throw new ServiceException("具有null的service key！");
        }


        // 新增/修改实体：参数不包含id为新增，包含为修改
        if (!params.containsKey("id")) {

            // 如果没有填写，那么填入一个缺省值
            if (MethodUtils.hasEmpty(entity.getEngineParam())) {
                entity.setEngineParam(this.engineParamService.getDefault(operateMode));
            }

            OperateEntity exist = this.entityManageService.getEntity(entity.makeServiceKey(), OperateEntity.class);
            if (exist != null) {
                throw new ServiceException("实体已存在");
            }

            this.entityManageService.insertEntity(entity);
        } else {
            Long id = Long.parseLong(params.get("id").toString());
            OperateEntity exist = this.entityManageService.getEntity(id, OperateEntity.class);
            if (exist == null) {
                throw new ServiceException("实体不存在");
            }

            // 如果没有填写，说明只是修改标题，那么填入原来的数值
            if (MethodUtils.hasEmpty(entity.getEngineParam())) {
                entity.setEngineParam(exist.getEngineParam());
            }

            if (!exist.getManufacturer().equals(manufacturer) // 不允许修改
                    || !exist.getDeviceType().equals(deviceType) // 不允许修改
                    || !exist.getEngineType().equals(engineType) // 不允许修改
                    || !exist.getOperateName().equals(operateName) // 不允许修改
            ) {
                throw new ServiceException("不允许修改，否则影响关联关系：manufacturer, deviceType, engineType, operateName");
            }

            // 修改数据
            entity.setId(id);
            this.entityManageService.updateEntity(entity);
        }
    }

    public void deleteEntity(Long id) {
        this.entityManageService.deleteEntity(id, OperateEntity.class);
    }

    public OperateEntity queryEntity(Long id) {
        return this.entityManageService.getEntity(id, OperateEntity.class);
    }

    public List<Map<String, Object>> selectOptionList(Map<String, Object> body) {
        String deviceType = (String) body.get(OperateVOFieldConstant.field_device_type);
        String manufacturer = (String) body.get(OperateVOFieldConstant.field_manufacturer);
        String operateName = (String) body.get(OperateVOFieldConstant.field_operate_name);
        if (MethodUtils.hasEmpty(manufacturer)) {
            throw new ServiceException("参数缺失：deviceType, manufacturer");
        }

        Set<String> operateModes = new HashSet<>();
        operateModes.add(Constants.OPERATE_MODE_PUBLISH);
        operateModes.add(Constants.OPERATE_MODE_EXCHANGE);

        Map<String, Object> param = new HashMap<>();
        param.put(OperateVOFieldConstant.field_manufacturer, manufacturer);
        param.put(OperateVOFieldConstant.field_operate_modes, operateModes);
        if (!MethodUtils.hasEmpty(deviceType)) {
            param.put(OperateVOFieldConstant.field_device_type, deviceType);
        }
        if (!MethodUtils.hasEmpty(operateName)) {
            param.put(OperateVOFieldConstant.field_operate_name, operateName);
        }

        // 转换为option格式
        List<BaseEntity> data = this.selectEntityList(param);
        if (!MethodUtils.hasEmpty(data)) {

            List<Map<String, Object>> resultList = new ArrayList<>();
            for (BaseEntity entity : data) {
                OperateEntity operateEntity = (OperateEntity) entity;

                Map<String, Object> result = new HashMap<>();

                if (deviceType != null && operateName == null) {
                    result.put("value", operateEntity.getOperateName());
                    result.put("label", operateEntity.getOperateName());
                }
                if (deviceType != null && operateName != null) {
                    result.put("value", operateEntity.getOperateMode());
                    result.put("label", operateEntity.getOperateMode());
                }

                resultList.add(result);
            }

            return resultList;
        }

        return new ArrayList<>();
    }
}
