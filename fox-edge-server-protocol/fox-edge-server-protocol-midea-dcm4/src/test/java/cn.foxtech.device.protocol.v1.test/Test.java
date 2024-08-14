/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.midea.dcm4.GetAddress;
import cn.foxtech.device.protocol.v1.midea.dcm4.GetStatus;
import cn.foxtech.device.protocol.v1.midea.dcm4.SetParam;
import cn.foxtech.device.protocol.v1.midea.dcm4.entity.PduEntity;
import cn.foxtech.device.protocol.v1.midea.dcm4.uitls.BitValueUtils;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

import java.util.HashMap;
import java.util.Map;

public class Test {
    public static void main(String[] args) {
        HashMap param = new HashMap<>();


        String hex = GetAddress.encodePdu(param);
        Map<String, Object> result = GetAddress.decodePdu("aa a1 a0 c9 a1 00 b1 00 b2 00 00 00 f2 55", new HashMap<>());

        param.put("devAddr", 1);
        hex = GetStatus.encodePdu(param);
        result = GetStatus.decodePdu("aa a1 a0 c2 b1 80 18 02 19 00 00 00 99 55", param);

        result.clear();
        result.put("devAddr", 1);

        result.put("强劲功能", "关闭");
        result.put("风速", "自动风");
        result.put("设定模式", "自动");
        result.put("空调开关", "开机");
        result.put("设定温度", 24);
        hex = SetParam.encodePdu(result);
        SetParam.decodePdu("aa a1 a0 c1 b1 80 18 00 00 00 00 00 b5 55", param);

        long value = BitValueUtils.getBitsValue(0x0f, 0, 3);
        // 读版本：
        byte[] send = HexUtils.hexStringToByteArray("aa a0 a1 c9 a1 00 00 00 00 00 00 00 55 55");
        byte[] recv = HexUtils.hexStringToByteArray("aa a1 a0 c9 a1 00 b1 00 b2 00 00 00 f2 55");

        PduEntity entity = PduEntity.decodePdu(HexUtils.hexStringToByteArray("aa a1 a0 c9 a1 00 b1 00 b2 00 00 00 f2 55 "));

    }


}
