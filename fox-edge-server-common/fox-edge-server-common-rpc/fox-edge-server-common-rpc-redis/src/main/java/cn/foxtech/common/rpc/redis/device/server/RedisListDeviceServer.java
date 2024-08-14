/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.rpc.redis.device.server;

import cn.foxtech.device.domain.vo.TaskRequestVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisListDeviceServer {
    @Autowired
    private RedisListDeviceServerRequest deviceRequest;

    @Autowired
    private RedisListDeviceServerRespond deviceRespond;

    @Autowired
    private RedisListDeviceServerReport deviceReport;

    public TaskRequestVO popDeviceRequest(long timeout, TimeUnit unit) {
        return this.deviceRequest.pop(timeout, unit);
    }

    public void pushDeviceRespond(String uuid, TaskRespondVO respondVO) {
        this.deviceRespond.set(uuid, respondVO);
    }

    public void pushDeviceReport(TaskRespondVO respondVO) {
        this.deviceReport.push(respondVO);
    }

}
