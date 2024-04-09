package cn.foxtech.device.protocol.v1.telecom.air.qztt_2219_2020.uitls;

public class ValueUtils {
    public static int decodeInteger(byte[] data, int offset) {
        return (data[offset + 0] & 0xff) * 0x100 + (data[offset + 1] & 0xff);
    }
}
