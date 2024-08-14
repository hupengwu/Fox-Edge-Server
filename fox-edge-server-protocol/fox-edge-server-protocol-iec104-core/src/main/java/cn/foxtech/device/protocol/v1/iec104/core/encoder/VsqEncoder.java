/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.iec104.core.encoder;

import cn.foxtech.device.protocol.v1.iec104.core.entity.VsqEntity;

public class VsqEncoder {
    public static VsqEntity decodeVsq(int value) {
        VsqEntity entity = new VsqEntity();
        entity.setSq((value & 0x80) != 0x00);
        entity.setNum(value & 0x7f);
        return entity;
    }

    public static int encodeVsq(VsqEntity entity) {
        int result = entity.getNum();
        if (entity.isSq() == true) {
            result |= 0x80;
        }

        return result;
    }
}
