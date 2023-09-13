package cn.foxtech.device.protocol.v1.zktl.electric;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgePublish;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeReport;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.core.utils.JsonUtils;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;
import cn.foxtech.device.protocol.v1.zktl.electric.encoder.Encoder;
import cn.foxtech.device.protocol.v1.zktl.electric.entity.ZktlConfigEntity;
import cn.foxtech.device.protocol.v1.zktl.electric.entity.ZktlDataEntity;

import java.io.IOException;
import java.util.Map;

/**
 * 读取Registers
 */
@FoxEdgeDeviceType(value = "电器火灾监控设备", manufacturer = "武汉中科图灵科技有限公司")
public class DecodePduEntity {
    /**
     * 解码数据：对主动上报数据的解码
     *
     * @param hexString 16进制文本格式的报文
     * @param param     必须包含 device_addr 和 modbus_holding_registers_template 两个输入参数
     * @return 解码后的数据
     */
    @FoxEdgeReport(type = FoxEdgeReport.alarm)
    @FoxEdgeOperate(name = "解码器PDU报文", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> decodePduEntity(String hexString, Map<String, Object> param) {
        try {
            // 转换数据
            byte[] pdu = HexUtils.hexStringToByteArray(hexString);

            // 解码数据
            ZktlDataEntity dataEntity = Encoder.decodeDataEntity(pdu);

            // 转换成Map对象
            Map<String, Object> value = JsonUtils.buildObject(dataEntity, Map.class);
            return value;
        } catch (IOException e) {
            throw new ProtocolException(e.getMessage());
        }
    }
}
