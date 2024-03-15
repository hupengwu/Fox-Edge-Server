package cn.foxtech.device.protocol.v1.test;


import cn.foxtech.device.protocol.v1.telecom.core.TelecomProtocol;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class TestTelecomProtocol {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String txtx = HexUtils.byteArrayToHexString("##0457QN=20210320163101890;ST=32;CN=2011;PW=123456;MN=81733553213013;Flag=4;CP=&&DataTime=20210320013400;w00000-Rtd=181.682,w00000-Flag=N;w21001-SampleTime=20210320005400,w21001-Rtd=45.160,w21001-Flag=N;w21011-SampleTime=20210320013400,w21011-Rtd=1.970,w21011-Flag=N;w21003-SampleTime=20210320013400,w21003-Rtd=53.131,w21003-Flag=N;w01018-SampleTime=20210320013400,w01018-Rtd=194.200,w01018-Flag=N;w01001-SampleTime=20210320013406,w01001-Rtd=7.496,w01001-Flag=N&&6E80\r\n".getBytes());
        byte[] arrCmd = HexUtils.hexStringToByteArray("1B 36 30 38 2F 31 32 2F 30 32 20 20 31 37 3A 32 34 0A 20 20 30 30 34  2D 30 30 34 0A1B 39 CAD7 B4 CE BB F0 BE AF  0AC8 B9 C2A5 D2 BB B2 E3 C9 CF BF D5 0A B8 D0 D1 CC CC BD B2 E2 C6 F7 0A0A");
        String txt1 = new String(arrCmd, "GB2312");
        System.out.println(txt1);
        boolean t = txt1.startsWith("\u001B");// 以utf8的1B字符开始

        arrCmd = HexUtils.hexStringToByteArray("1B 36  30 38 2F 31 32 2F 30 32 20 20 31 37 3A 32 31 0A 20 20 30 30 36  2D 30 30 37  0A 1B 39 BB F0 BE AF  BA F3 D0 F8 0A D6 D0D1 CC C1F9 B2E3 BC C6 BB AE B2 BF 0A B8 D0D1 CC CC BD B2E2 C6 F7 0A0A");
        txt1 = new String(arrCmd, "GB2312");
        System.out.println(txt1);


        arrCmd = HexUtils.hexStringToByteArray("1B 36 30 38 2F 31 32 2F 30 32 20 20 31 37 3A 32 35 0A 20 20 0A 1B 39 CF B5 CD B3 B8 B4 CE BB 0A 0A");
        txt1 = new String(arrCmd, "GB2312");
        System.out.println(txt1);


        arrCmd = HexUtils.hexStringToByteArray("7E323030323431343430303030464441460D");

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
