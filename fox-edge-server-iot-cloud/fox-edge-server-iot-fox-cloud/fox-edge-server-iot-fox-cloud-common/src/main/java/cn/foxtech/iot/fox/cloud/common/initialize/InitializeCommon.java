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
 
package cn.foxtech.iot.fox.cloud.common.initialize;


import cn.foxtech.common.status.ServiceStatusScheduler;
import cn.foxtech.iot.fox.cloud.common.scheduler.EntityManageScheduler;
import cn.foxtech.iot.fox.cloud.common.service.EntityManageService;
import cn.foxtech.iot.fox.cloud.common.mqtt.MqttClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class InitializeCommon {
    @Autowired
    private ServiceStatusScheduler serviceStatusScheduler;

    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private EntityManageScheduler entityManageScheduler;

    @Autowired
    private MqttClientService mqttClientService;

    public void initialize() {
        // 初始化进程的状态：通告本身服务的信息给其他服务
        this.serviceStatusScheduler.initialize();
        this.serviceStatusScheduler.schedule();

        // 装载数据实体
        this.entityManageService.instance();
        this.entityManageService.initLoadEntity();

        // 启动同步线程
        this.entityManageScheduler.schedule();

        // 初始化mqtt服务
        this.mqttClientService.Initialize();
    }
}
