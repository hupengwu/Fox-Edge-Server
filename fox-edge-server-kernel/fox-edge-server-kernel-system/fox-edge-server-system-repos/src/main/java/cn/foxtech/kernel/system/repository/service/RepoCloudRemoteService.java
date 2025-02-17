/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

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

    public Map<String, Object> queryCloudCompModelPage(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/model/page", body);
    }


    public Map<String, Object> queryCloudScriptVersionPage(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/script/version/page", body);
    }

    public Map<String, Object> queryCloudModelVersionPage(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/model/version/page", body);
    }


    public Map<String, Object> queryCloudScriptVersionList(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/script/version/entities", body);
    }

    public Map<String, Object> queryCloudModelVersionList(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/model/version/entities", body);
    }


    public Map<String, Object> queryCloudScriptOperateList(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/script/version/operate/entities", body);
    }

    public Map<String, Object> queryCloudModelObjectList(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/model/version/object/entities", body);
    }


    public Map<String, Object> queryCloudScriptOperateEntity(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/script/version/operate/entity", body);
    }

    public Map<String, Object> queryCloudModelObjectEntity(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/model/version/object/entity", body);
    }

    public Map<String, Object> queryCloudDevTemplateVersionPage(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/dev-template/version/page", body);
    }

    public Map<String, Object> queryCloudDevTemplateVersionList(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/dev-template/version/entities", body);
    }

    public Map<String, Object> queryCloudCompDevTemplatePage(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/dev-template/page", body);
    }

    public Map<String, Object> queryCloudDevTemplateObjectList(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/dev-template/version/object/entities", body);
    }

    public Map<String, Object> queryCloudDevTemplateObjectEntity(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/dev-template/version/object/entity", body);
    }

    public Map<String, Object> queryCloudIotTemplateVersionPage(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/iot-template/version/page", body);
    }

    public Map<String, Object> queryCloudIotTemplateVersionList(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/iot-template/version/entities", body);
    }

    public Map<String, Object> queryCloudCompIotTemplatePage(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/iot-template/page", body);
    }

    public Map<String, Object> queryCloudIotTemplateObjectList(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/iot-template/version/object/entities", body);
    }

    public Map<String, Object> queryCloudIotTemplateObjectEntity(Map<String, Object> body) throws IOException {
        return this.cloudRemoteService.executePost("/manager/repository/component/iot-template/version/object/entity", body);
    }
}
