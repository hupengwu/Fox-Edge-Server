package cn.foxtech.kernel.system.service.service;

import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.channel.domain.ChannelRestfulConstant;
import cn.foxtech.common.domain.constant.RedisStatusConstant;
import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.domain.vo.RestFulVO;
import cn.foxtech.common.entity.constant.BaseVOFieldConstant;
import cn.foxtech.common.entity.constant.ChannelVOFieldConstant;
import cn.foxtech.common.status.ServiceStatus;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.redis.topic.service.RedisTopicPublisher;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.device.domain.constant.DeviceMethodVOFieldConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class RedisTopicService {
    private static final int extra_timeout_channel = 3000;
    private static final int extra_timeout_device = extra_timeout_channel + 3000;


    @Autowired
    private RedisTopicPublisher publisher;

    @Autowired
    private ServiceStatus serviceStatus;

    public ChannelRespondVO executeChannel(Map<String, Object> request) throws InterruptedException, IOException {
        // 转换消息 结构
        String channelType = (String) request.remove(ChannelVOFieldConstant.field_channel_type);
        ChannelRequestVO requestVO = JsonUtils.buildObject(request, ChannelRequestVO.class);
        requestVO.setType(channelType);

        return executeChannel(requestVO);
    }

    public ChannelRespondVO querySouthLinks(String channelType) throws InterruptedException, IOException {
        RestFulVO param = new RestFulVO();
        param.setUri(ChannelRestfulConstant.resource_south_links_page);
        param.setMethod("post");
        param.setData(new HashMap<>());
        ((Map<String, Object>) param.getData()).put(BaseVOFieldConstant.field_page_num, 1);
        ((Map<String, Object>) param.getData()).put(BaseVOFieldConstant.field_page_size, 10);


        // 转换消息 结构
        ChannelRequestVO requestVO = new ChannelRequestVO();
        requestVO.setType(channelType);
        requestVO.setMode("manage");
        requestVO.setSend(param);

        return executeChannel(requestVO);
    }

    public ChannelRespondVO executeChannel(ChannelRequestVO requestVO) throws InterruptedException, IOException {
        String channelType = requestVO.getType();

        // 检查：参数是否为空
        if (MethodUtils.hasEmpty(channelType)) {
            throw new ServiceException("参数缺失：channelType");
        }

        // 检查：目标服务是否已经启动
        if (!this.serviceStatus.isActive(RedisStatusConstant.value_model_type_channel, channelType, 60 * 1000)) {
            throw new ServiceException("Channel服务尚未运行：" + channelType);
        }

        Integer timeout = requestVO.getTimeout();
        if (timeout == null) {
            timeout = 2000;
        }

        //填写UUID，从众多方便返回的数据中，识别出来对应的返回报文
        String key = requestVO.getUuid();
        if (MethodUtils.hasEmpty(key)) {
            key = UUID.randomUUID().toString().replace("-", "");
            requestVO.setUuid(key);
        }

        // 要求channel服务把数据发挥到这个topic之中
        String route = requestVO.getRoute();
        if (MethodUtils.hasEmpty(route)) {
            requestVO.setRoute(RedisTopicConstant.topic_channel_respond + RedisTopicConstant.model_manager);
        }


        // 重置信号
        SyncFlagObjectMap.inst().reset(key);


        // 发送数据
        this.publisher.sendMessage(RedisTopicConstant.topic_channel_request + channelType, JsonUtils.buildJson(requestVO));


        // 等待消息的到达：根据动态key
        ChannelRespondVO respond = (ChannelRespondVO) SyncFlagObjectMap.inst().waitDynamic(key, timeout + extra_timeout_channel);
        if (respond == null) {
            throw new ServiceException("设备响应超时！");
        }

        return respond;
    }

    public Object executeChannel1(Map<String, Object> request) throws InterruptedException, IOException {
        // 摒弃多余的参数：channelType
        String channelType = (String) request.remove(ChannelVOFieldConstant.field_channel_type);


        // 检查：参数是否为空
        if (MethodUtils.hasEmpty(channelType)) {
            throw new ServiceException("参数缺失：channelType");
        }

        // 检查：目标服务是否已经启动
        if (!this.serviceStatus.isActive(RedisStatusConstant.value_model_type_channel, channelType, 60 * 1000)) {
            throw new ServiceException("Channel服务尚未运行：" + channelType);
        }

        Integer timeout = (Integer) request.get(DeviceMethodVOFieldConstant.field_timeout);

        //填写UUID，从众多方便返回的数据中，识别出来对应的返回报文
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


        // 重置信号
        SyncFlagObjectMap.inst().reset(key);


        // 发送数据
        this.publisher.sendMessage(RedisTopicConstant.topic_channel_request + channelType, JsonUtils.buildJson(request));


        // 等待消息的到达：根据动态key
        ChannelRespondVO respond = (ChannelRespondVO) SyncFlagObjectMap.inst().waitDynamic(key, timeout + extra_timeout_channel);
        if (respond == null) {
            throw new ServiceException("设备响应超时！");
        }

        return respond;
    }

    public Object executeDevice(Map<String, Object> request) throws InterruptedException, IOException {
        // 检查：目标服务是否已经启动
        if (!this.serviceStatus.isActive(RedisStatusConstant.value_model_type_device, RedisStatusConstant.value_model_name_device, 60 * 1000)) {
            throw new ServiceException("Device服务尚未运行！");
        }

        // 如果是设备请求：那么插入一个"clientName:" "proxy4http2topic"属性，通知设备服务把请求返回到这个位置
        request.put(DeviceMethodVOFieldConstant.field_client_name, RedisTopicConstant.model_manager);


        Integer timeout = (Integer) request.get(DeviceMethodVOFieldConstant.field_timeout);

        //填写UID，从众多方便返回的数据中，识别出来对应的返回报文
        String key = (String) request.get(DeviceMethodVOFieldConstant.field_uuid);
        if (key == null || key.isEmpty()) {
            key = UUID.randomUUID().toString().replace("-", "");
            request.put(DeviceMethodVOFieldConstant.field_uuid, key);
        }

        // 重新打包数据
        String body = JsonUtils.buildJson(request);


        // 重置信号
        SyncFlagObjectMap.inst().reset(key);

        // 发送数据
        publisher.sendMessage(RedisTopicConstant.topic_device_request + RedisTopicConstant.model_public, body);

        // 等待消息的到达：根据动态key
        String respond = (String) SyncFlagObjectMap.inst().waitDynamic(key, timeout + extra_timeout_device);
        if (respond == null) {
            throw new ServiceException("设备响应超时！");
        }

        return JsonUtils.buildObject(respond, Map.class);
    }

}
