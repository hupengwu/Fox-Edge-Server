package cn.foxtech.kernel.system.service.service;

import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.domain.constant.RedisStatusConstant;
import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.entity.constant.ChannelVOFieldConstant;
import cn.foxtech.common.rpc.redis.channel.client.RedisListChannelClient;
import cn.foxtech.common.rpc.redis.device.client.RedisListDeviceClient;
import cn.foxtech.common.status.ServiceStatus;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.device.domain.constant.DeviceMethodVOFieldConstant;
import cn.foxtech.device.domain.vo.TaskRequestVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Component
public class RedisListClientService {
    private static final int extra_timeout_channel = 3000;
    private static final int extra_timeout_device = extra_timeout_channel + 3000;

    @Autowired
    private ServiceStatus serviceStatus;


    @Autowired
    private RedisListDeviceClient deviceClient;

    @Autowired
    private RedisListChannelClient channelClient;


    public ChannelRespondVO executeChannel(Map<String, Object> request) throws InterruptedException, IOException {
        // 转换消息 结构
        String channelType = (String) request.remove(ChannelVOFieldConstant.field_channel_type);
        ChannelRequestVO requestVO = JsonUtils.buildObject(request, ChannelRequestVO.class);
        requestVO.setType(channelType);

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

        // 发送数据
        this.channelClient.pushChannelRequest(requestVO.getType(), requestVO);

        // 等待消息的到达：根据动态key
        ChannelRespondVO respond = this.channelClient.getChannelRespond(requestVO.getType(), requestVO.getUuid(), timeout + extra_timeout_channel);
        if (respond == null) {
            throw new ServiceException("设备响应超时：" + requestVO.getType());
        }

        return respond;
    }

    public Object executeDevice(Map<String, Object> requestMap) {
        // 检查：目标服务是否已经启动
        if (!this.serviceStatus.isActive(RedisStatusConstant.value_model_type_device, RedisStatusConstant.value_model_name_device, 60 * 1000)) {
            throw new ServiceException("Device服务尚未运行！");
        }

        // 如果是设备请求：那么插入一个"clientName:" "proxy4http2topic"属性，通知设备服务把请求返回到这个位置
        requestMap.put(DeviceMethodVOFieldConstant.field_client_name, RedisTopicConstant.model_manager);


        Integer timeout = (Integer) requestMap.get(DeviceMethodVOFieldConstant.field_timeout);

        //填写UID，从众多方便返回的数据中，识别出来对应的返回报文
        String key = (String) requestMap.get(DeviceMethodVOFieldConstant.field_uuid);
        if (key == null || key.isEmpty()) {
            key = UUID.randomUUID().toString().replace("-", "");
            requestMap.put(DeviceMethodVOFieldConstant.field_uuid, key);
        }

        TaskRequestVO requestVO = TaskRequestVO.buildRequestVO(requestMap);

        // 发出操作请求
        this.deviceClient.pushDeviceRequest(requestVO);

        // 等待消息的到达：根据动态key
        TaskRespondVO respondVO = this.deviceClient.getDeviceRespond(key, timeout + extra_timeout_device);
        if (respondVO == null) {
            return TaskRespondVO.error("Device Service 响应超时！");
        }

        return respondVO;
    }

}
