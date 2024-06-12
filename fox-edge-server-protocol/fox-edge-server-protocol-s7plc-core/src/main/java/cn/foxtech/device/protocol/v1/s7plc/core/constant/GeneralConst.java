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

package cn.foxtech.device.protocol.v1.s7plc.core.constant;


/**
 * 通用常量
 *
 * @author xingshuang
 */
public class GeneralConst {

    private GeneralConst() {
        // NOOP
    }

    /**
     * 本地ip，127.0.0.1
     */
    public static final String LOCALHOST = "127.0.0.1";

    /**
     * S7的端口号
     */
    public static final int S7_PORT = 102;

    /**
     * Modbus的端口号
     */
    public static final int MODBUS_PORT = 502;

    /**
     * 三菱的端口号
     */
    public static final int MELSEC_PORT = 6000;

    /**
     * bit类型
     */
    public static final int TYPE_BIT = 0;

    /**
     * word类型
     */
    public static final int TYPE_WORD = 1;

    /**
     * dword类型
     */
    public static final int TYPE_DWORD = 2;

}
