package cn.foxtech.common.utils.iec104.core.test;

import cn.foxtech.device.protocol.iec104.core.encoder.ApduEncoder;
import cn.foxtech.device.protocol.iec104.core.encoder.ValueEncoder;
import cn.foxtech.device.protocol.iec104.core.entity.ApduEntity;

public class Iec104Test {
    public static void main(String[] args) {
        test();
    }

    public static void test() {
        try {
            ApduEntity apduEntity1 = ApduEncoder.decodeApdu(ValueEncoder.hexStringToBytes("68310E00000001A414000100D90000010000010001000001000100000100010000010001000001000100000100010000010001"));
            int end = 0;
        } catch (Exception e) {
            System.err.println(e);
        }
    }


}
