package cn.foxtech.manager.system.service;

import cn.foxtech.common.entity.entity.ConfigEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理配置服务：它简化从ConfigEntity获得数据的操作
 */
@Component
public class ManageConfigService {
    @Autowired
    private EntityManageService entityManageService;

    public Map<String, Object> getConfigValue(String serviceName, String serviceType, String configName) {
        // 获得配置信息
        ConfigEntity configEntity = this.entityManageService.getConfigEntity(serviceName, serviceType, configName);
        if (configEntity == null) {
            configEntity = new ConfigEntity();
            configEntity.setServiceName(serviceName);
            configEntity.setServiceType(serviceType);
            configEntity.setConfigName(configName);
            this.entityManageService.insertEntity(configEntity);
        }

        return configEntity.getConfigValue();
    }


    public void saveConfigValue(String serviceName, String serviceType, String configName, Map<String, Object> configParam) {
        ConfigEntity configEntity = this.entityManageService.getConfigEntity(serviceName, serviceType, configName);
        if (configEntity == null) {
            configEntity = new ConfigEntity();
            configEntity.setServiceName(serviceName);
            configEntity.setServiceType(serviceType);
            configEntity.setConfigName(configName);
            configEntity.setConfigValue(configParam);
            this.entityManageService.insertEntity(configEntity);
            return;
        }

        configEntity.setConfigValue(configParam);
        this.entityManageService.updateEntity(configEntity);
    }

    public Object getConfigValue(String serviceName, String serviceType, String configName, String key, Object defaultValue) {
        // 取出配置参数
        Map<String, Object> configParam = this.getConfigValue(serviceName, serviceType, configName);

        // 取出数值
        Object value = configParam.getOrDefault(key, defaultValue);

        // 检查：通过重新组装参数，判定是否发生初始值的变化
        Map<String, Object> actualityParam = new HashMap<>();
        actualityParam.putAll(configParam);
        actualityParam.put(key, value);

        // 保存初始值变化后的数据
        if (!actualityParam.equals(configParam)) {
            ConfigEntity configEntity = new ConfigEntity();
            configEntity.setServiceName(serviceName);
            configEntity.setServiceType(serviceType);
            configEntity.setConfigName(configName);
            configEntity.setConfigValue(actualityParam);

            this.entityManageService.updateEntity(configEntity);
        }

        return value;
    }
}
