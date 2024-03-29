package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.haiwu.air.v2.GetParam;
import cn.foxtech.device.protocol.v1.haiwu.air.v2.GetVersion;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        // 读版本：
        byte[] pdu = HexUtils.hexStringToByteArray("7e 31 30 30 31 36 30 34 46 30 30 30 30 46 44 39 45 0d");

        // 海悟空调用户参数设置-制冷模式温度设置：
        pdu = HexUtils.hexStringToByteArray("7e 31 30 30 31 36 30 34 39 41 30 30 36 38 36 30 30 31 42 46 43 35 33 0d");


        //   PduEntity entity = PduEntity.decodePdu(pdu);


        Map<String, Object> param = new HashMap<>();
        param.put("devAddr", 1);
        String txt = GetVersion.encodePdu(param);
        txt = GetParam.encodePdu(param);

        //    GetVersion.decodePdu("7e 31 30 30 31 36 30 30 30 30 30 30 30 46 44 42 38 0d", param);
        Map<String, Object> result = GetParam.decodePdu("7e 31 30 30 31 36 30 30 30 41 30 34 32 32 30 32 30 32 30 32 30 30 30 32 33 30 30 30 30 30 30 35 41 32 30 32 30 30 30 31 39 30 39 30 30 30 31 30 30 30 33 32 30 32 30 30 30 30 30 30 30 31 38 30 30 32 32 30 30 30 30 30 30 30 32 30 30 30 32 46 30 45 45 0d", param);
    }


}
