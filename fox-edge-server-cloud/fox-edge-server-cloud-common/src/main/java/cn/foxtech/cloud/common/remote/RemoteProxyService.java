package cn.foxtech.cloud.common.remote;

import cn.foxtech.common.entity.manager.LocalConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RemoteProxyService {
    @Autowired
    private LocalConfigService localConfigService;

    @Autowired
    private RemoteHttpProxyService httpProxyService;

    public void initialize() {
        // 读取配置参数
        Map<String, Object> configs = this.localConfigService.getConfigs();

        Map<String, Object> remote = (Map<String, Object>) configs.getOrDefault("remote", new HashMap<>());
        Map<String, Object> http = (Map<String, Object>) remote.getOrDefault("http", new HashMap<>());
        String host = (String) http.getOrDefault("host", "http://localhost");

        this.httpProxyService.setUri(host);
    }
}
