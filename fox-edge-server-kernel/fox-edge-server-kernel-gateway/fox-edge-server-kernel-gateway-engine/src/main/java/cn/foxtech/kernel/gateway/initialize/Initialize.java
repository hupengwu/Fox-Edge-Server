package cn.foxtech.kernel.gateway.initialize;


import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.kernel.common.initialize.KernelInitialize;
import cn.foxtech.kernel.gateway.service.EntityManageService;
import cn.foxtech.kernel.gateway.service.LocalSystemConfService;
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
    private KernelInitialize kernelInitialize;

    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private LocalSystemConfService systemConfService;

    @Override
    public void run(String... args) {
        logger.info("------------------------Manager Service 初始化开始！------------------------");

        this.systemConfService.initialize();

        this.kernelInitialize.initialize();

        // 装载数据实体
        this.entityManageService.instance();
        this.entityManageService.initLoadEntity();


        logger.info("------------------------Manager Service 初始化结束！------------------------");
    }
}
