package cn.foxtech.kernel.system.common.service;

import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.entity.manager.EntityServiceManager;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.file.FileTextUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 获得运行期配置的基础类型
 * 备注：该模式只能在 Manager 这个服务的 ConfigEntity 生产者采用
 * 其他服务，只能能够向 Manager 发出注册请求的 InitialConfigService
 */
public abstract class RuntimeConfigService {
    private final Logger logger = Logger.getLogger(this.getClass());

    private Map<String, Object> defaultConfig;
    private String serviceKey = "";

    public abstract EntityServiceManager getEntityManageService();

    public abstract RedisConsoleService getRedisConsoleService();

    public abstract String getFoxServiceType();

    public abstract String getFoxServiceName();

    public abstract String getConfigName();

    public abstract String getClassPathResource();

    public void initialize() {
        try {
            // 构造运行期会用到的serviceKey，用来查找运行期信息的
            ConfigEntity entity = new ConfigEntity();
            entity.setServiceName(this.getFoxServiceName());
            entity.setServiceType(this.getFoxServiceType());
            entity.setConfigName(this.getConfigName());
            this.serviceKey = entity.makeServiceKey();

            // 从文件中构造出缺省配置
            if (this.defaultConfig == null) {
                ClassPathResource classPathResource = new ClassPathResource(this.getClassPathResource());
                InputStream inputStream = classPathResource.getInputStream();
                String json = FileTextUtils.readTextFile(inputStream, StandardCharsets.UTF_8);
                this.defaultConfig = JsonUtils.buildObject(json, Map.class);
            }


            // 生成缺省的配置项目
            if (!this.getEntityManageService().hasEntity(this.serviceKey, ConfigEntity.class)) {
                entity = new ConfigEntity();
                entity.setServiceName(this.getFoxServiceName());
                entity.setServiceType(this.getFoxServiceType());
                entity.setConfigName(this.getConfigName());

                entity.setConfigParam((Map<String, Object>) this.defaultConfig.getOrDefault("configParam", new HashMap<>()));
                entity.setConfigValue((Map<String, Object>) this.defaultConfig.getOrDefault("configValue", new HashMap<>()));
                entity.setRemark((String) this.defaultConfig.getOrDefault("remark", ""));

                this.getEntityManageService().insertEntity(entity);
            }

        } catch (Exception e) {
            String message = this.getClassPathResource() + "配置初始化失败:" + e.getMessage();

            this.logger.error(message);
            this.getRedisConsoleService().error(message);
        }
    }

    public Map<String, Object> getConfigValue() {
        ConfigEntity entity = this.getEntityManageService().getEntity(this.serviceKey, ConfigEntity.class);
        return entity.getConfigValue();
    }
}
