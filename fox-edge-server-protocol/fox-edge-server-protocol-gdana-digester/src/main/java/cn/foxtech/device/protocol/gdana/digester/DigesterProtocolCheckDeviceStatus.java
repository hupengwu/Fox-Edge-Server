package cn.foxtech.device.protocol.gdana.digester;

import cn.foxtech.device.protocol.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.core.exception.ProtocolException;
import cn.foxtech.device.protocol.core.utils.HexUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 检查设备状态：这是轮询查询方法
 */
@FoxEdgeDeviceType(value = "全自动消解控制器", manufacturer = "广州格丹纳仪器有限公司")
public class DigesterProtocolCheckDeviceStatus {
    /**
     * 查询传感器状态
     *
     * @param param 输入参数
     * @return 操作是否成功
     */
    @FoxEdgeOperate(name = "检查设备状态", type = FoxEdgeOperate.encoder)
    public static String encodePack(Map<String, Object> param) {
        Integer deviceAddress = (Integer) param.get("设备地址");
        Integer subDeviceAddress = (Integer) param.get("子设备地址");
        if (deviceAddress == null || subDeviceAddress == null) {
            throw new ProtocolException("缺失参数:设备地址/子设备地址");
        }

        DigesterEntity entity = new DigesterEntity();
        entity.setAddr(deviceAddress);
        entity.setSubAddr(subDeviceAddress);
        entity.setFunc(0x01);


        // 编码
        byte[] pack = DigesterProtocolFrame.encodePack(entity);
        return HexUtils.byteArrayToHexString(pack);
    }

    /**
     * 查询传感器状态
     *
     * @param hexString 数据报文
     * @return 解码是否成功
     */
    @FoxEdgeOperate(name = "检查设备状态", type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> decodePack(String hexString, Map<String, Object> param) {
        byte[] pack = HexUtils.hexStringToByteArray(hexString);

        // 解码报文
        DigesterEntity entity = DigesterProtocolFrame.decodePack(pack);
        if (entity == null) {
            throw new ProtocolException("报文格式不正确！");
        }

        if (entity.getFunc() != 0x81) {
            throw new ProtocolException("设备拒绝！");
        }

        // 解码数据
        byte[] dat = entity.getData();
        if (dat.length != 15) {
            throw new ProtocolException("数据长度不正确！");
        }
        Map<String, Object> value = new HashMap<>();

        // 设备名称
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 11; i++) {
            sb.append((char) dat[i]);
        }
        value.put("设备名称", sb.toString());

        value.put("设备空闲", dat[11] == 0x00);
        value.put("看门狗复位", dat[12] == 0x01);
        value.put("消解仪孔数", (DigesterProtocolFrame.decodeBcd(dat[13])));

        return value;
    }
}
