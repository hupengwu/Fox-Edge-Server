/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.service.redis;

import java.util.Map;
import java.util.Set;

public interface HashMapRedisNotify {
    /**
     * 通知变更
     *
     * @param addMap
     * @param delSet
     * @param mdyMap
     */
    void notify(String entityType, long updateTime, Map<String, Map<String, Object>> addMap, Set<String> delSet, Map<String, Map<String, Object>> mdyMap);
}
