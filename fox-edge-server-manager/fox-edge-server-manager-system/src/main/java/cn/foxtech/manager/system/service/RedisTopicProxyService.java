package cn.foxtech.manager.system.service;

import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.domain.constant.RedisStatusConstant;
import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.status.ServiceStatus;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.redis.topic.service.RedisTopicPublisher;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.device.domain.constant.DeviceMethodVOFieldConstant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
public class RedisTopicProxyService {
    private static final Logger logger = Logger.getLogger(RedisTopicProxyService.class);
    private static final String channel_head = "/proxy/redis/topic/channel/";
    private static final String device_head = "/proxy/redis/topic/device/";

    private static final int extra_timeout_channel = 3000;
    private static final int extra_timeout_device = extra_timeout_channel + 3000;


    @Autowired
    private RedisTopicPublisher publisher;

    @Autowired
    private ServiceStatus serviceStatus;

    public String execute(String url, String queryString, String body, RequestMethod method) throws InterruptedException, IOException {
        if (url.startsWith(channel_head)) {
            return this.executeChannel(url, body, method);
        }
        if (url.startsWith(device_head)) {
            return this.executeDevice(url, body, method);
        }

        throw new ServiceException("url必須前缀为:" + channel_head + "或" + device_head);
    }

    public String executeChannel(String url, String body, RequestMethod method) throws InterruptedException, IOException {
        String topicRequest = url.substring(channel_head.length());
        String channelType = topicRequest.substring(RedisTopicConstant.topic_channel_request.length());

        // 检查：目标服务是否已经启动
        if (!this.serviceStatus.isActive(RedisStatusConstant.value_model_type_channel, channelType, 60 * 1000)) {
            throw new ServiceException("Channel服务尚未运行：" + channelType);
        }

        Map<String, Object> request = JsonUtils.buildObject(body, Map.class);


        Integer timeout = (Integer) request.get(DeviceMethodVOFieldConstant.field_timeout);

        //填写UID，从众多方便返回的数据中，识别出来对应的返回报文
        String key = (String) request.get(DeviceMethodVOFieldConstant.field_uuid);
        if (MethodUtils.hasEmpty(key)) {
            key = UUID.randomUUID().toString().replace("-", "");
            request.put(DeviceMethodVOFieldConstant.field_uuid, key);
        }

        // 要求channel服务把数据发挥到这个topic之中
        String route = (String) request.get(DeviceMethodVOFieldConstant.field_route);
        if (MethodUtils.hasEmpty(route)) {
            request.put(DeviceMethodVOFieldConstant.field_route, RedisTopicConstant.topic_channel_respond + RedisTopicConstant.model_manager);
        }


        // 重新打包数据
        body = JsonUtils.buildJson(request);


        // 重置信号
        SyncFlagObjectMap.inst().reset(key);

        // 发送数据
        publisher.sendMessage(topicRequest, body);

        logger.info(topicRequest + ":" + body);

        // 等待消息的到达：根据动态key
        ChannelRespondVO respond = (ChannelRespondVO) SyncFlagObjectMap.inst().waitDynamic(key, timeout + extra_timeout_channel);
        if (respond == null) {
            throw new ServiceException("设备响应超时！");
        }

        return JsonUtils.buildJson(respond);
    }

    public String executeDevice(String url, String body, RequestMethod method) throws InterruptedException, IOException {
        String topicRequest = url.substring(device_head.length());

        // 检查：目标服务是否已经启动
        if (!this.serviceStatus.isActive(RedisStatusConstant.value_model_type_device, RedisStatusConstant.value_model_name_device, 60 * 1000)) {
            throw new ServiceException("Device服务尚未运行！");
        }

        Map<String, Object> request = JsonUtils.buildObject(body, Map.class);

        // 如果是设备请求：那么插入一个"clientName:" "proxy4http2topic"属性，通知设备服务把请求返回到这个位置
        if ((RedisTopicConstant.topic_device_request + RedisTopicConstant.model_public).equals(topicRequest)) {
            request.put(DeviceMethodVOFieldConstant.field_client_name, RedisTopicConstant.model_manager);
        }

        Integer timeout = (Integer) request.get(DeviceMethodVOFieldConstant.field_timeout);

        //填写UID，从众多方便返回的数据中，识别出来对应的返回报文
        String key = (String) request.get(DeviceMethodVOFieldConstant.field_uuid);
        if (key == null || key.isEmpty()) {
            key = UUID.randomUUID().toString().replace("-", "");
            request.put(DeviceMethodVOFieldConstant.field_uuid, key);
        }

        // 重新打包数据
        body = JsonUtils.buildJson(request);


        // 重置信号
        SyncFlagObjectMap.inst().reset(key);

        // 发送数据
        publisher.sendMessage(topicRequest, body);

        logger.info(topicRequest + ":" + body);

        // 等待消息的到达：根据动态key
        String respond = (String) SyncFlagObjectMap.inst().waitDynamic(key, timeout + extra_timeout_device);
        if (respond == null) {
            throw new ServiceException("设备响应超时！");
        }

        return respond;
    }
}
