package cn.foxtech.device.protocol.v1.hj212_2017.utils;

public class Crc16Util {

    public static int getCrc16(byte[] arrCmd) {
        int usDataLen = arrCmd.length;

        int i, j, crc_reg, check;
        crc_reg = 0xFFFF;
        for (i = 0; i < usDataLen; i++) {
            crc_reg = (crc_reg >> 8) ^ (arrCmd[i] & 0xff);
            for (j = 0; j < 8; j++) {
                check = crc_reg & 0x0001;
                crc_reg >>= 1;
                if (check == 0x0001) {
                    crc_reg ^= 0xA001;
                }
            }
        }
        return crc_reg & 0xffff;
    }

}
