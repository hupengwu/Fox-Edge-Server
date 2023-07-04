package cn.foxtech.persist.service.controller;

import cn.foxtech.device.domain.vo.OperateRespondVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import cn.foxtech.persist.common.service.EntityManageService;
import cn.foxtech.persist.common.service.EntityUpdateService;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import cn.foxtech.device.domain.constant.DeviceMethodVOFieldConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 订阅设备的数据采集<br>
 * 背景：某些设备会主动发布数据给服务器，比如某些短信设备，MQTT设备，它们自己状态变化的时候，会发布数据给订阅者<br>
 */
@Component
public class PersistController extends PeriodTaskService {
    @Autowired
    EntityUpdateService entityUpdateService;

    @Autowired
    EntityManageService entityManageService;

    public void execute(long threadId) throws Exception {
        // 检查：是否装载完毕
        if (!this.entityManageService.isInitialized()) {
            Thread.sleep(1000);
            return;
        }

        // 设备主动上报的记录
        List<Object> respondVOList = SyncQueueObjectMap.inst().popup(DeviceMethodVOFieldConstant.value_operate_report, false, 1000);
        for (Object respondVO : respondVOList) {
            TaskRespondVO taskRespondVO = (TaskRespondVO) respondVO;
            for (OperateRespondVO operateRespondVO : taskRespondVO.getRespondVOS()) {
                this.entityUpdateService.updateDeviceRespond(operateRespondVO, taskRespondVO.getClientName());
            }
        }
    }
}
