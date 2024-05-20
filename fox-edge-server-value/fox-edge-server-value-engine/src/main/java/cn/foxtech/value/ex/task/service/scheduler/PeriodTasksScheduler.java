package cn.foxtech.value.ex.task.service.scheduler;


import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskScheduler;
import cn.foxtech.value.ex.task.service.task.DeviceValueNotifyTask;
import cn.foxtech.value.ex.task.service.task.TaskEngineReloadTask;
import cn.foxtech.value.ex.task.service.task.TaskManageReloadTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 后台定时任务调度器：一个线程调度多个任务，所以后台任务不要阻塞，也不能去响应很及时的任务
 */
@Component
public class PeriodTasksScheduler extends PeriodTaskScheduler {
    @Autowired
    private DeviceValueNotifyTask valueNotifyTask;

    @Autowired
    private TaskEngineReloadTask engineReloadTask;

    @Autowired
    private TaskManageReloadTask manageReloadTask;



    public void initialize() {
        this.insertPeriodTask(this.manageReloadTask);
        this.insertPeriodTask(this.engineReloadTask);
        this.insertPeriodTask(this.valueNotifyTask);
    }
}