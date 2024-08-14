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
 
package cn.foxtech.device.protocol.v1.midea.dcm4.entity;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 美的空调DCM4的PDU格式
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class PduEntity {
    /**
     * 源地址：1 byte
     */
    private int srcAddr = 0xA0;
    /**
     * 目的地址：1 byte
     */
    private int dstAddr = 0xA1;

    /**
     * 命令字：1 byte
     */
    private int cmd = 0xC2;

    /**
     * 数据内容：8 byte
     */
    private byte[] data = new byte[8];

    /**
     * 编码
     * 测试报文：aa a0 a1 c9 a1 00 00 00 00 00 00 00 55 55
     *
     * @param entity
     * @return
     */
    public static byte[] encodePdu(PduEntity entity) {
        byte[] data = new byte[14];

        int index = 0;

        // 起始（1）
        data[index++] = (byte) 0xAA;

        // 源地址（1）
        data[index++] = (byte) entity.srcAddr;

        // 宿地址（1）
        data[index++] = (byte) entity.dstAddr;

        // 命令字（8）
        data[index++] = (byte) entity.cmd;

        // 数据（9）
        System.arraycopy(entity.data, 0, data, index, entity.data.length);
        index += entity.data.length;

        // 校验和（1）
        data[index++] = getVerify(data);

        // 结束（1）
        data[index++] = 0x55;

        return data;
    }

    /**
     * 解码
     * 测试报文：aa a1 a0 c9 a1 00 b1 00 b2 00 00 00 f2 55
     *
     * @param pdu PDU报文
     * @return 实体
     */
    public static PduEntity decodePdu(byte[] pdu) {
        if (pdu == null || pdu.length < 14) {
            throw new ProtocolException("报文大小固必须定为14");
        }

        if ((pdu[0] & 0xff) != 0xAA) {
            throw new ProtocolException("包头不正确");
        }
        if ((pdu[13] & 0xff) != 0x55) {
            throw new ProtocolException("包尾不正确");
        }

        byte vrf = PduEntity.getVerify(pdu);
        if ((pdu[12] & 0xff) != (vrf & 0xff)) {
            throw new ProtocolException("校验和");
        }


        PduEntity entity = new PduEntity();
        entity.srcAddr = pdu[1] & 0xff;
        entity.dstAddr = pdu[2] & 0xff;
        entity.cmd = pdu[3] & 0xff;

        System.arraycopy(pdu, 4, entity.data, 0, entity.data.length);
        return entity;
    }

    private static byte getVerify(byte[] data) {
        int sum = 0;
        for (int i = 1; i < data.length - 2; i++) {
            sum += data[i] & 0xff;
        }

        sum &= 0xff;
        sum ^= 0xff;
        sum += 1;

        return (byte) sum;
    }

}
