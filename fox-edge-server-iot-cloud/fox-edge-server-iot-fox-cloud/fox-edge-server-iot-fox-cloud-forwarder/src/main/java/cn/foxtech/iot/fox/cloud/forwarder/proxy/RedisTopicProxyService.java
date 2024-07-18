package cn.foxtech.iot.fox.cloud.forwarder.proxy;

import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.domain.vo.RestfulLikeRequestVO;
import cn.foxtech.common.domain.vo.RestfulLikeRespondVO;
import cn.foxtech.common.rpc.redis.device.client.RedisListDeviceClient;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.device.domain.constant.DeviceMethodVOFieldConstant;
import cn.foxtech.device.domain.vo.TaskRequestVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * Redis Topic API接口的服务转发代理，比如Device和Channel
 */
@Component
public class RedisTopicProxyService {
    private static final Logger logger = Logger.getLogger(RedisTopicProxyService.class);

    private static final int extra_timeout_channel = 3;
    private static final int extra_timeout_device = extra_timeout_channel + 3;


    @Autowired
    private RedisListDeviceClient deviceClient;

    /**
     * 检查：是不是HttpProxy的资源
     *
     * @param resource
     * @return
     */
    public boolean isRedisResource(String resource) {
        return this.getTopicHead(resource) != null;
    }

    /**
     * 查询host
     *
     * @param resource
     * @return
     */
    private String getTopicHead(String resource) {

        if (resource.startsWith("/" + RedisTopicConstant.model_device + "/")) {
            return resource.substring(("/" + RedisTopicConstant.model_device + "/").length());
        }
        if (resource.startsWith("/" + RedisTopicConstant.model_channel + "/")) {
            return resource.substring(("/" + RedisTopicConstant.model_channel + "/").length());
        }

        return null;
    }

    /**
     * 执行操作
     *
     * @param requestVO
     * @return
     * @throws IOException
     */
    public RestfulLikeRespondVO executeDevice(RestfulLikeRequestVO requestVO) throws InterruptedException, IOException {
        String topicRequest = this.getTopicHead(requestVO.getResource());
        if (topicRequest == null) {
            throw new ServiceException("尚未支持的方法");
        }

        Map<String, Object> request = (Map<String, Object>) requestVO.getBody();


        Integer timeout = (Integer) request.get(DeviceMethodVOFieldConstant.field_timeout);

        //填写UID，从众多方便返回的数据中，识别出来对应的返回报文
        String key = requestVO.getUuid();
        request.put(DeviceMethodVOFieldConstant.field_uuid, requestVO.getUuid());


        // 发送数据
        this.deviceClient.pushDeviceRequest(TaskRequestVO.buildRequestVO(request));

        logger.info(topicRequest + ":" + request);

        // 等待消息的到达：根据动态key
        TaskRespondVO taskRespondVO = this.deviceClient.getDeviceRespond(key, this.buildTimeout(requestVO.getResource(), timeout));
        if (taskRespondVO == null) {
            throw new ServiceException("设备响应超时！");
        }

        RestfulLikeRespondVO respondVO = new RestfulLikeRespondVO();
        respondVO.bindVO(requestVO);
        respondVO.setBody(taskRespondVO);
        return respondVO;
    }

    private int buildTimeout(String resourceType, int timeout) {
        if (resourceType.startsWith("/" + RedisTopicConstant.model_channel + "/")) {
            return timeout + extra_timeout_channel;
        } else if (resourceType.startsWith("/" + RedisTopicConstant.model_device + "/")) {
            return timeout + extra_timeout_device;
        } else {
            throw new ServiceException("url必須前缀为:" + "/" + RedisTopicConstant.model_channel + "/" + "或" + "/" + RedisTopicConstant.model_device + "/");
        }
    }
}
