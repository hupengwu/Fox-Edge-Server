/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.haier.ycj.a002.entity.PduEntity;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

public class Test {
    public static void main(String[] args) {
        sysStatus();

    }

    public static void sysStatus() {
        PduEntity entity = new PduEntity();
        entity.setHostAddr(1);
        entity.setDevAddr(2);
        byte[] pdu = PduEntity.encodePdu(entity);

        String text = HexUtils.byteArrayToHexString(pdu, true);

        entity = PduEntity.decodePdu(pdu);
        text = "";

    }


}
