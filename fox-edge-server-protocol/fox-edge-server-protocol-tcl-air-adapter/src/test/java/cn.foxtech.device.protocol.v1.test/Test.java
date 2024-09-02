/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.tcl.air.adapter.entity.PduEntity;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

import java.io.UnsupportedEncodingException;

public class Test {
    public static void main(String[] args) throws UnsupportedEncodingException {
        int va = 'N';

        // 数据为 00 00 00 00 01 F4
        byte[] pdu = HexUtils.hexStringToByteArray("F4 F5 00 00 00 00 01 F4 F4 F4 FB");
        pdu = HexUtils.hexStringToByteArray("0A 1B 4A 01 1B 39");

        print("20 20 32 30 32 33 2F 31 30 2F 31 30 20 31 30 3A 35 35 3A 35 33 0A 20 20 28 30 30 31 2D 30 30 31 29 30 38 30 B6 B0 30 31 C7 F8 30 30 31 B2 E3 20 30 30 31 BA C5 0D 20 20 BB F0 BE AF 21 20 20 CA D6 B6 AF B0 B4 C5 A5 0D 0D 0D 0A 1C 76 00 0D 0D 0D 0A");

//        PduEntity entity = new PduEntity();
//        entity.setData(new byte[3]);
//        entity.getData()[0] = 0x0D;
//        entity.getData()[1] = 0x01;
//        entity.getData()[2] = 0x10;
//        pdu = PduEntity.encodePdu(entity);
//
//
//        entity = PduEntity.decodePdu(pdu);
//
//        String txt = HexUtils.byteArrayToHexString(pdu, true).toUpperCase();
    }

    private static void print(String hex) throws UnsupportedEncodingException {
        String txt = new String(HexUtils.hexStringToByteArray(hex), "GB2312");
        System.out.println(txt);
    }


}
