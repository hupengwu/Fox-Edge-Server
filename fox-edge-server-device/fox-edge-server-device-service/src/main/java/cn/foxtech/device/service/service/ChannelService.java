package cn.foxtech.device.service.service;

import cn.foxtech.channel.domain.ChannelBaseVO;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.constant.HttpStatus;
import cn.foxtech.common.entity.entity.ChannelEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.device.protocol.v1.core.channel.FoxEdgeChannelService;
import cn.foxtech.device.service.redistopic.RedisTopicPuberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 通道转发服务
 */
@Component
public class ChannelService implements FoxEdgeChannelService {
    @Autowired
    EntityManageService entityService;

    @Autowired
    private RedisTopicPuberService publisher;

    /**
     * 执行操作
     *
     * @param deviceName 返回的数据
     * @param deviceType 设备类型
     * @param send       发送的数据
     * @return 返回的报文内容
     * @throws Exception 通信异常信息
     */
    @Override
    public Object exchange(String deviceName, String deviceType, Object send, int timeout) throws Exception {
        // 进一步获取通道的详细信息
        DeviceEntity deviceEntity = entityService.getDeviceEntity(deviceName);
        if (deviceEntity == null) {
            throw new ServiceException("在数据库找不大该设备实体：" + deviceName);
        }

        // 通道实体
        ChannelEntity channelEntity = entityService.getChannelEntity(deviceEntity.getChannelName(), deviceEntity.getChannelType());
        if (channelEntity == null) {
            throw new ServiceException("在数据库找不大该设备实体对应的ChannelEntity：" + deviceEntity.getDeviceName());
        }

        // 与设备交换数据
        return this.exchange(channelEntity, deviceEntity, send, timeout);
    }

    /**
     * 执行操作
     *
     * @param deviceName 返回的数据
     * @param deviceType 设备类型
     * @param send       发送的数据
     * @throws Exception 通信异常信息
     */
    @Override
    public void publish(String deviceName, String deviceType, Object send, int timeout) throws Exception {
        // 进一步获取通道的详细信息
        DeviceEntity deviceEntity = entityService.getDeviceEntity(deviceName);
        if (deviceEntity == null) {
            throw new ServiceException("在数据库找不大该设备实体：" + deviceName);
        }

        // 通道实体
        ChannelEntity channelEntity = entityService.getChannelEntity(deviceEntity.getChannelName(), deviceEntity.getChannelType());
        if (channelEntity == null) {
            throw new ServiceException("在数据库找不大该设备实体对应的ChannelEntity：" + deviceEntity.getDeviceName());
        }

        // 与设备交换数据
        this.publish(channelEntity, deviceEntity, send, timeout);
    }

    /**
     * 发送数据
     */
    private Object exchange(ChannelEntity channelEntity, DeviceEntity deviceEntity, Object send, int timeout) throws Exception {
        // 构造请求报文
        ChannelRequestVO request = new ChannelRequestVO();
        request.setType(channelEntity.getChannelType());
        request.setMode(ChannelBaseVO.MODE_EXCHANGE);
        request.setName(deviceEntity.getChannelName());
        request.setTimeout(timeout);
        request.setSend(send);


        // 向主从应答类型的设备发送请求
        ChannelRespondVO respondVO = this.publisher.execute(request);
        if (!HttpStatus.SUCCESS.equals(respondVO.getCode())) {
            throw new ServiceException(respondVO.getMsg());
        }

        return respondVO.getRecv();
    }

    /**
     * 发送数据
     */
    private ChannelRespondVO publish(ChannelEntity channelEntity, DeviceEntity deviceEntity, Object send, int timeout) throws Exception {
        // 构造请求报文
        ChannelRequestVO request = new ChannelRequestVO();
        request.setType(channelEntity.getChannelType());
        request.setMode(ChannelBaseVO.MODE_PUBLISH);
        request.setName(deviceEntity.getChannelName());
        request.setTimeout(timeout);
        request.setSend(send);


        // 向非应答类型的设备发送请求
        return this.publisher.execute(request);
    }
}
