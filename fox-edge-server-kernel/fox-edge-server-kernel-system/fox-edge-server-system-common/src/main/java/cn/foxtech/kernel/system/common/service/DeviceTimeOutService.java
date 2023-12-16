package cn.foxtech.kernel.system.common.service;

import cn.foxtech.common.entity.manager.RedisConsoleService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 运行期配置信息：连接云端仓库的配置信息
 */
@Component
public class DeviceTimeOutService extends RuntimeConfigService {
    @Getter
    @Autowired
    private EntityManageService entityManageService;

    @Getter
    @Autowired
    private RedisConsoleService redisConsoleService;

    @Getter
    @Value("${spring.fox-service.service.type}")
    private String foxServiceType = "undefinedServiceType";

    @Getter
    @Value("${spring.fox-service.service.name}")
    private String foxServiceName = "undefinedServiceName";

    public String getConfigName() {
        return "deviceTimeOutConfig";
    }

    public String getClassPathResource() {
        return "deviceTimeOutConfig.json";
    }
}
