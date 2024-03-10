package cn.foxtech.iot.fox.cloud.forwarder.initialize;


import cn.foxtech.iot.fox.cloud.forwarder.service.MqttMessageRespond;
import cn.foxtech.iot.fox.cloud.forwarder.proxy.HttpRestfulProxyService;
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
    private MqttMessageRespond messageRespond;


    public void initialize() {
        // 初始化restful服务
        this.httpRestfulProxyService.Initialize();

        // 启动响应线程
        this.messageRespond.schedule();
    }
}
