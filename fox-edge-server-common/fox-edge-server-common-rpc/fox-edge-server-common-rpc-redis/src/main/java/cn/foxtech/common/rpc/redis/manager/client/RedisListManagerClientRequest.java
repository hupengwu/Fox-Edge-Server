package cn.foxtech.common.rpc.redis.manager.client;

import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * Restful风格的可靠性列表：无应答，也就是不会对发送者进行回复
 * <p>
 * 接收者： manage
 * 发送者： 其他服务
 */
@Component
public class RedisListManagerClientRequest extends RedisLoggerService {
    @Getter
    private final String key = "fox.edge.list:manager:restful:message:request";

    /**
     * 向管理服务，推送restful风格的消息
     *
     * @param uuid   uuid，用于标识唯一性的报文
     * @param uri    uri ，例如："/kernel/manager/device/entity"
     * @param method 方法，例如："post"
     * @param data   数据，对应body参数和url上的查询参数
     */
    public void pushRequest(String uuid, String uri, String method, Object data) {
        RestFulRequestVO requestVO = new RestFulRequestVO();
        requestVO.setUuid(uuid);
        requestVO.setUri(uri);
        requestVO.setMethod(method);
        requestVO.setData(data);

        this.pushRequest(requestVO);
    }

    public void pushRequest(RestFulRequestVO requestVO) {
        super.push(requestVO);
    }

    public void pushRequest(String uri, String method, Object data) {
        this.pushRequest(null, uri, method, data);
    }
}