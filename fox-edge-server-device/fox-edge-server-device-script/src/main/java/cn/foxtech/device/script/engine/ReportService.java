package cn.foxtech.device.script.engine;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.constants.FoxEdgeConstant;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ReportService {
    @Autowired
    private ScriptEngineService engineService;

    public Map<String, Object> decode(String manufacturer, String deviceType, List<BaseEntity> jspReportList, Object recv, Map<String, Object> params) throws ProtocolException {
        // 取出ScriptEngine
        ScriptEngine engine = this.engineService.getScriptEngine(manufacturer, deviceType);

        // 逐个解码器进行测试
        for (BaseEntity entity : jspReportList) {
            OperateEntity operateEntity = (OperateEntity) entity;

            // 取出编码/解码信息
            Map<String, Object> decode = (Map<String, Object>) operateEntity.getEngineParam().getOrDefault("decode", new HashMap<>());
            if (MethodUtils.hasEmpty(decode)) {
                throw new ProtocolException("找不到对应操作名称的解码函数：" + operateEntity.getOperateName());
            }

            // 解码脚本
            String decodeMain = (String) decode.getOrDefault("main", "");
            String decodeScript = (String) decode.getOrDefault("code", "");
            if (MethodUtils.hasEmpty(decodeMain, decodeScript)) {
                throw new ProtocolException("找不到对应操作名称的解码函数：" + operateEntity.getOperateName());
            }

            try {
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

                    Map<String, Object> recordValue = new HashMap<>();
                    recordValue.put(FoxEdgeOperate.record, values);

                    Map<String, Object> result = new HashMap<>();
                    result.put(FoxEdgeConstant.OPERATE_NAME_TAG, operateEntity.getOperateName());
                    result.put(FoxEdgeConstant.DATA_TAG, recordValue);

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

                        Map<String, Object> statusValue = new HashMap<>();
                        statusValue.put(FoxEdgeOperate.status, values);

                        Map<String, Object> result = new HashMap<>();
                        result.put(FoxEdgeConstant.OPERATE_NAME_TAG, operateEntity.getOperateName());
                        result.put(FoxEdgeConstant.DATA_TAG, statusValue);
                        return result;
                    } catch (Exception e) {
                        throw new ProtocolException("解码错误：" + e.getMessage());
                    }
                }

            } catch (Exception e) {
                continue;
            }
        }

        throw new ProtocolException("找不到对应设备类型的解码器：" + manufacturer + ":" + deviceType);
    }

}
