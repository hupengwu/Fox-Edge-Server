package cn.foxtech.device.protocol.gdana.digester;

import cn.foxtech.device.protocol.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.core.exception.ProtocolException;
import cn.foxtech.device.protocol.core.utils.HexUtils;

import java.util.HashMap;
import java.util.Map;

@FoxEdgeDeviceType(value = "全自动消解控制器", manufacturer = "广州格丹纳仪器有限公司")
public class DigesterProtocolSupport {
    /**
     * 查询传感器状态
     *
     * @param param 输入参数
     * @return 操作是否成功
     */
    @FoxEdgeOperate(name = "操作支架", type = FoxEdgeOperate.encoder)
    public static String encodePack(Map<String, Object> param) {
        Integer deviceAddress = (Integer) param.get("设备地址");
        Integer subDeviceAddress = (Integer) param.get("子设备地址");
        if (deviceAddress == null || subDeviceAddress == null) {
            throw new ProtocolException("缺失参数:设备地址/子设备地址");
        }

        Integer number = (Integer) param.get("支架编号");
        if (number == null) {
            throw new ProtocolException("缺失参数:支架编号");
        }
        String flag = (String) param.get("标记");
        if (flag == null) {
            throw new ProtocolException("缺失参数:标记");
        }


        DigesterEntity entity = new DigesterEntity();
        entity.setAddr(deviceAddress);
        entity.setSubAddr(subDeviceAddress);
        entity.setFunc(0x14);

        entity.setData(new byte[2]);
        // 支架编号
        entity.getData()[0] = number.byteValue();

        if (flag.equals("抬升")) {
            entity.getData()[1] = 1;
        } else if (flag.equals("复位")) {
            entity.getData()[1] = 0;
        } else {
            throw new ProtocolException("标记范围:抬升/复位");
        }


        // 编码
        byte[] pack = DigesterProtocolFrame.encodePack(entity);
        return HexUtils.byteArrayToHexString(pack);
    }

    /**
     * 操作支架
     *
     * @param hexString 数据报文
     * @return 解码是否成功
     */
    @FoxEdgeOperate(name = "操作支架", type = FoxEdgeOperate.decoder, mode = FoxEdgeOperate.result, timeout = 2000)
    public static Map<String, Object> decodePack(String hexString, Map<String, Object> param) {
        byte[] pack = HexUtils.hexStringToByteArray(hexString);

        // 解码报文
        DigesterEntity entity = DigesterProtocolFrame.decodePack(pack);
        if (entity == null) {
            throw new ProtocolException("报文格式不正确！");
        }

        if (entity.getFunc() != 0x94) {
            throw new ProtocolException("设备拒绝！");
        }

        // 解码数据
        byte[] dat = entity.getData();
        if (dat.length != 1) {
            throw new ProtocolException("数据长度不正确！");
        }
        Map<String, Object> value = new HashMap<>();

        value.put("操作结果", dat[0] == 0x01);
        return value;
    }
}
