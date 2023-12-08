package cn.foxtech.device.protocol.v1.telecom.demo;

import cn.foxtech.device.protocol.v1.telecom.core.TelecomProtocol;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import sun.nio.cs.ext.GBK;


import java.nio.charset.StandardCharsets;
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

        try {
            String txt = "";

            txt = new String(HexUtils.hexStringToByteArray("2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 0D 0A 32 30 32 33 C4 EA 31 31 D4 C2 32 36 C8 D5 20 31 36 CA B1 34 36 B7 D6 0D "),"GB2312");
            System.out.println(txt);

            txt = new String(HexUtils.hexStringToByteArray("0D "),"GB2312");
            System.out.println(txt);

            txt = new String(HexUtils.hexStringToByteArray("31 2F 35 20 0D"), "GB2312");
            System.out.println(txt);

            txt = new String(HexUtils.hexStringToByteArray("CF FB BB F0 CB A8 B0 B4 C5 A5 B1 A8 BB F0 BE AF 0D"), "GB2312");
            System.out.println(txt);

            txt = new String(HexUtils.hexStringToByteArray("2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 2D 0D 0A 32 30 32 33 C4 EA 31 31 D4 C2 32 36 C8 D5 20 31 36 CA B1 34 36 B7 D6 0D "), "GB2312");
            System.out.println(txt);

            txt = new String(HexUtils.hexStringToByteArray("0D "));
            System.out.println(txt);

            txt = new String(HexUtils.hexStringToByteArray("31 2F 35 20 0D"), "GB2312");
            System.out.println(txt);

            txt = new String(HexUtils.hexStringToByteArray("CA D6 B6 AF B1 A8 BE AF B0 B4 C5 A5 B1 A8 BB F0 BE AF 0D "), "GB2312");
            System.out.println(txt);

            txt = new String(HexUtils.hexStringToByteArray("0D 0A 50 6F 72 74 74 69 6D 65 20 54 69 6D 65 4F 75 74 20 53 79 73 74 65 6D 20 00 0D 0A 41 54 20 52 65 61 64 79 0D 0A "), "GB2312");
            System.out.println(txt);

            txt = new String(HexUtils.hexStringToByteArray("0D 0A 43 4F 4E 4E 45 43 54 20 4F 4B 0D 0A "), "GB2312");
            System.out.println(txt);



        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
