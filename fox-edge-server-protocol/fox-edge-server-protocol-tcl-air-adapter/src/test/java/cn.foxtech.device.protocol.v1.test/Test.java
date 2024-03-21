package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.tcl.air.adapter.entity.PduEntity;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

public class Test {
    public static void main(String[] args) {
        // 数据为 00 00 00 00 01 F4
        byte[] pdu = HexUtils.hexStringToByteArray("F4 F5 00 00 00 00 01 F4 F4 F4 FB");

        PduEntity entity = new PduEntity();
        entity.setMessageData(new byte[1]);
        entity.getMessageData()[0] = 1;
        pdu = PduEntity.encodePdu(entity);

        entity = PduEntity.decodePdu(pdu);

        String txt = HexUtils.byteArrayToHexString(pdu, true).toUpperCase();
    }


}
