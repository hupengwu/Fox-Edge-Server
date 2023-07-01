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
        return "/aggregator/value/timestamp";
    }

    @Override
    public String getUrlReset() {
        return "/aggregator/value/reset";
    }

    @Override
    public String getUrlComplete() {
        return "/aggregator/value/complete";
    }

    @Override
    public String getUrlEntity() {
        return "/aggregator/value/entity";
    }
}
