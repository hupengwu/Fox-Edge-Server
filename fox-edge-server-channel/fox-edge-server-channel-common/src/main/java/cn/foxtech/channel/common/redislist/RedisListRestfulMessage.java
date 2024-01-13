package cn.foxtech.channel.common.redislist;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.entity.entity.ChannelEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
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
public class RedisListRestfulMessage extends RedisLoggerService {
    @Autowired
    private ChannelProperties channelProperties;

    public RedisListRestfulMessage() {
        this.setKey("fox.edge.list.manager.restful.message");
    }

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

        RestFulRequestVO requestVO = new RestFulRequestVO();
        requestVO.setUri("/kernel/manager/channel/entity");
        requestVO.setMethod("post");
        requestVO.setData(entity);

        this.push(requestVO);
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

        RestFulRequestVO requestVO = new RestFulRequestVO();
        requestVO.setUri("/kernel/manager/device/entity");
        requestVO.setMethod("post");
        requestVO.setData(entity);

        this.push(requestVO);
    }
}