package cn.foxtech.device.protocol.lrw;

import cn.foxtech.device.protocol.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.core.annotation.FoxEdgePublish;
import cn.foxtech.device.protocol.core.utils.HexUtils;

import java.util.Map;

@FoxEdgeDeviceType(value = "LRW解码器")
public class LRWProtocolBrocast extends LRWProtocolFrame {
    /**
     * 查询传感器状态
     *
     * @param param 输入参数
     * @return 操作是否成功
     */
    @FoxEdgePublish
    @FoxEdgeOperate(name = "广播地址", polling = true, type = FoxEdgeOperate.encoder)
    public static String encodePackSensor(Map<String, Object> param) {
        return HexUtils.byteArrayToHexString(encodePack((byte) 0x02));
    }
}
