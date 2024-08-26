/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.kernel.system.service.initialize;


import cn.foxtech.common.entity.manager.RedisConsoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class Initialize implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(Initialize.class);
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService console;


    @Autowired
    private SystemEngineInitialize engineInitialize;


    public void run(String... args) {
        String message = "------------------------Initialize初始化开始！------------------------";
        console.info(message);
        logger.info(message);

        this.engineInitialize.initialize();

        message = "------------------------Initialize初始化结束！------------------------";
        console.info(message);
        logger.info(message);
    }

}
