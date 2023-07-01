package cn.foxtech.device.protocol.gdana.digester;

import cn.foxtech.device.protocol.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.core.exception.ProtocolException;
import cn.foxtech.device.protocol.core.utils.HexUtils;

import java.util.HashMap;
import java.util.Map;

@FoxEdgeDeviceType(value = "全自动消解控制器", manufacturer = "广州格丹纳仪器有限公司")
public class DigesterProtocolResetMotor {
    /**
     * 查询传感器状态
     *
     * @param param 输入参数
     * @return 操作是否成功
     */
    @FoxEdgeOperate(name = "复位电机", type = FoxEdgeOperate.encoder)
    public static String encodePack(Map<String, Object> param) {
        Integer deviceAddress = (Integer) param.get("设备地址");
        Integer subDeviceAddress = (Integer) param.get("子设备地址");
        if (deviceAddress == null || subDeviceAddress == null) {
            throw new ProtocolException("缺失参数:设备地址/子设备地址");
        }

        String type = (String) param.get("电机类型");
        if (type == null) {
            throw new ProtocolException("缺失参数:电机类型");
        }


        DigesterEntity entity = new DigesterEntity();
        entity.setAddr(deviceAddress);
        entity.setSubAddr(subDeviceAddress);
        entity.setFunc(0x12);

        entity.setData(new byte[1]);
        if (type.equals("X轴电机")) {
            entity.getData()[0] = 1;
        } else if (type.equals("Y轴电机")) {
            entity.getData()[0] = 2;
        } else if (type.equals("升降电机1")) {
            entity.getData()[0] = 3;
        } else if (type.equals("升降电机2")) {
            entity.getData()[0] = 4;
        } else {
            throw new ProtocolException("类型范围:X轴电机/Y轴电机/升降电机1/升降电机2");
        }


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
    @FoxEdgeOperate(name = "复位电机", type = FoxEdgeOperate.decoder, mode = FoxEdgeOperate.result, timeout = 2000)
    public static Map<String, Object> decodePack(String hexString, Map<String, Object> param) {
        byte[] pack = HexUtils.hexStringToByteArray(hexString);

        // 解码报文
        DigesterEntity entity = DigesterProtocolFrame.decodePack(pack);
        if (entity == null) {
            throw new ProtocolException("报文格式不正确！");
        }

        if (entity.getFunc() != 0x92) {
            throw new ProtocolException("设备拒绝！");
        }


        // 解码数据
        byte[] dat = entity.getData();
        if (dat.length != 2) {
            throw new ProtocolException("数据长度不正确！");
        }

        Map<String, Object> value = new HashMap<>();

        String type = "未知类型";
        if (dat[0] == 1) {
            type = "X轴电机";
        }
        if (dat[0] == 2) {
            type = "Y轴电机";
        }
        if (dat[0] == 3) {
            type = "升降电机1";
        }
        if (dat[0] == 4) {
            type = "升降电机2";
        }

        value.put("电机类型", type);
        value.put("操作结果", dat[1] == 1);

        return value;
    }
}
