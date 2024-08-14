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
 
package cn.foxtech.device.protocol.v1.hikvision.fire.core.enums;

import lombok.Getter;

public enum Sender {
    device(0, "设备"),//设备发出
    platform(1, "平台"),//平台发出
    any(2, "设备或平台");// 设备或者平台发出
    @Getter
    private final int value;


    @Getter
    private final String msg;

    Sender(int value, String msg) {
        this.value = value;
        this.msg = msg;
    }

    public static Sender getEnum(int value) {
        for (Sender type : Sender.values()) {
            if (type.value == value) {
                return type;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.msg;
    }
}
