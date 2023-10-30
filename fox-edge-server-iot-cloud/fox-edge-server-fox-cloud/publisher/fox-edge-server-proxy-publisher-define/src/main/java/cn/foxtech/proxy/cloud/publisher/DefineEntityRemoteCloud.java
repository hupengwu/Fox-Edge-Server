package cn.foxtech.proxy.cloud.publisher;

import cn.foxtech.proxy.cloud.publisher.service.EntityRemoteCloudService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 远端云
 */
@Component
public class DefineEntityRemoteCloud extends EntityRemoteCloudService {
    @Override
    public String getUrlTimestamp() {
        return "/define/timestamp";
    }

    @Override
    public String getUrlReset() {
        return "/define/reset";
    }

    @Override
    public String getUrlComplete() {
        return "/define/complete";
    }

    @Override
    public String getUrlEntity() {
        return "/define/entity";
    }
}
