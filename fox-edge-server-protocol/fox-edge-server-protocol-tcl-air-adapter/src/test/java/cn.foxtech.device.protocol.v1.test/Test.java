package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.tcl.air.adapter.entity.PduEntity;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

public class Test {
    public static void main(String[] args) {
        // 数据为 00 00 00 00 01 F4
        byte[] pdu = HexUtils.hexStringToByteArray("F4 F5 00 00 00 00 01 F4 F4 F4 FB");
        pdu = HexUtils.hexStringToByteArray("F4 F5 05 00 00 0D 01 00 13 F4 FB");

        PduEntity entity = new PduEntity();
        entity.setData(new byte[3]);
        entity.getData()[0] = 0x0D;
        entity.getData()[1] = 0x01;
        entity.getData()[2] = 0x10;
        pdu = PduEntity.encodePdu(entity);


        entity = PduEntity.decodePdu(pdu);

        String txt = HexUtils.byteArrayToHexString(pdu, true).toUpperCase();
    }

    private void ss(){
//                "recv": "f4 f5 09 00 00 6f 00 00 84 61 00 00 5d f4 fb",
//                "rspd": "f4 f5 09 00 00 6f 00 00 84 61 00 00 5d f4 fb"
//
//
//                "recv": "F4 F5 05 00 00 0D 01 00 13 F4 FB",
//                "rspd": "F4 F5 05 00 00 0D 01 00 13 F4 FB"
//
//                 "name": "tcl空调",
//                "recv": "f4 f5 05 00 00 70 00 00 75 f4 fb",
//                "rspd": "f4 f5 0d 00 00 70 00 00 00 00 00 00 00 00 00 00 7d f4 fb"

    }


}
