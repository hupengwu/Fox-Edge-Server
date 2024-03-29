package cn.foxtech.common.utils.iec104.core.test;

import cn.foxtech.device.protocol.v1.iec104.core.encoder.ApduEncoder;
import cn.foxtech.device.protocol.v1.iec104.core.encoder.ValueEncoder;
import cn.foxtech.device.protocol.v1.iec104.core.entity.ApduEntity;

public class Test2 {
    public static void main(String[] args) {
        test();
    }

    public static void test() {
        try {
            ApduEntity apduEntity1 = ApduEncoder.decodeApdu(ValueEncoder.hexStringToBytes("68a3e6030e000bb214000100014000020100050400328900323200323200989800243400232300244500331200020100050400328900323200323200989800243400232300244500331200020100050400328900323200323200989800243400232300244500331200020100050400328900323200323200989800243400232300244500331200020100050400328900323200323200989800243400232300244500331200"));
            int end = 0;
        } catch (Exception e) {
            System.err.println(e);
        }
    }

}
