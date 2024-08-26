/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.rpc.redis.persist.client;

import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.domain.vo.RestFulRespondVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisListPersistClient {
    @Autowired
    private RedisListPersistClientManageRespond manageRespond;

    @Autowired
    private RedisListPersistClientManageRequest manageRequest;

    @Autowired
    private RedisListPersistClientRecordRequest recordRequest;

    @Autowired
    private RedisListPersistClientValueRequest valueRequest;

    public RestFulRespondVO popManageRespond(long timeout, TimeUnit unit) {
        return this.manageRespond.popRespond(timeout, unit);
    }


    public void pushManageRequest(RestFulRequestVO requestVO) {
        this.manageRequest.push(requestVO);
    }

    public void pushRecordRequest(TaskRespondVO value) {
        this.recordRequest.push(value);
    }

    public void pushValueRequest(TaskRespondVO value) {
        this.valueRequest.push(value);
    }

    public boolean isValueRequestBlock() {
        return this.valueRequest.isBlock();
    }

    public boolean isValueRequestBusy(int percentage) {
        return this.valueRequest.isBusy(percentage);
    }
}
