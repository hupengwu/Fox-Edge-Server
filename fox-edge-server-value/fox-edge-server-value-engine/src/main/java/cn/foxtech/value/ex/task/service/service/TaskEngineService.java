package cn.foxtech.value.ex.task.service.service;

import cn.foxtech.common.entity.entity.*;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.entity.service.redis.RedisWriter;
import cn.foxtech.value.ex.task.service.entity.DataObject;
import cn.foxtech.value.ex.task.service.entity.DataTask;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import java.util.HashMap;
import java.util.Map;

@Component
public class TaskEngineService {
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService logger;

    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private ScriptEngineService engineService;

    @Autowired
    private DataTaskManager dataTaskManager;

    @Setter
    @Getter
    private boolean needReset = true;

    @Getter
    private boolean initialized = false;

    public void reset() {
        Map<String, DataTask> dataTaskMap = this.dataTaskManager.getDataTaskMap();
        if (dataTaskMap.isEmpty()) {
            return;
        }

        for (String key : dataTaskMap.keySet()) {
            DataTask dataTask = dataTaskMap.get(key);

            this.reset(dataTask);
        }

        this.needReset = false;
        this.initialized = true;
    }


    private void reset(DataTask dataTask) {
        try {
            if (dataTask == null) {
                return;
            }

            String jsp = dataTask.getMethodScript();
            if (jsp == null || jsp.isEmpty()) {
                return;
            }

            // 取出对应的引擎
            ScriptEngine engine = this.engineService.getScriptEngine(dataTask.getTaskName());

            // 加载脚本
            engine.eval(jsp);
        } catch (Exception e) {
            String message = "初始化脚本引擎异常：" + dataTask.getTaskName() + "; " + e.getMessage();
            this.logger.error(message);
        }
    }

    private Map<String, Object> evalScript(DataTask dataTask, Map<String, Object> values, Map<String, Object> param) {
        try {
            ScriptEngine scriptEngine = this.engineService.getScriptEngine(dataTask.getTaskName());
            scriptEngine.put("values", values);
            scriptEngine.put("params", param);

            // 执行脚本
            Object data = scriptEngine.eval("main();");
            if (data == null) {
                return null;
            }

            // 检查返回的结构
            if (!(data instanceof Map)) {
                return null;
            }

            return (Map<String, Object>) data;

        } catch (Exception e) {
            return null;
        }
    }

    public void evalScript(DeviceValueEntity deviceValueEntity, Map<String, DeviceValueExObjectValue> deviceMap) {
        if (!this.initialized) {
            return;
        }

        DeviceEntity deviceEntity = this.entityManageService.getDeviceEntity(deviceValueEntity.getDeviceName());
        if (deviceEntity == null) {
            return;
        }

        Map<String, DataTask> taskMap = this.dataTaskManager.buildTaskMap(deviceEntity);
        if (taskMap.isEmpty()) {
            return;
        }


        // 将CacheList中的values提取出来
        Map<String, Object> values = this.buildValues(deviceMap);

        // 计算数据
        Map<String, Object> result = new HashMap<>();
        for (String key : taskMap.keySet()) {
            DataTask dataTask = taskMap.get(key);

            // 取出任务相关数据
            Map<String, Object> map = this.getValueMap(dataTask.getDataSource().getDataObject(), values);

            // 执行脚本
            Map<String, Object> data = this.evalScript(dataTask, map, deviceEntity.getDeviceParam());
            if (data == null) {
                continue;
            }

            result.putAll(data);
        }

        // 保存数据到redis
        this.saveDeviceValueEx(deviceEntity, result);
    }

    private void saveDeviceValueEx(DeviceEntity deviceEntity, Map<String, Object> values) {
        if (values.isEmpty()) {
            return;
        }

        RedisWriter redisWriter = this.entityManageService.getRedisWriter(DeviceValueExEntity.class);

        Long time = System.currentTimeMillis();

        DeviceValueExEntity deviceValueExEntity = new DeviceValueExEntity();
        deviceValueExEntity.setId(deviceEntity.getId());
        deviceValueExEntity.setManufacturer(deviceEntity.getManufacturer());
        deviceValueExEntity.setDeviceName(deviceEntity.getDeviceName());
        deviceValueExEntity.setDeviceType(deviceEntity.getDeviceType());
        deviceValueExEntity.setUpdateTime(time);

        for (String key : values.keySet()) {
            DeviceObjectValue deviceObjectValue = new DeviceObjectValue();
            deviceObjectValue.setValue(values.get(key));
            deviceObjectValue.setTime(time);
            deviceValueExEntity.getParams().put(key, deviceObjectValue);
        }

        redisWriter.writeEntity(deviceValueExEntity);
    }

    private Map<String, Object> buildValues(Map<String, DeviceValueExObjectValue> cacheMap) {
        Map<String, Object> taskMap = new HashMap<>();
        for (String key : cacheMap.keySet()) {
            DeviceValueExObjectValue dataCacheList = cacheMap.get(key);

            taskMap.put(key, dataCacheList.getValues());
        }

        return taskMap;
    }

    private Map<String, Object> getValueMap(DataObject dataObject, Map<String, Object> deviceMap) {
        if ("all".equals(dataObject.getObjectType())) {
            return deviceMap;
        }

        if (dataObject.getObjectName().equals(deviceMap.keySet())) {
            return deviceMap;
        }

        Map<String, Object> result = new HashMap<>();
        for (String key : dataObject.getObjectName()) {
            Object data = deviceMap.get(key);
            if (data == null) {
                continue;
            }

            result.put(key, data);
        }

        return result;
    }
}
