package cn.foxtech.iot.common.remote;

import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.http.HttpClientUtils;
import cn.hutool.http.HttpResponse;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Getter(value = AccessLevel.PUBLIC)
public class RemoteHttpProxyService {
    private static final Logger logger = Logger.getLogger(RemoteHttpProxyService.class);

    /**
     * header
     */
    private final Map<String, String> header = new ConcurrentHashMap<>();
    /**
     * 服务
     */
    @Setter
    private String uri = "http://demo.thingsboard.io/api";

    @Autowired
    private RedisConsoleService consoleService;


    public int executeRestful(String res, String method, String requestJson) throws IOException {
        if (this.header.isEmpty()) {
            this.header.put("Content-Type", "application/json");
        }


        HttpResponse response = HttpClientUtils.executeRestful(this.uri + res, method, this.header, requestJson);
        return response.getStatus();
    }
}
