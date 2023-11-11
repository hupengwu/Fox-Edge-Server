package cn.foxtech.period.service.service;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.DeviceObjectValue;
import cn.foxtech.common.entity.entity.DeviceValueEntity;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.number.NumberUtils;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.period.service.entity.PeriodRecordEntity;
import cn.foxtech.period.service.entity.PeriodTaskEntity;
import cn.foxtech.period.service.mapper.periodrecord.PeriodRecordEntityMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class PeriodRecordService extends PeriodTaskService {
    private static final Logger logger = Logger.getLogger(PeriodRecordService.class);

    private final Map<String, Long> lastTimeMap = new HashMap<>();

    @Autowired
    private EntityManageService entityManageService;


    @Autowired(required = false)
    private PeriodRecordEntityMapper mapper;
    private Long lastDeleteInvalid = 0L;


    /**
     * 执行任务
     * 步骤：
     * 1.获取操作模板列表，进行循环遍历操作
     * 2.将操作请求编码后发送给设备，并对设备返回的数据进行解码，得到设备的各项数值
     * 3.将设备的各项数值，写入redis保存，通过redis分享给其他服务
     *
     * @throws Exception 异常情况
     */
    public void execute(long threadId) throws Exception {
        Thread.sleep(1000);

        // 检查：是否装载完毕
        if (!this.entityManageService.isInitialized()) {
            return;
        }

        // 删除失效的数据
        this.deleteInvalid();


        List<PeriodRecordEntity> periodRecordEntityList = new ArrayList<>();

        List<BaseEntity> taskList = this.entityManageService.getEntityList(PeriodTaskEntity.class);
        for (BaseEntity entity : taskList) {
            PeriodTaskEntity taskEntity = (PeriodTaskEntity) entity;

            // 检查：是否到了执行周期
            long lastTime = this.lastTimeMap.getOrDefault(taskEntity.getTaskName(), 0L);
            String periodBatch = this.makePeriodBatch(taskEntity, lastTime);
            if (periodBatch == null) {
                continue;
            }
            this.lastTimeMap.put(taskEntity.getTaskName(), System.currentTimeMillis());

            // 组织跟该任务相关的设备ID
            List<Object> deviceIds = this.getBindDeviceIds(taskEntity);
            for (Object objectId : deviceIds) {
                Long deviceId = NumberUtils.makeLong(objectId);
                if (deviceId == null) {
                    continue;
                }

                // 获得设备对象
                DeviceEntity deviceEntity = this.entityManageService.getEntity(deviceId, DeviceEntity.class);
                if (deviceEntity == null) {
                    continue;
                }

                // 获得数值对象
                DeviceValueEntity valueEntity = this.entityManageService.readEntity(deviceEntity.makeServiceKey(), DeviceValueEntity.class);
                if (valueEntity == null) {
                    continue;
                }

                // 每超过100条记录，就保存数据
                this.appendRecordEntity(periodBatch, taskEntity.getId(), taskEntity.getObjectIds(), valueEntity, periodRecordEntityList);
                if (periodRecordEntityList.size() > 100) {
                    this.insert(periodRecordEntityList);
                    periodRecordEntityList.clear();
                }
            }

            // 每个任务最大10W条记录，超过的就删除
            this.deleteOverload(taskEntity.getId(), 10 * 10000);
        }

        // 保存剩余记录
        if (!periodRecordEntityList.isEmpty()) {
            this.insert(periodRecordEntityList);
            periodRecordEntityList.clear();
        }
    }

    private List<Object> getBindDeviceIds(PeriodTaskEntity taskEntity) {
        List<Object> deviceIds = new ArrayList<>();
        if (Boolean.TRUE.equals(taskEntity.getSelectDevice())) {
            // 组织跟该任务相关的设备ID
            deviceIds = taskEntity.getDeviceIds();
        } else {
            List<BaseEntity> entityList = this.entityManageService.getEntityList(DeviceEntity.class);
            for (BaseEntity entity : entityList) {
                DeviceEntity deviceEntity = (DeviceEntity) entity;
                String manufacturer = deviceEntity.getManufacturer();
                String deviceType = deviceEntity.getDeviceType();
                if (manufacturer == null || deviceType == null) {
                    continue;
                }

                if (manufacturer.equals(taskEntity.getManufacturer()) && deviceType.equals(taskEntity.getDeviceType())) {
                    deviceIds.add(deviceEntity.getId());
                }
            }
        }

        return deviceIds;
    }

    private String makePeriodBatch(PeriodTaskEntity taskEntity, long lastTime) {
        String mode = (String) taskEntity.getTaskParam().get("mode");
        String timeUnit = (String) taskEntity.getTaskParam().get("timeUnit");
        Integer timeInterval = (Integer) taskEntity.getTaskParam().get("timeInterval");

        if (MethodUtils.hasEmpty(mode, timeUnit, timeInterval)) {
            return null;
        }

        Long currentTime = System.currentTimeMillis();
        Date nowTime = new Date(currentTime);


        /**
         * 按时间间隔模式
         */
        if (mode.equals("interval")) {
            if (timeUnit.equals("second")) {
                if ((currentTime - lastTime > timeInterval * 1000)) {
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    return sf.format(nowTime);
                }
            }
            if (timeUnit.equals("minute")) {
                if ((currentTime - lastTime > timeInterval * 1000 * 60)) {
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    return sf.format(nowTime);
                }
            }
            if (timeUnit.equals("hour")) {
                if ((currentTime - lastTime > timeInterval * 1000 * 3600)) {
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH");
                    return sf.format(nowTime);
                }
            }
            if (timeUnit.equals("day")) {
                if ((currentTime - lastTime > timeInterval * 1000 * 3600 * 24)) {
                    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                    return sf.format(nowTime);
                }
            }
        }


        return null;
    }

    private void appendRecordEntity(String recordBatch, Long taskId, List<String> taskObjectIds, DeviceValueEntity valueEntity, List<PeriodRecordEntity> periodRecordEntityList) {
        try {
            Long time = System.currentTimeMillis();

            if (valueEntity == null) {
                return;
            }

            for (String objectName : taskObjectIds) {
                DeviceObjectValue value = valueEntity.getParams().get(objectName);
                if (value == null) {
                    continue;
                }

                PeriodRecordEntity periodRecordEntity = new PeriodRecordEntity();
                periodRecordEntity.setTaskId(taskId);
                periodRecordEntity.setDeviceId(valueEntity.getId());
                periodRecordEntity.setRecordBatch(recordBatch);
                periodRecordEntity.setObjectName(objectName);
                periodRecordEntity.setObjectValue(JsonUtils.buildJson(value));
                periodRecordEntity.setCreateTime(time);

                periodRecordEntityList.add(periodRecordEntity);
            }

        } catch (Exception e) {
            logger.info(e);
        }
    }


    /**
     * 分批插入数据
     *
     * @param entityList
     */
    private void insert(List<PeriodRecordEntity> entityList) {
        int pageSize = 100;
        List<PeriodRecordEntity> pageList = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i++) {
            pageList.add(entityList.get(i));

            boolean insert = pageList.size() == pageSize;
            if ((entityList.size() / pageSize == i / pageSize) && (entityList.size() % pageSize - 1 == i % pageSize)) {
                insert = true;
            }
            if (insert) {
                StringBuilder sb = new StringBuilder();
                sb.append("INSERT INTO `tb_period_record` (`task_id`, `record_batch`, `device_id`, `object_name`, `object_value`, `create_time`) VALUES ");

                for (int pageId = 0; pageId < pageList.size(); pageId++) {
                    PeriodRecordEntity entity = pageList.get(pageId);
                    sb.append(" (" + entity.getTaskId() + ", ");
                    sb.append(" '" + entity.getRecordBatch() + "', ");
                    sb.append(" " + entity.getDeviceId() + ", ");
                    sb.append(" '" + this.replace(entity.getObjectName()) + "', ");
                    sb.append(" '" + this.replace(entity.getObjectValue()) + "', ");
                    if (pageId < pageList.size() - 1) {
                        sb.append(" " + entity.getCreateTime() + "),");
                    } else {
                        sb.append(" " + entity.getCreateTime() + ")");
                    }
                }

                this.mapper.executeInsert(sb.toString());

                pageList.clear();
            }
        }
    }

    /**
     * 防止注入攻击：\和'，这两个字符要用\\ 和 \'替换
     *
     * @param value
     * @return
     */
    private Object replace(Object value) {
        if (value instanceof String) {
            value = ((String) value).replace("\\", "\\\\");
            value = ((String) value).replace("'", "\\'");
        }
        return value;
    }

    /**
     * 删除旧的记录
     *
     * @param taskId   任务ID
     * @param maxBatch
     */
    private void deleteOverload(Long taskId, int maxBatch) {
        Integer sumCount = this.mapper.executeSelectCount("SELECT COUNT(1) FROM  tb_period_record WHERE task_id = " + taskId);
        if (sumCount <= maxBatch) {
            return;
        }

        // 删除旧记录
        String sql = String.format("DELETE FROM  tb_period_record t WHERE task_id = %d order BY t.id LIMIT  %d", taskId, sumCount - maxBatch);
        this.mapper.executeDelete(sql);
    }

    private void deleteInvalid() {
        if (System.currentTimeMillis() - this.lastDeleteInvalid < 10 * 60 * 1000) {
            return;
        }

        this.lastDeleteInvalid = System.currentTimeMillis();

        List<PeriodRecordEntity> entityList = mapper.executeSelectData("SELECT DISTINCT t.task_id  FROM tb_period_record t");
        for (PeriodRecordEntity entity : entityList) {
            if (!this.entityManageService.hasEntity(entity.getTaskId(), PeriodTaskEntity.class)) {
                this.mapper.executeDelete("DELETE FROM tb_period_record t WHERE t.task_id = " + entity.getTaskId());
            }
        }
    }
}
