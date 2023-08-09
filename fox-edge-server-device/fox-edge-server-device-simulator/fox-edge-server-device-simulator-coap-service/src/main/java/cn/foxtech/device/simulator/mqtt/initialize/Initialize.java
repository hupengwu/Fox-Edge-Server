package cn.foxtech.device.simulator.mqtt.initialize;

import cn.foxtech.device.simulator.mqtt.config.CoapProperties;
import cn.foxtech.device.simulator.mqtt.entity.CoapConfigEntity;
import cn.foxtech.device.simulator.mqtt.service.CoapSimulatorService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class Initialize implements CommandLineRunner {
    private static Logger logger = Logger.getLogger(Initialize.class);

    @Autowired
    CoapProperties properties;

    @Autowired
    private CoapSimulatorService simulatorService;


    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        // 装载发送/接收数据对应表
        this.simulatorService.reload();

        // 从yaml配置文件，生成配置实体
        CoapConfigEntity deviceTemplateEntity = properties.buildConfigEntity();

        // 设置配置实体
        this.simulatorService.setConfig(deviceTemplateEntity);

        // 注册资源
        this.simulatorService.register();

        // 启动服务
        this.simulatorService.start();


        logger.info("------------------------初始化结束！------------------------");
    }
}
