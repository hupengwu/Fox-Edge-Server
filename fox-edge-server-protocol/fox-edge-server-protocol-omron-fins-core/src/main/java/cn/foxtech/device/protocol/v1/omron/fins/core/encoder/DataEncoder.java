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
 
package cn.foxtech.device.protocol.v1.omron.fins.core.encoder;

import cn.foxtech.device.protocol.v1.omron.fins.core.entity.data.*;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.omron.fins.core.entity.data.*;
import cn.foxtech.device.protocol.v1.omron.fins.core.entity.data.*;

public class DataEncoder {
    public static byte[] encodeReadData(ReadDataRequest request) {
        byte[] data = new byte[6];
        // 区域码
        data[0] = (byte) request.getArea();
        // 字地址
        data[1] = (byte) ((request.getWordAddress() >> 8) & 0xff);
        data[2] = (byte) (request.getWordAddress() & 0xff);
        // 位地址
        data[3] = (byte) (request.getBitAddress() & 0xff);

        // 数值
        ValueEncoder.setWord(data, 4, request.getCount());
        return data;
    }

    public static <T> ReadDataRespond decodeReadData(byte[] data) {
        if (data.length < 2) {
            throw new ProtocolException("这不是一个合法数据！");
        }

        ReadDataRespond respond = new ReadDataRespond();
        respond.setMain(data[0]);
        respond.setSub(data[1]);

        byte[] value = new byte[data.length - 2];
        System.arraycopy(data, 2, value, 0, value.length);
        respond.setData(value);

        return respond;
    }

    public static byte[] encodeWriteData(WriteDataRequest request) {
        byte[] data = new byte[4 + request.getData().length];
        // 区域类型
        data[0] = (byte) request.getArea();
        // 字地址
        data[1] = (byte) (request.getWordAddress() & 0xff);
        data[2] = (byte) ((request.getWordAddress() >> 8) & 0xff);
        // 位地址
        data[3] = (byte) request.getBitAddress();

        // 数值
        System.arraycopy(request.getData(), 4, data, 0, request.getData().length);
        return data;
    }

    public static WriteDataRespond decodeWriteData(byte[] data) {
        if (data.length != 2) {
            throw new ProtocolException("这不是一个合法数据！");
        }

        WriteDataRespond respond = new WriteDataRespond();
        respond.setMain(data[0]);
        respond.setSub(data[1]);
        return respond;
    }

    public static Respond decodeEndCode(byte[] data) {
        if (data.length < 2) {
            throw new ProtocolException("这不是一个合法数据！");
        }

        Respond endCode = new Respond();
        endCode.setMain(data[0]);
        endCode.setSub(data[1]);
        return endCode;
    }
}
