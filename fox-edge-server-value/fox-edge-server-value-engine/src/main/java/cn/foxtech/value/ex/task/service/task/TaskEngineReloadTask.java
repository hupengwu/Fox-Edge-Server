package cn.foxtech.value.ex.task.service.task;

import cn.foxtech.common.utils.scheduler.multitask.PeriodTask;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskType;
import cn.foxtech.value.ex.task.service.service.TaskEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 周期性GC任务
 */
@Component
public class TaskEngineReloadTask extends PeriodTask {
    @Autowired
    private TaskEngineService taskEngineService;


    @Override
    public int getTaskType() {
        return PeriodTaskType.task_type_share;
    }

    /**
     * 获得调度周期
     *
     * @return 调度周期，单位秒
     */
    public int getSchedulePeriod() {
        return 1;
    }

    /**
     * 待周期性执行的操作
     */
    @Override
    public void execute() {
        if (this.taskEngineService.isNeedReset()) {
            this.taskEngineService.reset();
        }
    }
}
