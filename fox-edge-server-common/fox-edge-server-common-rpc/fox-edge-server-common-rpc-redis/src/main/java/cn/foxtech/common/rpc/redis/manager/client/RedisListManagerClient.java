package cn.foxtech.common.rpc.redis.manager.client;

import cn.foxtech.common.domain.vo.RestFulRespondVO;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RedisListManagerClient {
    @Autowired
    private RedisListManagerClientRequest managerRequest;

    @Autowired
    private RedisListManagerClientRespond managerRespond;

    /**
     * 向管理服务，推送restful风格的消息
     *
     * @param uuid   uuid，用于标识唯一性的报文
     * @param uri    uri ，例如："/kernel/manager/device/entity"
     * @param method 方法，例如："post"
     * @param data   数据，对应body参数和url上的查询参数
     */
    public void pushRequest(String uuid, String uri, String method, Object data) {
        this.managerRequest.pushRequest(uuid, uri, method, data);
    }

    public void pushRequest(String uri, String method, Object data) {
        this.managerRequest.pushRequest(uri, method, data);
    }

    /**
     * 查询响应
     *
     * @param uuid
     * @param timeout
     */
    public RestFulRespondVO queryRespond(String uuid, long timeout) {
        if (uuid == null || uuid.isEmpty()) {
            throw new ServiceException("参数缺失：uuid 不能为空");
        }

        return this.managerRespond.queryRespond(uuid, timeout);
    }
}
