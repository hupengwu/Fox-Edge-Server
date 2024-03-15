package cn.foxtech.device.protocol.v1.test;


import cn.foxtech.device.protocol.v1.utils.Crc16Utils;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.enums.CrcType;

public class TestCrc16Utils {
    public static void main(String[] args) {
        byte[] pdu = HexUtils.hexStringToByteArray("7E0001852500425768864274BF9B3FDC87B141DA88A3427BA3D9420AB60C4052E227436D0A3D7FFFFFFF655F");
        int crc = Crc16Utils.getCRC16(pdu, 0, pdu.length - 2, CrcType.CRC16XMODEM);

        byte[] crcs = new byte[2];
        crcs[0] = (byte) (crc >> 8 & 0xff);
        crcs[1] = (byte) (crc >> 0 & 0xff);
    }


}
