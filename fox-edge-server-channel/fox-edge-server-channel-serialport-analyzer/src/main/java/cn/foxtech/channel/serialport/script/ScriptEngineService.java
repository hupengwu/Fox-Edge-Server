package cn.foxtech.channel.serialport.script;

import cn.foxtech.channel.common.service.EntityManageService;
import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.utils.Maps;
import cn.foxtech.common.utils.hex.HexUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ScriptEngineService {
    private final ScriptEngineManager manager = new ScriptEngineManager();

    private final Map<String, Object> engineMap = new ConcurrentHashMap<>();

    @Autowired
    private EntityManageService entityManageService;

    public ScriptEngine getScriptEngine(String manufacturer, String deviceType, String operateName) {
        ScriptEngine engine = (ScriptEngine) Maps.getValue(this.engineMap, manufacturer, deviceType, operateName);
        if (engine == null) {
            engine = this.manager.getEngineByName("JavaScript");
            Maps.setValue(this.engineMap, manufacturer, deviceType, operateName, engine);
        }

        return engine;
    }

    public ScriptSplitMessage buildSplitOperate(OperateEntity operateEntity) throws ScriptException, NoSuchMethodException {
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
        String main = (String) decode.get("main");
        if (MethodUtils.hasEmpty(main, decodeScript)) {
            throw new ServiceException("未定义解码脚本！");
        }

        // 获得引擎
        ScriptEngine scriptEngine = this.getScriptEngine(operateEntity.getManufacturer(), operateEntity.getDeviceType(), operateEntity.getOperateName());

        // 装载脚本
        scriptEngine.eval(decodeScript);

        // 验证脚本：是否能够基本执行
        byte[] pack = new byte[16];
        String message = HexUtils.byteArrayToHexString(pack);

        // 尝试执行脚本，看JSP是否语法正确
        Invocable invoke = (Invocable) scriptEngine;
        invoke.invokeFunction(main, message);

        ScriptSplitMessage scriptSplitMessage = new ScriptSplitMessage();
        scriptSplitMessage.setScriptEngine(scriptEngine);
        scriptSplitMessage.setScript(decodeScript);

        return scriptSplitMessage;
    }

    public ScriptServiceKey buildServiceKeyOperate(OperateEntity operateEntity) throws ScriptException, NoSuchMethodException {
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
        String main = (String) decode.get("main");
        if (MethodUtils.hasEmpty(main, decodeScript)) {
            throw new ServiceException("未定义解码脚本！");
        }

        // 获得引擎
        ScriptEngine scriptEngine = this.getScriptEngine(operateEntity.getManufacturer(), operateEntity.getDeviceType(), operateEntity.getOperateName());

        // 装载脚本
        scriptEngine.eval(decodeScript);

        // 验证脚本：是否能够基本执行
        byte[] pack = new byte[16];
        String message = HexUtils.byteArrayToHexString(pack);

        // 尝试执行脚本，看JSP是否语法正确
        Invocable invoke = (Invocable) scriptEngine;
        invoke.invokeFunction(main, message);


        ScriptServiceKey scriptServiceKey = new ScriptServiceKey();
        scriptServiceKey.setScriptEngine(scriptEngine);
        scriptServiceKey.setScript(decodeScript);

        return scriptServiceKey;
    }

    public OperateEntity getSplitOperate(Map<String, Object> channelParam) {
        try {
            String manufacturer = (String) channelParam.get("manufacturer");
            String deviceType = (String) channelParam.get("deviceType");
            String splitHandler = (String) channelParam.get("splitHandler");
            String keyHandler = (String) channelParam.get("keyHandler");
            String serviceKey = (String) channelParam.get("serviceKey");
            if (MethodUtils.hasEmpty(manufacturer, deviceType, splitHandler, keyHandler, serviceKey)) {
                throw new ServiceException("参数不能为空: manufacturer, deviceType, splitHandler, keyHandler, serviceKey");
            }

            OperateEntity operateEntity = new OperateEntity();
            operateEntity.setManufacturer(manufacturer);
            operateEntity.setDeviceType(deviceType);
            operateEntity.setOperateName(splitHandler);
            operateEntity.setEngineType("JavaScript");
            operateEntity = this.entityManageService.getEntity(operateEntity.makeServiceKey(), OperateEntity.class);
            if (operateEntity == null) {
                throw new ServiceException("参数不能为空: 找不到对应的操作实体");
            }

            return operateEntity;

        } catch (Exception e) {
            e.getMessage();
        }

        return null;
    }

    public OperateEntity getKeyOperate(Map<String, Object> channelParam) {
        try {
            String manufacturer = (String) channelParam.get("manufacturer");
            String deviceType = (String) channelParam.get("deviceType");
            String splitHandler = (String) channelParam.get("splitHandler");
            String keyHandler = (String) channelParam.get("keyHandler");
            String serviceKey = (String) channelParam.get("serviceKey");
            if (MethodUtils.hasEmpty(manufacturer, deviceType, splitHandler, keyHandler, serviceKey)) {
                throw new ServiceException("参数不能为空: manufacturer, deviceType, splitHandler, keyHandler, serviceKey");
            }

            OperateEntity operateEntity = new OperateEntity();
            operateEntity.setManufacturer(manufacturer);
            operateEntity.setDeviceType(deviceType);
            operateEntity.setOperateName(keyHandler);
            operateEntity.setEngineType("JavaScript");
            operateEntity = this.entityManageService.getEntity(operateEntity.makeServiceKey(), OperateEntity.class);
            if (operateEntity == null) {
                throw new ServiceException("参数不能为空: 找不到对应的操作实体");
            }

            return operateEntity;

        } catch (Exception e) {
            e.getMessage();
        }

        return null;
    }
}

