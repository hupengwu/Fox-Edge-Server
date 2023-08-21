package cn.foxtech.trigger.service.trigger;

import cn.foxtech.common.entity.entity.TriggerConfigEntity;
import cn.foxtech.trigger.logic.common.ObjectValue;
import cn.foxtech.common.entity.entity.DeviceObjectValue;
import cn.foxtech.common.entity.entity.DeviceValueEntity;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 触发器缓存值管理器
 * 业务逻辑：
 * 1、前期DeviceValue通过Finder找到设备的触发器配置后，判定缓存数据列表的最大深度
 * 2、将DeviceValue追加到对应的Device/Object的ValueList中，超出部分剔除旧数据
 * 3、后期，别人可以根据Device/Object消费ValueList
 */
@Component
public class TriggerValueManager {
    public static final int MAX_SIZE = 10;
    /**
     * deviceName->objectName->valueList;
     */
    private final Map<String, Map<String, List<ObjectValue>>> triggerValues = new ConcurrentHashMap<>();

    /**
     * 根据触发器配置和设备名称，查询触发器需要的对象级TriggerValue
     *
     * @param deviceName          设备名称
     * @param triggerConfigEntity 触发器配置
     * @return objectName-ValueList
     */
    public Map<String, List<ObjectValue>> selectDeviceTriggerValue(String deviceName, TriggerConfigEntity triggerConfigEntity) {

        // 获得设备的触发器缓存值
        Map<String, List<ObjectValue>> deviceTriggerValue = selectDeviceTriggerValue(deviceName);

        Map<String, List<ObjectValue>> result = new HashMap<>();

        // 场景1：为空时，指的是全体数据
        if (triggerConfigEntity.getObjectList().isEmpty()) {
            result.putAll(deviceTriggerValue);
            return result;
        }

        // 场景2：非空时，指的是指定数据
        for (String objectName : triggerConfigEntity.getObjectList()) {
            List<ObjectValue> values = deviceTriggerValue.get(objectName);
            if (values == null || values.isEmpty()) {
                continue;
            }

            result.put(objectName, values);
        }


        return result;
    }

    private List<ObjectValue> selectObjectTriggerValue(String deviceName, String objectName) {
        Map<String, List<ObjectValue>> deviceTriggerValue = selectDeviceTriggerValue(deviceName);

        // 取出设备级的缓存数据
        List<ObjectValue> objectTriggerValue = deviceTriggerValue.get(objectName);
        if (objectTriggerValue == null) {
            objectTriggerValue = new CopyOnWriteArrayList<>();
            deviceTriggerValue.put(objectName, objectTriggerValue);
        }

        return objectTriggerValue;
    }

    /**
     * 查找设备级别的TriggerValue缓存数据
     *
     * @param deviceName 设备名称
     * @return 设备的TriggerValue缓存数据
     */
    private Map<String, List<ObjectValue>> selectDeviceTriggerValue(String deviceName) {
        // 取出设备级的缓存数据
        Map<String, List<ObjectValue>> deviceTriggerValue = this.triggerValues.get(deviceName);
        if (deviceTriggerValue == null) {
            deviceTriggerValue = new ConcurrentHashMap<>();
            this.triggerValues.put(deviceName, deviceTriggerValue);
        }

        return deviceTriggerValue;
    }

    /**
     * 合并对象列表
     *
     * @param objectNameList 一批对象名称列表
     * @return 队列名称
     */
    private Set<String> mergeObjectName(Collection<Set<String>> objectNameList) {
        Set<String> result = new HashSet<>();
        for (Set<String> objectNames : objectNameList) {
            result.addAll(objectNames);
        }

        return result;
    }

    /**
     * 根据触发器对象的信息，计算每个对象的队列深度
     *
     * @param objectNameList 对象名称列表
     * @param entityList     触发器配置列表
     * @return objectName->queueDeep
     */
    private Map<String, Integer> maxQueueDeep(Set<String> objectNameList, Set<TriggerConfigEntity> entityList) {
        Map<String, Integer> queueDeep = new HashMap<>();
        for (String objectName : objectNameList) {
            int maxSize = 1;

            for (TriggerConfigEntity entity : entityList) {
                if (!entity.getObjectList().contains(objectName)) {
                    continue;
                }

                if (entity.getQueueDeep() > maxSize) {
                    maxSize = entity.getQueueDeep();
                }
            }

            if (maxSize > MAX_SIZE) {
                maxSize = MAX_SIZE;
            }

            queueDeep.put(objectName, maxSize);
        }

        return queueDeep;
    }

    /**
     * 追加数据
     *
     * @param finder 携带了DeviceValueEntity和触发器配置条件的信息，它通过foreachFinder后来获得的
     */
    public void appendValue(TriggerConfigFinder finder) {
        // 检查查找结果
        if (finder.getMap().isEmpty()) {
            return;
        }

        // 将查找到的多个触发器的交集对象，进行合并处理
        Set<String> objectNameList = this.mergeObjectName(finder.getMap().values());

        // 计算镜像数据的深度
        Map<String, Integer> queueDeep = this.maxQueueDeep(objectNameList, finder.getMap().keySet());

        // 将到达的原生数据，追加到触发器需要的缓存数据中
        DeviceValueEntity deviceValueEntity = finder.getEntity();
        for (String objectName : objectNameList) {
            DeviceObjectValue rawValue = deviceValueEntity.getParams().get(objectName);
            List<ObjectValue> triggerValue = this.selectObjectTriggerValue(deviceValueEntity.getDeviceName(), objectName);

            // 追加到尾部
            ObjectValue objectValue = new ObjectValue();
            objectValue.setValue(rawValue.getValue());
            objectValue.setTime(rawValue.getTime());
            triggerValue.add(objectValue);

            // 超过深度，就删除头部的旧数据
            if (triggerValue.size() > queueDeep.get(objectName)) {
                triggerValue.remove(0);
            }
        }
    }
}
