package cn.foxtech.channel.common.service;

import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ConfigManageService {
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService logger;

    @Autowired
    private EntityManageService entityManageService;

    @Value("${spring.fox-service.service.type}")
    private String foxServiceType = "undefinedServiceType";

    @Value("${spring.fox-service.service.name}")
    private String foxServiceName = "undefinedServiceName";


    /**
     * 获得配置信息
     *
     * @param configName 配置名称
     * @return 配置表
     */
    public Map<String, Object> getConfigParam(String configName) {
        Map<String, Object> result = new HashMap<>();

        // 读取解码器配置信息
        ConfigEntity configEntity = this.entityManageService.getConfigEntity(this.foxServiceName, this.foxServiceType, configName);
        if (configEntity == null) {
            logger.error("找不到指定的配置信息:" + configName);
            return result;
        }

        return configEntity.getConfigValue();
    }

    public <T> T getOrDefaultValue(String configName, Class<T> clazz, Object... keys) {
        Map<String, Object> configParam = this.getConfigParam(configName);
        return Maps.getOrDefault(configParam, clazz, keys);
    }
}
