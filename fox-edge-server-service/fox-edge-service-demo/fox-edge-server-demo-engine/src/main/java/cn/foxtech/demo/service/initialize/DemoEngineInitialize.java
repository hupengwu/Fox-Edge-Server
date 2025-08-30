/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.demo.service.initialize;


import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.demo.service.service.DemoRecordService;
import cn.foxtech.service.common.initialize.ServiceCommonInitialize;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class DemoEngineInitialize {
    private static final Logger logger = Logger.getLogger(DemoEngineInitialize.class);
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService console;

    @Autowired
    private ServiceCommonInitialize commonInitialize;

    @Autowired
    private DemoRecordService periodTaskService;


    public void initialize() {
        String message = "------------------------PeriodEngine 初始化开始！------------------------";
        console.info(message);
        logger.info(message);

        // 公共初始化
        this.commonInitialize.initialize();

        // 周期保存数据
        this.periodTaskService.schedule();

        message = "------------------------PeriodEngine 初始化结束！------------------------";
        console.info(message);
        logger.info(message);
    }
}
