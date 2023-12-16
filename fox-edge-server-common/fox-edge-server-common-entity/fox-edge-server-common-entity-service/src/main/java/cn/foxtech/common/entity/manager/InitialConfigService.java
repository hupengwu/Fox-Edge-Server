package cn.foxtech.common.entity.manager;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 初始化配置的管理
 *
 * 简化初始化配置的注册流程
 */
@Getter
@Component
public class InitialConfigService extends EntityConfigInitializer {
    /**
     * 日志
     */
    @Autowired
    private RedisConsoleService logger;

    @Autowired
    private EntityServiceManager entityManageService;

    @Autowired
    private InitialConfigNotifier initialConfigNotifier;

    @Value("${spring.fox-service.service.type}")
    private String foxServiceType = "undefinedServiceType";

    @Value("${spring.fox-service.service.name}")
    private String foxServiceName = "undefinedServiceName";
}
