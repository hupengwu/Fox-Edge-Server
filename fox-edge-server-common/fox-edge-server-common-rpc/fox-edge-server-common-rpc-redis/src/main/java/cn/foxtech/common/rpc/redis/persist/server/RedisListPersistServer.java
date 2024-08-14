/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.rpc.redis.persist.server;

import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.domain.vo.RestFulRespondVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisListPersistServer {
    @Autowired
    private RedisListPersistServerRecordRequest recordRequest;

    @Autowired
    private RedisListPersistServerValueRequest valueRequest;

    @Autowired
    private RedisListPersistServerManageRequest manageRequest;

    @Autowired
    private RedisListPersistServerManageRespond manageRespond;


    public Object popRecordRequest(long timeout, TimeUnit unit) {
        return this.recordRequest.pop(timeout, unit);
    }

    public Object popValueRequest(long timeout, TimeUnit unit) {
        return this.valueRequest.pop(timeout, unit);
    }

    public RestFulRequestVO popManageRequest(long timeout, TimeUnit unit) {
        return this.manageRequest.popRequest(timeout, unit);
    }

    public void pushManageRequest(RestFulRespondVO respondVO) {
        this.manageRespond.push(respondVO);
    }


}
