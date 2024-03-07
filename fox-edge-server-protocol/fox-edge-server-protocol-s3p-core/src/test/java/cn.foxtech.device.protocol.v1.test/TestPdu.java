package cn.foxtech.device.protocol.v1.test;


import cn.foxtech.device.protocol.v1.s3p.core.entity.PduEntity;
import cn.foxtech.device.protocol.v1.s3p.core.enums.Escape;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

public class TestPdu {
    public static void main(String[] args) {
        byte[] pdu = HexUtils.hexStringToByteArray("7E 7D 20 7D 20 40 7D 21 7D 20 EB A3");

        PduEntity entity = PduEntity.decodePdu(pdu, Escape.Max);
        pdu = PduEntity.encodePdu(entity, Escape.Max);
        String txt = HexUtils.byteArrayToHexString(pdu, true).toUpperCase();
    }


}
