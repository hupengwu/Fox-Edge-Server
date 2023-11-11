package cn.foxtech.device.script.engine;

import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
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
public class PublishService {
    @Autowired
    private ScriptEngineService engineService;

    public void publish(String deviceName, String manufacturer, String deviceType, OperateEntity operateEntity, Map<String, Object> params, int timeout, FoxEdgeChannelService channelService) throws ProtocolException, CommunicationException {
        try {
            // 取出ScriptEngine
            ScriptEngine engine = this.engineService.getScriptEngine(manufacturer, deviceType);

            // 取出编码/解码信息
            Map<String, Object> encode = (Map<String, Object>) operateEntity.getEngineParam().getOrDefault("encode", new HashMap<>());
            if (MethodUtils.hasEmpty(encode)) {
                throw new ProtocolException("找不到对应操作名称的编码函数：" + operateEntity.getOperateName());
            }

            // 编码脚本
            String encodeMain = (String) encode.getOrDefault("main", "");
            String encodeScript = (String) encode.getOrDefault("code", "");
            if (MethodUtils.hasEmpty(encodeMain, encodeScript)) {
                throw new ProtocolException("找不到对应操作名称的编码函数：" + operateEntity.getOperateName());
            }


            Object send;


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
                channelService.publish(deviceName, deviceType, send, timeout);
            } catch (Exception e) {
                throw new CommunicationException(e.getMessage());
            }

        } catch (Exception e) {
            throw new ProtocolException(e.getMessage());
        }
    }
}
