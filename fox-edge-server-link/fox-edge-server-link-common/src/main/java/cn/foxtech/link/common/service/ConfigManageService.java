package cn.foxtech.link.common.service;

import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.entity.manager.EntityConfigManager;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.Maps;
import cn.foxtech.common.utils.file.FileTextUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import java.io.File;
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

    @Autowired
    private EntityConfigManager entityConfigManager;

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

    /**
     * 从resource下的json文件中装载缺省的配置参数，从管理服务中装载用户的配置参数，并合并成一个启动参数
     * @param configName 配置名称
     * @param classpathFile resource下的json文件
     * @return 启动参数
     */
    public Map<String, Object> loadInitConfig(String configName,String classpathFile) {
        try {
            // 从配置文件中，读取缺省的配置参数
            File file = ResourceUtils.getFile("classpath:" + classpathFile);
            String json = FileTextUtils.readTextFile(file);
            Map<String, Object> defaultConfig = JsonUtils.buildObject(json, Map.class);

            // 填写该信息：通告给管理服务，添加该配置作为缺省配置
            this.entityConfigManager.setConfigEntity("serverConfig", defaultConfig);

            // 取出管理服务通告的配置信息
            Map<String, Object> systemConfig = this.getConfigParam("serverConfig");

            // 通过合并两者信息，获得配置参数
            Map<String, Object> configValue = new HashMap<>();
            configValue.putAll(defaultConfig);
            configValue.putAll(systemConfig);
            return configValue;

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new ServiceException(e.getMessage());
        }
    }
}
