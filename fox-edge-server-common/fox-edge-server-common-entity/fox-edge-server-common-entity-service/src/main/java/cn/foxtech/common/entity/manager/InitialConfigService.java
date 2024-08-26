/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.manager;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 初始化配置的管理
 * <p>
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

    /**
     * 重新绑定
     * 说明：
     * 默认（服务模式），全局只有一个entityManageService实例，可以有spring框架的Autowired自动组装。
     * 组合模式，此时多个模块都会有entityManageService实例，需要手动指定一个entityManageService
     *
     * @param entityManageService
     */
    public void bindEntityManageService(EntityServiceManager entityManageService) {
        this.entityManageService = entityManageService;
    }
}
