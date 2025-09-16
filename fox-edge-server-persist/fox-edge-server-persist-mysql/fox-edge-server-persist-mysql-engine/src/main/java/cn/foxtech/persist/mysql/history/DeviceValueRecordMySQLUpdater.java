/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.persist.mysql.history;


import cn.foxtech.common.entity.entity.DeviceValueEntity;
import cn.foxtech.common.entity.entity.DeviceValueRecordEntity;
import cn.foxtech.common.entity.manager.InitialConfigService;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.persist.common.history.IDeviceValueRecordUpdater;
import cn.foxtech.persist.common.service.PersistManageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DeviceValueRecordMySQLUpdater implements IDeviceValueRecordUpdater {
    private static final Logger logger = Logger.getLogger(DeviceValueRecordMySQLUpdater.class);

    /**
     * 实体管理
     */
    @Autowired
    private PersistManageService entityManageService;

    @Autowired
    private InitialConfigService configService;

    /**
     * 上次处理时间
     */
    private long lastTime = 0;

    @Override
    public void saveDeviceValueRecord(List<DeviceValueEntity> valueEntityList) {
        try {
            if (valueEntityList == null || valueEntityList.isEmpty()) {
                return;
            }

            Map<String, Object> configs = this.configService.getConfigParam("serverConfig");
            Map<String, Object> params = (Map<String, Object>) configs.getOrDefault("deviceValueRecord", new HashMap<>());

            Boolean enable = (Boolean) params.getOrDefault("enable", false);
            if (!enable) {
                return;
            }

            // 从第一个元素中，取得设备厂商、型号、名称信息
            DeviceValueEntity valueEntity = valueEntityList.get(0);

            long time = System.currentTimeMillis();

            // 构造历史记录对象
            DeviceValueRecordEntity deviceValueRecordEntity = new DeviceValueRecordEntity();
            deviceValueRecordEntity.setManufacturer(valueEntity.getManufacturer());
            deviceValueRecordEntity.setDeviceType(valueEntity.getDeviceType());
            deviceValueRecordEntity.setDeviceName(valueEntity.getDeviceName());
            deviceValueRecordEntity.setCreateTime(time);
            deviceValueRecordEntity.setCreateTime(time);

            for (DeviceValueEntity entity : valueEntityList){
                Map<String, Object> values = DeviceValueEntity.buildTimeValue(entity.getParams());
                deviceValueRecordEntity.getDeviceValue().add(values);
            }

            this.entityManageService.getDeviceValueRecordEntityService().insertEntity(deviceValueRecordEntity);

        } catch (Exception e) {
            logger.warn(e);
        }
    }


    @Override
    public void clearDeviceValueRecordEntity() {
        try {
            if (!this.entityManageService.isInitialized()) {
                return;
            }

            Map<String, Object> configs = this.configService.getConfigParam("serverConfig");
            Map<String, Object> params = (Map<String, Object>) configs.getOrDefault("deviceValueRecord", new HashMap<>());

            Integer maxCount = (Integer) params.getOrDefault("maxCount", 1000000);
            Integer period = (Integer) params.getOrDefault("period", 3600);

            // 检查：执行周期是否到达
            long currentTime = System.currentTimeMillis();
            if ((currentTime - this.lastTime) < period * 1000) {
                return;
            }
            this.lastTime = currentTime;

            // 除了最近的maxCount条数据，旧数据全部删除
            this.entityManageService.getDeviceValueRecordEntityService().delete(maxCount);
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
