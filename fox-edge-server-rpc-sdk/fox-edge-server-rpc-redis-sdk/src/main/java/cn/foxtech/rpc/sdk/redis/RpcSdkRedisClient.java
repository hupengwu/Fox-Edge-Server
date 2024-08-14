/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.rpc.sdk.redis;

import cn.foxtech.common.rpc.redis.channel.client.RedisListChannelClient;
import cn.foxtech.common.rpc.redis.device.client.RedisListDeviceClient;
import cn.foxtech.common.rpc.redis.manager.client.RedisListManagerClient;
import cn.foxtech.common.rpc.redis.persist.client.RedisListPersistClient;
import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RpcSdkRedisClient {
    @Autowired
    @Getter(value = AccessLevel.PUBLIC)
    private RedisListChannelClient channelClient;

    @Autowired
    @Getter(value = AccessLevel.PUBLIC)
    private RedisListDeviceClient deviceClient;

    @Autowired
    @Getter(value = AccessLevel.PUBLIC)
    private RedisListManagerClient managerClient;

    @Autowired
    @Getter(value = AccessLevel.PUBLIC)
    private RedisListPersistClient persistClient;
}
