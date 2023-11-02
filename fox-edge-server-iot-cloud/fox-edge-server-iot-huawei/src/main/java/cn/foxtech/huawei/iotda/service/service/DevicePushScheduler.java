package cn.foxtech.huawei.iotda.service.service;

import cn.foxtech.common.utils.Maps;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DevicePushScheduler extends PeriodTaskService {
    private final Map<String, Object> timeMap = new HashMap<>();
    /**
     * 云端发布者
     */
    @Autowired
    private DeviceOnlineExecutor deviceOnlineExecutor;

    @Autowired
    private DeviceRegisterExecutor deviceRegisterExecutor;

    @Autowired
    private DevValueExecutor devValueExecutor;


    public void initialize() {

    }

    @Override
    public void execute(long threadId) throws Exception {
        // 10秒检查一下，对云平台，并不需要太实时性的数据推送
        Thread.sleep(10 * 1000);

        Long time = System.currentTimeMillis();

        // 注册全体设备
        Long registerAllDevice = (Long) Maps.getOrDefault(this.timeMap, "registerAllDevice", 0L);
        if (time > registerAllDevice + 3600 * 1000) {
            Maps.setValue(this.timeMap, time, "registerAllDevice", time);

            // 强制推送全量设备
            this.deviceRegisterExecutor.registerDevice(true);
        } else {
            // 动态推送注册/注销设备
            this.deviceRegisterExecutor.registerDevice(false);
        }

        Long pushDeviceStatus = (Long) Maps.getOrDefault(this.timeMap, "pushDeviceStatus", 0L);
        if (time > pushDeviceStatus + 3600 * 1000) {
            Maps.setValue(this.timeMap, time, "pushDeviceStatus", time);

            // 强制推送全量设备
            this.deviceOnlineExecutor.pushDeviceStatus(true);
        } else {
            // 动态推送注册/注销设备
            this.deviceOnlineExecutor.pushDeviceStatus(false);
        }

        // 推送设备的数值
        this.devValueExecutor.pushDeviceValue();
    }
}
