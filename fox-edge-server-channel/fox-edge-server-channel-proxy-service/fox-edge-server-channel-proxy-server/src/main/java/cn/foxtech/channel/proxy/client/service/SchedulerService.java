package cn.foxtech.channel.proxy.client.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SchedulerService {
    @Autowired
    MqttMessageReporter mqttMessageReporter;

    /**
     * 每隔1秒查询一次
     */
    @Scheduled(fixedDelay = 1000)
    public void fixedDelayTask() {
        mqttMessageReporter.reportMessage();
    }
}
