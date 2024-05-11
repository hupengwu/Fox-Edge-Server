package cn.foxtech.persist.iotdb.history;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.DeviceMapperEntity;
import cn.foxtech.common.entity.entity.DeviceValueEntity;
import cn.foxtech.common.entity.manager.InitialConfigService;
import cn.foxtech.common.utils.ContainerUtils;
import cn.foxtech.persist.common.history.IDeviceHistoryUpdater;
import cn.foxtech.persist.common.service.EntityManageService;
import cn.foxtech.persist.iotdb.service.IoTDBSessionService;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DeviceHistoryIoTDBUpdater implements IDeviceHistoryUpdater {
    private static final Logger logger = Logger.getLogger(DeviceHistoryIoTDBUpdater.class);

    /**
     * 实体管理
     */
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private IoTDBSessionService sessionService;

    @Autowired
    private InitialConfigService configService;

    /**
     * 上次处理时间
     */
    private long lastTime = 0;

    @Override
    public void saveHistoryEntity(DeviceValueEntity existValueEntity, Map<String, Object> statusValues) {
        if (!this.sessionService.isInitialize()) {
            return;
        }

        if (existValueEntity == null) {
            return;
        }

        // 根据设备类型信息，找到设备下面的对象定义表
        List<BaseEntity> deviceMapperEntity = this.entityManageService.getEntityList(DeviceMapperEntity.class, (Object value) -> {
            DeviceMapperEntity entity = (DeviceMapperEntity) value;
            return entity.getDeviceType().equals(existValueEntity.getDeviceType()) && entity.getManufacturer().equals(existValueEntity.getManufacturer());
        });

        // 重组结构
        Map<String, BaseEntity> mapper = ContainerUtils.buildMapByKey(deviceMapperEntity, DeviceMapperEntity::getObjectName);


        try {
            long time = System.currentTimeMillis();

            String deviceId = "root.tb_device_history.device_" + existValueEntity.getId();

            Map<String, Object> values = new HashMap<>();
            for (String key : statusValues.keySet()) {
                Object value = statusValues.get(key);
                if (!this.isValue(value)) {
                    continue;
                }

                DeviceMapperEntity mapperEntity = (DeviceMapperEntity) mapper.get(key);
                if (mapperEntity == null) {
                    continue;
                }

                values.put("oid_" + mapperEntity.getId(), value);
            }

            // 写入历史数据
            this.sessionService.insertRecord(deviceId, time, values);
        } catch (Exception e) {
            // 不打印日志
            e.getMessage();
        }
    }

    private boolean isValue(Object paramValue) {
        if (paramValue instanceof Double) {
            return true;
        }

        if (paramValue instanceof Float) {
            return true;
        }

        if (paramValue instanceof Integer) {
            return true;
        }

        if (paramValue instanceof Long) {
            return true;
        }

        return paramValue instanceof Short;

    }

    @Override
    public void clearHistoryEntity() {
        try {
            if (!this.entityManageService.isInitialized()) {
                return;
            }
            if (!this.sessionService.isInitialize()) {
                return;
            }

            Map<String, Object> configs = this.configService.getConfigParam("serverConfig");
            Map<String, Object> params = (Map<String, Object>) configs.getOrDefault("deviceHistory", new HashMap<>());

            Integer lifeCycle = (Integer) params.getOrDefault("lifeCycle", 3600 * 24);
            Integer period = (Integer) params.getOrDefault("period", 3600);

            // 检查：执行周期是否到达
            long currentTime = System.currentTimeMillis();
            if ((currentTime - this.lastTime) < period * 1000) {
                return;
            }
            this.lastTime = currentTime;

            // 删除过期的数据
            this.deleteData(currentTime - lifeCycle * 1000L);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void deleteData(long time) throws IoTDBConnectionException, StatementExecutionException {
        List<BaseEntity> entityList = this.entityManageService.getEntityList(DeviceEntity.class);
        for (BaseEntity entity : entityList) {
            String deviceId = "root.tb_device_history.device_" + entity.getId();
            this.sessionService.deleteData(deviceId, time);
        }
    }
}
