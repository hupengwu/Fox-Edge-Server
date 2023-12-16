package cn.foxtech.common.entity.manager;

import cn.foxtech.common.entity.constant.ConfigVOFieldConstant;
import cn.foxtech.common.entity.entity.ConfigEntity;
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

    private final Map<String, Object> defaultConfigs = new HashMap<>();

    public abstract RedisConsoleService getLogger();

    public abstract EntityServiceManager getEntityManageService();

    public abstract InitialConfigNotifier getInitialConfigNotifier();

    public abstract String getFoxServiceType();

    public abstract String getFoxServiceName();

    /**
     * 获得运行期的配置信息
     *
     * @param configName 配置名称
     * @return 配置表
     */
    public Map<String, Object> getConfigParam(String configName) {
        // 取出该配置的信息
        Map<String, Object> defaultConfig = (Map<String, Object>) this.defaultConfigs.getOrDefault(configName, new HashMap<>());

        // 取出本服务提供的缺省配置
        Map<String, Object> configValue = new HashMap<>();
        configValue.putAll((Map<String, Object>) defaultConfig.getOrDefault(ConfigVOFieldConstant.field_config_value, new HashMap<>()));

        // 取出管理服务提供的用户配置
        ConfigEntity configEntity = this.getEntityManageService().getConfigEntity(this.getFoxServiceName(), this.getFoxServiceType(), configName);
        if (configEntity == null) {
            return configValue;
        }


        // 如果能取到，那么合并用户的配置值
        configValue.putAll(configEntity.getConfigValue());
        return configValue;
    }

    /**
     * 从resource下的json文件中装载缺省的配置参数，从管理服务中装载用户的配置参数，并合并成一个启动参数
     *
     * @param configName    配置名称
     * @param classpathFile resource下的json文件
     */
    public void initialize(String configName, String classpathFile) {
        try {
            Map<String, Object> defaultConfig = (Map<String, Object>) this.defaultConfigs.get(configName);

            // 从配置文件中，读取缺省的配置参数
            if (defaultConfig == null) {
                ClassPathResource classPathResource = new ClassPathResource(classpathFile);
                InputStream inputStream = classPathResource.getInputStream();
                String json = FileTextUtils.readTextFile(inputStream, StandardCharsets.UTF_8);
                defaultConfig = JsonUtils.buildObject(json, Map.class);

                this.defaultConfigs.put(configName, defaultConfig);
            }


            // 填写该信息：通知管理服务，添加该配置作为缺省配置
            this.getInitialConfigNotifier().notifyRegisterConfig(configName, defaultConfig);

            // 取出管理服务通告的配置信息
            Map<String, Object> systemConfig = this.getConfigParam(configName);

            // 通过合并两者信息，获得配置参数
            Map<String, Object> configValue = new HashMap<>();
            configValue.putAll((Map<String, Object>) defaultConfig.getOrDefault(ConfigVOFieldConstant.field_config_value, new HashMap<>()));
            configValue.putAll(systemConfig);
        } catch (Exception e) {
            if (this.getLogger() != null) {
                this.getLogger().error(e.getMessage());
            }

            throw new ServiceException(e.getMessage());
        }
    }
}
