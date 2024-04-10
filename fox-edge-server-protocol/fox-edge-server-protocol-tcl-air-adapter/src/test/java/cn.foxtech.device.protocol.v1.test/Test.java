package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.tcl.air.adapter.entity.PduEntity;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

import java.io.UnsupportedEncodingException;

public class Test {
    public static void main(String[] args) throws UnsupportedEncodingException {
        int va = 'N';

        // 数据为 00 00 00 00 01 F4
        byte[] pdu = HexUtils.hexStringToByteArray("F4 F5 00 00 00 00 01 F4 F4 F4 FB");
        pdu = HexUtils.hexStringToByteArray("F4 F5 05 00 00 0D 01 00 13 F4 FB");

        print("09 08 0F 20 00 00 FC 24 24 E4 24 24 24 22 22 A3 62 00 00 00 40 30 8F 28 58 40 5C 54 54 7F 54 54 DE 44 40 00 0D  ");
        print("09 08 0F 20 40 30 0F 80 80 40 23 14 08 14 22 21 40 C0 40 00 00 00 7F 20 10 80 9F 41 21 1D 21 21 5F C1 00 00 0D  ");
        print("30 38 8D 30 34 8E 02 31 33 3A 33 38 0D");
        print("4E 4F 02 33 30 30 30 36 35 20 20 02 0D ");

        print("09 08 0F 30 00 FE 02 22 DA 16 10 10 F1 96 90 90 D0 98 10 00 00 00 80 70 00 00 00 FF 00 80 40 20 30 00 00 00 00 F8 01 86 E0 9A 82 FA 42 4A 52 42 02 FF 02 00 0D");
        print("09 08 0F 30 00 FF 08 10 08 87 40 30 0F 40 80 40 3F 00 00 00 00 81 40 20 10 0C 03 00 03 0C 10 20 40 C0 40 00 00 FF 01 00 7F 00 10 09 06 1A 21 30 80 FF 00 00 0D");
        print("09 08 0F 40 10 62 04 4C 20 50 48 44 C3 44 68 50 20 60 20 00 00 42 24 10 FF 00 44 A4 24 3F 24 34 26 84 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0D");
        print("09 08 0F 40 04 04 FE 01 22 12 4A 82 7F 02 0A 12 33 02 00 00 01 21 21 11 09 FD 43 21 0D 11 29 25 43 C1 41 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0D");
        print("09 08 0F 08 00 00 00 00 00 00 00 00 0D");
        print("09 08 0F 08 00 00 00 00 00 00 00 00 0D");
        print("7F 0E 10 0D");

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
