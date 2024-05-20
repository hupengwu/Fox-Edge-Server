package cn.foxtech.value.ex.task.service.service;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.DeviceValueExTaskEntity;
import cn.foxtech.value.ex.task.service.entity.DataObject;
import cn.foxtech.value.ex.task.service.entity.DataSource;
import cn.foxtech.value.ex.task.service.entity.DataTask;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DataTaskManager {
    @Getter
    private final Map<String, DataTask> dataTaskMap = new ConcurrentHashMap<>();

    @Setter
    @Getter
    private boolean needReset = true;

    @Autowired
    private EntityManageService entityManageService;

    public void reset() {
        List<BaseEntity> taskEntityList = this.entityManageService.getEntityList(DeviceValueExTaskEntity.class);
        for (BaseEntity entity : taskEntityList) {
            DeviceValueExTaskEntity valueExTaskEntity = (DeviceValueExTaskEntity) entity;

            DataTask dataTask = new DataTask();
            dataTask.setTaskName(valueExTaskEntity.getTaskName());
            dataTask.bind(valueExTaskEntity.getTaskParam());

            this.dataTaskMap.put(dataTask.getTaskName(), dataTask);
        }

        this.needReset = false;
    }

    public Map<String, DataTask> buildTaskMap(DeviceEntity deviceEntity) {
        Map<String, DataTask> taskMap = new HashMap<>();
        for (String key : this.dataTaskMap.keySet()) {
            DataTask dataTask = this.dataTaskMap.get(key);

            DataObject dataObject = this.hasDataObject(deviceEntity, dataTask.getDataSource());
            if (dataObject == null) {
                continue;
            }

            taskMap.put(key, dataTask);
        }

        return taskMap;
    }


    /**
     * 某个对象可能同时存在多个任务之中，那么以这些任务最大的那个队列深度，作为缓存大小
     *
     * @param taskMap    设备相关的任务
     * @param objectName 某个对象
     * @return 缓存大小
     */
    public int getCacheMaxSize(Map<String, DataTask> taskMap, String objectName) {
        int maxSize = -1;
        for (String key : taskMap.keySet()) {
            DataTask dataTask = taskMap.get(key);
            DataObject dataObject = dataTask.getDataSource().getDataObject();

            if ("object".equals(dataObject.getObjectType()) && !dataObject.getObjectName().contains(objectName)) {
                continue;
            }

            int cacheSize = dataTask.getCacheSize();
            if (cacheSize > maxSize) {
                maxSize = cacheSize;
            }
        }

        return maxSize;
    }

    private DataObject hasDataObject(DeviceEntity deviceEntity, DataSource dataSource) {
        DataObject globalType = this.testGlobal(dataSource);
        if (globalType != null) {
            return globalType;
        }

        DataObject deviceTypes = this.testDeviceType(dataSource, deviceEntity.getManufacturer(), deviceEntity.getDeviceType());
        if (deviceTypes != null) {
            return deviceTypes;
        }

        return this.testDeviceName(dataSource, deviceEntity.getDeviceName());
    }


    private DataObject testGlobal(DataSource dataSource) {
        if ("global".equals(dataSource.getSourceType())) {
            return dataSource.getDataObject();
        }

        return null;
    }

    private DataObject testDeviceType(DataSource dataSource, String manufacturer, String deviceType) {
        if ("deviceType".equals(dataSource.getSourceType()) && manufacturer.equals(dataSource.getManufacturer()) && deviceType.equals(dataSource.getDeviceType())) {
            return dataSource.getDataObject();
        }

        return null;
    }

    private DataObject testDeviceName(DataSource dataSource, String deviceName) {
        if ("deviceType".equals(dataSource.getSourceType()) && deviceName.equals(dataSource.getDeviceName())) {
            return dataSource.getDataObject();
        }

        return null;
    }
}
