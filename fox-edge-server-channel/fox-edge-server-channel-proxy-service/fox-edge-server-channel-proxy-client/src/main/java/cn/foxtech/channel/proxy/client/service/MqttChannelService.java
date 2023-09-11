package cn.foxtech.channel.proxy.client.service;

import cn.foxtech.channel.common.api.ChannelServerAPI;
import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.utils.ContainerUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class MqttChannelService extends ChannelServerAPI {
    /**
     * 公网MQTT的转发延迟
     */
    private final int extraTime = 2000;

    /**
     * 配置服务
     */
    @Autowired
    private MqttConfigService configService;
    /**
     * 连接服务
     */
    @Autowired
    private MqttClientService clientService;
    /**
     * 常量
     */
    @Autowired
    private ChannelProperties constants;

    /**
     * 执行请求
     *
     * @param requestVO
     * @return
     * @throws ServiceException
     */
    @Override
    public ChannelRespondVO execute(ChannelRequestVO requestVO) throws ServiceException {
        try {
            String topicHead = this.configService.getPublish();

            // 构造发送到云端服务器的topic
            StringBuilder stringBuilder = new StringBuilder();
            if (topicHead.endsWith("#")) {
                stringBuilder.append(topicHead, 0, topicHead.length() - 1);
                stringBuilder.append(constants.getChannelType());
            } else {
                stringBuilder.append(topicHead);
                stringBuilder.append(constants.getChannelType());
            }
            String topic = stringBuilder.toString();

            // ChannelProxy之间通信，需要夹杂ChannelType，而上层应用是没有ChannelType的，因为是Type在TOPIC后缀
            requestVO.setType(constants.getChannelType());

            // 转换成json报文
            String body = JsonUtils.buildJson(requestVO);

            // 重置信号
            String key = requestVO.getUuid();
            SyncFlagObjectMap.inst().reset(key);

            // 发送数据
            this.clientService.getMqttClient().publish(topic, body.getBytes(StandardCharsets.UTF_8));

            // 等待消息的到达：根据动态key
            Object recv = SyncFlagObjectMap.inst().waitDynamic(key, requestVO.getTimeout() + this.extraTime);
            if (recv == null) {
                throw new ServiceException("在超时范围内，未接收到返回数据！");
            }

            return (ChannelRespondVO) recv;
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }

    }

    /**
     * 设备的主动上报消息
     *
     * @return 上报消息
     * @throws ServiceException 异常信息
     */
    @Override
    public List<ChannelRespondVO> report() throws ServiceException {
        try {
            List<Object> result = SyncQueueObjectMap.inst().popup(constants.getChannelType(), false);
            return ContainerUtils.buildClassList(result, ChannelRespondVO.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
