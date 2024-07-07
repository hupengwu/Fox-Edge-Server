package cn.foxtech.persist.iotdb.service;

import cn.foxtech.common.entity.manager.InitialConfigService;
import cn.foxtech.common.utils.iotdb.IoTDBSessionPool;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class IoTDBSessionService extends IoTDBSessionPool {
    @Autowired
    private InitialConfigService configService;

    public void initialize() {
        Map<String, Object> configs = this.configService.getConfigParam("serverConfig");
        Map<String, Object> params = (Map<String, Object>) configs.getOrDefault("deviceHistory", new HashMap<>());

        String host = (String) params.get("host");
        Integer port = (Integer) params.get("port");
        String username = (String) params.get("username");
        String password = (String) params.get("password");

        if (MethodUtils.hasEmpty(host, port, username, password)) {
            throw new ServiceException("参数不能为空：host, port, username, password");
        }

        this.initialize(host, port, username, password);
    }
}
