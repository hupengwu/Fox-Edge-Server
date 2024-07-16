/*
 * MIT License
 *
 * Copyright (c) 2021-2099 Oscura (xingshuang) <xingshuang_cool@163.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package cn.foxtech.device.protocol.v1.s7plc.core.enums;


import java.util.HashMap;
import java.util.Map;

/**
 * 操作的返回值，0xff信号成功。在“ 写入请求”消息中，此字段始终设置为零。
 *
 * @author xingshuang
 */
public enum EReturnCode {

    /**
     * 未定义，预留
     */
    RESERVED((byte) 0x00, "reserved"),

    /**
     * 硬件错误
     */
    HARDWARE_ERROR((byte) 0x01, "hardware error"),

    /**
     * 对象不允许访问
     */
    ACCESSING_THE_OBJECT_NOT_ALLOWED((byte) 0x03, "accessing the object not allowed"),

    /**
     * 无效地址，所需的地址超出此PLC的极限
     */
    INVALID_ADDRESS((byte) 0x05, "invalid address"),

    /**
     * 数据类型不支持
     */
    DATA_TYPE_NOT_SUPPORTED((byte) 0x06, "data type not supported"),

    /**
     * 数据类型不一致
     */
    DATA_TYPE_INCONSISTENT((byte) 0x07, "data type inconsistent"),

    /**
     * 对象不存在
     */
    OBJECT_DOES_NOT_EXIST((byte) 0x0A, "object does not exist"),

    /**
     * 成功
     */
    SUCCESS((byte) 0xFF, "success"),

    ;

    private static Map<Byte, EReturnCode> map;
    private final byte code;
    private final String description;

    EReturnCode(byte code, String description) {
        this.code = code;
        this.description = description;
    }

    public static EReturnCode from(byte data) {
        if (map == null) {
            map = new HashMap<>();
            for (EReturnCode item : EReturnCode.values()) {
                map.put(item.code, item);
            }
        }
        return map.get(data);
    }

    public byte getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}