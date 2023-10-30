package cn.foxtech.proxy.cloud.publisher;

import cn.foxtech.proxy.cloud.publisher.service.EntityRemoteCloudService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 远端云
 */
@Component
public class RecordEntityRemoteCloud extends EntityRemoteCloudService {
    public String getUrlTimestamp() {
        return "/record/timestamp";
    }

    public String getUrlReset() {
        return "/record/reset";
    }

    public String getUrlEntity() {
        return "/record/entity";
    }
}
