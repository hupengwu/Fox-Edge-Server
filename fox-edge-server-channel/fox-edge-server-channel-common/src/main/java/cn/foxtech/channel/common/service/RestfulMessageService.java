package cn.foxtech.channel.common.service;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.common.entity.entity.ChannelEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.rpc.redis.manager.client.RedisListManagerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Restful风格的可靠性列表：无应答，也就是不会对发送者进行回复
 * <p>
 * 接收者： manage
 * 发送者： persist
 */
@Component
public class RestfulMessageService {
    @Autowired
    private ChannelProperties channelProperties;

    @Autowired
    private RedisListManagerClient managerClient;

    /**
     * 向管理服务，发出一个创建一个通道的无响应消息
     *
     * @param channelName  通道名称
     * @param channelParam 通道参数格式
     */
    public void createChannel(String channelName, Map<String, Object> channelParam) {
        ChannelEntity entity = new ChannelEntity();
        entity.setChannelType(this.channelProperties.getChannelType());
        entity.setChannelName(channelName);
        if (channelParam != null) {
            entity.setChannelParam(channelParam);
        }

        this.managerClient.pushRequest("/kernel/manager/channel/entity", "post", entity);
    }

    public void createDevice(String manufacturer, String deviceType, String deviceName, String channelName, Map<String, Object> deviceParam) {
        DeviceEntity entity = new DeviceEntity();
        entity.setManufacturer(manufacturer);
        entity.setDeviceType(deviceType);
        entity.setDeviceName(deviceName);
        entity.setChannelType(this.channelProperties.getChannelType());
        entity.setChannelName(channelName);
        if (deviceParam != null) {
            entity.setDeviceParam(deviceParam);
        }

        this.managerClient.pushRequest("/kernel/manager/device/entity", "post", entity);
    }
}