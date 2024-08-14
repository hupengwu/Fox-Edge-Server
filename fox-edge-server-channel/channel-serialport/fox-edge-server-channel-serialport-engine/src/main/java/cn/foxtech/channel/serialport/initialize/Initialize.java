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
 
package cn.foxtech.channel.serialport.initialize;

import cn.foxtech.channel.common.initialize.ChannelInitialize;
import cn.foxtech.channel.serialport.service.ChannelService;
import cn.foxtech.common.entity.manager.InitialConfigService;
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
    private ChannelInitialize channelInitialize;

    @Autowired
    private InitialConfigService configService;


    @Override
    public void run(String... args) {
        logger.info("------------------------初始化开始！------------------------");

        this.channelInitialize.initialize();

        this.configService.initialize("serverConfig", "serverConfig.json");

        logger.info("------------------------初始化结束！------------------------");

    }
}
