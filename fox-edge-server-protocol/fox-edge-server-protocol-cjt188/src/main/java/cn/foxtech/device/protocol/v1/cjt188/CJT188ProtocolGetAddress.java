/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.cjt188;


import cn.foxtech.device.protocol.v1.cjt188.core.CJT188Entity;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.cjt188.core.CJT188ProtocolFrame;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@FoxEdgeDeviceType(value = "CJT188", manufacturer = "Fox Edge")
public class CJT188ProtocolGetAddress extends CJT188ProtocolFrame {

    /**
     * 读取数据
     * 发送：68 aa aa aa aa aa aa aa aa 03 03 81 0a 00 49 16
     *
     * @param param
     * @return
     */
    @FoxEdgeOperate(name = "读表地址", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static String encode(Map<String, Object> param) {


        CJT188Entity entity = new CJT188Entity();
        entity.getType().setValue(0xAA);
        entity.getAddress().setValue("AA AA AA AA AA AA AA");
        entity.getCtrl().setValue(0x03);

        byte[] data = new byte[3];
        data[0] = (byte) 0x81;
        data[1] = (byte) 0x0A;
        data[2] = (byte) 0x00;
        entity.setData(data);

        byte[] pack = CJT188ProtocolFrame.encodePack(entity);
        return HexUtils.byteArrayToHexString(pack);
    }

    /**
     * 读取数据
     * 应答数据：68 10 01 00 00 05 08 00 00 83 03 81 0A 00 97 16
     *
     * @param hexString
     * @return
     */
    @FoxEdgeOperate(name = "读表地址", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> decode(String hexString, Map<String, Object> param) {
        byte[] pack = HexUtils.hexStringToByteArray(hexString);

        // 解码报文
        CJT188Entity entity = CJT188ProtocolFrame.decodePack(pack);
        if (entity == null) {
            throw new ProtocolException("报文格式不正确！");
        }

        // 解码数据
        byte[] data = entity.getData();
        if (data.length != 3) {
            throw new ProtocolException("数据长度不正确！");
        }

        Map<String, Object> value = new HashMap<>();
        value.put("type", entity.getType().getValue());
        value.put("address", entity.getAddress().getValue());
        value.put("仪表类型", entity.getType().getName());
        value.put("设备地址", entity.getAddress().getValue());

        // 检查：标识是否为 1F 09
        if (((data[0] & 0xff) != 0x81) || ((data[1] & 0xff) != 0x0A)) {
            throw new ProtocolException("返回的标识不正确！");
        }
        // 序号
        value.put("sn", data[2] & 0xff);

        return value;
    }
}
