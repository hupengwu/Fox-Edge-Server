/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.persist.iotdb.initialize;


import cn.foxtech.persist.common.initialize.PersistCommonInitialize;
import cn.foxtech.persist.iotdb.service.IoTDBSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class Initialize implements CommandLineRunner {

    @Autowired
    private PersistCommonInitialize persistInitialize;

    @Autowired
    private IoTDBSessionService iotDBSessionService;

    @Override
    public void run(String... args) {
        this.persistInitialize.initialize();

        this.iotDBSessionService.initialize();
    }
}
