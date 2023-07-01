package cn.foxtech.device.protocol.gdana.digester;

import cn.foxtech.device.protocol.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.core.exception.ProtocolException;
import cn.foxtech.device.protocol.core.utils.HexUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 添加样本：这是一个手动操作方法，不是轮询查询方法
 */
@FoxEdgeDeviceType(value = "全自动消解控制器", manufacturer = "广州格丹纳仪器有限公司")
public class DigesterProtocolAddSample {
    /**
     * 查询传感器状态
     *
     * @param param 输入参数
     * @return 操作是否成功
     */
    @FoxEdgeOperate(name = "添加样本", type = FoxEdgeOperate.encoder)
    public static String encodePack(Map<String, Object> param) {
        Integer deviceAddress = (Integer) param.get("设备地址");
        Integer subDeviceAddress = (Integer) param.get("子设备地址");
        if (deviceAddress == null || subDeviceAddress == null) {
            throw new ProtocolException("缺失参数:设备地址/子设备地址");
        }

        Integer speed = (Integer) param.get("速度档位");
        List<Map<String, Object>> infoList = (List<Map<String, Object>>) param.get("孔位参数");
        if (speed == null || infoList == null) {
            throw new ProtocolException("缺失参数:速度档位/孔位参数");
        }


        DigesterEntity entity = new DigesterEntity();
        entity.setAddr(deviceAddress);
        entity.setSubAddr(subDeviceAddress);
        entity.setFunc((byte) 0x13);

        entity.setData(new byte[1 + 4 * infoList.size()]);
        byte[] data = entity.getData();

        int index = 0;
        data[0] = speed.byteValue();
        for (Map<String, Object> info : infoList) {
            Integer number = (Integer) info.get("孔位");
            Integer channel = (Integer) info.get("试剂通道");
            Integer vol = (Integer) info.get("体积");

            if (number == null || channel == null || vol == null) {
                throw new ProtocolException("缺失参数:孔位/试剂通道/体积");
            }


            data[1 + index * 4] = number.byteValue();
            data[2 + index * 4] = channel.byteValue();
            data[3 + index * 4] = (byte) ((vol.intValue() >> 8) & 0xff);
            data[4 + index * 4] = (byte) ((vol.intValue() >> 0) & 0xff);

            index++;
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
    @FoxEdgeOperate(name = "添加样本", type = FoxEdgeOperate.decoder, mode = FoxEdgeOperate.result, timeout = 2000)
    public static Map<String, Object> decodePack(String hexString, Map<String, Object> param) {
        byte[] pack = HexUtils.hexStringToByteArray(hexString);

        // 解码报文
        DigesterEntity entity = DigesterProtocolFrame.decodePack(pack);
        if (entity == null) {
            throw new ProtocolException("报文格式不正确！");
        }

        if (entity.getFunc() != 0x93) {
            throw new ProtocolException("设备拒绝！");
        }


        // 解码数据
        byte[] data = entity.getData();
        if (data.length % 5 != 0) {
            throw new ProtocolException("数据长度不正确！");
        }


        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < data.length / 5; i++) {
            Map<String, Object> info = new HashMap<>();
            info.put("孔位", data[0 + i * 5]);
            info.put("操作成功", data[1 + i * 5] == 1);
            info.put("试剂通道", data[2 + i * 5]);
            info.put("体积", ((data[3 + i * 5] & 0xff) << 8) + ((data[4 + i * 5] & 0xff)));
            result.add(info);
        }


        Map<String, Object> value = new HashMap<>();
        value.put("操作结果", result);

        return value;
    }
}
