package cn.foxtech.iot.common.scheduler;


import cn.foxtech.iot.common.service.EntityManageService;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 实体管理器的定时同步数据
 */
@Component
public class EntityManageScheduler extends PeriodTaskService {

    @Autowired
    private EntityManageService entityManageService;


    @Override
    public void execute(long threadId) throws Exception {
        Thread.sleep(1000);

        // 从redis和cache之间互相同步
        this.entityManageService.syncEntity();
    }

}
