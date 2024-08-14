/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.haier.ycj.a002;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.haier.ycj.a002.entity.PduEntity;
import cn.foxtech.device.protocol.v1.haier.ycj.a002.enums.Fault;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;

import java.util.HashMap;
import java.util.Map;

@FoxEdgeDeviceType(value = "海尔空调-YCJ-A000", manufacturer = "海尔集团公司")
public class YcjA002GetMonitorStatus {
    @FoxEdgeOperate(name = "查询系统监视状态", polling = true, type = FoxEdgeOperate.encoder, mode = FoxEdgeOperate.status, timeout = 2000)
    public static String encodePdu(Map<String, Object> param) {
        Integer devAddr = (Integer) param.get("devAddr");

        if (MethodUtils.hasEmpty(devAddr)) {
            throw new ProtocolException("参数缺失：devAddr");
        }

        PduEntity entity = new PduEntity();

        entity.setCmd((byte) 0x3E);
        entity.setDevAddr(devAddr);

        byte[] pdu = PduEntity.encodePdu(entity);

        return HexUtils.byteArrayToHexString(pdu);
    }

    @FoxEdgeOperate(name = "查询系统监视状态", polling = true, type = FoxEdgeOperate.decoder, mode = FoxEdgeOperate.status, timeout = 2000)
    public static Map<String, Object> decodePdu(String hexString, Map<String, Object> param) {
        byte[] pdu = HexUtils.hexStringToByteArray(hexString);

        PduEntity entity = PduEntity.decodePdu(pdu);

        if (entity.getCmd() != 0x3E) {
            throw new ProtocolException("返回的命令字不正确!");
        }
        if (entity.getData().length != 4) {
            throw new ProtocolException("返回的数据长度不正确!");
        }


        Map<String, Object> result = new HashMap<>();
        result.put("devAddr", entity.getDevAddr());

        byte dat0 = entity.getData()[0];

        // 主机故障信息
        Fault fault = Fault.getEnum(dat0 & 0x1f);
        if (fault == null) {
            fault = Fault.value0;
        }
        result.put("主机故障信息", fault.getName());

        // 主机开/关机检测
        result.put("主机开关机检测", (dat0 & 0x20) != 0);


        // 检测器停电补偿检测
        result.put("检测器停电补偿检测", (dat0 & 0x40) != 0);


        // 空调器停电补偿检测
        result.put("空调器停电补偿检测", (dat0 & 0x80) != 0);


        byte dat1 = entity.getData()[1];

        // 主机室内温度传感器
        if ((dat1 & 0xff) == 0x00) {
            result.put("温度传感器", "传感器开路");
        } else if ((dat1 & 0xff) == 0x0f) {
            result.put("温度传感器", "未设该传感器");
        } else if ((dat1 & 0xff) == 0xff) {
            result.put("温度传感器", "传感器短路");
        } else {
            result.put("温度传感器", ((dat1 & 0xff) - 64) + "℃");
        }


        byte dat2 = entity.getData()[2];

        // 从机故障信息
        fault = Fault.getEnum(dat2 & 0x1f);
        if (fault == null) {
            fault = Fault.value0;
        }
        result.put("从机故障信息", fault.getName());


        // 从机开关机检测
        result.put("从机开关机检测", (dat2 & 0x20) != 0);

        // 电子锁（取消--单/双机检测0：单机1：双机）
        result.put("电子锁", (dat2 & 0x40) != 0);

        // 控制信息重置标志
        result.put("控制信息重置标志", (dat2 & 0x80) != 0);

        byte dat3 = entity.getData()[3];

        result.put("湿度检测", dat3 & 0xff);
        return result;
    }

}
