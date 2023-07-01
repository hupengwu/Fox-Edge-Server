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
        return "/aggregator/define/timestamp";
    }

    @Override
    public String getUrlReset() {
        return "/aggregator/define/reset";
    }

    @Override
    public String getUrlComplete() {
        return "/aggregator/define/complete";
    }

    @Override
    public String getUrlEntity() {
        return "/aggregator/define/entity";
    }
}
