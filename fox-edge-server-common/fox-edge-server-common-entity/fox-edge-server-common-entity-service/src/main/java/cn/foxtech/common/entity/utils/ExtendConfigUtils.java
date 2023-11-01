package cn.foxtech.common.entity.utils;

import cn.foxtech.common.entity.constant.DeviceMapperVOFieldConstant;
import cn.foxtech.common.entity.constant.DeviceVOFieldConstant;
import cn.foxtech.common.entity.entity.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtendConfigUtils {
    public static <T> Map<String, ExtendConfigEntity> getExtendConfigList(List<BaseEntity> extendConfigEntityList, Class<T> clazz) {
        Map<String, ExtendConfigEntity> result = new HashMap<>();
        for (BaseEntity entity : extendConfigEntityList) {
            ExtendConfigEntity extendConfigEntity = (ExtendConfigEntity) entity;
            if (clazz.equals(DeviceEntity.class) || clazz.equals(DeviceMapperEntity.class)) {
                if (extendConfigEntity.getExtendType().equals(clazz.getSimpleName() + "Globe")) {
                    result.put("Globe", extendConfigEntity);
                    continue;
                }
                if (extendConfigEntity.getExtendType().equals(clazz.getSimpleName() + "Type")) {
                    result.put("Type", extendConfigEntity);
                    continue;
                }
                if (extendConfigEntity.getExtendType().equals(clazz.getSimpleName() + "Object")) {
                    result.put("Object", extendConfigEntity);
                    continue;
                }
            }
        }

        return result;
    }

    public static <T> void extendMapList(List<Map<String, Object>> mapList, List<BaseEntity> extendConfigEntityList, Class<T> clazz) {
        Map<String, ExtendConfigEntity> entityMap = getExtendConfigList(extendConfigEntityList, clazz);
        for (Map<String, Object> map : mapList) {
            extendMapList(map, entityMap);

        }
    }

    public static <T> void extendEntityList(List<BaseEntity> entityList, List<BaseEntity> extendConfigEntityList, Class<T> clazz) {
        Map<String, ExtendConfigEntity> entityMap = getExtendConfigList(extendConfigEntityList, clazz);
        for (BaseEntity entity : entityList) {
            extendMapList(entity, entityMap);

        }
    }

    public static <T> void extendMapList(Object entity, Map<String, ExtendConfigEntity> extendMap) {
        if (entity == null || extendMap == null) {
            return;
        }

        // 按优先级进行添加：存量参数 > Object缺省值 > Type缺省值 >Globe缺省值
        extendMapList(entity, extendMap, "Object");
        extendMapList(entity, extendMap, "Type");
        extendMapList(entity, extendMap, "Globe");

    }

    public static <T> void extendMapList(Object entity, Map<String, ExtendConfigEntity> extendMap, String level) {
        if (extendMap == null || entity == null) {
            return;
        }

        Map<String, Object> extendParam = null;
        if (entity instanceof Map) {
            extendParam = (Map<String, Object>) ((Map<String, Object>) entity).get("extendParam");
        }
        if (entity instanceof DeviceEntity) {
            extendParam = ((DeviceEntity) entity).getExtendParam();
        }
        if (entity instanceof DeviceMapperEntity) {
            extendParam = ((DeviceMapperEntity) entity).getExtendParam();
        }


        if (extendParam == null) {
            return;
        }

        ExtendConfigEntity extendConfigEntity = extendMap.get(level);
        if (extendConfigEntity == null) {
            return;
        }

        // 设备对象级别
        if (extendConfigEntity.getExtendType().equals("DeviceEntityObject")) {
            String deviceName = null;
            if (entity instanceof Map) {
                deviceName = (String) ((Map<String, Object>) entity).get(DeviceVOFieldConstant.field_device_name);
            }
            if (entity instanceof DeviceEntity) {
                deviceName = ((DeviceEntity) entity).getDeviceName();
            }
            if (deviceName == null) {
                return;
            }

            if (extendConfigEntity.getExtendParam().getBinds().contains(deviceName)) {
                extendField(extendParam, extendConfigEntity.getExtendParam().getFields());
            }
            return;
        }

        // 设备类型级别
        if (extendConfigEntity.getExtendType().equals("DeviceEntityType")) {
            String deviceType = null;
            if (entity instanceof Map) {
                deviceType = (String) ((Map<String, Object>) entity).get(DeviceVOFieldConstant.field_device_type);
            }
            if (entity instanceof DeviceEntity) {
                deviceType = ((DeviceEntity) entity).getDeviceType();
            }
            if (deviceType == null) {
                return;
            }


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

        // 设备类型级别
        if (extendConfigEntity.getExtendType().equals("DeviceMapperEntityType")) {
            String deviceType = null;
            if (entity instanceof Map) {
                deviceType = (String) ((Map<String, Object>) entity).get(DeviceMapperVOFieldConstant.field_device_type);
            }
            if (entity instanceof DeviceMapperEntity) {
                deviceType = ((DeviceMapperEntity) entity).getDeviceType();
            }
            if (deviceType == null) {
                return;
            }

            if (extendConfigEntity.getExtendParam().getBinds().contains(deviceType)) {
                extendField(extendParam, extendConfigEntity.getExtendParam().getFields());
            }
            return;
        }

        // 设备全局级别
        if (extendConfigEntity.getExtendType().equals("DeviceMapperEntityGlobe")) {
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
