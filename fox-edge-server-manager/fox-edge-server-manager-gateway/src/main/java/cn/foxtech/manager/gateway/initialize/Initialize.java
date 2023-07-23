package cn.foxtech.manager.gateway.initialize;


import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.process.ProcessUtils;
import cn.foxtech.manager.common.initialize.CommonInitialize;
import cn.foxtech.manager.gateway.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class Initialize implements CommandLineRunner {
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService logger;

    @Autowired
    private CommonInitialize commonInitialize;

    @Autowired
    private EntityManageService entityManageService;


    @Override
    public void run(String... args) {
        logger.info("------------------------Manager Service 初始化开始！------------------------");

        ProcessUtils.killLoader();

        this.commonInitialize.initialize();

        // 装载数据实体
        this.entityManageService.instance();
        this.entityManageService.initLoadEntity();


        logger.info("------------------------Manager Service 初始化结束！------------------------");
    }
}
