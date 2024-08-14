/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.demo;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

import java.util.HashMap;
import java.util.Map;

@FoxEdgeDeviceType(value = "LRW解码器", manufacturer = "Fox-Edge")
public class DemoProtocolGetVersion extends DemoProtocolFrame {
    @FoxEdgeOperate(name = "检查版本号", polling = true, type = FoxEdgeOperate.encoder)
    public static String encodePackVersion(Map<String, Object> param) {
        return HexUtils.byteArrayToHexString(encodePack((byte) 0x01));
    }

    @FoxEdgeOperate(name = "检查版本号", polling = true, type = FoxEdgeOperate.decoder)
    public static Map<String, Object> decodePackVersion(String hexString, Map<String, Object> param) {
        byte[] pack = HexUtils.hexStringToByteArray(hexString);

        // 解码报文
        DemoEntity entity = decodePack(pack);
        if (entity == null) {
            throw new ProtocolException("报文格式不正确！");
        }

        if (entity.getCmd() != (byte) 0x81) {
            throw new ProtocolException("设备拒绝！");
        }

        // 解码数据
        Map<String, Object> value = new HashMap<>();
        byte[] dat = entity.getDat();
        value.put("verSoft", dat[0]);
        value.put("verHard", dat[1]);
        value.put("verProt", dat[2]);

        return value;
    }
}
