/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.test;


import cn.foxtech.device.protocol.v1.telecom.core.entity.PduEntity;
import cn.foxtech.device.protocol.v1.utils.HexUtils;


public class TestTelecomProtocol {
    public static void main(String[] args) {
        byte[] pdu = HexUtils.hexStringToByteArray("7e 31 30 30 31 36 30 34 46 30 30 30 30 46 44 39 45 0d ");

        PduEntity entity = PduEntity.decodePdu(pdu);


        pdu = HexUtils.hexStringToByteArray("7E31303031383430303330314330303334463242463442323030373033313331323538333830303030463744330D ");

        entity = PduEntity.decodePdu(pdu);

        pdu = PduEntity.encodePdu(entity);

        String txt = HexUtils.byteArrayToHexString(pdu).toUpperCase();

       // PduEntity.getUnPackCmdVfyCode()

    }

}
