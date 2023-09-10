package cn.foxtech.device.protocol.v1.cjt188;


import cn.foxtech.device.protocol.v1.cjt188.core.CJT188Entity;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.cjt188.core.CJT188ProtocolFrame;
import cn.foxtech.device.protocol.v1.cjt188.core.CJT188Unit;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.utils.BcdUtils;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@FoxEdgeDeviceType(value = "CJT188", manufacturer = "Fox Edge")
public class CJT188ProtocolGetData extends CJT188ProtocolFrame {

    /**
     * 读取数据
     * 发送：68 10 01 00 00 05 08 00 00 01 03 1F 90 00 39 16
     *
     * @param param
     * @return
     */
    @FoxEdgeOperate(name = "读表数据", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static String encode(Map<String, Object> param) {
        Integer type = (Integer) param.get("type");
        String address = (String) param.get("address");

        // 检查输入参数
        if (MethodUtils.hasEmpty(type, address)) {
            throw new ProtocolException("输入参数不能为空:type, address");
        }

        CJT188Entity entity = new CJT188Entity();
        entity.getType().setValue(type);
        entity.getAddress().setValue(address);
        entity.getCtrl().setValue(0x01);

        byte[] data = new byte[3];
        data[0] = (byte) 0x90;
        data[1] = (byte) 0x1f;
        data[2] = (byte) 0x00;
        entity.setData(data);

        byte[] pack = CJT188ProtocolFrame.encodePack(entity);
        return HexUtils.byteArrayToHexString(pack);
    }

    /**
     * 读取数据
     * 应答数据：68 10 44 33 22 11 00 33 78 81 16 1F 90 00 00 77 66 55 2C 00 77 66 55 2C 31 01 22 11 05 15 20 21 84 13 16
     *
     * @param hexString
     * @return
     */
    @FoxEdgeOperate(name = "读表数据", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> decode(String hexString, Map<String, Object> param) {
        byte[] pack = HexUtils.hexStringToByteArray(hexString);

        // 解码报文
        CJT188Entity entity = CJT188ProtocolFrame.decodePack(pack);
        if (entity == null) {
            throw new ProtocolException("报文格式不正确！");
        }

        // 解码数据
        byte[] data = entity.getData();
        if (data.length != 22) {
            throw new ProtocolException("数据长度不正确！");
        }

        Map<String, Object> value = new HashMap<>();

        // 检查：标识是否为 09 1F
        if (((data[0] & 0xff) != 0x90) || ((data[1] & 0xff) != 0x1f)) {
            throw new ProtocolException("返回的标识不正确！");
        }
        // 序列号
        value.put("sn", data[2] & 0xff);

        // 累计用量
        long sum = 0;
        sum += BcdUtils.bcd2int(data[6]) * 1;
        sum += BcdUtils.bcd2int(data[5]) * 100;
        sum += BcdUtils.bcd2int(data[4]) * 10000;
        sum += BcdUtils.bcd2int(data[3]) * 1000000;
        value.put("累计用量", sum);

        // 单位
        value.put("累计用量单位", CJT188Unit.getUnit(data[7] & 0xff));

        // 本月用量
        long month = 0;
        month += BcdUtils.bcd2int(data[11]) * 1;
        month += BcdUtils.bcd2int(data[10]) * 100;
        month += BcdUtils.bcd2int(data[9]) * 10000;
        month += BcdUtils.bcd2int(data[8]) * 1000000;
        value.put("本月用量", month);
        value.put("本月用量单位", CJT188Unit.getUnit(data[12] & 0xff));

        // 时间
        StringBuilder sb = new StringBuilder();
        sb.append(BcdUtils.bcd2int(data[19]));
        sb.append(BcdUtils.bcd2int(data[18]));
        sb.append("-");
        sb.append(BcdUtils.bcd2int(data[17]));
        sb.append("-");
        sb.append(BcdUtils.bcd2int(data[16]));
        sb.append(" ");
        sb.append(BcdUtils.bcd2int(data[15]));
        sb.append(":");
        sb.append(BcdUtils.bcd2int(data[14]));
        sb.append(":");
        sb.append(BcdUtils.bcd2int(data[13]));
        value.put("时间", sb.toString());

        value.put("状态1", data[19] & 0xff);
        value.put("状态2", data[20] & 0xff);

        return value;
    }
}
