/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.fastbee.service.scheduler;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.DeviceValueEntity;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.common.utils.time.interval.TimeIntervalMap;
import cn.foxtech.fastbee.service.remote.RemoteService;
import cn.foxtech.fastbee.service.service.FastBeeService;
import cn.foxtech.iot.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DeviceValuePushScheduler extends PeriodTaskService {
    private final TimeIntervalMap timeIntervalMap = new TimeIntervalMap();

    @Autowired
    private RemoteService remoteService;
    @Autowired
    private FastBeeService fastBeeService;
    @Autowired
    private EntityManageService entityManageService;

    @Override
    public void execute(long threadId) throws Exception {
        Thread.sleep(1000);

        if (!this.entityManageService.isInitialized()) {
            return;
        }


        List<BaseEntity> entityList = this.entityManageService.getEntityList(DeviceEntity.class);
        for (BaseEntity entity : entityList) {
            DeviceEntity deviceEntity = (DeviceEntity) entity;

            // 取出注册参数
            String deviceName = deviceEntity.getDeviceName();
            Object productId = deviceEntity.getDeviceParam().getOrDefault(this.fastBeeService.getProductId(), "");
            Object deviceNum = deviceEntity.getDeviceParam().getOrDefault(this.fastBeeService.getDeviceNum(), "");
            if (MethodUtils.hasEmpty(productId, deviceNum)) {
                continue;
            }

            // 检查：是否已经连接上云端的MQTT服务器
            if (!this.remoteService.isConnected(deviceName, productId, deviceNum)) {
                continue;
            }

            // 检查：计算整点时间间隔，是否已经到达
            long timestamp = TimeIntervalMap.getFixTime(System.currentTimeMillis(), this.fastBeeService.getTimeInterval(), this.fastBeeService.getTimeUnit());
            long timeInterval = TimeIntervalMap.calculate(this.fastBeeService.getTimeInterval(), this.fastBeeService.getTimeUnit());
            if (!this.timeIntervalMap.testLastTime(deviceEntity.getDeviceName(), timestamp, timeInterval)) {
                continue;
            }

            DeviceValueEntity deviceValueEntity = this.entityManageService.readEntity(deviceEntity.makeServiceKey(), DeviceValueEntity.class);
            if (deviceValueEntity == null) {
                continue;
            }

            // 推送数据到云端
            this.remoteService.publishDeviceValue(deviceEntity.getDeviceName(), productId, deviceNum, deviceValueEntity.getParams(), timestamp);
        }
    }

}
