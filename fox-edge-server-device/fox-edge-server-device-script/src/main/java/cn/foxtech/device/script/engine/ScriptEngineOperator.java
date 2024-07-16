package cn.foxtech.device.script.engine;

import cn.foxtech.common.entity.manager.InitialConfigService;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.constants.FoxEdgeConstant;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ScriptEngineOperator {
    @Autowired
    private InitialConfigService configService;

    /**
     * 设置环境变量
     *
     * @param engine 脚本引擎
     * @param params 设备参数
     */
    public void setSendEnvValue(ScriptEngine engine, Map<String, Object> params) {
        try {
            // 先将Map转成JSP能够处理的JSON字符串
            engine.put("fox_edge_param", JsonUtils.buildJson(params));
        } catch (Exception e) {
            // 打印日志
            this.printLogger(e.getMessage());

            throw new ProtocolException(e.getMessage());
        }
    }

    public void setRecvEnvValue(ScriptEngine engine, Object recv, Map<String, Object> params) {
        try {
            // 先将Map转成JSP能够处理的JSON字符串
            engine.put("fox_edge_param", JsonUtils.buildJson(params));

            // 接收数据，转成字符串
            if (recv instanceof Map) {
                engine.put("fox_edge_data", JsonUtils.buildJson(recv));
                return;
            } else if (recv instanceof List) {
                engine.put("fox_edge_data", JsonUtils.buildJson(recv));
                return;
            } else if (recv instanceof String) {
                engine.put("fox_edge_data", recv);
                return;
            }

            throw new ServiceException("对通道返回的数据，只支持String、Map、List三种数据结构！");
        } catch (Exception e) {
            // 打印日志
            this.printLogger(e.getMessage());

            throw new ProtocolException(e.getMessage());
        }
    }

    public Map<String, Object> decodeRecord(ScriptEngine engine, String operateName, String decodeMain, String decodeScript) {
        try {
            // 记录格式

            // 装载待执行的脚本
            engine.eval(decodeScript);

            // 执行JSP脚本中的函数
            Object data = engine.eval(decodeMain + "();");

            //再将JSON格式的字符串，转换回Map
            List<Map<String, Object>> values = JsonUtils.buildObject((String) data, List.class);

            Map<String, Object> recordValue = new HashMap<>();
            recordValue.put(FoxEdgeOperate.record, values);

            Map<String, Object> result = new HashMap<>();
            result.put(FoxEdgeConstant.OPERATE_NAME_TAG, operateName);
            result.put(FoxEdgeConstant.DATA_TAG, recordValue);

            return result;
        } catch (InvalidFormatException e) {
            // 不打印JSON转换日志
            throw new ProtocolException(e.getMessage());
        } catch (MismatchedInputException e) {
            // 不打印JSON转换日志
            throw new ProtocolException(e.getMessage());
        } catch (Exception e) {
            // 打印日志
            this.printLogger(e.getMessage());

            throw new ProtocolException(e.getMessage());
        }
    }

    public Map<String, Object> decodeStatus(ScriptEngine engine, String operateName, String decodeMain, String decodeScript) {
        // 状态格式
        try {
            // 装载待执行的脚本
            engine.eval(decodeScript);

            // 执行JSP脚本中的函数
            Object data = engine.eval(decodeMain + "();");

            //再将JSON格式的字符串，转换回Map
            Map<String, Object> values = JsonUtils.buildObject((String) data, Map.class);

            Map<String, Object> result = new HashMap<>();
            result.put(FoxEdgeOperate.status, values);
            return result;
        } catch (InvalidFormatException ife) {
            // 不打印JSON转换日志
            throw new ProtocolException(ife.getMessage());
        } catch (MismatchedInputException mie) {
            // 不打印JSON转换日志
            throw new ProtocolException(mie.getMessage());
        } catch (Exception e) {
            // 打印日志
            this.printLogger(e.getMessage());

            throw new ProtocolException(e.getMessage());
        }
    }

    public Map<String, Object> decodeResult(ScriptEngine engine, String operateName, String decodeMain, String decodeScript) {
        try {
            // 装载待执行的脚本
            engine.eval(decodeScript);

            // 执行JSP脚本中的函数
            Object data = engine.eval(decodeMain + "();");

            //再将JSON格式的字符串，转换回Map
            Map<String, Object> values = JsonUtils.buildObject((String) data, Map.class);

            Map<String, Object> result = new HashMap<>();
            result.put(FoxEdgeConstant.OPERATE_NAME_TAG, operateName);
            result.put(FoxEdgeOperate.result, values);
            return result;

        } catch (InvalidFormatException ife) {
            // 不打印JSON转换日志
            throw new ProtocolException(ife.getMessage());
        } catch (MismatchedInputException mie) {
            // 不打印JSON转换日志
            throw new ProtocolException(mie.getMessage());
        } catch (Exception e) {
            // 打印日志
            this.printLogger(e.getMessage());

            throw new ProtocolException(e.getMessage());
        }
    }

    public Object encode(ScriptEngine engine, String encodeMain, String encodeScript) {
        try {
            // 装载待执行的脚本
            engine.eval(encodeScript);

            // 执行JSP脚本中的函数
            String out = (String) engine.eval(encodeMain + "();");
            if (out == null) {
                throw new ProtocolException("编码错误：输出为null");
            }

            // 根据文本格式，转为Map/List，或者是原始的字符串
            if (out.startsWith("{") && out.endsWith("}")) {
                return JsonUtils.buildObject(out, Map.class);
            } else if (out.startsWith("[") && out.endsWith("[}]")) {
                return JsonUtils.buildObject(out, List.class);
            } else {
                return out;
            }
        } catch (Exception e) {
            // 打印日志
            this.printLogger(e.getMessage());

            throw new ServiceException(e.getMessage());
        }
    }

    private void printLogger(Object recv) {
        if (recv == null) {
            return;
        }

        if (!Boolean.TRUE.equals(this.configService.getConfigParam("serverConfig").get("logger"))) {
            return;
        }


        this.configService.getLogger().info(recv.toString());
    }
}
