package cn.foxtech.controller.service.scheduler;

import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import cn.foxtech.controller.common.redislist.RedisListDeviceModelRespond;
import cn.foxtech.device.domain.constant.DeviceMethodVOFieldConstant;
import cn.foxtech.device.domain.vo.OperateRespondVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 记录队列：从topic改为采用list方式，是为了让记录数据更可靠
 */
@Component
public class RedisListRespondScheduler extends PeriodTaskService {
    private static final Logger logger = Logger.getLogger(RedisListRespondScheduler.class);
    @Autowired
    private RedisListDeviceModelRespond redisListDeviceModelRespond;
    @Autowired
    private RedisConsoleService console;

    public void initialize() {
        this.redisListDeviceModelRespond.setKey("fox.edge.list.device.system_controller.respond");
    }

    @Override
    public void execute(long threadId) throws Exception {
        int size = 0;

        size += this.receiveDevicePublic();

        // 如果没有数据到达，那么休眠200毫秒
        if (size <= 0) {
            Thread.sleep(200);
        }
    }

    private int receiveDevicePublic() {
        // 预览消息队列
        List<Object> respondVOList = this.redisListDeviceModelRespond.range();

        // 读取到的数据量
        int size = respondVOList.size();

        // 处理并删除这个数据
        for (Object respondMap : respondVOList) {
            // 处理数据
            this.receiveDevicePublic(respondMap);

            // 删除这个对象
            this.redisListDeviceModelRespond.pop();
        }

        return size;
    }


    private void receiveDevicePublic(Object taskRespondMap) {
        try {
            TaskRespondVO taskRespondVO = TaskRespondVO.buildRespondVO((Map<String, Object>) taskRespondMap);

            String key = taskRespondVO.getUuid();
            if (key == null || key.isEmpty()) {
                this.receiveDeviceReport(taskRespondVO);
            } else {
                this.receiveDeviceExchange(taskRespondVO);
            }
        } catch (Exception e) {
            String message = "接收到的报文格式不正确，它不是一个合法的包裹：" + e.getMessage();
            logger.error(message);
            console.error(message);
        }
    }

    private void receiveDeviceExchange(TaskRespondVO taskRespondVO) {
        SyncFlagObjectMap.inst().notifyDynamic(taskRespondVO.getUuid(), taskRespondVO);
    }

    public void receiveDeviceReport(TaskRespondVO respondVO) {
        for (OperateRespondVO operateRespondVO : respondVO.getRespondVOS()) {
            String operateMode = operateRespondVO.getOperateMode();
            if (operateMode == null) {
                continue;
            }

            // 捕获的是设备主动上报
            if (DeviceMethodVOFieldConstant.value_operate_report.equals(operateMode)) {
                SyncQueueObjectMap.inst().push(DeviceMethodVOFieldConstant.value_operate_report, operateRespondVO, 1000);
                continue;
            }

            // 捕获的是操作记录
            if (DeviceMethodVOFieldConstant.value_operate_exchange.equals(operateMode)) {
                SyncQueueObjectMap.inst().push(DeviceMethodVOFieldConstant.value_operate_exchange, operateRespondVO, 1000);
                continue;
            }
        }
    }

}