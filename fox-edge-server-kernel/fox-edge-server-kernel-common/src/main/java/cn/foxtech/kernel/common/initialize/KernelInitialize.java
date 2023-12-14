package cn.foxtech.kernel.common.initialize;

import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.status.ServiceStatusScheduler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class KernelInitialize {
    private static final Logger logger = Logger.getLogger(KernelInitialize.class);
    @Autowired
    private RedisConsoleService console;


    @Autowired
    private ServiceStatusScheduler serviceStatusScheduler;


    public void initialize() {
        String message = "------------------------CommonInitialize初始化开始！------------------------";
        console.info(message);
        logger.info(message);

        // 进程状态
        this.serviceStatusScheduler.initialize();
        this.serviceStatusScheduler.schedule();


        message = "------------------------CommonInitialize初始化结束！------------------------";
        console.info(message);
        logger.info(message);
    }
}
