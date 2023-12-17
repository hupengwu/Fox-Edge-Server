package cn.foxtech.kernel.system.common.scheduler;

import cn.foxtech.common.domain.vo.RestFulRespondVO;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import cn.foxtech.kernel.system.common.redislist.RedisListDeviceManageRespond;
import cn.foxtech.kernel.system.common.redislist.RedisListPersistManageRespond;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class RedisListRespondScheduler extends PeriodTaskService {
    private static final Logger logger = Logger.getLogger(RedisListRespondScheduler.class);

    @Autowired
    private RedisListPersistManageRespond listPersistManageRespond;

    @Autowired
    private RedisListDeviceManageRespond listDeviceManageRespond;

    @Autowired
    private RedisConsoleService console;


    @Override
    public void execute(long threadId) throws Exception {

        int size = 0;

        size += this.respondPersistManage();

        size += this.respondDeviceManage();


        // 如果没有数据到达，那么休眠1000毫秒
        if (size <= 0) {
            Thread.sleep(1000);
        }
    }

    private int respondPersistManage() {
        // 预览消息队列
        List<Object> respondVOList = this.listPersistManageRespond.range();

        // 读取到的数据量
        int size = respondVOList.size();

        // 处理并删除这个数据
        for (Object respondMap : respondVOList) {
            // 处理数据
            this.respondPersistManage(respondMap);

            // 删除这个对象
            this.listPersistManageRespond.pop();
        }

        return size;
    }

    private int respondDeviceManage() {
        // 预览消息队列
        List<Object> respondVOList = this.listDeviceManageRespond.range();

        // 读取到的数据量
        int size = respondVOList.size();

        // 处理并删除这个数据
        for (Object respondMap : respondVOList) {
            // 处理数据
            this.receiveDevicePublic(respondMap);

            // 删除这个对象
            this.listDeviceManageRespond.pop();
        }

        return size;
    }

    private void respondPersistManage(Object respondMap) {
        try {
            RestFulRespondVO respondVO = JsonUtils.buildObject(respondMap, RestFulRespondVO.class);
            if (!MethodUtils.hasEmpty(respondVO.getUuid())) {
                SyncFlagObjectMap.inst().notifyDynamic(respondVO.getUuid(), respondVO);
            }
        } catch (Exception e) {
            String message = "更新设备数据，发生异常：" + e.getMessage();
            logger.error(message);
            console.error(message);
        }
    }

    private void receiveDevicePublic(Object taskRespondMap) {
        try {
            TaskRespondVO taskRespondVO = TaskRespondVO.buildRespondVO((Map<String, Object>) taskRespondMap);

            SyncFlagObjectMap.inst().notifyDynamic(taskRespondVO.getUuid(), taskRespondVO);
        } catch (Exception e) {
            String message = "接收到的报文格式不正确，它不是一个合法的包裹：" + e.getMessage();
            logger.error(message);
            console.error(message);
        }
    }
}