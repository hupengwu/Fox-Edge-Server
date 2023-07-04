package cn.foxtech.persist.common.service.updater;

import cn.foxtech.common.entity.constant.DeviceStatusVOFieldConstant;
import cn.foxtech.device.domain.vo.OperateRespondVO;
import cn.foxtech.persist.common.service.EntityManageService;
import cn.foxtech.common.entity.entity.DeviceStatusEntity;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DeviceCommStatusUpdater {
    private static final Logger logger = Logger.getLogger(DeviceCommStatusUpdater.class);

    /**
     * 实体管理
     */
    @Autowired
    private EntityManageService entityManageService;

    /**
     * 根据通信状态数据到redis
     */
    public void updateStatusEntity(Long deviceId, Map<String, Object> status) {
        try {
            if (status == null) {
                return;
            }

            DeviceStatusEntity statusEntity = new DeviceStatusEntity();
            statusEntity.setId(deviceId);

            long commFailedTime = Long.parseLong(status.get(OperateRespondVO.data_comm_status_failed_time).toString());
            long commSuccessTime = Long.parseLong(status.get(OperateRespondVO.data_comm_status_success_time).toString());

            Long time = System.currentTimeMillis();

            Map<String, Object> existEntity = this.entityManageService.readHashMap(statusEntity.makeServiceKey(), DeviceStatusEntity.class);
            if (existEntity == null) {
                int commFailedCount = 0;
                if (commFailedTime > 0) {
                    commFailedCount++;
                }

                statusEntity.setCreateTime(time);
                statusEntity.setUpdateTime(time);

                // 插入对象
                statusEntity.setCommFailedCount(commFailedCount);
                statusEntity.setCommFailedTime(commFailedTime);
                statusEntity.setCommSuccessTime(commSuccessTime);
                this.entityManageService.writeEntity(statusEntity);

            } else {
                int commFailedCount = Integer.parseInt(existEntity.getOrDefault(DeviceStatusVOFieldConstant.field_failed_count,0).toString());
                if (commFailedTime > 0) {
                    commFailedCount++;
                } else {
                    commFailedCount = 0;
                }

                statusEntity.setUpdateTime(time);

                // 更新对象
                statusEntity.setCommFailedCount(commFailedCount);
                statusEntity.setCommFailedTime(commFailedTime);
                statusEntity.setCommSuccessTime(commSuccessTime);
                this.entityManageService.writeEntity(statusEntity);
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
