/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.script.engine;

import cn.foxtech.common.utils.MapUtils;
import cn.foxtech.device.protocol.v1.core.context.ApplicationContext;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ScriptEngineModel {
    private final Map<String, Object> modelMap = new ConcurrentHashMap<>();

    @Autowired
    private ScriptEngineService engineService;

    /**
     * 获得设备模型
     * 说明：用户参数的约定，会尝试读取modelName
     *
     * @param manufacturer 厂商
     * @param deviceType   设备类型
     * @param params       用户参数
     */
    public synchronized void setEnvDeviceModel(String manufacturer, String deviceType, String modelNameKey, Map<String, Object> params) {
        try {
            ScriptEngine scriptEngine = this.engineService.getScriptEngine(manufacturer, deviceType);

            // 检测：是否定义了该字段
            if (MethodUtils.hasEmpty(modelNameKey)) {
                return;
            }

            // 从用户的输入参数中，取出设备模型名称
            String modelName = (String) params.get(modelNameKey);
            if (MethodUtils.hasEmpty(modelName)) {
                return;
            }

            // 取出全局的设备模型
            Map<String, Object> globalModel = ApplicationContext.getDeviceModels(modelName);
            if (MethodUtils.hasEmpty(globalModel)) {
                return;
            }

            // 从引擎侧，取出合并设备模型
            Map<String, Object> engineModel = (Map<String, Object>) MapUtils.getValue(this.modelMap, manufacturer, deviceType, modelName);
            if (engineModel != null) {
                // 检测：上下文侧的时间戳和当前模型的时间戳是否一致
                Object globalUpdateTime = globalModel.getOrDefault("updateTime", 0L);
                Object engineUpdateTime = engineModel.getOrDefault("updateTime", 0L);
                if (globalUpdateTime.equals(engineUpdateTime)) {
                    return;
                }
            }

            // 保存模型级别的数据
            engineModel = globalModel;
            MapUtils.setValue(this.modelMap, manufacturer, deviceType, modelName, engineModel);

            // 构造跟该型号设备相关的的设备模型信息
            Map<String, Object> deviceModels = this.buildEngineModels(manufacturer, deviceType);

            // 将模型信息注入到引擎之中
            scriptEngine.put("fox_edge_model", deviceModels);
        } catch (Exception e) {
            return;
        }
    }

    private Map<String, Object> buildEngineModels(String manufacturer, String deviceType) {
        // 取出整个设备类型级别的数据
        Map<String, Object> engineModels = (Map<String, Object>) MapUtils.getValue(this.modelMap, manufacturer, deviceType);

        Map<String, Object> result = new HashMap<>();
        for (String key : engineModels.keySet()) {

            Map<String, Object> engineModel = (Map<String, Object>) engineModels.get(key);

            // 取出其中的modelParam
            Map<String, Object> modelParam = (Map<String, Object>) engineModel.get("modelParam");
            if (modelParam == null) {
                continue;
            }

            // 取出其中的list數據
            List<Map<String, Object>> list = (List<Map<String, Object>>) modelParam.get("list");
            if (list == null) {
                continue;
            }

            result.put(key, list);
        }

        return result;
    }
}