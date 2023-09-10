package cn.foxtech.device.protocol.v1.telecom.demo;

import cn.foxtech.device.protocol.v1.telecom.core.TelecomProtocol;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

import java.util.Map;

public class DemoTelecom {

    public static void main(String[] args) {
        byte[] arrCmd = HexUtils.hexStringToByteArray("7E323030323431343430303030464441460D");

        Map<String, Object> value = TelecomProtocol.unPackCmd2Map(arrCmd);
        byte[] dat = (byte[]) value.get("INFO");
        String d = HexUtils.byteArrayToHexString(dat);
        //        ByteRef byVer = new ByteRef();
//        ByteRef byAddr = new ByteRef();
//        ByteRef byCID1 = new ByteRef();
//        ByteRef byCID2 = new ByteRef();
//        BytesRef arrData = new BytesRef();
//        boolean s = TelecomProtocol.unPackCmd(arrCmd, byVer, byAddr, byCID1, byCID2, arrData);
//        String str1 = HexUtils.byteArrayToHexString(arrCmd);
//
//        BytesRef arrCmdRef = new BytesRef();
//        TelecomEntity entity = new TelecomEntity();
//        arrCmd = TelecomProtocol.packCmd4Entity(entity);
//        String str2 = HexUtils.byteArrayToHexString(arrCmdRef.getValue());
        //   s = str1.equals(str2);

    }
}
