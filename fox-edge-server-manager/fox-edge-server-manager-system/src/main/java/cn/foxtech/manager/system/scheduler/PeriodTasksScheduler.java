package cn.foxtech.manager.system.scheduler;


import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskScheduler;
import cn.foxtech.manager.system.constants.RepoComponentConstant;
import cn.foxtech.manager.system.service.RepoComponentService;
import cn.foxtech.manager.system.task.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 后台定时任务调度器：一个线程调度多个任务，所以后台任务不要阻塞，也不能去响应很及时的任务
 */
@Component
public class PeriodTasksScheduler extends PeriodTaskScheduler {

    /**
     * 定时对进程GC定时任务
     */
    @Autowired
    private GcProcessTask gcProcessTask;

    /**
     * 定时清理操作系统的Cache
     */
    @Autowired
    private CleanCacheTask cleanCacheTask;

    @Autowired
    private ConfigEntityTask configEntityTask;

    /**
     * 删除失效链路任务
     */
    @Autowired
    private CleanLogFileTask cleanLogFileTask;

    @Autowired
    private ProcessStartTask processStartTask;

    @Autowired
    private RepoComponentService repositoryComponentService;

    @Autowired
    private RouteUpdateTask routeUpdateTask;

    public void initialize() {
        this.insertPeriodTask(this.gcProcessTask);
        this.insertPeriodTask(this.cleanCacheTask);
        this.insertPeriodTask(this.cleanLogFileTask);
        this.insertPeriodTask(this.routeUpdateTask);
        this.insertPeriodTask(this.configEntityTask);

        // 一次性任务
        this.insertPeriodTask(this.processStartTask);
        this.insertPeriodTask(new RepoStatusTask(this.repositoryComponentService, RepoComponentConstant.repository_type_decoder));
        this.insertPeriodTask(new RepoStatusTask(this.repositoryComponentService, RepoComponentConstant.repository_type_template));
        this.insertPeriodTask(new RepoStatusTask(this.repositoryComponentService, RepoComponentConstant.repository_type_webpack));
        this.insertPeriodTask(new RepoStatusTask(this.repositoryComponentService, RepoComponentConstant.repository_type_service));

    }
}
