/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.zktl.air5in1.handler;

import cn.foxtech.device.protocol.v1.utils.netty.ServiceKeyHandler;
import cn.foxtech.device.protocol.v1.zktl.air5in1.encoder.Encoder;
import cn.foxtech.device.protocol.v1.zktl.air5in1.entity.ZktlDataEntity;

public class ZktlServiceKeyHandler extends ServiceKeyHandler {
    @Override
    public String getServiceKey(byte[] pdu) {
        ZktlDataEntity entity = Encoder.decodeDataEntity(pdu);
        return entity.getServiceKey();
    }
}
