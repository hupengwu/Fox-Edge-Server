package cn.foxtech.common.utils.iec104.core.test;

import cn.foxtech.device.protocol.iec104.core.encoder.ApduEncoder;
import cn.foxtech.device.protocol.iec104.core.encoder.BasicSessionEncoder;
import cn.foxtech.device.protocol.iec104.core.encoder.ValueEncoder;
import cn.foxtech.device.protocol.iec104.core.entity.ApduEntity;
import cn.foxtech.device.protocol.iec104.core.enums.FrameTypeEnum;

public class Iec104Test {
    public static void main(String[] args) {
        test();
    }


    public static void test() {
        try {
            System.out.println("测试代码");
            String sendString = " 68 04 07 00 00 00";
            String recvString = " 68 04 0b 00 00 00";
            ApduEntity apduEntity = null;

            FrameTypeEnum type = ApduEncoder.identifyFormatType(ValueEncoder.hexStringToBytes("68310E00000001A414000100D90000010000010001000001000100000100010000010001000001000100000100010000010001"));

            sendString = ValueEncoder.byteArrayToHexString(BasicSessionEncoder.encodeSTARTDTByRequest());
            apduEntity = BasicSessionEncoder.decodeSTARTDTByRespond(ValueEncoder.hexStringToBytes("68 04 0b 00 00 00"));

            sendString = ValueEncoder.byteArrayToHexString(BasicSessionEncoder.encodeTESTFRByRequest());
            apduEntity = BasicSessionEncoder.decodeTESTFRByRespond(ValueEncoder.hexStringToBytes(" 68 04 83 00 00 00"));

            apduEntity = BasicSessionEncoder.decodeGeneralCallByRespond(ValueEncoder.hexStringToBytes("68310200000001A414000100010000000100000100010000010001000001000100000100010000010001000001000100000100"));
            sendString = ValueEncoder.byteArrayToHexString(ApduEncoder.encodeApdu(apduEntity));
            int end = 0;
        } catch (Exception e) {
            System.err.println(e);
        }
    }


}
