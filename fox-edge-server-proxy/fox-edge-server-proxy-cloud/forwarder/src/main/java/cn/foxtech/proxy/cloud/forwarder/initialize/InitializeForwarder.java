package cn.foxtech.proxy.cloud.forwarder.initialize;


import cn.foxtech.proxy.cloud.forwarder.service.MqttClientService;
import cn.foxtech.proxy.cloud.forwarder.service.proxy.HttpRestfulProxyService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class InitializeForwarder {
    private static final Logger logger = Logger.getLogger(InitializeForwarder.class);


    @Autowired
    private HttpRestfulProxyService httpRestfulProxyService;


    @Autowired
    private MqttClientService mqttClientService;


    public void initialize() {
        // 初始化restful服务
        this.httpRestfulProxyService.Initialize();

        // 初始化mqtt服务
        this.mqttClientService.Initialize();
    }
}
