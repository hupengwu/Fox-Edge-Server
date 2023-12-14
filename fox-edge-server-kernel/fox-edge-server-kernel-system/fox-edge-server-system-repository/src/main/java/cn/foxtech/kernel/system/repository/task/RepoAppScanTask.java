package cn.foxtech.kernel.system.repository.task;

import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTask;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskType;
import cn.foxtech.kernel.system.repository.service.RepoLocalApplicationService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 启动其他进程的一次性任务
 */
public class RepoAppScanTask extends PeriodTask {
    private final RepoLocalApplicationService service;

    private final String appType;
    @Autowired
    private RedisConsoleService logger;


    public RepoAppScanTask(RepoLocalApplicationService service, String appType) {
        this.service = service;
        this.appType = appType;
    }

    @Override
    public int getTaskType() {
        return PeriodTaskType.task_type_once;
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
        try {
            this.service.syncRepoCompEntity4Application(appType);
        } catch (Exception e) {
            this.logger.error("同步AppService到组件表出错:" + e.getMessage());
        }
    }
}
