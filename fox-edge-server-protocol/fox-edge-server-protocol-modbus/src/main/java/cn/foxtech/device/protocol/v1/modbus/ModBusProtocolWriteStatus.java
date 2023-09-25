package cn.foxtech.device.protocol.v1.modbus;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.core.template.TemplateFactory;
import cn.foxtech.device.protocol.v1.modbus.core.*;
import cn.foxtech.device.protocol.v1.modbus.template.JReadStatusTemplate;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 读取Registers
 */
@FoxEdgeDeviceType(value = "ModBus Device", manufacturer = "Fox Edge")
public class ModBusProtocolWriteStatus {
    /**
     * 读取保持寄存器
     *
     * @param param 必须包含device_addr和modbus_holding_registers_template两个输入参数
     * @return 16进制文本格式的报文
     */
    @FoxEdgeOperate(name = "Write Single Status", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static String packWriteSingleStatus(Map<String, Object> param) {
        return (String) operateWriteStatus("", param);
    }

    /**
     * 解码保持寄存器
     *
     * @param hexString 16进制文本格式的报文
     * @param param     必须包含 device_addr 和 modbus_holding_registers_template 两个输入参数
     * @return 解码后的数据
     */
    @FoxEdgeOperate(name = "Write Single Status", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> unpackWriteSingleStatus(String hexString, Map<String, Object> param) {
        return (Map<String, Object>) operateWriteStatus(hexString, param);
    }

    /**
     * 编码/解码 读取保持寄存器
     *
     * @param hexString 解码报文：如果它为空，那么为编码操作，如果不为空，那么为编码操作
     * @param param     参数表
     * @return 编码为String，解码为Map<String, Object>
     */
    private static Object operateWriteStatus(String hexString, Map<String, Object> param) {
        // 取出设备地址
        Integer devAddr = (Integer) param.get("devAddr");
        String modbusMode = (String) param.get("modbusMode");
        String templateName = (String) param.get("templateName");
        String tableName = (String) param.get("tableName");
        String objectName = (String) param.get("objectName");
        Object objectValue = param.get("objectValue");

        // 检查输入参数
        if (MethodUtils.hasEmpty(devAddr, modbusMode, templateName, tableName, objectName, objectValue)) {
            throw new ProtocolException("输入参数不能为空:devAddr, modbusMode, templateName, tableName, objectName, objectValue");
        }

        JReadStatusTemplate template = TemplateFactory.getTemplate("fox-edge-server-protocol-modbus").getTemplate(templateName, tableName, JReadStatusTemplate.class);
        if (template == null) {
            throw new ProtocolException("找不到对应的模板");
        }

        ModBusWriteStatusRequest writeStatusRequest = template.encode(objectName, objectValue);
        if (writeStatusRequest == null) {
            throw new ProtocolException("编码失败");
        }

        // 准备参数
        param.put(ModBusConstants.ADDR, devAddr);
        param.put(ModBusConstants.REG_ADDR, writeStatusRequest.getMemAddr());
        param.put(ModBusConstants.REG_CNT, 1);
        param.put(ModBusConstants.MODE, modbusMode);

        // 选择对应的协议类型
        ModBusProtocol protocol = ModBusProtocolFactory.createProtocol(modbusMode);

        // 编码处理
        if (hexString.isEmpty()) {
            byte[] pack = protocol.packCmdWriteStatus4Request(writeStatusRequest);
            return HexUtils.byteArrayToHexString(pack);
        }

        // 解码处理

        byte[] arrCmd = HexUtils.hexStringToByteArray(hexString);
        // 报文解析
        ModBusWriteStatusRespond respond = protocol.unPackCmdWriteStatus2Respond(arrCmd);
        if (respond == null) {
            throw new ProtocolException("报文格式不正确，解析失败！");
        }

        // 使用模板拆解数据
        boolean[] statusList = new boolean[1];
        statusList[0] = respond.isStatus();
        Map<String, Object> value = template.decode(respond.getMemAddr(), 1, statusList);
        return value;
    }
}
