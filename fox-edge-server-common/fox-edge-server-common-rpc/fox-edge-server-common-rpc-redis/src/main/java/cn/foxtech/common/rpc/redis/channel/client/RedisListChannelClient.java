/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.rpc.redis.channel.client;

import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisListChannelClient {
    @Autowired
    private RedisListChannelClientRespond channelRespond;

    @Autowired
    private RedisListChannelClientRequest channelRequest;

    @Autowired
    private RedisListChannelClientReport channelReport;

    public ChannelRespondVO getChannelRespond(String channelType, String uuid, long timeout) {
        return this.channelRespond.get(channelType, uuid, timeout);
    }


    public void pushChannelRequest(String channelType, ChannelRequestVO value) {
        this.channelRequest.push(channelType, value);
    }

    public ChannelRespondVO popChannelReport(long timeout, TimeUnit unit) {
        return this.channelReport.pop(timeout, unit);
    }


}
