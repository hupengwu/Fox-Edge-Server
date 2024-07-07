package cn.foxtech.period.service.initialize;


import cn.foxtech.period.service.service.PeriodRecordService;
import cn.foxtech.service.common.initialize.ServiceCommonInitialize;
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
    private ServiceCommonInitialize commonInitialize;

    @Autowired
    private PeriodRecordService periodTaskService;


    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        // 公共初始化
        this.commonInitialize.initialize();

        // 周期保存数据
        this.periodTaskService.schedule();

        logger.info("------------------------初始化结束！------------------------");
    }
}
