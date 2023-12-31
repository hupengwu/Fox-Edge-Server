package cn.foxtech.kernel.system.repository.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * JavaScript版本的JSP解码器服务
 */
@Component
public class RepoCloudRemoteService {
    @Autowired
    private CloudRemoteService cloudRemoteService;

    public Map<String, Object> queryCloudCompFileList(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/entities", body);
    }

    public Map<String, Object> queryCloudCompScriptPage(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/script/page", body);
    }

    public Map<String, Object> queryCloudScriptVersionPage(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/script/version/page", body);
    }

    public Map<String, Object> queryCloudScriptVersionList(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/script/version/entities", body);
    }

    public Map<String, Object> queryCloudScriptOperateList(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/script/version/operate/entities", body);
    }

    public Map<String, Object> queryCloudScriptOperateEntity(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/script/version/operate/entity", body);
    }
}
