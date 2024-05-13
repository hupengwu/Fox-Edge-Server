package cn.foxtech.device.script.engine;

import cn.foxtech.common.utils.MapUtils;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ScriptEngineService {
    private final ScriptEngineManager manager = new ScriptEngineManager();
    private final Map<String, Object> engineMap = new ConcurrentHashMap<>();


    public ScriptEngine getScriptEngine(String manufacturer, String deviceType) {
        ScriptEngine engine = (ScriptEngine) MapUtils.getValue(this.engineMap, manufacturer, deviceType);
        if (engine == null) {
            engine = this.manager.getEngineByName("JavaScript");
            MapUtils.setValue(this.engineMap, manufacturer, deviceType, engine);
        }

        return engine;
    }

}
