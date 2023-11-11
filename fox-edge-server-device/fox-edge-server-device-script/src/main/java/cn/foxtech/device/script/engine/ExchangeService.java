package cn.foxtech.device.script.engine;

import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.channel.FoxEdgeChannelService;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.naming.CommunicationException;
import javax.script.ScriptEngine;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExchangeService {
    @Autowired
    private ScriptEngineService engineService;

    public Map<String, Object> exchange(String deviceName, String manufacturer, String deviceType, OperateEntity operateEntity, Map<String, Object> params, int timeout, FoxEdgeChannelService channelService) throws ProtocolException, CommunicationException {
        try {
            // 取出ScriptEngine
            ScriptEngine engine = this.engineService.getScriptEngine(manufacturer, deviceType);

            // 取出编码/解码信息
            Map<String, Object> encode = (Map<String, Object>) operateEntity.getEngineParam().getOrDefault("encode", new HashMap<>());
            Map<String, Object> decode = (Map<String, Object>) operateEntity.getEngineParam().getOrDefault("decode", new HashMap<>());
            if (MethodUtils.hasEmpty(encode, decode)) {
                throw new ProtocolException("找不到对应操作名称的编码/解码函数：" + operateEntity.getOperateName());
            }

            // 编码脚本
            String encodeMain = (String) encode.getOrDefault("main", "");
            String encodeScript = (String) encode.getOrDefault("code", "");
            if (MethodUtils.hasEmpty(encodeMain, encodeScript)) {
                throw new ProtocolException("找不到对应操作名称的编码函数：" + operateEntity.getOperateName());
            }

            // 解码脚本
            String decodeMain = (String) decode.getOrDefault("main", "");
            String decodeScript = (String) decode.getOrDefault("code", "");
            if (MethodUtils.hasEmpty(decodeMain, decodeScript)) {
                throw new ProtocolException("找不到对应操作名称的解码函数：" + operateEntity.getOperateName());
            }


            Object send;
            Object recv;


            try {
                // 先将Map转成JSP能够处理的JSON字符串
                String jsonParams = JsonUtils.buildJson(params);

                // 装载JSP脚本
                engine.eval(encodeScript);
                // 执行JSP脚本中的函数
                String out = (String) engine.eval(encodeMain + "('" + jsonParams + "');");
                if (out == null) {
                    throw new ProtocolException("编码错误：输出为null");
                }

                // 根据文本格式，转为Map/List，或者是原始的字符串
                if (out.startsWith("{") && out.endsWith("}")) {
                    send = JsonUtils.buildObject(out, Map.class);
                } else if (out.startsWith("[") && out.endsWith("[}]")) {
                    send = JsonUtils.buildObject(out, List.class);
                } else {
                    send = out;
                }

            } catch (Exception e) {
                throw new ProtocolException("编码错误：" + e.getMessage());
            }


            try {
                recv = channelService.exchange(deviceName, deviceType, send, timeout);
            } catch (Exception e) {
                throw new CommunicationException(e.getMessage());
            }

            // 将解码结果，根据模式，用各自的字段带回
            if (FoxEdgeOperate.record.equals(operateEntity.getDataType())) {
                // 记录格式
                // 先将Map转成JSP能够处理的JSON字符串
                String jsonParams = JsonUtils.buildJson(params);

                // 装载JSP脚本
                engine.eval(decodeScript);
                // 执行JSP脚本中的函数
                String jsonValues = (String) engine.eval(decodeMain + "('" + recv + "','" + jsonParams + "');");

                //再将JSON格式的字符串，转换回Map
                List<Map<String, Object>> values = JsonUtils.buildObject(jsonValues, List.class);

                Map<String, Object> result = new HashMap<>();
                result.put(FoxEdgeOperate.record, values);
                return result;
            } else if (FoxEdgeOperate.result.equals(operateEntity.getDataType())) {
                // 结果格式

                // 先将Map转成JSP能够处理的JSON字符串
                String jsonParams = JsonUtils.buildJson(params);

                // 装载JSP脚本
                engine.eval(decodeScript);
                // 执行JSP脚本中的函数
                String jsonValues = (String) engine.eval(decodeMain + "('" + recv + "','" + jsonParams + "');");

                //再将JSON格式的字符串，转换回Map
                Map<String, Object> values = JsonUtils.buildObject(jsonValues, Map.class);

                Map<String, Object> result = new HashMap<>();
                result.put(FoxEdgeOperate.result, values);
                return result;

            } else {
                // 状态格式
                try {
                    // 先将Map转成JSP能够处理的JSON字符串
                    String jsonParams = JsonUtils.buildJson(params);

                    // 装载JSP脚本
                    engine.eval(decodeScript);
                    // 执行JSP脚本中的函数
                    String jsonValues = (String) engine.eval(decodeMain + "('" + recv + "','" + jsonParams + "');");

                    //再将JSON格式的字符串，转换回Map
                    Map<String, Object> values = JsonUtils.buildObject(jsonValues, Map.class);

                    Map<String, Object> result = new HashMap<>();
                    result.put(FoxEdgeOperate.status, values);
                    return result;
                } catch (Exception e) {
                    throw new ProtocolException("解码错误：" + e.getMessage());
                }
            }

        } catch (Exception e) {
            throw new ProtocolException(e.getMessage());
        }
    }
}
