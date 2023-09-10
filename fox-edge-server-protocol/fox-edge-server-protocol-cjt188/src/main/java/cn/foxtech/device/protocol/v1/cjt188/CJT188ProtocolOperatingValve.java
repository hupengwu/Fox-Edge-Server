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
public class CJT188ProtocolOperatingValve extends CJT188ProtocolFrame {

    /**
     * 编码函数
     * 发送：FE FE FE 68 10 01 00 00 05 08 00 00 2A 04 A0 17 00 55 C0 16
     * 发送：FE FE FE 68 10 01 00 00 05 08 00 00 2A 04 A0 17 00 99 04 16
     *
     * @param param
     * @return
     */
    @FoxEdgeOperate(name = "操作阀门", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static String encode(Map<String, Object> param) {
        Integer type = (Integer) param.get("type");
        String address = (String) param.get("address");
        Integer operate = (Integer) param.get("operate");

        // 检查输入参数
        if (MethodUtils.hasEmpty(type, address, operate)) {
            throw new ProtocolException("输入参数不能为空:type, address, operate");
        }

        CJT188Entity entity = new CJT188Entity();
        entity.getType().setValue(type);
        entity.getAddress().setValue(address);
        entity.getCtrl().setValue(0x2A);

        byte[] data = new byte[4];
        data[0] = (byte) 0xA0;
        data[1] = (byte) 0x17;
        data[2] = (byte) 0x00;

        // 操作
        data[3] = operate.byteValue();

        entity.setData(data);

        byte[] pack = CJT188ProtocolFrame.encodePack(entity);
        return HexUtils.byteArrayToHexString(pack);
    }

    /**
     * 解码函数
     * 接收：FE FE FE 68 10 01 00 00 05 08 00 00 AA 05 A0 17 00 00 FF EB 16
     * 接收：FE FE FE 68 10 01 00 00 05 08 00 00 AA 05 A0 17 00 01 FF EC 16
     *
     * @param hexString
     * @return
     */
    @FoxEdgeOperate(name = "操作阀门", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> decode(String hexString, Map<String, Object> param) {
        byte[] pack = HexUtils.hexStringToByteArray(hexString);

        // 解码报文
        CJT188Entity entity = CJT188ProtocolFrame.decodePack(pack);
        if (entity == null) {
            throw new ProtocolException("报文格式不正确！");
        }

        // 检查控制码
        if (entity.getCtrl().getValue() != 0xAA && entity.getCtrl().getValue() != 0x2A) {
            throw new ProtocolException("控制码不正确！");
        }

        // 解码数据
        byte[] data = entity.getData();
        if (data.length != 5) {
            throw new ProtocolException("数据长度不正确！");
        }

        Map<String, Object> value = new HashMap<>();

        // 检查：标识是否为 A0 18
        if (((data[0] & 0xff) != 0xA0) || ((data[1] & 0xff) != 0x17)) {
            throw new ProtocolException("返回的标识不正确！");
        }
        // 序号
        value.put("sn", data[2] & 0xff);

        value.put("状态1", data[3] & 0xff);
        value.put("状态2", data[4] & 0xff);

        return value;
    }
}

