/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.utils;

import cn.foxtech.device.protocol.v1.utils.enums.CrcType;

/**
 * 循环冗余校验码（CRC），简称循环码
 * <p>
 * 基本概念：CRC的计算有非常多的行业计算标准，但是这些计算方法都是采用同一个循环冗余计算方法
 * 它们之间，主要是多项式POLY、初始值INIT、结果异或值，各不相同
 * 所以，设备厂商提供某种CRC算法的时候，一般会说自己采用的是某个行业计算标准。同时为了更准确描述，
 * 通常会提供对应的多项式POLY、初始值INIT、结果异或值
 * <p>
 * 参考工具：https://crccalc.com/
 */
public class Crc16Utils {
    /**
     * CRC16计算：带输入/输出的反转，即	RefIn	RefOut 为 true
     *
     * @param data       数组
     * @param offset     偏移量：数组中开始计算的位置
     * @param length     数据长度，从开始计算位位置开始，往后计算多少个字节
     * @param polynomial CRC16的多项式
     * @param initial    CRC16的初始值
     * @param xorOut     结果异或
     * @return CRC16
     */
    public static int getCRC16RefTrue(byte[] data, int offset, int length, int polynomial, int initial, int xorOut) {
        int crc = initial;
        polynomial = invertWord(polynomial);

        for (int i = offset; i < length; i++) {
            byte b = data[i];

            crc ^= b & 0xFF;
            for (int j = 0; j < 8; j++) {
                if ((crc & 1) == 1) {
                    crc = (crc >>> 1) ^ polynomial;
                } else {
                    crc = crc >>> 1;
                }
            }
        }
        return (crc & 0xFFFF) ^ xorOut;
    }

    /**
     * CRC16计算：无输入/输出的反转，即	RefIn	RefOut 为 false
     *
     * @param data       数组
     * @param offset     偏移量：数组中开始计算的位置
     * @param length     数据长度，从开始计算位位置开始，往后计算多少个字节
     * @param polynomial CRC16的多项式
     * @param initial    CRC16的初始值
     * @param xorOut     结果异或
     * @return CRC16
     */
    public static int getCRC16RefFalse(byte[] data, int offset, int length, int polynomial, int initial, int xorOut) {
        int crc = initial;

        for (int i = offset; i < length; i++) {
            byte b = data[i];

            crc ^= (b & 0xFF) << 8; // XOR with byte value
            for (int j = 0; j < 8; j++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ polynomial;
                } else {
                    crc <<= 1;
                }
            }
        }
        return (crc & 0xFFFF) ^ xorOut;
    }

    /**
     * CRC16计算
     *
     * @param data       数组
     * @param offset     偏移量：数组中开始计算的位置
     * @param length     数据长度，从开始计算位位置开始，往后计算多少个字节
     * @param polynomial CRC16的多项式
     * @param initial    CRC16的初始值
     * @param xorOut     结果异或
     * @param refInOut   是否翻转输入输出
     * @return CRC16
     */
    public static int getCRC16(byte[] data, int offset, int length, int polynomial, int initial, int xorOut, boolean refInOut) {
        if (refInOut) {
            return getCRC16RefTrue(data, offset, length, polynomial, initial, xorOut);
        } else {
            return getCRC16RefFalse(data, offset, length, polynomial, initial, xorOut);
        }
    }

    public static int getCRC16(byte[] data, int offset, int length, CrcType type) {
        return getCRC16(data, offset, length, type.getPolynomial(), type.getInitial(), type.getXorOut(), type.isRefInOut());
    }

    public static int invertWord(int dat) {
        int value = 0;
        for (int i = 0; i < 16; i++) {
            value |= ((dat >> i) & 0x01) << (15 - i);
        }

        return value;
    }

}
