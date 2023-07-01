package cn.foxtech.device.protocol.snmp;

import cn.foxtech.device.protocol.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.core.exception.ProtocolException;
import cn.foxtech.device.protocol.core.template.TemplateFactory;
import cn.foxtech.device.protocol.core.utils.MethodUtils;
import cn.foxtech.device.protocol.snmp.template.JDefaultTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 读数据
 */
@FoxEdgeDeviceType(value = "SNMP Device", manufacturer = "Fox Edge")
public class SnmpGetData {
    /**
     * 读数据
     *
     * @param param 必须包含device_addr和modbus_holding_registers_template两个输入参数
     * @return 16进制文本格式的报文
     */
    @FoxEdgeOperate(name = "读数据", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static Map<String, Object> packGetData(Map<String, Object> param) {
        // 提取业务参数：设备地址/对象名称/CSV模板文件
        List<String> objectNameList = (List<String>) param.get("objectNameList");
        String tableName = (String) param.get("tableName");
        String templateName = (String) param.get("templateName");

        // 简单校验参数
        if (MethodUtils.hasNull(objectNameList, templateName, tableName)) {
            throw new ProtocolException("参数不能为空:objectNameList, templateName, tableName");
        }


        JDefaultTemplate template = TemplateFactory.getTemplate("fox-edge-server-protocol-snmp").getTemplate(templateName, tableName, JDefaultTemplate.class);
        List<String> oidList = template.encodeOIDList(objectNameList);

        Map<String, Object> result = new HashMap<>();
        result.put("operate", "GET");
        result.put("oids", oidList);

        return result;
    }

    /**
     * 解码保持寄存器
     *
     * @param respond 16进制文本格式的报文
     * @param param   必须包含 device_addr 和 modbus_holding_registers_template 两个输入参数
     * @return 解码后的数据
     */
    @FoxEdgeOperate(name = "读数据", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> unpackReadData(Map<String, Object> respond, Map<String, Object> param) {
        String tableName = (String) param.get("tableName");
        String templateName = (String) param.get("templateName");


        // 简单校验参数
        if (MethodUtils.hasNull(templateName, tableName)) {
            throw new ProtocolException("参数不能为空:templateName, tableName");
        }

        JDefaultTemplate template = TemplateFactory.getTemplate("fox-edge-server-protocol-snmp").getTemplate(templateName, tableName, JDefaultTemplate.class);
        return template.decodeValue(respond);
    }
}
