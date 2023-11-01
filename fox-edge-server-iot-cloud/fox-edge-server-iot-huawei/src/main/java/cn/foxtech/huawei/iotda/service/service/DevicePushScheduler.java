package cn.foxtech.huawei.iotda.service.service;

import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DevicePushScheduler extends PeriodTaskService {
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
        // 推送出现变化的设备
        this.deviceRegisterExecutor.registerDevice();

        // 推送出现变化的设备在线状态
        this.deviceOnlineExecutor.pushDeviceStatus();

        // 推送设备的数值
        this.devValueExecutor.pushDeviceValue();

        Thread.sleep(10 * 1000);
    }
}
