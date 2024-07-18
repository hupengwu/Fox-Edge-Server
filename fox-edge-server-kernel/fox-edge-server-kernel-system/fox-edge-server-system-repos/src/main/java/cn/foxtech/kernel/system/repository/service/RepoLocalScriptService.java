package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.entity.constant.OperateVOFieldConstant;
import cn.foxtech.common.entity.constant.RepoCompVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.utils.ContainerUtils;
import cn.foxtech.common.utils.bean.BeanMapUtils;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RepoLocalScriptService {
    @Autowired
    private EntityManageService entityManageService;

    public List<Map<String, Object>> extendCompOperateInfo(List<BaseEntity> compEntityList) {

        // 获得OperateEntity的Map
        Map<String, List<OperateEntity>> operateEntityMap = this.getOperateEntityMap();

        // 将operateEntity的数量，添加到返回的内容之中
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (BaseEntity entity : compEntityList) {
            RepoCompEntity compEntity = (RepoCompEntity) entity;

            Map<String, Object> map = BeanMapUtils.objectToMap(compEntity);

            Map<String, Object> compParam = (Map<String, Object>) map.get(RepoCompVOFieldConstant.field_comp_param);

            String manufacturer = (String) compEntity.getCompParam().get(RepoCompConstant.filed_manufacturer);
            String deviceType = (String) compEntity.getCompParam().get(RepoCompConstant.filed_device_type);
            List<OperateEntity> list = operateEntityMap.get(manufacturer + "|" + deviceType);
            if (list == null) {
                compParam.put("operateCount", 0);
            } else {
                compParam.put("operateCount", list.size());
            }

            mapList.add(map);
        }

        return mapList;

    }

    private Map<String, List<OperateEntity>> getOperateEntityMap() {
        Map<String, List<OperateEntity>> result = new HashMap<>();

        List<BaseEntity> entityList = this.entityManageService.getEntityList(OperateEntity.class);
        for (BaseEntity entity : entityList) {
            OperateEntity operateEntity = (OperateEntity) entity;

            if (!operateEntity.getEngineType().equals(OperateVOFieldConstant.value_engine_javascript)) {
                continue;
            }

            String key = operateEntity.getManufacturer() + "|" + operateEntity.getDeviceType();
            List<OperateEntity> operateEntities = result.computeIfAbsent(key, k -> new ArrayList<>());
            operateEntities.add(operateEntity);
        }

        return result;
    }


    public RepoCompEntity getCompEntity(String manufacturer, String deviceType) {
        RepoCompEntity compEntity = this.entityManageService.getEntity(RepoCompEntity.class, (Object value) -> {
            RepoCompEntity entity = (RepoCompEntity) value;

            if (!entity.getCompType().equals(RepoCompVOFieldConstant.value_comp_type_jsp_decoder)) {
                return false;
            }

            if (!manufacturer.equals(entity.getCompParam().get(RepoCompConstant.filed_manufacturer))) {
                return false;
            }

            return deviceType.equals(entity.getCompParam().get(RepoCompConstant.filed_device_type));
        });

        return compEntity;
    }

    public List<OperateEntity> getOperateEntityList(String manufacturer, String deviceType) {
        List<BaseEntity> entityList = this.entityManageService.getEntityList(OperateEntity.class, (Object value) -> {
            OperateEntity entity = (OperateEntity) value;

            if (!entity.getEngineType().equals(OperateVOFieldConstant.value_engine_javascript)) {
                return false;
            }

            if (!manufacturer.equals(entity.getManufacturer())) {
                return false;
            }

            return deviceType.equals(entity.getDeviceType());
        });

        return ContainerUtils.buildClassList(entityList, OperateEntity.class);
    }
}
