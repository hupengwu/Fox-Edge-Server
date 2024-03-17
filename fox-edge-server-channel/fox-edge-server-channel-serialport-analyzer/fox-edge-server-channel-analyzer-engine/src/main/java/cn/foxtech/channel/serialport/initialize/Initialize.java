package cn.foxtech.channel.serialport.initialize;

import cn.foxtech.channel.common.initialize.ChannelInitialize;
import cn.foxtech.channel.common.service.EntityManageService;
import cn.foxtech.channel.serialport.notify.OperateEntityTypeNotify;
import cn.foxtech.channel.serialport.service.ServerInitializer;
import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.entity.service.redis.ConsumerRedisService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * 初始化
 */
@Component
public class Initialize implements CommandLineRunner {
    private static final Logger logger = Logger.getLogger(Initialize.class);

    @Autowired
    private ChannelInitialize channelInitialize;

    @Autowired
    private ServerInitializer serverInitializer;

    @Autowired
    private OperateEntityTypeNotify operateEntityTypeNotify;

    @Autowired
    private EntityManageService entityManageService;


    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        // 初始化装载数据
        Set<String> consumer = new HashSet<>();
        consumer.add(OperateEntity.class.getSimpleName());
        this.channelInitialize.initialize(consumer);

        // 绑定一个类型级别的数据变更通知
        ConsumerRedisService consumerRedisService = (ConsumerRedisService) this.entityManageService.getBaseRedisService(OperateEntity.class);
        consumerRedisService.bind(this.operateEntityTypeNotify);

        // 初始化服务
        this.serverInitializer.initialize();

        logger.info("------------------------初始化结束！------------------------");

    }
}
