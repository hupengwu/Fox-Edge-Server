package cn.foxtech.channel.common.service;

import cn.foxtech.common.entity.manager.EntityConfigInitializer;
import cn.foxtech.common.entity.manager.EntityConfigManager;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ConfigManageService extends EntityConfigInitializer {
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
}
