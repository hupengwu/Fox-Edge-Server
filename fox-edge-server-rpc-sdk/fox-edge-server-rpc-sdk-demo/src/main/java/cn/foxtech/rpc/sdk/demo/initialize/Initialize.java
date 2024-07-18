package cn.foxtech.rpc.sdk.demo.initialize;


import cn.foxtech.rpc.sdk.demo.test.RpcMqttSdkTester;
import cn.foxtech.rpc.sdk.demo.test.RpcRedisSdkTester;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class Initialize implements CommandLineRunner {
    private static final Logger logger = Logger.getLogger(Initialize.class);

    @Autowired
    private RpcRedisSdkTester redisSdkTester;

    @Autowired
    private RpcMqttSdkTester mqttSdkTester;


    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        this.redisSdkTester.test();
        this.mqttSdkTester.test();


        logger.info("------------------------初始化完成！------------------------");
    }
}
