/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.service.context;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.service.redis.BaseConsumerTypeNotify;
import cn.foxtech.common.utils.json.JsonUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class DeviceModelNotify implements BaseConsumerTypeNotify {
    private Map<String, Object> context;

    public void bindContext(Map<String, Object> context) {
        this.context = context;
    }

    /**
     * 通知变更
     *
     * @param addMap 增加
     * @param delSet 删除
     * @param mdyMap 修改
     */
    @Override
    public void notify(String entityType, long updateTime, Map<String, BaseEntity> addMap, Set<String> delSet, Map<String, BaseEntity> mdyMap) {
        Map<String, Object> deviceTemplateContext = this.context;


        for (String key : addMap.keySet()) {
            BaseEntity add = addMap.get(key);

            String json = JsonUtils.buildJsonWithoutException(add);
            Map<String, Object> map = JsonUtils.buildMapWithDefault(json, new ConcurrentHashMap<>());

            deviceTemplateContext.put(key, map);
        }
        for (String key : mdyMap.keySet()) {
            BaseEntity mdy = mdyMap.get(key);

            String json = JsonUtils.buildJsonWithoutException(mdy);
            Map<String, Object> map = JsonUtils.buildMapWithDefault(json, new ConcurrentHashMap<>());

            deviceTemplateContext.put(key, map);
        }
        for (String key : delSet) {
            deviceTemplateContext.remove(key);
        }
    }

}
