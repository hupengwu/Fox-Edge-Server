package cn.foxtech.thingspanel.service.service;

import cn.foxtech.common.entity.manager.LocalConfigService;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DeviceValueEntityScheduler extends PeriodTaskService {
    /**
     * 云端发布者
     */
    @Autowired
    private DeviceValuePeriodSynchronizer deviceValuePeriodSynchronizer;

    @Autowired
    private DeviceValueTriggerSynchronizer deviceValueTriggerSynchronizer;

    @Autowired
    private LocalConfigService localConfigService;

    private String mode;

    public void initialize() {
        // 读取配置参数
        Map<String, Object> configs = this.localConfigService.getConfig();
        this.mode = (String) configs.getOrDefault("mode", "period");
        Map<String, Object> period = (Map<String, Object>) configs.getOrDefault("period", new HashMap<>());
        String unit = (String) period.getOrDefault("unit", "minute");
        Integer interval = (Integer) period.getOrDefault("interval", 30);

        // 时间间隔单位
        Long u = 1000L;
        if (unit.equals("second")) {
            u = 1000L;
        } else if (unit.equals("minute")) {
            u = 60 * 1000L;
        } else if (unit.equals("hour")) {
            u = 60 * 60 * 1000L;
        } else {
            u = 60 * 1000L;
        }

        this.deviceValuePeriodSynchronizer.setInterval(interval * u);
    }

    @Override
    public void execute(long threadId) throws Exception {
        Thread.sleep(1000);

        if (this.mode.equals("trigger")) {
            this.deviceValueTriggerSynchronizer.syncEntity();
        } else {
            this.deviceValuePeriodSynchronizer.syncEntity();
        }
    }
}
