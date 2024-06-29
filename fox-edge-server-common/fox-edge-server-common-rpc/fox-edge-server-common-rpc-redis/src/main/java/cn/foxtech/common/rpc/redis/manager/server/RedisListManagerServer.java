package cn.foxtech.common.rpc.redis.manager.server;


import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.domain.vo.RestFulRespondVO;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisListManagerServer {
    @Autowired
    private RedisListManagerServerRequest managerRequest;

    @Autowired
    private RedisListManagerServerRespond managerRespond;

    public RestFulRequestVO popRequest(long timeout, TimeUnit unit) {
        return this.managerRequest.popRequest(timeout, unit);
    }

    public void pushRespond(String uuid, RestFulRespondVO value) {
        if (uuid == null || uuid.isEmpty()) {
            throw new ServiceException("参数缺失：uuid 不能为空");
        }

        this.managerRespond.pushRespond(uuid, value);
    }
}
