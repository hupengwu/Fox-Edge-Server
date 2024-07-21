package cn.foxtech.iot.fox.publish.service.service;

import cn.foxtech.common.entity.manager.LocalConfigService;
import cn.foxtech.common.utils.number.NumberUtils;
import cn.foxtech.common.utils.time.interval.TimeIntervalMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TimeIntervalService {
    /**
     * 运行时间
     */
    private final TimeIntervalMap timeIntervalMap = new TimeIntervalMap();
    @Autowired
    private LocalConfigService localConfigService;

    public boolean testLastTime(String entityType) {
        try {
            // 取出全局配置参数
            Map<String, Object> entities = (Map<String, Object>) this.localConfigService.getConfig().getOrDefault("entities", new HashMap<>());

            Map<String, Object> map = (Map<String, Object>) entities.get(entityType);
            if (map == null) {
                return false;
            }

            Object publish = map.getOrDefault("publish", false);
            Object interval = map.getOrDefault("interval", 1);

            if (!Boolean.TRUE.equals(publish)) {
                return false;
            }

            // 测试时间间隔
            return this.timeIntervalMap.testLastTime(entityType, NumberUtils.makeLong(interval) * 1000);
        } catch (Exception e) {
            return false;
        }
    }
}
