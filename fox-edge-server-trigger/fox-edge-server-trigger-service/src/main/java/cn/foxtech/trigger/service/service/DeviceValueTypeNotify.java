package cn.foxtech.trigger.service.service;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.service.redis.BaseConsumerTypeNotify;
import cn.foxtech.trigger.service.trigger.TriggerValueUpdater;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
public class DeviceValueTypeNotify implements BaseConsumerTypeNotify {
    private static final Logger logger = Logger.getLogger(DeviceValueTypeNotify.class);

    @Autowired
    private TriggerValueUpdater triggerValueUpdater;


    /**
     * 通知变更
     *
     * @param addMap 增加
     * @param delSet 删除
     * @param mdyMap 修改
     */
    @Override
    public void notify(String entityType, long updateTime, Map<String, BaseEntity> addMap, Set<String> delSet, Map<String, BaseEntity> mdyMap) {
        for (String key : addMap.keySet()) {
            try {
                this.triggerValueUpdater.update(addMap.get(key));
            } catch (Exception e) {
                logger.warn(e);
            }
        }
        for (String key : mdyMap.keySet()) {
            try {
                this.triggerValueUpdater.update(mdyMap.get(key));
            } catch (Exception e) {
                logger.warn(e);
            }
        }
        for (String key : delSet) {
        }
    }

}
