/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.midea.dcm4.uitls;

import java.util.Map;

public class BitValueUtils {
    public static void decodeBitValue(int value, int bitIndex, String v1, String v0, String key, Map<String, Object> result) {
        int bitValue = getBitsValue(value, bitIndex, 1);
        result.put(key, bitValue != 0 ? v1 : v0);
    }

    /**
     * 按位获得数值
     * 例如:getBitsValue(0b0000111, 1, 2)
     * 意思是希望取得bit1位开始的2个bit(也就是0b0000110),并按0b11返回
     *
     * @param value
     * @param bitIndex
     * @param bitWidth
     * @return
     */
    public static int getBitsValue(int value, int bitIndex, int bitWidth) {
        if (bitWidth < 1 || bitWidth > 16) {
            return 0;
        }

        // 掩码
        int mask = 1;
        for (int i = 0; i < bitWidth - 1; i++) {
            mask = (mask << 1) + 1;
        }

        // 移位,并掩码
        return value >> bitIndex & mask;
    }
}
