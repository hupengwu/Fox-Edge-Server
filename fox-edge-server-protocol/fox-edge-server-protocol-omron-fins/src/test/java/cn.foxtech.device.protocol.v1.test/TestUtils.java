/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.omron.fins.core.encoder.DataEncoder;
import cn.foxtech.device.protocol.v1.omron.fins.core.encoder.PduEncoder;
import cn.foxtech.device.protocol.v1.omron.fins.core.entity.pdu.ConnectRequest;
import cn.foxtech.device.protocol.v1.omron.fins.core.entity.pdu.ConnectRespond;
import cn.foxtech.device.protocol.v1.omron.fins.core.entity.pdu.OmronFinsPdu;
import cn.foxtech.device.protocol.v1.omron.fins.core.entity.pdu.TransferRespond;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

public class TestUtils {
    public static void main(String[] args) throws Exception {
        test1();
    }

    public static void test1() {
        try {
            OmronFinsPdu entity = PduEncoder.decodePduPack(HexUtils.hexStringToByteArray("46 49 4E 53 00 00 00 1A 00 00 00 02 00 00 00 00 80 00 02 00 0A 00 00 71 00 FF 01 01 B1 00 0A 00 00 01 "));

            ConnectRequest session = new ConnectRequest();
            session.setClientNode(0x71);
            String hexString = HexUtils.byteArrayToHexString(PduEncoder.encodePduPack(session));
            Object obj1 = PduEncoder.decodePdu(HexUtils.hexStringToByteArray("46 49 4E 53 00 00 00 0C 00 00 00 00 00 00 00 00 00 00 00 71"), ConnectRequest.class);
            Object obj2 = PduEncoder.decodePdu(HexUtils.hexStringToByteArray("46 49 4E 53 00 00 00 10 00 00 00 01 00 00 00 00 00 00 00 71 00 00 00 0A"), ConnectRespond.class);
            Object obj3 = PduEncoder.decodePdu(HexUtils.hexStringToByteArray("46 49 4E 53 00 00 00 1C 00 00 00 02 00 00 00 00 80 00 02 00 0A 00 00 71 00 00 01 02 B1 00 0A 00 00 01 00 06"), TransferRespond.class);


            TransferRespond obj4 = PduEncoder.decodePdu(HexUtils.hexStringToByteArray("46 49 4E 53 00 00 00 18 00 00 00 02 00 00 00 00 C0 00 02 00 71 00 00 0A 00 FF 01 01 00 00 00 0C"), TransferRespond.class);
            DataEncoder.decodeReadData(obj4.getData());

            obj4 = PduEncoder.decodePdu(HexUtils.hexStringToByteArray("46 49 4E 53 00 00 00 1A 00 00 00 02 00 00 00 00 C0 00 02 00 71 00 00 0A 00 FF 01 01 00 00 00 0C 3F 7B"), TransferRespond.class);
//            Object value = DataEncoder.decodeReadData(obj4.getData(), 1).getValue();
//
//            obj4 = PduEncoder.decodePdu(HexUtils.hexStringToByteArray("46 49 4E 53 00 00 00 22 00 00 00 02 00 00 00 00 C0 00 02 00 71 00 00 0A 00 FF 01 01 00 00 48 4A 4B 31 32 33 34 35 36 37 38 00"), TransferRespond.class);
//            value = DataEncoder.decodeReadData(obj4.getData(), 1, String.class).getValue();
//
//            obj4 = PduEncoder.decodePdu(HexUtils.hexStringToByteArray("46 49 4E 53 00 00 00 1A 00 00 00 02 00 00 00 00 C0 00 02 00 71 00 00 0A 00 FF 01 01 00 00 00 00 3F C0"), TransferRespond.class);
//            value = DataEncoder.decodeReadData(obj4.getData(), 1, Float.class).getValue();

            hexString = "1";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
