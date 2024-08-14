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
 
package cn.foxtech.channel.tcp.server.handler;

import cn.foxtech.channel.common.service.RestfulMessageService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.method.MethodUtils;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ManageHandler {
    private final Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private RestfulMessageService redisList;

    @Autowired
    private RedisConsoleService console;

    @Setter
    private String channelName;

    @Setter
    private String manufacturer;
    @Setter
    private String deviceType;
    @Setter
    private String deviceName;

    public void createChannel(String serviceKey) {
        try {
            // 检测：是否输入了通道名称前缀
            if (MethodUtils.hasEmpty(this.channelName)) {
                return;
            }

            // 格式化名称
            String chnName = this.channelName + "-" + serviceKey;

            // 填写通道参数
            Map<String, Object> channelParam = new HashMap<>();
            channelParam.put("serviceKey", serviceKey);

            // 发出消息
            this.redisList.createChannel(chnName, channelParam);
        } catch (Exception e) {
            String message = "发送消息失败：serviceKey=" + serviceKey + "； " + e.getMessage();
            this.logger.error(message);
            this.console.error(message);
        }
    }

    public void createDevice(String serviceKey) {
        try {
            // 检测：是否输入了厂商、设备类型、设备名称
            if (MethodUtils.hasEmpty(this.manufacturer, this.deviceType, this.deviceName)) {
                return;
            }

            // 格式化名称
            String devName = this.deviceName + "-" + serviceKey;
            // 格式化名称
            String chnName = this.channelName + "-" + serviceKey;

            // 填写通道参数
            Map<String, Object> channelParam = new HashMap<>();
            channelParam.put("serviceKey", serviceKey);

            // 发出消息
            this.redisList.createDevice(this.manufacturer, this.deviceType, devName, chnName, channelParam);
        } catch (Exception e) {
            String message = "发送消息失败：serviceKey=" + serviceKey + "； " + e.getMessage();
            this.logger.error(message);
            this.console.error(message);
        }
    }
}
