package cn.foxtech.kernel.system.common.scheduler;

import cn.foxtech.common.domain.vo.RestFulRespondVO;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.common.redislist.RedisListPersistManageRespond;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersistRespondScheduler  extends PeriodTaskService {
    private static final Logger logger = Logger.getLogger(PersistRespondScheduler.class);

    @Autowired
    private RedisListPersistManageRespond valueService;

    @Autowired
    private RedisConsoleService console;


    @Override
    public void execute(long threadId) throws Exception {

        // 预览消息队列
        List<Object> respondVOList = this.valueService.range();

        // 读取到的数据量
        int size = respondVOList.size();

        // 处理并删除这个数据
        for (Object respondMap : respondVOList) {
            // 处理数据
            this.updateDeviceRespond(respondMap);

            // 删除这个对象
            this.valueService.pop();
        }

        // 如果没有数据到达，那么休眠1000毫秒
        if (size <= 0) {
            Thread.sleep(1000);
        }
    }

    private void updateDeviceRespond(Object respondMap) {
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

    public void receiveTopic4th(String message) {
        try {
            RestFulRespondVO respondVO = JsonUtils.buildObject(message, RestFulRespondVO.class);
        //    this.redisTopicService.respondPersist(respondVO);
        } catch (Exception e) {
            logger.warn(e);
        }
    }

    public void respondPersist(RestFulRespondVO respondVO) throws ServiceException {
        try {
            if (!MethodUtils.hasEmpty(respondVO.getUuid())) {
                SyncFlagObjectMap.inst().notifyDynamic(respondVO.getUuid(), respondVO);
            }
        } catch (Exception e) {
            logger.warn(e.toString());
        }
    }
}