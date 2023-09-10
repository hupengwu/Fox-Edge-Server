package cn.foxtech.device.protocol.v1.mitsubishi.plc.fx;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;
import cn.foxtech.device.protocol.v1.mitsubishi.plc.fx.entity.MitsubishiPlcFxDeviceReadEntity;
import cn.foxtech.device.protocol.v1.mitsubishi.plc.fx.frame.MitsubishiPlcFxProtocolFrame;

import java.util.HashMap;
import java.util.Map;

/**
 * 读取Registers
 */
@FoxEdgeDeviceType(value = "mitsubishi-plc-fx", manufacturer = "mitsubishi")
public class PlcFxProtocolDeviceRead {
    /**
     * 读取保持寄存器
     *
     * @param param 必须包含device_addr和modbus_holding_registers_template两个输入参数
     * @return 16进制文本格式的报文
     */
    @FoxEdgeOperate(name = "DEVICE READ", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static String packReadHoldingRegister(Map<String, Object> param) {
        return (String) operateReadRegister("", param);
    }

    /**
     * 解码保持寄存器
     *
     * @param hexString 16进制文本格式的报文
     * @param param     必须包含 device_addr 和 modbus_holding_registers_template 两个输入参数
     * @return 解码后的数据
     */
    @FoxEdgeOperate(name = "DEVICE READ", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> unpackReadHoldingRegister(String hexString, Map<String, Object> param) {
        return (Map<String, Object>) operateReadRegister(hexString, param);
    }

    /**
     * 编码/解码 读取保持寄存器
     *
     * @param hexString 解码报文：如果它为空，那么为编码操作，如果不为空，那么为编码操作
     * @param param     参数表
     * @return 编码为String，解码为Map<String, Object>
     */
    private static Object operateReadRegister(String hexString, Map<String, Object> param) {
        // 取出设备地址
        String target = (String) param.get("target");
        Integer address = (Integer) param.get("address");
        Integer count = (Integer) param.get("count");
        String templateName = (String) param.get("templateName");
        String tableName = (String) param.get("tableName");

        // 检查输入参数
        if (MethodUtils.hasEmpty(target, address, count, templateName, tableName)) {
            throw new ProtocolException("输入参数不能为空:operateName, target, address, count, templateName, tableName");
        }

        MitsubishiPlcFxDeviceReadEntity entity = new MitsubishiPlcFxDeviceReadEntity();
        entity.setTarget(target);
        entity.setAddress(address);
        entity.setCount(count);

        // 编码处理
        if (hexString.isEmpty()) {
            byte[] pack = MitsubishiPlcFxProtocolFrame.encodePack(entity);
            return HexUtils.byteArrayToHexString(pack);
        }

        // 解码处理

        byte[] pack = HexUtils.hexStringToByteArray(hexString);


        // 报文解析
        MitsubishiPlcFxProtocolFrame.decodePack(pack, entity);

//        JDefaultTemplate template = TemplateFactory.getTemplate("fox-edge-server-protocol-mitsubishi-plc-fx").getTemplate(templateName, tableName, JDefaultTemplate.class);
//        template.decode(address,count,entity);

        // 使用模板拆解数据
        Map<String, Object> value = new HashMap<>();//template.decode(regAddr, regCnd, respond.getStatus());
        value.put("data", entity.getData());
        return value;
    }
}
