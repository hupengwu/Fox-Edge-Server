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
import cn.foxtech.device.protocol.v1.utils.MethodUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 点对点的修改地址
 */
@FoxEdgeDeviceType(value = "CJT188", manufacturer = "Fox Edge")
public class CJT188ProtocolSetAddress extends CJT188ProtocolFrame {

    /**
     * 编码函数
     * 发送：68 aa aa aa aa aa aa aa aa 03 03 81 0a 00 49 16
     *
     * @param param
     * @return
     */
    @FoxEdgeOperate(name = "设置地址", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static String encode(Map<String, Object> param) {
        Integer type = (Integer) param.get("type");
        String address = (String) param.get("address");
        String newAddress = (String) param.get("newAddress");

        // 检查输入参数
        if (MethodUtils.hasEmpty(type, address)) {
            throw new ProtocolException("输入参数不能为空:type, address");
        }

        CJT188Entity entity = new CJT188Entity();
        entity.getType().setValue(type);
        entity.getAddress().setValue(address);
        entity.getCtrl().setValue(0x15);

        byte[] data = new byte[10];
        data[0] = (byte) 0xA0;
        data[1] = (byte) 0x18;
        data[2] = (byte) 0x00;

        // 设备地址:BCD
        newAddress = newAddress.replace(" ", "");
        if (newAddress == null || newAddress.length() != 14) {
            throw new ProtocolException("地址码长度不正确，应该是14字节长度的BCD格式文本字符串");
        }
        data[3] = (byte) encodeBcd(newAddress.charAt(0), newAddress.charAt(1));
        data[4] = (byte) encodeBcd(newAddress.charAt(2), newAddress.charAt(3));
        data[5] = (byte) encodeBcd(newAddress.charAt(4), newAddress.charAt(5));
        data[6] = (byte) encodeBcd(newAddress.charAt(6), newAddress.charAt(7));
        data[7] = (byte) encodeBcd(newAddress.charAt(8), newAddress.charAt(9));
        data[8] = (byte) encodeBcd(newAddress.charAt(10), newAddress.charAt(11));
        data[9] = (byte) encodeBcd(newAddress.charAt(12), newAddress.charAt(13));

        entity.setData(data);

        byte[] pack = CJT188ProtocolFrame.encodePack(entity);
        return HexUtils.byteArrayToHexString(pack);
    }

    /**
     * 解码函数
     * 应答数据：68 10 01 00 00 05 08 00 00 95 03 A0 18 00 D6 16
     *
     * @param hexString
     * @return
     */
    @FoxEdgeOperate(name = "设置地址", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> decode(String hexString, Map<String, Object> param) {
        byte[] pack = HexUtils.hexStringToByteArray(hexString);

        // 解码报文
        CJT188Entity entity = CJT188ProtocolFrame.decodePack(pack);
        if (entity == null) {
            throw new ProtocolException("报文格式不正确！");
        }

        // 检查控制码
        if (entity.getCtrl().getValue() != 0x15) {
            throw new ProtocolException("控制码不正确！");
        }

        // 解码数据
        byte[] data = entity.getData();
        if (data.length != 10) {
            throw new ProtocolException("数据长度不正确！");
        }

        Map<String, Object> value = new HashMap<>();
        value.put("type", entity.getType().getValue());
        value.put("address", entity.getAddress().getValue());
        value.put("仪表类型", entity.getType().getName());
        value.put("设备地址", entity.getAddress().getValue());

        // 检查：标识是否为 A0 18
        if (((data[0] & 0xff) != 0xA0) || ((data[1] & 0xff) != 0x18)) {
            throw new ProtocolException("返回的标识不正确！");
        }
        // 序号
        value.put("sn", data[2] & 0xff);

        return value;
    }
}
