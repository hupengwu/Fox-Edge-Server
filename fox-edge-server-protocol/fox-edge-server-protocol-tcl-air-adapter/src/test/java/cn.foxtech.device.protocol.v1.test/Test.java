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

        print("0A 1B 31 00 1B 4B C0 00 E0 F0 18 88 18 F0 E0 00 00 08 08 08 18 F0 E0 00 30 60 C0 80 00 00 00 00 00 08 08 F8 F8 08 08 00 10 18 08 08 08 F8 F0 00 00 00 00 00 00 00 00 00 38 78 C8 88 08 18 18 00 E0 F0 18 88 18 F0 E0 00 00 00 00 60 60 00 00 00 10 18 08 08 08 F8 F0 00 E0 F0 18 88 18 F0 E0 00 00 00 00 00 00 00 00 00 40 40 80 FE 54 54 54 7C 54 54 54 FE 80 C0 80 00 00 E0 40 40 40 40 FC 42 42 42 42 E2 02 0E 00 00 00 FC 08 08 08 FC 02 02 84 48 30 48 84 06 04 00 FE 20 10 20 C8 08 E8 A8 A8 BE A8 A8 E8 08 08 00 00 00 00 FE 08 30 C2 02 84 58 E0 58 84 86 04 00 00 08 12 22 44 C4 68 58 50 68 44 44 02 02 02 00 0D 1B 4B C0 00 07 0F 18 11 18 0F 07 00 0E 1F 11 11 11 1F 0F 00 00 00 00 01 03 06 0C 00 00 04 0C 1F 1F 00 00 00 08 18 11 11 11 1F 0E 00 00 00 00 00 00 12 00 00 08 18 10 11 13 1E 0C 00 07 0F 18 11 18 0F 07 00 00 00 00 0C 0C 00 00 00 1F 1F 11 11 11 11 10 00 07 0F 18 11 18 0F 07 00 00 00 00 00 00 00 00 00 00 00 04 08 11 69 26 24 26 29 31 20 00 00 00 00 00 1F 12 12 12 12 7F 12 12 12 12 3F 10 00 00 00 08 09 09 7F 09 09 0A 04 0B 78 08 08 0F 18 08 00 3F 24 2A 31 08 28 2B 3A 2A 6A 2A 3A 2B 28 08 00 01 0E 00 7F 08 14 10 17 78 10 17 10 10 31 10 00 04 08 10 60 3F 35 35 35 35 35 35 3F 60 20 00 00 0D ");

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
        String txt = new String(HexUtils.hexStringToByteArray(hex), "UTF-8");
        System.out.println(txt);
    }


}
