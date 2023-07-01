package cn.foxtech.controller.service.service;

import cn.foxtech.controller.common.redistopic.RedisTopicPuberService;
import cn.foxtech.controller.common.service.EntityManageService;
import cn.foxtech.device.domain.vo.OperateRespondVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import cn.foxtech.device.domain.constant.DeviceMethodVOFieldConstant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 订阅设备的数据采集<br>
 * 背景：某些设备会主动发布数据给服务器，比如某些短信设备，MQTT设备，它们自己状态变化的时候，会发布数据给订阅者<br>
 */
@Component
public class CollectorSubscribeService extends PeriodTaskService {
    private static final Logger logger = Logger.getLogger(CollectorSubscribeService.class);
    @Autowired
    EntityManageService entityManageService;
    @Autowired
    private RedisTopicPuberService puberService;

    public void execute(long threadId) throws Exception {
        // 检查：是否装载完毕
        if (!this.entityManageService.isInitialized()) {
            Thread.sleep(1000);
            return;
        }

        List<Object> respondVOList;

        // 设备主动上报的记录
        respondVOList = SyncQueueObjectMap.inst().popup(DeviceMethodVOFieldConstant.value_operate_report, false, 1000);
        for (Object respondVO : respondVOList) {
            this.updateDeviceReport((OperateRespondVO) respondVO);
        }

        // 用户操作的记录
        respondVOList = SyncQueueObjectMap.inst().popup(DeviceMethodVOFieldConstant.value_operate_exchange, false, 1000);
        for (Object respondVO : respondVOList) {
            this.updateDeviceReport((OperateRespondVO) respondVO);
        }
    }

    /**
     * 设备主动上报的记录
     *
     * @param operateRespondVO 上报的操作
     */
    private void updateDeviceReport(OperateRespondVO operateRespondVO) {
        try {
            String deviceName = operateRespondVO.getDeviceName();

            DeviceEntity deviceEntity = this.entityManageService.getDeviceEntity(deviceName);
            if (deviceEntity == null) {
                return;
            }

            // 打包成为单步操作
            TaskRespondVO taskRespondVO = TaskRespondVO.buildRespondVO(operateRespondVO, null);

            // 更新设备消息到数据库和redis
            this.puberService.sendRespondVO(taskRespondVO);
        } catch (Exception e) {
            logger.warn(e);
        }
    }
}
