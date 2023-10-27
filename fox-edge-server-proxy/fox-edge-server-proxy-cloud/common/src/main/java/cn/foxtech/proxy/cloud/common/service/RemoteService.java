package cn.foxtech.proxy.cloud.common.service;

import cn.foxtech.common.entity.manager.ConfigManageService;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.proxy.cloud.common.service.proxy.CloudHttpProxyService;
import cn.foxtech.proxy.cloud.common.service.proxy.CloudMqttProxyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Getter(value = AccessLevel.PUBLIC)
public class RemoteService {
    private static final Logger logger = Logger.getLogger(RemoteService.class);

    @Autowired
    private CloudHttpProxyService httpProxyService;

    @Autowired
    private CloudMqttProxyService mqttProxyService;

    @Autowired
    private ConfigManageService configManageService;

    private String mode;


    public boolean isLockdown() {
        return this.httpProxyService.isLockdown();
    }

    public boolean isLogin() {
        if (this.getMode().equals("mqtt")) {
            return this.mqttProxyService.isLogin();
        } else {
            return this.httpProxyService.isLogin();
        }


    }

    private String getMode() {
        if (MethodUtils.hasEmpty(this.mode)) {
            Map<String, Object> configs = this.configManageService.loadInitConfig("serverConfig", "serverConfig.json");
            Map<String, Object> cloudConfig = (Map<String, Object>) configs.getOrDefault("cloud", new HashMap<>());
            this.mode = (String) cloudConfig.getOrDefault("mode", "mix");
        }


        return this.mode;

    }

    public <REQ> Map<String, Object> executePost(String res, REQ requestVO) throws IOException {
        if (this.getMode().equals("mqtt")) {
            return this.mqttProxyService.executeRestful(res, "post", requestVO);
        } else {
            ObjectMapper objectMapper = new ObjectMapper();
            String requestJson = objectMapper.writeValueAsString(requestVO);
            return this.httpProxyService.executeRestful("/aggregator" + res, "post", requestJson);
        }
    }

}
