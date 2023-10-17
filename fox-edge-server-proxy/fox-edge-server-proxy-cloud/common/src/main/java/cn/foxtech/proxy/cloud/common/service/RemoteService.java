package cn.foxtech.proxy.cloud.common.service;

import cn.foxtech.proxy.cloud.common.service.proxy.CloudHttpProxyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@Getter(value = AccessLevel.PUBLIC)
public class RemoteService {
    private static final Logger logger = Logger.getLogger(RemoteService.class);


    @Autowired
    private CloudHttpProxyService httpProxyService;

    public boolean isLockdown() {
        return this.httpProxyService.isLockdown();
    }

    public boolean isLogin() {
        return this.httpProxyService.isLogin();
    }


    public <REQ> Map<String, Object> executePost(String res, REQ requestVO) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(requestVO);
        return this.executePost(res, json);
    }

    public Map<String, Object> executePost(String res, String requestJson) throws IOException {
        return this.httpProxyService.executeRestful(res, "post", requestJson);
    }
}
