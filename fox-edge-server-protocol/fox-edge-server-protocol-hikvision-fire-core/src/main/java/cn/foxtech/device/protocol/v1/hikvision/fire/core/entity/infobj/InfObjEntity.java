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
 
package cn.foxtech.device.protocol.v1.hikvision.fire.core.entity.infobj;

import java.util.List;

public abstract class InfObjEntity {
    /**
     * 编码
     *
     * @param data 数据
     */
    public abstract void decode(byte[] data);

    /**
     * 解码
     *
     * @return 数据
     */
    public abstract byte[] encode();

    /**
     * 获得ADU们的长度
     * @param data 完整的PDU数据报
     * @param offset ADU在PDU中的起始位置
     * @param aduLength ADU在CTRL中标识的长度信息
     */
    public abstract List<Integer> getAduSizes(byte[] data, int offset, int aduLength);
}
