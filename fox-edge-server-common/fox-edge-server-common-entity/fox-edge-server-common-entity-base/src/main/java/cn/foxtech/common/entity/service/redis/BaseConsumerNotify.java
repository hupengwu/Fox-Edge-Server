package cn.foxtech.common.entity.service.redis;

import cn.foxtech.common.entity.entity.BaseEntity;

import java.util.Map;
import java.util.Set;

/**
 * 通知
 */
public interface BaseConsumerNotify {
    /**
     * 通知变更
     *
     * @param addMap
     * @param delSet
     * @param mdyMap
     */
    void notify(String entityType, long updateTime, Map<String, BaseEntity> addMap, Set<String> delSet, Map<String, BaseEntity> mdyMap);
}
