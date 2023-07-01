package cn.foxtech.device.protocol.gdana.digester;

import cn.foxtech.device.protocol.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.core.exception.ProtocolException;
import cn.foxtech.device.protocol.core.utils.HexUtils;

import java.util.HashMap;
import java.util.Map;

@FoxEdgeDeviceType(value = "全自动消解控制器", manufacturer = "广州格丹纳仪器有限公司")
public class DigesterProtocolSolutionVolume {
    /**
     * 查询传感器状态
     *
     * @param param 输入参数
     * @return 操作是否成功
     */
    @FoxEdgeOperate(name = "读取溶液体积", type = FoxEdgeOperate.encoder)
    public static String encodePack(Map<String, Object> param) {
        Integer deviceAddress = (Integer) param.get("设备地址");
        Integer subDeviceAddress = (Integer) param.get("子设备地址");
        if (deviceAddress == null || subDeviceAddress == null) {
            throw new ProtocolException("缺失参数:设备地址/子设备地址");
        }

        DigesterEntity entity = new DigesterEntity();
        entity.setAddr(deviceAddress);
        entity.setSubAddr(subDeviceAddress);
        entity.setFunc((byte) 0x01);

        byte[] pack = DigesterProtocolFrame.encodePack(entity);

        return HexUtils.byteArrayToHexString(pack);
    }

    /**
     * 查询传感器状态
     *
     * @param hexString 数据报文
     * @return 解码是否成功
     */
    @FoxEdgeOperate(name = "读取溶液体积", type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> decodePack(String hexString, Map<String, Object> param) {
        byte[] pack = HexUtils.hexStringToByteArray(hexString);

        // 解码报文
        DigesterEntity entity = DigesterProtocolFrame.decodePack(pack);
        if (entity == null) {
            throw new ProtocolException("报文格式不正确！");
        }

        if (entity.getFunc() != (byte) 0x82) {
            throw new ProtocolException("设备拒绝！");
        }

        // 解码数据
        byte[] dat = entity.getData();
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
