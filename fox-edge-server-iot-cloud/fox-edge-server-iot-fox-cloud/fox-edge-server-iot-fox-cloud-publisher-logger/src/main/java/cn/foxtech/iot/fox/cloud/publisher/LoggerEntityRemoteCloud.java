package cn.foxtech.iot.fox.cloud.publisher;

import cn.foxtech.iot.fox.cloud.publisher.service.EntityRemoteCloudService;
import org.springframework.stereotype.Component;

/**
 * 远端云
 */
@Component
public class LoggerEntityRemoteCloud extends EntityRemoteCloudService {
    private final String url_timestamp = "/logger/timestamp";
    private final String url_reset = "/logger/reset";
    private final String url_entity = "/logger/entity";

    public String getUrlTimestamp() {
        return this.url_timestamp;
    }

    public String getUrlReset() {
        return this.url_reset;
    }

    public String getUrlEntity() {
        return this.url_entity;
    }
}
