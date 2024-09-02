package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.core.context.IApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class Ctx implements IApplicationContext {
    public Map<String, Object> getDeviceModels(String modelName){
        return new HashMap<>();
    }
}
