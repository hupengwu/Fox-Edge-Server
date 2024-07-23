package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.telecom.core.entity.PduEntity;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        // 读版本：
        byte[] pdu = HexUtils.hexStringToByteArray("7e 31 30 30 31 36 30 34 46 30 30 30 30 46 44 39 45 0d");

        // 海悟空调用户参数设置-制冷模式温度设置：
        pdu = HexUtils.hexStringToByteArray("7e 31 30 30 31 36 30 34 39 41 30 30 36 38 36 30 30 31 42 46 43 35 33 0d");

        PduEntity entity = PduEntity.decodePdu(HexUtils.hexStringToByteArray("7e 31 30 30 31 36 30 30 30 41 30 34 32 30 30 30 30 30 30 30 30 30 30 33 43 30 30 30 32 30 30 30 30 30 30 30 30 30 30 31 39 30 39 30 30 30 31 30 30 30 33 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 30 31 39 30 30 30 32 30 30 30 32 46 31 30 34 0d "));

    }


}
