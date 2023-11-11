package cn.foxtech.trigger.service.trigger;

import cn.foxtech.common.entity.entity.TriggerConfigEntity;
import cn.foxtech.common.entity.service.redis.IBaseFinder;
import cn.foxtech.common.utils.DifferUtils;
import cn.foxtech.common.entity.entity.DeviceValueEntity;
import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 为DeviceValueEntity寻找一组匹配的触发器配置
 * 业务逻辑：
 * 1、根据设备值中的Device名称和类型，对触发器配置进行初步匹配
 * 2、匹配上该Device的触发器配置，再进行Object级别是否存在交叠，进行进一步筛选
 * 3、最终为这个设备找到每一个触发器配置，已经相关的对象，并保存在MAP中，后面别人可以消费这个MAP
 */
@Data
public class TriggerConfigFinder implements IBaseFinder {
    private final DeviceValueEntity entity;

    private Map<TriggerConfigEntity, Set<String>> map = new HashMap<>();

    public TriggerConfigFinder(DeviceValueEntity valueEntity) {
        this.entity = valueEntity;
    }

    public Set<TriggerConfigEntity> getTriggerConfig() {
        return this.map.keySet();
    }

    public boolean compareValue(Object value) {
        TriggerConfigEntity triggerConfigEntity = (TriggerConfigEntity) value;

        // 全局级别:需要测试
        if (TriggerConfigEntity.GlobalLevel.equals(triggerConfigEntity.getObjectRange())) {
            // 场景1：全体设备
            if (triggerConfigEntity.getDeviceType() == null || triggerConfigEntity.getDeviceType().isEmpty()) {
                return this.put(triggerConfigEntity);
            }

            // 场景2：指定类型的设备
            if (triggerConfigEntity.getDeviceType().equals(entity.getDeviceType())) {
                return this.put(triggerConfigEntity);
            }

            return false;
        }

        // 设备级别
        if (TriggerConfigEntity.DeviceLevel.equals(triggerConfigEntity.getObjectRange())) {
            // 场景3：指定名称的设备
            if (triggerConfigEntity.getDeviceName().equals(entity.getDeviceName())) {
                return this.put(triggerConfigEntity);
            }

            return false;
        }

        return false;
    }

    private boolean put(TriggerConfigEntity triggerConfigEntity) {
        // 没有配置具体Object，则是全体Object
        if (triggerConfigEntity.getObjectList().isEmpty()) {
            this.map.put(triggerConfigEntity, this.entity.getParams().keySet());
            return true;
        }

        // 配置了具体Object，则是指定Object的两者是否有交集
        if (!triggerConfigEntity.getObjectList().isEmpty()) {
            Set<String> addList = new HashSet<>();
            Set<String> delList = new HashSet<>();
            Set<String> eqlList = new HashSet<>();
            DifferUtils.differByValue(this.entity.getParams().keySet(), triggerConfigEntity.getObjectList(), addList, delList, eqlList);
            if (eqlList.isEmpty()) {
                return false;
            }

            this.map.put(triggerConfigEntity, eqlList);
            return true;
        }

        return false;
    }
}
