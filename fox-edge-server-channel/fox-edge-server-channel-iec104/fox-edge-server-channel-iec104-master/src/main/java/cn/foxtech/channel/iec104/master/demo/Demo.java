package cn.foxtech.channel.iec104.master.demo;

import cn.foxtech.common.utils.iec104.server.Iec104ClientLinkerManager;
import cn.foxtech.common.utils.iec104.server.Iec104ClientLinkerScheduler;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.core.utils.JsonUtils;
import cn.foxtech.device.protocol.v1.iec104.core.builder.ApduVOBuilder;
import cn.foxtech.device.protocol.v1.iec104.core.encoder.ApduEncoder;
import cn.foxtech.device.protocol.v1.iec104.core.encoder.BasicSessionEncoder;
import cn.foxtech.device.protocol.v1.iec104.core.entity.ApduEntity;
import cn.foxtech.device.protocol.v1.iec104.core.enums.FrameTypeEnum;
import cn.foxtech.device.protocol.v1.iec104.core.vo.ApduVO;
import cn.foxtech.channel.iec104.master.handler.MasterLinkerHandler;

public class Demo {

    public static void main(String[] args) {
        test();
        //    test1();
    }

    public static byte[] longToBytes(long data) {
        byte[] res = new byte[8];
        for (int i = 0; i < res.length; i++) {
            res[i] = (byte) (data >> 8 * i);
        }
        return res;
    }

    public static void test() {
        try {
            String sendString = " 68 04 07 00 00 00";
            String recvString = " 68 04 0b 00 00 00";
            ApduEntity apduEntity = null;


            FrameTypeEnum type = ApduEncoder.identifyFormatType(HexUtils.hexStringToByteArray("680483000000"));

            ApduEntity apduEntity1 = ApduEncoder.decodeApdu(HexUtils.hexStringToByteArray("680401000000"));
            ApduVO apduVO = ApduVOBuilder.buildVO(apduEntity1);
            String json = JsonUtils.buildJson(apduVO);

            ApduEntity apduEntity2 = ApduEncoder.decodeApdu(HexUtils.hexStringToByteArray("680401000400"));
            apduVO = ApduVOBuilder.buildVO(apduEntity2);
            json = JsonUtils.buildJson(apduVO);

            ApduEntity apduEntity3 = ApduEncoder.decodeApdu(HexUtils.hexStringToByteArray("680401008C00"));
            apduVO = ApduVOBuilder.buildVO(apduEntity3);
            json = JsonUtils.buildJson(apduVO);

            ApduEntity apduEntity4 = ApduEncoder.decodeApdu(HexUtils.hexStringToByteArray("685D8E0002000F94050001000164000102030403040506050607080708090009000102010203040304050605060708070809000900010201020304030405060506070807080900090001020102030403040506050607080708090009000102"));
            apduVO = ApduVOBuilder.buildVO(apduEntity4);
            json = JsonUtils.buildJson(apduVO);
            apduVO = JsonUtils.buildObject(json, ApduVO.class);
            ApduEntity apduEntityt = ApduVOBuilder.buildEntity(apduVO);

            ApduEntity apduEntity5 = ApduEncoder.decodeApdu(HexUtils.hexStringToByteArray("680E9000020065010A00010000000045"));
            ApduEntity apduEntity6 = ApduEncoder.decodeApdu(HexUtils.hexStringToByteArray("680401007C00"));


            ApduEntity apduEntity7 = ApduEncoder.decodeApdu(HexUtils.hexStringToByteArray("68 0e 8a 00 00 00 64 01 0a 00 01 00 14 00 00 14"));
//
//            sendString = HexUtils.byteArrayToHexString(LinkerMethodEntity.encodeSTARTDTByRequest());
//            apduEntity = LinkerMethodEntity.decodeSTARTDTByRespond(HexUtils.hexStringToByteArray("68 04 0b 00 00 00"));
//
//            sendString = HexUtils.byteArrayToHexString(LinkerMethodEntity.encodeTESTFRByRequest());
//            apduEntity = LinkerMethodEntity.decodeTESTFRByRespond(HexUtils.hexStringToByteArray(" 68 04 83 00 00 00"));
//
            apduEntity = BasicSessionEncoder.decodeGeneralCallByRespond(HexUtils.hexStringToByteArray("68310200000001A414000100010000000100000100010000010001000001000100000100010000010001000001000100000100"));
            sendString = HexUtils.byteArrayToHexString(ApduEncoder.encodeApdu(apduEntity));
            int end = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test1() {
        MasterLinkerHandler linkerHandler = new MasterLinkerHandler();
//        linkerHandler.addHandler(new GeneralCallHandler());
//        linkerHandler.addHandler(new PowerPulseCallHandler());


        Iec104ClientLinkerManager.bindHandler(linkerHandler);
        Iec104ClientLinkerManager.registerRemoteAddress("localhost", 2404);
        Iec104ClientLinkerScheduler.getInstance().schedule();
    }
}

