package cn.foxtech.controller.service.service;

import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.rpc.redis.device.client.RedisListDeviceClient;
import cn.foxtech.common.rpc.redis.persist.client.RedisListPersistClient;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.controller.common.service.EntityManageService;
import cn.foxtech.device.domain.vo.OperateRespondVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 订阅设备的数据采集：发送给持久化服务的，是走高可靠的队列fox.edge.record.persist.record
 * <p>
 * 背景：某些设备会主动发布数据给服务器，比如某些短信设备，MQTT设备，它们自己状态变化的时候，会发布数据给订阅者<br>
 */
@Component
public class CollectorSubscribeService extends PeriodTaskService {
    private static final Logger logger = Logger.getLogger(CollectorSubscribeService.class);
    @Autowired
    EntityManageService entityManageService;

    @Autowired
    private RedisListPersistClient persistClient;

    @Autowired
    private RedisListDeviceClient deviceClient;

    public void execute(long threadId) throws Exception {
        // 检查：是否装载完毕
        if (!this.entityManageService.isInitialized()) {
            Thread.sleep(1000);
            return;
        }

        // 从redis中弹出一个到达的消息
        TaskRespondVO respondVO = this.deviceClient.popDeviceReport(1, TimeUnit.SECONDS);
        if (respondVO == null) {
            return;
        }

        // 讲上报数据中的操作对象，逐个推送到redis
        for (OperateRespondVO operateRespondVO : respondVO.getRespondVOS()) {
            String operateMode = operateRespondVO.getOperateMode();
            if (operateMode == null) {
                continue;
            }

            this.updateDeviceReport(operateRespondVO);
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

            // 记录数据，走高可靠队列，发送给持久化服务，要求存储数据库记录
            this.persistClient.pushRecordRequest(taskRespondVO);
        } catch (Exception e) {
            logger.warn(e);
        }
    }
}
