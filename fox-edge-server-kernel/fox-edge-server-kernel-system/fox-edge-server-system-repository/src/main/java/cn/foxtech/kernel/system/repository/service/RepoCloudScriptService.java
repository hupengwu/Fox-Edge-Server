package cn.foxtech.kernel.system.repository.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * JavaScript版本的JSP解码器服务
 */
@Component
public class RepoCloudScriptService {
    @Autowired
    private CloudRemoteService cloudRemoteService;

    public Map<String, Object> queryCloudCompEntity(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/script/entity", body);
    }

    public Map<String, Object> queryCloudCompList(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/script/page", body);
    }

    public Map<String, Object> queryCloudVersionList(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/script/version/page", body);
    }

    public Map<String, Object> queryCloudOperateList(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/script/version/operate/entities", body);
    }

    public Map<String, Object> queryCloudOperateEntity(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/script/version/operate/entity", body);
    }
}
