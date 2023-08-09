package cn.foxtech.device.simulator.mqtt.initialize;


import cn.foxtech.device.simulator.mqtt.service.MqttClientService;
import cn.foxtech.device.simulator.mqtt.service.MqttSimulatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import org.apache.log4j.Logger;

/**
 * 初始化
 */
@Component
public class Initialize implements CommandLineRunner {
    private static final Logger logger = Logger.getLogger(Initialize.class);

    @Autowired
    private MqttSimulatorService simulatorService;

    @Autowired
    private MqttClientService mqttClientService;


    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");
        // 装载发送/接收数据对应表
        this.simulatorService.reload();

        this.mqttClientService.Initialize();

        logger.info("------------------------初始化完成！------------------------");
    }
}
