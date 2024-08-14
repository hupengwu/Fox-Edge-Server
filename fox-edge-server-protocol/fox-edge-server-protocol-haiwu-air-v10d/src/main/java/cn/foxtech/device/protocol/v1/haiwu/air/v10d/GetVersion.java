/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.haiwu.air.v10d;


import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.telecom.core.entity.PduEntity;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 发送范例：7e 31 30 30 31 36 30 34 46 30 30 30 30 46 44 39 45 0d
 * 返回范例：7e 31 30 30 31 36 30 30 30 30 30 30 30 46 44 42 38 0d
 */
@FoxEdgeDeviceType(value = "基站空调(V1.0D)", manufacturer = "广东海悟科技有限公司")
public class GetVersion {
    @FoxEdgeOperate(name = "读取版本号", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static String encodePdu(Map<String, Object> param) {
        // 取出设备地址
        Integer devAddr = (Integer) param.get("devAddr");

        // 检查输入参数
        if (MethodUtils.hasEmpty(devAddr)) {
            throw new ProtocolException("输入参数不能为空:devAddr");
        }

        PduEntity pduEntity = new PduEntity();
        pduEntity.setAddr(devAddr);
        pduEntity.setVer(0x10);
        pduEntity.setCid1(0x60);
        pduEntity.setCid2(0x4F);

        byte[] pdu = PduEntity.encodePdu(pduEntity);


        return HexUtils.byteArrayToHexString(pdu);
    }

    @FoxEdgeOperate(name = "读取版本号", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> decodePdu(String hexString, Map<String, Object> param) {
        byte[] pdu = HexUtils.hexStringToByteArray(hexString);

        PduEntity entity = PduEntity.decodePdu(pdu);

        if (entity.getCid1() != 0x60) {
            throw new ProtocolException("返回的CID1不正确!");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("版本号", String.format("%1x.%1x", entity.getVer() / 0x10,entity.getVer() % 0x10));

        return result;
    }
}