/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.persist.iotdb.history;

import cn.foxtech.common.entity.entity.DeviceSequenceEntity;
import cn.foxtech.common.entity.manager.InitialConfigService;
import cn.foxtech.common.entity.service.devicesequence.DeviceSequenceEntityService;
import cn.foxtech.common.tags.RedisTagService;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.number.NumberUtils;
import cn.foxtech.persist.common.history.IDeviceSequenceRecordUpdater;
import cn.foxtech.persist.common.service.PersistManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class DeviceSequenceMySQLUpdater implements IDeviceSequenceRecordUpdater {
    @Autowired
    private DeviceSequenceEntityService deviceSequenceEntityService;

    @Autowired
    private InitialConfigService configService;

    @Autowired
    private PersistManageService entityManageService;

    @Autowired
    private RedisTagService tagService;

    private long lastTimeOperate = 0;

    /**
     * 更新记录类型的数据
     *
     * @param deviceName 设备名称
     * @param deviceType 设备类型
     * @param sequence   记录信息
     */
    public void updateDeviceSequenceValue(String deviceName, String manufacturer, String deviceType, Map<String, Object> sequence) {
        if (sequence == null) {
            return;
        }

        Object deviceValue = sequence.get("deviceValue");
        String metricName = (String) sequence.get("metricName");
        Long timestamp = NumberUtils.makeLong(sequence.get("timestamp"));

        if (MethodUtils.hasEmpty(deviceValue) || MethodUtils.hasEmpty(metricName) || !sequence.containsKey("timestamp")) {
            return;
        }

        DeviceSequenceEntity recordEntity = new DeviceSequenceEntity();
        recordEntity.setDeviceName(deviceName);
        recordEntity.setMetricName(metricName);
        recordEntity.setTimestamp(timestamp);
        recordEntity.setDeviceValue(sequence);

        // 保存到数据库
        this.deviceSequenceEntityService.insertEntity(recordEntity);

        // 更新标记
        this.tagService.setValue(DeviceSequenceEntity.class.getSimpleName(), recordEntity);
    }

    public void clearDeviceSequenceRecord() {
        try {
            if (!this.entityManageService.isInitialized()) {
                return;
            }

            Map<String, Object> configs = this.configService.getConfigParam("serverConfig");
            Map<String, Object> params = (Map<String, Object>) configs.getOrDefault("operateRecord", new HashMap<>());

            Integer maxHour = (Integer) params.getOrDefault("maxHour", 72);
            Integer period = (Integer) params.getOrDefault("period", 3600);


            // 检查：执行周期是否到达
            long currentTime = System.currentTimeMillis();
            if ((currentTime - this.lastTimeOperate) < period * 1000) {
                return;
            }
            this.lastTimeOperate = currentTime;

            // 除了最近的maxCount条数据，旧数据全部删除
            this.deviceSequenceEntityService.deleteRecords(maxHour);
        } catch (Exception e) {
        }
    }
}
