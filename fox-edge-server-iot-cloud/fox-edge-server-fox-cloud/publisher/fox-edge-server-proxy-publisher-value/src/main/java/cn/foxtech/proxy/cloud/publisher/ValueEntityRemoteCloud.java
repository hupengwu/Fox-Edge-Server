package cn.foxtech.proxy.cloud.publisher;

import cn.foxtech.proxy.cloud.publisher.service.EntityRemoteCloudService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 远端云
 */
@Component
public class ValueEntityRemoteCloud extends EntityRemoteCloudService {
    @Override
    public String getUrlTimestamp() {
        return "/value/timestamp";
    }

    @Override
    public String getUrlReset() {
        return "/value/reset";
    }

    @Override
    public String getUrlComplete() {
        return "/value/complete";
    }

    @Override
    public String getUrlEntity() {
        return "/value/entity";
    }
}
