/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.period.service.initialize;


import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.period.service.service.PeriodRecordService;
import cn.foxtech.service.common.initialize.ServiceCommonInitialize;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class PeriodEngineInitialize {
    private static final Logger logger = Logger.getLogger(PeriodEngineInitialize.class);
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService console;

    @Autowired
    private ServiceCommonInitialize commonInitialize;

    @Autowired
    private PeriodRecordService periodTaskService;


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
