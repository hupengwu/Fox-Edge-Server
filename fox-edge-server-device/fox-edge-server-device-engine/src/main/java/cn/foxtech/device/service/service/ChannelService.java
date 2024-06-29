package cn.foxtech.device.service.service;

import cn.foxtech.channel.domain.ChannelBaseVO;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.constant.HttpStatus;
import cn.foxtech.common.entity.entity.ChannelEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.manager.InitialConfigService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.rpc.redis.channel.client.RedisListChannelClient;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.device.protocol.v1.core.channel.FoxEdgeChannelService;
import cn.foxtech.device.protocol.v1.core.enums.WorkerLoggerType;
import cn.foxtech.device.protocol.v1.core.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * 通道转发服务
 */
@Component
public class ChannelService implements FoxEdgeChannelService {
    private static final int TIMEOUT_CHANNEL = 1000;

    @Autowired
    private EntityManageService entityManageService;


    @Autowired
    private RedisListChannelClient channelClient;


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
        DeviceEntity deviceEntity = this.entityManageService.getDeviceEntity(deviceName);
        if (deviceEntity == null) {
            throw new ServiceException("在数据库找不大该设备实体：" + deviceName);
        }

        // 通道实体
        ChannelEntity channelEntity = this.entityManageService.getChannelEntity(deviceEntity.getChannelName(), deviceEntity.getChannelType());
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
        DeviceEntity deviceEntity = this.entityManageService.getDeviceEntity(deviceName);
        if (deviceEntity == null) {
            throw new ServiceException("在数据库找不大该设备实体：" + deviceName);
        }

        // 通道实体
        ChannelEntity channelEntity = this.entityManageService.getChannelEntity(deviceEntity.getChannelName(), deviceEntity.getChannelType());
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
        ChannelRespondVO respondVO = this.executeChannel(request);
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
        return this.executeChannel(request);
    }

    /**
     * 将请求发送给对应的channel服务
     *
     * @return 响应报文
     */
    private ChannelRespondVO executeChannel(ChannelRequestVO requestVO) throws TimeoutException {
        // 填写UID，从众多方便返回的数据中，识别出来对应的返回报文
        if (requestVO.getUuid() == null || requestVO.getUuid().isEmpty()) {
            requestVO.setUuid(UUID.randomUUID().toString().replace("-", ""));
        }

        // 发送数据
        this.channelClient.pushChannelRequest(requestVO.getType(), requestVO);

        // 等待消息的到达：根据动态key
        ChannelRespondVO respond = this.channelClient.getChannelRespond(requestVO.getType(), requestVO.getUuid(), requestVO.getTimeout() + TIMEOUT_CHANNEL);
        if (respond == null) {
            throw new TimeoutException("通道服务响应超时：" + requestVO.getType());
        }

        return respond;
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
