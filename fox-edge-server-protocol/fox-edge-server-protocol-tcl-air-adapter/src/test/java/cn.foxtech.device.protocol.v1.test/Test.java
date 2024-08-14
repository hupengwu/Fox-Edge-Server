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

        print("2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 0D 0A 32 30 32 34 C4 EA 30 34 D4 C2 31 37 C8 D5 20 31 31 CA B1 30 35 B7 D6 0D 0D 31 2F 33 20 0D CA D6 B6 AF B1 A8 BE AF B0 B4 C5 A5 B1 A8 BB F0 BE AF 0D \n ");

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
