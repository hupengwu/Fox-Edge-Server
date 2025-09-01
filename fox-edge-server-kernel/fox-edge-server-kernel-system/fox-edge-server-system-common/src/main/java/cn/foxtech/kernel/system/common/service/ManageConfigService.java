/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.kernel.system.common.service;

import cn.foxtech.common.entity.constant.ConfigVOFieldConstant;
import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.entity.manager.InitialConfigService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.MapUtils;
import cn.foxtech.common.utils.file.FileTextUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理配置服务：它简化从ConfigEntity获得数据的操作
 */
@Component
public class ManageConfigService {
    @Autowired
    private EntityManageService entityManageService;

    /**
     * 初始化配置：需要感知运行期的用户动态输入的配置，所以直接使用这个组件
     */
    @Autowired
    private InitialConfigService configService;

    @Autowired
    private RedisConsoleService console;

    @Value("${spring.fox-service.service.type}")
    private String foxServiceType = "undefinedServiceType";

    @Value("${spring.fox-service.service.name}")
    private String foxServiceName = "undefinedServiceName";

    public void initialize(String configName, String classpathFile) {
        try {
            // 初始化配置信息
            ConfigEntity configEntity = this.entityManageService.getConfigEntity(this.foxServiceName, this.foxServiceType, configName);
            if (configEntity == null) {
                ClassPathResource classPathResource = new ClassPathResource(classpathFile);
                InputStream inputStream = classPathResource.getInputStream();
                String json = FileTextUtils.readTextFile(inputStream, StandardCharsets.UTF_8);
                Map<String,Object> defaultConfig = JsonUtils.buildObject(json, Map.class);

                Map<String,Object> configParam = (Map<String,Object>)MapUtils.getOrDefault(defaultConfig,"configParam",new HashMap<>());
                Map<String,Object> configValue = (Map<String,Object>)MapUtils.getOrDefault(defaultConfig,"configValue",new HashMap<>());
                String remark = (String) MapUtils.getOrDefault(defaultConfig,"remark","");

                configEntity = new ConfigEntity();
                configEntity.setServiceName(this.foxServiceName);
                configEntity.setServiceType(this.foxServiceType);
                configEntity.setConfigName(configName);
                configEntity.setConfigParam(configParam);
                configEntity.setConfigValue(configValue);
                configEntity.setRemark(remark);
                this.entityManageService.insertEntity(configEntity);
            }

            // 在通用配置这边，也形成一份后期通告的配置
            this.configService.initialize(configName,classpathFile);

        } catch (Exception e) {
            this.console.error("初始化参数失败:"+e.getMessage());
        }
    }

    public Map<String, Object> getConfigValue(String configName) {
        return this.getConfigValue(this.foxServiceName, this.foxServiceType, configName);
    }

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

    public void saveConfigValue(String configName, Map<String, Object> configValue) {
        this.saveConfigValue(this.foxServiceName, this.foxServiceType, configName, configValue);
    }

    public void saveConfigValue(String serviceName, String serviceType, String configName, Map<String, Object> configValue) {
        if (configValue==null){
            return;
        }

        ConfigEntity configEntity = this.entityManageService.getConfigEntity(serviceName, serviceType, configName);
        if (configEntity == null) {
            configEntity = new ConfigEntity();
            configEntity.setServiceName(serviceName);
            configEntity.setServiceType(serviceType);
            configEntity.setConfigName(configName);
            configEntity.setConfigValue(configValue);
            this.entityManageService.insertEntity(configEntity);
            return;
        }

        configEntity.setConfigValue(configValue);
        this.entityManageService.updateEntity(configEntity);
    }


    public Object getConfigValueOrDefault(String configName, String key, Object defaultValue) {
        return this.getConfigValueOrDefault(this.foxServiceName, this.foxServiceType, configName, key, defaultValue);
    }

    public Object getConfigValueOrDefault(String serviceName, String serviceType, String configName, String key, Object defaultValue) {
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
