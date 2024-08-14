/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.rpc.redis.channel.server;

import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisListChannelServer {
    @Autowired
    private RedisListChannelServerRequest channelRequest;

    @Autowired
    private RedisListChannelServerRespond channelRespond;

    @Autowired
    private RedisListChannelServerReport channelReport;

    public void setChannelType(String channelType){
        this.channelRequest.setChannelType(channelType);
        this.channelRespond.setChannelType(channelType);
    }

    public ChannelRequestVO popChannelRequest(long timeout, TimeUnit unit) {
        return this.channelRequest.pop(timeout, unit);
    }

    public void pushChannelRespond(String uuid, ChannelRespondVO respondVO) {
        this.channelRespond.set(uuid, respondVO);
    }

    public void pushChannelReport(ChannelRespondVO respondVO) {
        this.channelReport.push(respondVO);
    }

}
