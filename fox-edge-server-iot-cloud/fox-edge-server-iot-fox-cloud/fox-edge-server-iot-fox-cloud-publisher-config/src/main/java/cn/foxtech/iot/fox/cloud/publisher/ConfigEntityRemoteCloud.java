package cn.foxtech.iot.fox.cloud.publisher;

import cn.foxtech.iot.fox.cloud.publisher.service.EntityRemoteCloudService;
import org.springframework.stereotype.Component;

/**
 * 对云端进行数据增量同步操作
 */
@Component
public class ConfigEntityRemoteCloud extends EntityRemoteCloudService {
    public String getUrlTimestamp() {
        return "/config/timestamp";
    }

    public String getUrlReset() {
        return "/config/reset";
    }

    public String getUrlEntity() {
        return "/config/entity";
    }
}
