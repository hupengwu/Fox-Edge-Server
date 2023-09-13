package cn.foxtech.device.protocol.v1.zktl.air5in1;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgePublish;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;
import cn.foxtech.device.protocol.v1.zktl.air5in1.encoder.Encoder;
import cn.foxtech.device.protocol.v1.zktl.air5in1.entity.ZktlConfigEntity;

import java.util.Map;

/**
 * 读取Registers
 */
@FoxEdgeDeviceType(value = "五合一空气监测传感器", manufacturer = "武汉中科图灵科技有限公司")
public class EncodePduEntity {
    /**
     * 查询传感器状态
     *
     * @param param 输入参数
     * @return 操作是否成功
     */
    @FoxEdgePublish
    @FoxEdgeOperate(name = "设置设备参数", polling = true, type = FoxEdgeOperate.encoder)
    public static String encodePackSensor(Map<String, Object> param) {
        Integer communType = (Integer) param.get("communType");
        Integer deviceType = (Integer) param.get("deviceType");
        Integer value = (Integer) param.get("value");

        // 检查：参数是否完整
        if (MethodUtils.hasEmpty(communType, deviceType, value)) {
            throw new ProtocolException("缺少配置参数：communType, deviceType, value");
        }

        ZktlConfigEntity entity = new ZktlConfigEntity();
        entity.setCommunType(communType);
        entity.setDeviceType(deviceType);
        entity.setValue(value);

        String pdu = Encoder.encodePdu(entity);

        return HexUtils.byteArrayToHexString(pdu.getBytes());
    }
}
