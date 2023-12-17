package cn.foxtech.kernel.system.common.scheduler;


import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.ConfigEntity;
import cn.foxtech.common.entity.entity.DeviceTimeOutEntity;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.kernel.system.common.service.DeviceTimeOutService;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
