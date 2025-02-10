/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.fastbee.service.scheduler;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.fastbee.service.remote.RemoteService;
import cn.foxtech.fastbee.service.service.FastBeeService;
import cn.foxtech.iot.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 订阅fast-bee平台发送过来的控制命令
 */
@Component
public class DeviceSubscribeScheduler extends PeriodTaskService {
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

        Set<String> existKeys = new HashSet<>();
        List<BaseEntity> entityList = this.entityManageService.getEntityList(DeviceEntity.class);
        for (BaseEntity entity : entityList) {
            DeviceEntity deviceEntity = (DeviceEntity) entity;

            // 取出注册参数
            Object productId = deviceEntity.getExtendParam().getOrDefault(this.fastBeeService.getProductId(), "");
            Object deviceNum = deviceEntity.getExtendParam().getOrDefault(this.fastBeeService.getDeviceNum(), "");
            if (MethodUtils.hasEmpty(productId, deviceNum)) {
                continue;
            }

            // 保存：计划创建的连接
            existKeys.add(deviceEntity.getDeviceName());

            // 订阅云端的控制命令
            this.remoteService.subscribeTopic(deviceEntity.getDeviceName(), productId, deviceNum);
        }

        // 关闭配置无效的mqtt连接
        this.remoteService.closeNotExist(existKeys);
    }
}
