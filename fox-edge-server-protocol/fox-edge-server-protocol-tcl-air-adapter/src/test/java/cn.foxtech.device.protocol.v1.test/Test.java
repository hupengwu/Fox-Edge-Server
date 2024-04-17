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

        print("31 35 32 B7 C2 D8 BB 31 32 30 FA BB F7 D6 31 30 53 11 25 10 22 B5 3E");

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
