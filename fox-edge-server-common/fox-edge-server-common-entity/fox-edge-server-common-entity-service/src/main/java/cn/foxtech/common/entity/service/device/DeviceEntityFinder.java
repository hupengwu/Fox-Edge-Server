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
 
package cn.foxtech.common.entity.service.device;

import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.service.redis.IBaseFinder;
import lombok.AccessLevel;
import lombok.Setter;

@Setter(value = AccessLevel.PUBLIC)
public class DeviceEntityFinder implements IBaseFinder {
    /**
     * 通道名称：连接设备的服务名，比如"serialport"
     */
    private String channelType;

    /**
     * 通道名称：连接设备的串口名，比如"COM1"
     */
    private String channelName;

    public boolean compareValue(Object value) {
        DeviceEntity deviceEntity = (DeviceEntity) value;
        if (channelName != null || !channelName.equals(deviceEntity.getDeviceName())) {
            return false;
        }
        if (channelType != null || !channelType.equals(deviceEntity.getChannelType())) {
            return false;
        }
        return true;
    }
}
