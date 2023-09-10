package cn.foxtech.device.protocol.v1.modbus;


import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.core.template.TemplateFactory;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;
import cn.foxtech.device.protocol.v1.modbus.core.ModBusConstants;
import cn.foxtech.device.protocol.v1.modbus.core.ModBusProtocol;
import cn.foxtech.device.protocol.v1.modbus.core.ModBusProtocolFactory;
import cn.foxtech.device.protocol.v1.modbus.core.ModBusReadStatusRespond;
import cn.foxtech.device.protocol.v1.modbus.template.JReadStatusTemplate;

import java.util.Map;

/**
 * 读取Status
 */
@FoxEdgeDeviceType(value = "ModBus Device", manufacturer = "Fox Edge")
public class ModBusProtocolReadStatus {
    /**
     * 读取保持寄存器
     *
     * @param param 必须包含device_addr和modbus_holding_registers_template两个输入参数
     * @return 16进制文本格式的报文
     */
    @FoxEdgeOperate(name = "Read Coil Status", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static String packReadCoilStatus(Map<String, Object> param) {
        return (String) operateReadStatus("", param);
    }

    /**
     * 解码保持寄存器
     *
     * @param hexString 16进制文本格式的报文
     * @param param     必须包含 device_addr 和 modbus_holding_registers_template 两个输入参数
     * @return 解码后的数据
     */
    @FoxEdgeOperate(name = "Read Coil Status", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> unpackReadCoilStatus(String hexString, Map<String, Object> param) {
        return (Map<String, Object>) operateReadStatus(hexString, param);
    }


    /**
     * 读取保持寄存器
     *
     * @param param 必须包含device_addr和modbus_holding_registers_template两个输入参数
     * @return 16进制文本格式的报文
     */
    @FoxEdgeOperate(name = "Read Input Status", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static String packReadInputStatus(Map<String, Object> param) {
        return (String) operateReadStatus("", param);
    }

    /**
     * 解码保持寄存器
     *
     * @param hexString 16进制文本格式的报文
     * @param param     必须包含 device_addr 和 modbus_holding_registers_template 两个输入参数
     * @return 解码后的数据
     */
    @FoxEdgeOperate(name = "Read Input Status", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> unpackReadInputStatus(String hexString, Map<String, Object> param) {
        return (Map<String, Object>) operateReadStatus(hexString, param);
    }

    /**
     * 编码/解码 读取保持寄存器
     *
     * @param hexString 解码报文：如果它为空，那么为编码操作，如果不为空，那么为编码操作
     * @param param     参数表
     * @return 编码为String，解码为Map<String, Object>
     */
    private static Object operateReadStatus(String hexString, Map<String, Object> param) {
        // 取出设备地址
        Integer devAddr = (Integer) param.get("devAddr");
        Integer regAddr = (Integer) param.get("regAddr");
        Integer regCnt = (Integer) (param.get("regCnt"));
        String modbusMode = (String) param.get("modbusMode");
        String templateName = (String) param.get("templateName");
        String operateName = (String) param.get("operateName");
        String tableName = (String) param.get("tableName");

        // 检查输入参数
        if (MethodUtils.hasEmpty(devAddr, regAddr, regCnt, modbusMode, templateName, operateName, tableName)) {
            throw new ProtocolException("输入参数不能为空:devAddr, regAddr, regCnd, modbusMode, templateName, operateName, tableName");
        }

        JReadStatusTemplate template = TemplateFactory.getTemplate("fox-edge-server-protocol-modbus").getTemplate(templateName, tableName, JReadStatusTemplate.class);

        // 确定命令字
        Byte func = 0;
        if (JReadStatusTemplate.READ_COIL_STATUS.equals(operateName)) {
            func = 0x01;
        }
        if (JReadStatusTemplate.WRITE_SINGLE_STATUS.equals(operateName)) {
            func = 0x02;
        }

        // 准备参数
        param.put(ModBusConstants.ADDR, devAddr);
        param.put(ModBusConstants.REG_ADDR, regAddr);
        param.put(ModBusConstants.REG_CNT, regCnt);
        param.put(ModBusConstants.MODE, modbusMode);

        // 选择对应的协议类型
        ModBusProtocol protocol = ModBusProtocolFactory.createProtocol(modbusMode);

        // 编码处理
        if (hexString.isEmpty()) {
            // 返回处理数据
            byte[] pack = protocol.packCmdReadStatus4Map(func, param);
            return HexUtils.byteArrayToHexString(pack);
        }

        // 解码处理

        byte[] arrCmd = HexUtils.hexStringToByteArray(hexString);
        // 报文解析
        ModBusReadStatusRespond respond = protocol.unPackCmdReadStatus2Respond(arrCmd);
        if (respond == null) {
            throw new ProtocolException("报文格式不正确，解析失败！");
        }

        // 使用模板拆解数据
        return template.decode(regAddr, regCnt, respond.getStatus());
    }
}
