package cn.foxtech.device.protocol.lrw;

import cn.foxtech.device.protocol.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.core.exception.ProtocolException;
import cn.foxtech.device.protocol.core.utils.HexUtils;

import java.util.HashMap;
import java.util.Map;

@FoxEdgeDeviceType(value = "LRW解码器")
public class LRWProtocolGetSensor extends LRWProtocolFrame {
    /**
     * 查询传感器状态
     *
     * @param param 输入参数
     * @return 操作是否成功
     */
    @FoxEdgeOperate(name = "查询传感器状态", polling = true, type = FoxEdgeOperate.encoder)
    public static String encodePackSensor(Map<String, Object> param) {
        return HexUtils.byteArrayToHexString(encodePack((byte) 0x02));
    }

    /**
     * 查询传感器状态
     *
     * @param hexString 数据报文
     * @return 解码是否成功
     */
    @FoxEdgeOperate(name = "查询传感器状态", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> decodePackSensor(String hexString, Map<String, Object> param) {
        byte[] pack = HexUtils.hexStringToByteArray(hexString);

        // 解码报文
        LRWEntity entity = decodePack(pack);
        if (entity == null) {
            throw new ProtocolException("报文格式不正确！");
        }

        if (entity.getCmd() != (byte) 0x82) {
            throw new ProtocolException("设备拒绝！");
        }

        // 解码数据
        byte[] dat = entity.getDat();
        boolean sample = (dat[0] != 0x00);
        boolean plate = (dat[1] != 0x00);
        boolean stick = (dat[2] != 0x00);
        boolean interval = (dat[3] != 0x00);
        boolean spark = (dat[4] != 0x00);
        boolean box = (dat[5] != 0x00);

        int tempH = dat[6] & 0xff;
        int tempL = dat[7] & 0xff;
        int temp = (tempH * 0x100 + tempL) / 10;

        Map<String, Object> value = new HashMap<>();
        value.put("sample", sample);
        value.put("plate", plate);
        value.put("stick", stick);
        value.put("interval", interval);
        value.put("spark", spark);
        value.put("box", box);
        value.put("temp", temp);

        return value;
    }
}
