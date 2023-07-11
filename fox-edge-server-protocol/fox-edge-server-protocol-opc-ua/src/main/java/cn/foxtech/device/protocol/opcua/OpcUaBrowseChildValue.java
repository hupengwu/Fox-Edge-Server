package cn.foxtech.device.protocol.opcua;

import cn.foxtech.device.protocol.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.core.exception.ProtocolException;
import cn.foxtech.device.protocol.core.template.TemplateFactory;
import cn.foxtech.device.protocol.core.utils.MethodUtils;
import cn.foxtech.device.protocol.opcua.entity.OpcUaNodeId;
import cn.foxtech.device.protocol.opcua.template.JDefaultTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 读数据
 */
@FoxEdgeDeviceType(value = "OPC-UA", manufacturer = "Fox Edge")
public class OpcUaBrowseChildValue {
    /**
     * 读数据
     *
     * @param param 必须包含device_addr和modbus_holding_registers_template两个输入参数
     * @return 16进制文本格式的报文
     */
    @FoxEdgeOperate(name = "读子节点数值", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static Map<String, Object> packBrowseChildValue(Map<String, Object> param) {
        // 提取业务参数：设备地址/对象名称/CSV模板文件
        String objectName = (String) param.get("objectName");
        String tableName = (String) param.get("tableName");
        String templateName = (String) param.get("templateName");

        // 简单校验参数
        if (MethodUtils.hasNull(objectName, templateName, tableName)) {
            throw new ProtocolException("参数不能为空:objectName, templateName, tableName");
        }


        JDefaultTemplate template = TemplateFactory.getTemplate("fox-edge-server-protocol-snmp").getTemplate(templateName, tableName, JDefaultTemplate.class);
        OpcUaNodeId nodeId = template.encodeNodeId(objectName);

        Map<String, Object> result = new HashMap<>();
        result.put("operate", "browseChildValue");
        result.put("nodeId", nodeId);

        return result;
    }

    /**
     * 解码保持寄存器
     *
     * @param respond 16进制文本格式的报文
     * @param param   必须包含 device_addr 和 modbus_holding_registers_template 两个输入参数
     * @return 解码后的数据
     */
    @FoxEdgeOperate(name = "读子节点数值", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> unpackReadData(Map<String, Object> respond, Map<String, Object> param) {
        String tableName = (String) param.get("tableName");
        String templateName = (String) param.get("templateName");


        // 简单校验参数
        if (MethodUtils.hasNull(templateName, tableName)) {
            throw new ProtocolException("参数不能为空:templateName, tableName");
        }

        JDefaultTemplate template = TemplateFactory.getTemplate("fox-edge-server-protocol-snmp").getTemplate(templateName, tableName, JDefaultTemplate.class);


        Map<String, Object> result = new HashMap<>();

        List<Map<String, Object>> children = (List<Map<String, Object>>) respond.get("children");
        for (Map<String, Object> child : children) {
            OpcUaNodeId nodeId = OpcUaNodeId.buildEntity((Map<String, Object>) child.get("nodeId"));
            if (nodeId == null) {
                continue;
            }

            String objectName = template.getObjectName(nodeId);
            if (objectName == null) {
                continue;
            }

            result.put(objectName,child.get("value"));
        }

        return result;
    }
}
