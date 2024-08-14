/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.haier.ycj.a002;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.haier.ycj.a002.entity.PduEntity;
import cn.foxtech.device.protocol.v1.haier.ycj.a002.enums.Mode;
import cn.foxtech.device.protocol.v1.haier.ycj.a002.enums.Speed;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;

import java.util.HashMap;
import java.util.Map;

@FoxEdgeDeviceType(value = "海尔空调-YCJ-A000", manufacturer = "海尔集团公司")
public class YcjA002SetControlStatus {
    @FoxEdgeOperate(name = "控制", polling = true, type = FoxEdgeOperate.encoder, mode = FoxEdgeOperate.status, timeout = 2000)
    public static String encodePdu(Map<String, Object> param) {
        String mode = (String) param.get("mode");
        Integer temp = (Integer) param.get("temp");
        Boolean open = (Boolean) param.get("open");
        Boolean damper = (Boolean) param.get("damper");
        String speed = (String) param.get("speed");
        Integer devAddr = (Integer) param.get("devAddr");

        if (MethodUtils.hasEmpty(mode, temp, open, damper, speed, devAddr)) {
            throw new ProtocolException("参数缺失：mode, temp, open, damper, speed, devAddr");
        }

        PduEntity entity = new PduEntity();

        // 命令字：0x01
        entity.setCmd((byte) 0x01);
        entity.setDevAddr(devAddr);
        entity.setData(new byte[2]);

        byte dat0 = 0;
        byte dat1 = 0;

        // 设定温度
        dat0 = (byte) ((temp - 16) & 0x0f);

        // 模式
        Mode md = Mode.getEnum(mode);
        if (md != null) {
            dat0 |= md.getCode() << 4;
        }

        // 开关
        if (open) {
            dat0 |= (byte) 0x80;
        }

        // 风速
        Speed sp = Speed.getEnum(speed);
        if (sp == null) {
            sp = Speed.value0;
        }
        dat1 |= (byte) sp.getCode();

        // 挡风板
        if (damper) {
            dat1 |= (byte) 0x08;
        }

        entity.getData()[0] = dat0;
        entity.getData()[1] = dat1;

        byte[] pdu = PduEntity.encodePdu(entity);

        return HexUtils.byteArrayToHexString(pdu);
    }

    @FoxEdgeOperate(name = "控制", polling = true, type = FoxEdgeOperate.decoder, mode = FoxEdgeOperate.status, timeout = 2000)
    public static Map<String, Object> decodePdu(String hexString, Map<String, Object> param) {
        byte[] pdu = HexUtils.hexStringToByteArray(hexString);

        PduEntity entity = PduEntity.decodePdu(pdu);

        Map<String, Object> result = new HashMap<>();

        result.put("devAddr", entity.getDevAddr());

        result.put("success", entity.getCmd() == 0x10);


        return result;
    }
}
