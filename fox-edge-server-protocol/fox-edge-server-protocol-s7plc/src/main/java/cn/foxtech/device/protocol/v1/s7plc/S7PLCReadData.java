package cn.foxtech.device.protocol.v1.s7plc;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.core.template.TemplateFactory;
import cn.foxtech.device.protocol.v1.s7plc.template.JDefaultTemplate;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 读数据
 */
@FoxEdgeDeviceType(value = "S7 PLC", manufacturer = "Siemens")
public class S7PLCReadData {

    @FoxEdgeOperate(name = "readData", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static Map<String, Object> packGetData(Map<String, Object> param) {
        // 提取业务参数：设备地址/对象名称/CSV模板文件
        List<String> objectNames = (List<String>) param.get("objectNames");
        String templateName = (String) param.get("templateName");

        // 简单校验参数
        if (MethodUtils.hasNull(objectNames, templateName)) {
            throw new ProtocolException("参数不能为空: objectNames, templateName");
        }


        JDefaultTemplate template = TemplateFactory.getTemplate("fox-edge-server-protocol-s7plc").getTemplate("jsn", templateName, JDefaultTemplate.class);


        List<Map<String, Object>> params = template.encodeReadObjects(objectNames);

        Map<String, Object> result = new HashMap<>();
        result.put("method", "readData");
        result.put("params", params);

        return result;
    }

    @FoxEdgeOperate(name = "readData", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> unpackReadData(Map<String, Object> respond, Map<String, Object> param) {
        String templateName = (String) param.get("templateName");

        // 简单校验参数
        if (MethodUtils.hasNull(templateName)) {
            throw new ProtocolException("参数不能为空: templateName");
        }

        JDefaultTemplate template = TemplateFactory.getTemplate("fox-edge-server-protocol-s7plc").getTemplate("jsn", templateName, JDefaultTemplate.class);


        List<Map<String, Object>> list = (List<Map<String, Object>>) respond.getOrDefault("list", new ArrayList<>());


        return template.decodeReadValue(list);
    }
}
