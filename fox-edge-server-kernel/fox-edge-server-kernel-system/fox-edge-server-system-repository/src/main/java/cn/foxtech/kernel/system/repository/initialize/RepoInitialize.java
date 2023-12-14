package cn.foxtech.kernel.system.repository.initialize;


import cn.foxtech.common.domain.constant.ServiceVOFieldConstant;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.kernel.system.common.scheduler.PeriodTasksScheduler;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import cn.foxtech.kernel.system.repository.service.*;
import cn.foxtech.kernel.system.repository.task.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 初始化
 */
@Component
public class RepoInitialize {
    @Autowired
    private RedisConsoleService logger;

    @Autowired
    private JarMethodTask jarMethodTask;

    @Autowired
    private ProcessStartTask processStartTask;

    @Autowired
    private ProcessGcTask processGcTask;

    @Autowired
    private RepoCloudInstallService installService;

    @Autowired
    private RepoCloudInstallStatus installStatus;

    @Autowired
    private RepoLocalPathNameService pathNameService;

    @Autowired
    private PeriodTasksScheduler periodTasksScheduler;

    @Autowired
    private EngineParamService engineParamService;

    @Autowired
    private RepoLocalApplicationService appServerService;

    @Autowired
    private RepoLocalJarFileCompScanner compScanner;


    public void initialize() {
        this.engineParamService.initialize();

        // 周期性任务
        this.periodTasksScheduler.insertPeriodTask(this.processGcTask);
        this.periodTasksScheduler.insertPeriodTask(this.jarMethodTask);
        this.periodTasksScheduler.insertPeriodTask(this.processStartTask);

        // 一次性任务
        this.periodTasksScheduler.insertPeriodTask(new RepoScanStatusTask(this.installService, this.installStatus, this.pathNameService, this.logger, RepoCompConstant.repository_type_decoder));
        this.periodTasksScheduler.insertPeriodTask(new RepoScanStatusTask(this.installService, this.installStatus, this.pathNameService, this.logger, RepoCompConstant.repository_type_template));
        this.periodTasksScheduler.insertPeriodTask(new RepoScanStatusTask(this.installService, this.installStatus, this.pathNameService, this.logger, RepoCompConstant.repository_type_webpack));
        this.periodTasksScheduler.insertPeriodTask(new RepoScanStatusTask(this.installService, this.installStatus, this.pathNameService, this.logger, RepoCompConstant.repository_type_service));

        // 一次性任务
        this.periodTasksScheduler.insertPeriodTask(new RepoAppScanTask(this.appServerService, ServiceVOFieldConstant.field_type_kernel));
        this.periodTasksScheduler.insertPeriodTask(new RepoAppScanTask(this.appServerService, ServiceVOFieldConstant.field_type_system));
        this.periodTasksScheduler.insertPeriodTask(new RepoAppScanTask(this.appServerService, ServiceVOFieldConstant.field_type_service));


        // 一次性任务
        this.periodTasksScheduler.insertPeriodTask(new JarFileScanTask(this.compScanner, this.logger));
    }


}
