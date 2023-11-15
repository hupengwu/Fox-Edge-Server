package cn.foxtech.channel.mqtt.service;

import cn.foxtech.channel.common.api.ChannelServerAPI;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.channel.mqtt.handler.MqttHandler;
import cn.foxtech.common.entity.manager.LocalConfigService;
import cn.foxtech.common.mqtt.MqttClientService;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelService extends ChannelServerAPI {
    private final Map<String, List<String>> channelName2ServiceKey = new ConcurrentHashMap<>();

    @Autowired
    private MqttClientService mqttClientService;

    @Autowired
    private LocalConfigService localConfigService;

    @Autowired
    private ReportService reportService;

    public void initialize() {
        // 装载全局配置参数
        this.localConfigService.initialize();
        Map<String, Object> configs = this.localConfigService.getConfigs();
        Map<String, Object> mqttConfig = (Map<String, Object>) configs.getOrDefault("mqtt", new HashMap<>());
        String topic = (String) configs.getOrDefault("topic", "#");
        String clientId = (String) mqttConfig.getOrDefault("clientId", "");


        MqttHandler handler = new MqttHandler();
        handler.setTopic(topic);
        handler.setReportService(this.reportService);

        // 检测：是否需要随机生成一个clientId
        if (MethodUtils.hasEmpty(clientId)) {
            clientId = UUID.randomUUID().toString();
            mqttConfig.put("clientId", clientId);
        }

        // 绑定当前的handler
        this.mqttClientService.getMqttClientListener().setClientHandler(handler);

        this.mqttClientService.Initialize(mqttConfig);
    }

    /**
     * 打开通道：tcp-server是服务端，连接是自动触发的，不存在真正的打开和关闭操作
     *
     * @param channelName  通道名称
     * @param channelParam 通道参数
     */
    @Override
    public void openChannel(String channelName, Map<String, Object> channelParam) {
        List<String> topics = (List<String>) channelParam.get("topics");
        if (MethodUtils.hasEmpty(topics)) {
            throw new ServiceException("通道上，设备级别的topic参数不能为空: topics");
        }

        this.channelName2ServiceKey.put(channelName, topics);
    }

    /**
     * 关闭通道：tcp-server是服务端，连接是自动触发的，不存在真正的打开和关闭操作
     *
     * @param channelName  通道名称
     * @param channelParam 通道参数
     */
    @Override
    public void closeChannel(String channelName, Map<String, Object> channelParam) {
        List<String> topics = (List<String>) channelParam.get("topics");
        if (MethodUtils.hasEmpty(topics)) {
            throw new ServiceException("通道上，设备级别的topic参数不能为空: topics");
        }

        this.channelName2ServiceKey.remove(channelName);
    }


    /**
     * 设备的主动上报消息
     *
     * @return 上报消息
     * @throws ServiceException 异常信息
     */
    @Override
    public List<ChannelRespondVO> report() throws ServiceException {
        return this.reportService.popAll();
    }
}
