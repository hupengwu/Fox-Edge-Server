package cn.foxtech.persist.common.service.updater;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceRecordEntity;
import cn.foxtech.common.entity.service.devicerecord.DeviceRecordEntityService;
import cn.foxtech.common.tags.RedisTagService;
import cn.foxtech.device.protocol.v1.core.constants.FoxEdgeConstant;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DeviceRecordValueUpdater {
    /**
     * 记录的更新时间
     */
    private final Map<String, Long> recordLastTime = new HashMap<>();

    /**
     * 设备记录服务
     */
    @Autowired
    private DeviceRecordEntityService deviceRecordEntityService;

    @Autowired
    private RedisTagService tagService;

    /**
     * 更新记录类型的数据
     *
     * @param deviceName 设备名称
     * @param deviceType 设备类型
     * @param recordList 记录信息
     */
    public void updateDeviceRecordValue(String deviceName, String manufacturer, String deviceType, List<Map<String, Object>> recordList) {
        if (recordList == null) {
            return;
        }

        for (Map<String, Object> record : recordList) {
            // 记录类型
            String recordType = (String) record.get(FoxEdgeConstant.RECORD_TYPE_TAG);
            if (recordType == null) {
                continue;
            }

            DeviceRecordEntity recordEntity = new DeviceRecordEntity();
            recordEntity.setDeviceName(deviceName);
            recordEntity.setDeviceType(deviceType);
            recordEntity.setManufacturer(manufacturer);
            recordEntity.setRecordName(recordType);

            // MD5签名
            recordEntity.setRecordData(record);

            // 保存到数据库
            this.deviceRecordEntityService.insertEntity(recordEntity);

            // 更新标记
            this.tagService.setValue(DeviceRecordEntity.class.getSimpleName(),recordEntity);
        }
    }

    /**
     * 保存记录实体到数据库：该版本不一定合理，先注释掉
     *
     * @param recordEntity 记录实体
     */
    public void saveDeviceRecordEntity(DeviceRecordEntity recordEntity) {
        // 检查：这条记录是否已经存在于最近的缓存
        Long lastTime = this.queryLastTime(recordEntity);
        if (lastTime != null) {
            return;
        }

        // 转换为PO对象，实际上数据库查询只接受PO查询
        List<BaseEntity> recordEntityList = this.deviceRecordEntityService.selectEntityList((QueryWrapper) recordEntity.makeWrapperKey());

        // 如果没有这个数据，那么插入
        if (recordEntityList.isEmpty()) {
            // 保存到数据库
            this.deviceRecordEntityService.insertEntity(recordEntity);

            // 记录记录的最近状态
            this.updateLastTime(recordEntity);
            return;
        }

        // 检查：是否存在该数据
        for (BaseEntity entity : recordEntityList) {
            // 该数据存在，不需要插入
            if (recordEntity.getRecordData().equals(((DeviceRecordEntity) entity).getRecordData())) {
                // 记录记录的最近状态
                this.updateLastTime(recordEntity);
                return;
            }
        }


        // 该数据不存在，需要插入
        this.deviceRecordEntityService.insertEntity(recordEntity);

        // 记录记录的最近状态
        this.updateLastTime(recordEntity);
    }

    /**
     * 查询记录的最近更新时间
     *
     * @param recordEntity
     * @return
     */
    private Long queryLastTime(DeviceRecordEntity recordEntity) {
        List<Object> key = new ArrayList<>();
        key.add(recordEntity.makeServiceKey());
        key.add(recordEntity.makeServiceValue());

        Long lastTime = this.recordLastTime.get(key.toString());
        return lastTime;
    }

    /**
     * 更新数据到LastUpdate缓存中
     *
     * @param recordEntity
     */
    private void updateLastTime(DeviceRecordEntity recordEntity) {
        List<Object> key = new ArrayList<>();
        key.add(recordEntity.makeServiceKey());
        key.add(recordEntity.makeServiceValue());

        // 插入数据
        this.recordLastTime.put(key.toString(), System.currentTimeMillis());

        // 队列是否已经满了
        if (this.recordLastTime.size() <= 10) {
            return;
        }

        // 检查：最老的数据
        String oldKey = null;
        Long oldValue = Long.MAX_VALUE;
        for (Map.Entry<String, Long> entry : this.recordLastTime.entrySet()) {
            if (entry.getValue() < oldValue) {
                oldValue = entry.getValue();
                oldKey = entry.getKey();
                continue;
            }
        }

        // 删除老数据
        this.recordLastTime.remove(oldKey);
    }
}
