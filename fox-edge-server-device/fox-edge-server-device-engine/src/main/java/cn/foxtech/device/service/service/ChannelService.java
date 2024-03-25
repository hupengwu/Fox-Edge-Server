package cn.foxtech.device.service.service;

import cn.foxtech.channel.domain.ChannelBaseVO;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.constant.HttpStatus;
import cn.foxtech.common.entity.entity.ChannelEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.manager.InitialConfigService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.device.protocol.v1.core.channel.FoxEdgeChannelService;
import cn.foxtech.device.protocol.v1.core.enums.WorkerLoggerType;
import cn.foxtech.device.protocol.v1.core.utils.JsonUtils;
import cn.foxtech.device.service.redistopic.RedisTopicPuberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
     * 动态参数配置
     */
    @Autowired
    private InitialConfigService configService;

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

    /**
     * 打印日志
     *
     * @param deviceName   设备名称
     * @param manufacturer 设备厂商
     * @param deviceType   设备类型
     * @param type         日志类型
     * @param content      日志内容
     */

    public void printLogger(String deviceName, String manufacturer, String deviceType, WorkerLoggerType type, Object content) {
        try {
            // 检测：是否需要记录日志
            Object isLogger = this.configService.getConfigValue("serverConfig", "logger");
            if (!Boolean.TRUE.equals(isLogger)) {
                return;
            }

            // 检测：设备名称是否相同
            Object name = this.configService.getConfigValue("serverConfig", "deviceName");
            if (!deviceName.equals(name)) {
                return;
            }

            RedisConsoleService logger = this.configService.getLogger();

            if (content == null) {
                logger.debug("设备厂商：" + manufacturer + "\n设备类型：" + deviceType + "\n设备名称：" + deviceName + "\n" + type.getName() + "：" + null);
                return;
            }
            if (content instanceof String) {
                logger.debug("设备厂商：" + manufacturer + "\n设备类型：" + deviceType + "\n设备名称：" + deviceName + "\n" + type.getName() + "：" + content);
                return;
            }
            if ((content instanceof Map) || (content instanceof List)) {
                logger.debug("设备厂商：" + manufacturer + "\n设备类型：" + deviceType + "\n设备名称：" + deviceName + "\n" + type.getName() + "：" + JsonUtils.buildJsonWithoutException(content));
                return;
            }
        } catch (Exception e) {
            e.getMessage();
        }

    }
}
