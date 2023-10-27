package cn.foxtech.common.entity.utils;

import cn.foxtech.common.entity.constant.DeviceVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.ExtendConfigEntity;
import cn.foxtech.common.entity.entity.ExtendField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtendConfigUtils {
    private static <T> Map<String, ExtendConfigEntity> getExtendConfigList(List<BaseEntity> extendConfigEntityList, Class<T> clazz) {
        Map<String, ExtendConfigEntity> result = new HashMap<>();
        for (BaseEntity entity : extendConfigEntityList) {
            ExtendConfigEntity extendConfigEntity = (ExtendConfigEntity) entity;
            if (clazz.equals(DeviceEntity.class)) {
                if (extendConfigEntity.getExtendType().equals("DeviceEntity" + "Globe")) {
                    result.put("Globe", extendConfigEntity);
                    continue;
                }
                if (extendConfigEntity.getExtendType().equals("DeviceEntity" + "Type")) {
                    result.put("Type", extendConfigEntity);
                    continue;
                }
                if (extendConfigEntity.getExtendType().equals("DeviceEntity" + "Object")) {
                    result.put("Object", extendConfigEntity);
                    continue;
                }
            }
        }

        return result;
    }

    public static <T> void extend(List<Map<String, Object>> mapList, List<BaseEntity> extendConfigEntityList, Class<T> clazz) {
        Map<String, ExtendConfigEntity> entityMap = getExtendConfigList(extendConfigEntityList, clazz);
        for (Map<String, Object> map : mapList) {
            extend(map, entityMap);

        }
    }

    public static <T> void extend(Map<String, Object> entityMap, Map<String, ExtendConfigEntity> extendMap) {
        if (entityMap == null || extendMap == null) {
            return;
        }


        // 按优先级进行添加：存量参数 > Object缺省值 > Type缺省值 >Globe缺省值
        extend(entityMap, extendMap, "Object");
        extend(entityMap, extendMap, "Type");
        extend(entityMap, extendMap, "Globe");

    }

    public static <T> void extend(Map<String, Object> entityMap, Map<String, ExtendConfigEntity> extendMap, String level) {
        if (extendMap == null || entityMap == null) {
            return;
        }

        Map<String, Object> extendParam = (Map<String, Object>) entityMap.get("extendParam");
        if (extendParam == null) {
            return;
        }

        ExtendConfigEntity extendConfigEntity = extendMap.get(level);
        if (extendConfigEntity == null) {
            return;
        }

        // 设备对象级别
        if (extendConfigEntity.getExtendType().equals("DeviceEntityObject")) {
            String deviceName = (String) entityMap.get(DeviceVOFieldConstant.field_device_name);

            if (extendConfigEntity.getExtendParam().getBinds().contains(deviceName)) {
                extendField(extendParam, extendConfigEntity.getExtendParam().getFields());
            }
            return;
        }

        // 设备类型级别
        if (extendConfigEntity.getExtendType().equals("DeviceEntityType")) {
            String deviceType = (String) entityMap.get(DeviceVOFieldConstant.field_device_type);

            if (extendConfigEntity.getExtendParam().getBinds().contains(deviceType)) {
                extendField(extendParam, extendConfigEntity.getExtendParam().getFields());
            }
            return;
        }

        // 设备全局级别
        if (extendConfigEntity.getExtendType().equals("DeviceEntityGlobe")) {
            extendField(extendParam, extendConfigEntity.getExtendParam().getFields());
            return;
        }
    }

    private static void extendField(Map<String, Object> extendParam, List<ExtendField> fields) {
        for (ExtendField field : fields) {
            if (!extendParam.containsKey(field.getFieldName())) {
                extendParam.put(field.getFieldName(), field.getDefaultValue());
            }
        }
    }
}
