package cn.foxtech.common.entity.manager;

import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.utils.Maps;
import cn.foxtech.common.utils.file.FileTextUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 程序启动的时候，初始化全局配置参数的基础类
 * 背景：
 * 用户开发的程序，在启动的时候，想要想要用初始化文件向管理服务异步注册初始化参数。
 * 当管理服务接受注册请求后，管理服务会有出现配置参数，那么用户就可以在管理服务的界面照猫画虎，进行修改配置了。
 * 当用户在管理服务那边修改之后，那么本服务启动后，会优先使用用户用户在管理服务那边的手动配置
 */
public abstract class EntityConfigInitializer {

    public abstract RedisConsoleService getLogger();

    public abstract EntityServiceManager getEntityManageService();

    public abstract EntityConfigManager getEntityConfigManager();


    public abstract String getFoxServiceType();


    public abstract String getFoxServiceName();


    /**
     * 获得配置信息
     *
     * @param configName 配置名称
     * @return 配置表
     */
    public Map<String, Object> getConfigParam(String configName) {
        Map<String, Object> result = new HashMap<>();

        // 读取解码器配置信息
        ConfigEntity configEntity = this.getEntityManageService().getConfigEntity(this.getFoxServiceName(), this.getFoxServiceType(), configName);
        if (configEntity == null) {
            if (this.getLogger() != null) {
                this.getLogger().error("找不到指定的配置信息:" + configName);
            }

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
     *
     * @param configName    配置名称
     * @param classpathFile resource下的json文件
     * @return 启动参数
     */
    public Map<String, Object> loadInitConfig(String configName, String classpathFile) {
        try {
            // 从配置文件中，读取缺省的配置参数
            ClassPathResource classPathResource = new ClassPathResource(classpathFile);
            InputStream inputStream = classPathResource.getInputStream();
            String json = FileTextUtils.readTextFile(inputStream, StandardCharsets.UTF_8);
            Map<String, Object> defaultConfig = JsonUtils.buildObject(json, Map.class);

            // 填写该信息：通告给管理服务，添加该配置作为缺省配置
            this.getEntityConfigManager().setConfigEntity(configName, defaultConfig);

            // 取出管理服务通告的配置信息
            Map<String, Object> systemConfig = this.getConfigParam(configName);

            // 通过合并两者信息，获得配置参数
            Map<String, Object> configValue = new HashMap<>();
            configValue.putAll((Map<String, Object>) defaultConfig.getOrDefault("configValue", new HashMap<>()));
            configValue.putAll(systemConfig);
            return configValue;

        } catch (Exception e) {
            if (this.getLogger() != null) {
                this.getLogger().error(e.getMessage());
            }

            throw new ServiceException(e.getMessage());
        }
    }
}
