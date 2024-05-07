package cn.foxtech.device.protocol.v1.s7plc;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.core.template.TemplateFactory;
import cn.foxtech.device.protocol.v1.s7plc.template.JDefaultTemplate;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 读数据
 */
@FoxEdgeDeviceType(value = "S7 PLC", manufacturer = "Siemens")
public class S7PLCWriteData {

    @FoxEdgeOperate(name = "writeData", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static Map<String, Object> packGetData(Map<String, Object> param) {
        // 提取业务参数：设备地址/对象名称/CSV模板文件
        Map<String, Object> objectValues = (Map<String, Object>) param.get("objectValues");
        String templateName = (String) param.get("templateName");

        // 简单校验参数
        if (MethodUtils.hasNull(objectValues, templateName)) {
            throw new ProtocolException("参数不能为空: objectNames, templateName");
        }

        JDefaultTemplate template = TemplateFactory.getTemplate("fox-edge-server-protocol-s7plc").getTemplate("jsn", templateName, JDefaultTemplate.class);


        List<Map<String, Object>> params = template.encodeWriteObjects(objectValues);

        Map<String, Object> result = new HashMap<>();
        result.put("method", "writeData");
        result.put("params", params);

        return result;
    }

    @FoxEdgeOperate(name = "writeData", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> unpackReadData(Map<String, Object> respond, Map<String, Object> param) {
        String templateName = (String) param.get("templateName");

        // 简单校验参数
        if (MethodUtils.hasNull(templateName)) {
            throw new ProtocolException("参数不能为空:templateName");
        }

        return new HashMap<>();
    }
}
