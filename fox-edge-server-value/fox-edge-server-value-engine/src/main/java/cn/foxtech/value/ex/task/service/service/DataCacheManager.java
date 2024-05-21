package cn.foxtech.value.ex.task.service.service;

import cn.foxtech.common.entity.entity.*;
import cn.foxtech.common.entity.service.redis.RedisReader;
import cn.foxtech.common.entity.service.redis.RedisWriter;
import cn.foxtech.value.ex.task.service.entity.DataTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataCacheManager {
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private DataTaskManager dataTaskManager;

    public Map<String, DeviceValueExObjectValue> append(DeviceValueEntity deviceValueEntity) {
        RedisReader redisReader = this.entityManageService.getRedisReader(DeviceValueExCacheEntity.class);
        RedisWriter redisWriter = this.entityManageService.getRedisWriter(DeviceValueExCacheEntity.class);


        try {
            // 从redis中读取缓存数据
            DeviceValueExCacheEntity entity = (DeviceValueExCacheEntity) redisReader.readEntity(deviceValueEntity.makeServiceKey());
            if (entity == null) {
                return null;
            }

            // 取出其中的缓存数据列表
            Map<String, DeviceValueExObjectValue> deviceMap = entity.getParams();

            // 把数据追加到末尾
            boolean updated = false;
            for (String key : deviceValueEntity.getParams().keySet()) {
                DeviceObjectValue deviceObjectValue = deviceValueEntity.getParams().get(key);

                DeviceValueExObjectValue valueExObjectValue = deviceMap.get(key);
                if (valueExObjectValue == null) {
                    continue;
                }

                int cacheSize = valueExObjectValue.getCacheSize();
                if (cacheSize > 10) {
                    cacheSize = 10;
                }

                valueExObjectValue.getValues().add(deviceObjectValue);
                while (valueExObjectValue.getValues().size() > cacheSize) {
                    valueExObjectValue.getValues().remove(0);
                }
                updated = true;
            }

            if (!updated) {
                return null;
            }

            entity.setParams(deviceMap);
            redisWriter.writeEntity(entity);

            return deviceMap;

        } catch (Exception e) {
            return null;
        }
    }

    public void reset() {
        // 分类：根据设备类型进行分类
        Map<String, List<DeviceEntity>> deviceListMap = this.buildDeviceEntity();

        // 分类：根据设备类型进行分类
        Map<String, List<String>> mapperMap = this.buildDeviceMapper();

        // 根据每个对象，生成缓存
        for (String deviceTypeKey : mapperMap.keySet()) {
            List<String> objectNames = mapperMap.get(deviceTypeKey);

            // 获得同类的设备
            List<DeviceEntity> deviceList = deviceListMap.get(deviceTypeKey);
            if (deviceList == null) {
                continue;
            }

            // 为每一个设备，根据类型进行初始化缓存
            this.restCache(deviceList, objectNames);
        }
    }

    private void restCache(List<DeviceEntity> deviceList, List<String> objectNames) {
        RedisReader redisReader = this.entityManageService.getRedisReader(DeviceValueExCacheEntity.class);
        RedisWriter redisWriter = this.entityManageService.getRedisWriter(DeviceValueExCacheEntity.class);

        for (DeviceEntity deviceEntity : deviceList) {
            // 构造相关的任务
            Map<String, DataTask> taskMap = this.dataTaskManager.buildTaskMap(deviceEntity);

            // 构造相关的缓存结构
            Map<String, DeviceValueExObjectValue> params = this.buildDeviceCache(taskMap, objectNames);

            try {
                DeviceValueExCacheEntity entity = new DeviceValueExCacheEntity();
                entity.setId(deviceEntity.getId());
                entity.setManufacturer(deviceEntity.getManufacturer());
                entity.setDeviceType(deviceEntity.getDeviceType());
                entity.setDeviceName(deviceEntity.getDeviceName());
                entity.setParams(params);

                // 读取redis缓存数据
                DeviceValueExCacheEntity exist = (DeviceValueExCacheEntity) redisReader.readEntity(deviceEntity.makeServiceKey());

                // 场景1：数据不存在于redis，此时要刷入数据
                if (exist == null) {
                    redisWriter.writeEntity(entity);
                    continue;
                }

                // 场景2：发生了结构性变化，此时重新刷入数据
                if (!entity.getParams().keySet().equals(exist.getParams().keySet())) {
                    redisWriter.writeEntity(entity);
                    continue;
                }

                // 场景3：缓存大小发生变化，此时重新刷入数据
                for (String key : entity.getParams().keySet()) {
                    DeviceValueExObjectValue entityValue = entity.getParams().get(key);
                    DeviceValueExObjectValue existValue = exist.getParams().get(key);
                    if (entityValue.getCacheSize() != existValue.getCacheSize()) {
                        while (entityValue.getValues().size() > entityValue.getCacheSize()) {
                            entityValue.getValues().remove(0);
                        }
                        redisWriter.writeEntity(entity);
                        break;
                    }
                }

            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    private Map<String, DeviceValueExObjectValue> buildDeviceCache(Map<String, DataTask> taskMap, List<String> objectNames) {
        Map<String, DeviceValueExObjectValue> deviceMap = new HashMap<>();

        for (String objectName : objectNames) {
            // 确认最大的MAP
            int cacheMaxSize = this.dataTaskManager.getCacheMaxSize(taskMap, objectName);
            if (cacheMaxSize < 0) {
                deviceMap.remove(objectName);
                continue;
            }

            // 初始化
            DeviceValueExObjectValue valueExObjectValue = deviceMap.get(objectName);
            if (valueExObjectValue == null) {
                valueExObjectValue = new DeviceValueExObjectValue();
                deviceMap.put(objectName, valueExObjectValue);
            }

            // 清空多余的旧数据
            valueExObjectValue.setCacheSize(cacheMaxSize);
            while (valueExObjectValue.getValues().size() > cacheMaxSize) {
                valueExObjectValue.getValues().remove(0);
            }
        }

        return deviceMap;

    }

    private Map<String, List<DeviceEntity>> buildDeviceEntity() {
        Map<String, List<DeviceEntity>> result = new HashMap<>();
        List<BaseEntity> entityList = this.entityManageService.getEntityList(DeviceEntity.class);
        for (BaseEntity entity : entityList) {
            DeviceEntity deviceEntity = (DeviceEntity) entity;

            String deviceTypeKey = this.makeDeviceTypeKey(deviceEntity.getManufacturer(), deviceEntity.getDeviceType());
            List<DeviceEntity> list = result.computeIfAbsent(deviceTypeKey, k -> new ArrayList<>());
            list.add(deviceEntity);
        }

        return result;
    }

    private Map<String, List<String>> buildDeviceMapper() {
        Map<String, List<String>> result = new HashMap<>();
        List<BaseEntity> entityList = this.entityManageService.getEntityList(DeviceMapperEntity.class);
        for (BaseEntity entity : entityList) {
            DeviceMapperEntity mapperEntity = (DeviceMapperEntity) entity;

            String deviceTypeKey = this.makeDeviceTypeKey(mapperEntity.getManufacturer(), mapperEntity.getDeviceType());
            List<String> list = result.computeIfAbsent(deviceTypeKey, k -> new ArrayList<>());
            list.add(mapperEntity.getObjectName());
        }

        return result;
    }

    private String makeDeviceTypeKey(String manufacturer, String deviceType) {
        return manufacturer + "|" + deviceType;
    }

}
