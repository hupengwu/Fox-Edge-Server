/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

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

/**
 * 脚本引擎执行器
 * 说明：JS引擎的管理方式，使用的是一个设备类型，拥有一个ScriptEngine
 * 1、优点：这样带来的好处，就是减少了ScriptEngine实例的数量，同时允许一个设备类型，支持lib的方式，
 * 互相调用各个operateEntity中的JS脚本，而且允许encode/decode的JS脚本中的JS函数是允许同名
 * 这就大大提高了开发者编写JS脚本代码的可读取。
 * 2、限制：由于encode/decode的JS脚本函数是允许同名的，这样带来的限制，每次执行operateEntity的JS脚本的时候，
 * 需要重启导入encode/decode的入口函数脚本，否则会调用成别的入口函数脚本，会带来额外的性能开销。
 * 3、结论：综合考虑上面的优缺点，还是当前方案更适合，毕竟愿意使用JS的开发者，是可以容忍这种性能开销的。
 */
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

            // 重新装载待待执行的脚本
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
            // 重新装载待待执行的脚本
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
            // 重新装载待待执行的脚本
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
            // 重新装载待待执行的脚本
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
