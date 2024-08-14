/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.channel.socket.core.script;

import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.utils.MapUtils;
import cn.foxtech.common.utils.hex.HexUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ScriptEngineService {
    private final ScriptEngineManager manager = new ScriptEngineManager();

    private final Map<String, Object> engineMap = new ConcurrentHashMap<>();

    public ScriptEngine getScriptEngine(String manufacturer, String deviceType, String operateName) {
        ScriptEngine engine = (ScriptEngine) MapUtils.getValue(this.engineMap, manufacturer, deviceType, operateName);
        if (engine == null) {
            engine = this.manager.getEngineByName("JavaScript");
            MapUtils.setValue(this.engineMap, manufacturer, deviceType, operateName, engine);
        }

        return engine;
    }

    public ScriptSplitMessage buildSplitOperate(OperateEntity operateEntity) throws ScriptException {
        // 检测：收发为JSP引擎
        if (!operateEntity.getEngineType().equals("JavaScript") // 检测：是否是JavaScript引擎
                || !operateEntity.getOperateName().equals("splitHandler")//检测：是否是splitHandler操作
                || !operateEntity.getOperateMode().equals("splitHandler")//检测：是否是splitHandler操作
                || !operateEntity.getServiceType().equals("channel")//检测：是否是channel类型
        ) {
            throw new ServiceException("获得操作实体，不是一个Jsp引擎方法");
        }

        // 检测：引擎参数是否正确
        Map<String, Object> decode = (Map<String, Object>) operateEntity.getEngineParam().get("decode");
        if (MethodUtils.hasEmpty(decode)) {
            throw new ServiceException("engineParam为空！");
        }

        // 检测：脚本是否正确
        String decodeScript = (String) decode.get("code");
        if (MethodUtils.hasEmpty(decodeScript)) {
            throw new ServiceException("未定义解码脚本！");
        }

        // 获得引擎
        ScriptEngine scriptEngine = this.getScriptEngine(operateEntity.getManufacturer(), operateEntity.getDeviceType(), operateEntity.getOperateName());

        // 装载脚本
        scriptEngine.eval(decodeScript);

        // 执行函数：getHeaderLength
        Integer headerLength = (Integer) scriptEngine.eval("getHeaderLength()");
        if (headerLength == null || headerLength < 1) {
            throw new ServiceException("脚本中未定义正确的脚本，请自行参考正确的范例：function getHeaderLength()");
        }

        // 验证脚本：是否能够基本执行
        byte[] pack = new byte[headerLength];
        String hex = HexUtils.byteArrayToHexString(pack);
        Object value = scriptEngine.eval("getPackLength('" + hex + "')");
        if (value == null) {
            throw new ServiceException("脚本中未定义正确的脚本，请自行参考正确的范例：function getPackLength(pack)");
        }

        // 验证脚本：是否能够基本执行
        Boolean invalidPack = (Boolean) scriptEngine.eval("isInvalidPack('" + hex + "')");
        if (invalidPack == null) {
            throw new ServiceException("脚本中未定义正确的脚本，请自行参考正确的范例：function getPackLength(pack)");
        }

        ScriptSplitMessage scriptSplitMessage = new ScriptSplitMessage();
        scriptSplitMessage.setScriptEngine(scriptEngine);
        scriptSplitMessage.setScript(decodeScript);

        return scriptSplitMessage;
    }

    public ScriptServiceKey buildServiceKeyOperate(OperateEntity operateEntity) throws ScriptException {
        // 检测：收发为JSP引擎
        if (!operateEntity.getEngineType().equals("JavaScript") // 检测：是否是JavaScript引擎
                || !operateEntity.getOperateName().equals("keyHandler")//检测：是否是splitHandler操作
                || !operateEntity.getOperateMode().equals("keyHandler")//检测：是否是splitHandler操作
                || !operateEntity.getServiceType().equals("channel")//检测：是否是channel类型
        ) {
            throw new ServiceException("获得操作实体，不是一个Jsp引擎方法");
        }

        // 检测：引擎参数是否正确
        Map<String, Object> decode = (Map<String, Object>) operateEntity.getEngineParam().get("decode");
        if (MethodUtils.hasEmpty(decode)) {
            throw new ServiceException("engineParam为空！");
        }

        // 检测：脚本是否正确
        String decodeScript = (String) decode.get("code");
        if (MethodUtils.hasEmpty(decodeScript)) {
            throw new ServiceException("未定义解码脚本！");
        }

        // 获得引擎
        ScriptEngine scriptEngine = this.getScriptEngine(operateEntity.getManufacturer(), operateEntity.getDeviceType(), operateEntity.getOperateName());

        // 装载脚本
        scriptEngine.eval(decodeScript);

        // 验证脚本：是否能够基本执行
        byte[] pack = new byte[16];
        String hex = HexUtils.byteArrayToHexString(pack);
        scriptEngine.eval("getServiceKey('" + hex + "')");

        ScriptServiceKey scriptServiceKey = new ScriptServiceKey();
        scriptServiceKey.setScriptEngine(scriptEngine);
        scriptServiceKey.setScript(decodeScript);

        return scriptServiceKey;
    }
}

