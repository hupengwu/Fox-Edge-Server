/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.manager;

import cn.foxtech.common.domain.constant.RedisStatusConstant;
import cn.foxtech.common.status.ServiceStatus;
import cn.foxtech.common.utils.redis.status.RedisStatusConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 初始化配置的通知
 *
 * 很多服务需要通知System-Manage服务，把自己的一些初始化配置发布到configEntity表中，作为自己的初始化配置，以及给用户示范配置
 * 通知到ProxyCloud服务。
 * 该模块会将数据写入指定的redis缓存中，System-Manage会到指定的redis缓冲去读取这些配置数据
 */
@Component
public class InitialConfigNotifier {

    @Autowired
    private ServiceStatus serviceStatus;

    public List<Map<String, Object>> getConfigEntity() {
        List<Map<String, Object>> result = new ArrayList<>();

        for (Object statusValue : this.serviceStatus.getConsumerData().values()) {
            Map<String, Object> value = (Map<String, Object>)  statusValue;
            if (value == null) {
                continue;
            }

            Map<String, Object> publishEntity = (Map<String, Object>) value.get(RedisStatusConstant.field_config_entity);
            if (publishEntity == null) {
                continue;
            }

            result.add(value);
        }

        return result;
    }

    public void notifyRegisterConfig(String configName, Map<String, Object> config) {
        Map<String, Object> configEntity = (Map<String, Object>) this.serviceStatus.getProducerData().computeIfAbsent(RedisStatusConstant.field_config_entity, k -> new HashMap<>());
        Map<String, Object> configItem = (Map<String, Object>) configEntity.computeIfAbsent(configName, k -> new HashMap<>());
        configItem.putAll(config);
    }
}
