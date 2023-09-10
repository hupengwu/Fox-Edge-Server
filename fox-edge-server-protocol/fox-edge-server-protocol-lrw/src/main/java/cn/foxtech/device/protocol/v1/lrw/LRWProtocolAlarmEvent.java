package cn.foxtech.device.protocol.v1.lrw;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeReport;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 告警通知的范例
 */
@FoxEdgeDeviceType(value = "LRW解码器")
public class LRWProtocolAlarmEvent extends LRWProtocolFrame {

    /**
     * FoxEdgeEvent注解，指明这是一个设备主动上报，所有它只有解码函数
     * FoxEdgeEventType注解指明这是一种告警（上报后，设备自己不保留）
     * 主动上报的告警，因为不是问答式参数，所有没有编码函数
     *
     * @param hexString
     * @param param
     * @return
     */
    @FoxEdgeReport(type = FoxEdgeReport.alarm)
    @FoxEdgeOperate(name = "告警状态", mode = FoxEdgeOperate.status, polling = false, type = FoxEdgeOperate.decoder)
    public static Map<String, Object> decodePackAlarm(String hexString, Map<String, Object> param) throws ProtocolException {
        byte[] pack = HexUtils.hexStringToByteArray(hexString);

        // 解码报文
        LRWEntity entity = decodePack(pack);
        if (entity == null) {
            throw new ProtocolException("报文格式不正确！");
        }

        if (entity.getCmd() != (byte) 0x81) {
            throw new ProtocolException("功能代码不正确！");
        }

        // 解码数据
        Map<String, Object> value = new HashMap<>();
        value.put("temp", true);
        value.put("spark", true);

        return value;
    }
}
