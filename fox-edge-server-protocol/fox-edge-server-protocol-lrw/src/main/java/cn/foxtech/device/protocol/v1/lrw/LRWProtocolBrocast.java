package cn.foxtech.device.protocol.v1.lrw;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgePublish;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

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
