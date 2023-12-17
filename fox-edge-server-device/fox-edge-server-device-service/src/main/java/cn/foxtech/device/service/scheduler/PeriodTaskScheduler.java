package cn.foxtech.device.service.scheduler;

import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import cn.foxtech.device.domain.vo.TaskRequestVO;
import cn.foxtech.device.service.redislist.RedisListDevicePublicRequest;
import cn.foxtech.device.service.service.EntityManageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 记录队列：从topic改为采用list方式，是为了让记录数据更可靠
 */
@Component
public class PeriodTaskScheduler extends PeriodTaskService {
    private static final Logger logger = Logger.getLogger(PeriodTaskScheduler.class);

    @Autowired
    private EntityManageService entityManageService;


    @Autowired
    private RedisListDevicePublicRequest redisListDevicePublicRequest;


    @Autowired
    private RedisConsoleService console;

    @Override
    public void execute(long threadId) throws Exception {
        // 检查：是否装载完毕
        if (!this.entityManageService.isInitialized()) {
            Thread.sleep(1000);
            return;
        }


        int size = 0;


        size += this.receiveDevicePublic();

        // 如果没有数据到达，那么休眠200毫秒
        if (size <= 0) {
            Thread.sleep(200);
        }
    }

    private int receiveDevicePublic() {
        // 预览消息队列
        List<Object> respondVOList = this.redisListDevicePublicRequest.range();

        // 读取到的数据量
        int size = respondVOList.size();

        // 处理并删除这个数据
        for (Object respondMap : respondVOList) {
            // 处理数据
            this.receiveDevicePublic(respondMap);

            // 删除这个对象
            this.redisListDevicePublicRequest.pop();
        }

        return size;
    }


    private void receiveDevicePublic(Object taskRequestMap) {
        try {
            TaskRequestVO taskRequestVO = TaskRequestVO.buildRequestVO((Map<String, Object>) taskRequestMap);

            SyncQueueObjectMap.inst().push(RedisTopicConstant.model_device, taskRequestVO, 1000);
        } catch (Exception e) {
            String message = "接收到的报文格式不正确，它不是一个合法的包裹：" + e.getMessage();
            logger.error(message);
            console.error(message);
        }
    }


}