/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 *
 *     This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * --------------------------------------------------------------------------- */
 
package cn.foxtech.thingsboard.service.initialize;


import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.DeviceValueEntity;
import cn.foxtech.common.entity.entity.ExtendConfigEntity;
import cn.foxtech.iot.common.initialize.InitializeCommon;
import cn.foxtech.thingsboard.service.service.DeviceValueEntityScheduler;
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
    private InitializeCommon initializeCommon;

    @Autowired
    private DeviceValueEntityScheduler deviceValueEntityScheduler;

    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        // 开始进行数据装载
        this.initializeCommon.getEntityManageService().addConsumer(ConfigEntity.class.getSimpleName());
        this.initializeCommon.getEntityManageService().addConsumer(ExtendConfigEntity.class.getSimpleName());
        this.initializeCommon.getEntityManageService().addConsumer(DeviceEntity.class.getSimpleName());
        this.initializeCommon.getEntityManageService().addReader(DeviceValueEntity.class.getSimpleName());
        this.initializeCommon.initialize();

        this.deviceValueEntityScheduler.initialize();
        this.deviceValueEntityScheduler.schedule();

        logger.info("------------------------初始化结束！------------------------");
    }
}
