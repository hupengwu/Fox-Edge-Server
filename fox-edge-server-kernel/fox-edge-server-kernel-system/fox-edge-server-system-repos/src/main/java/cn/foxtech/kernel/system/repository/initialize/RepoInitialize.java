package cn.foxtech.kernel.system.repository.initialize;


import cn.foxtech.common.domain.constant.ServiceVOFieldConstant;
import cn.foxtech.common.entity.manager.InitialConfigService;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.kernel.common.service.EdgeService;
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
    private RepoCloudFIleInstallService installService;

    @Autowired
    private RepoCloudFileInstallStatus installStatus;

    @Autowired
    private RepoCloudCacheService cacheService;

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

    @Autowired
    private EdgeService edgeService;


    /**
     * 初始化配置：需要感知运行期的用户动态输入的配置，所以直接使用这个组件
     */
    @Autowired
    private InitialConfigService configService;


    public void initialize() {
        this.engineParamService.initialize();

        this.configService.initialize("repositoryConfig", "repositoryConfig.json");
        this.configService.initialize("systemProcessConfig", "systemProcessConfig.json");

        // 创建周期性任务
        this.createPeriodTask();
    }

    /**
     * 下列周期性任务，都涉及到本地文件操作，命令行操作，所以只能工作在device模式下，不允许工作在docker模式下
     */
    private void createPeriodTask() {
        // 检查：是否工作在docker模式中，只有在非docker模式下，才能进行后续的任务操作
        if (this.edgeService.isDockerEnv()) {
            return;
        }

        // 周期性任务
        this.periodTasksScheduler.insertPeriodTask(this.processGcTask);
        this.periodTasksScheduler.insertPeriodTask(this.jarMethodTask);
        this.periodTasksScheduler.insertPeriodTask(this.processStartTask);

        // 一次性任务
        this.periodTasksScheduler.insertPeriodTask(new RepoScanStatusTask(this.installService, this.installStatus, this.cacheService, this.pathNameService, this.logger, RepoCompConstant.repository_type_decoder));
        this.periodTasksScheduler.insertPeriodTask(new RepoScanStatusTask(this.installService, this.installStatus, this.cacheService, this.pathNameService, this.logger, RepoCompConstant.repository_type_template));
        this.periodTasksScheduler.insertPeriodTask(new RepoScanStatusTask(this.installService, this.installStatus, this.cacheService, this.pathNameService, this.logger, RepoCompConstant.repository_type_webpack));
        this.periodTasksScheduler.insertPeriodTask(new RepoScanStatusTask(this.installService, this.installStatus, this.cacheService, this.pathNameService, this.logger, RepoCompConstant.repository_type_service));

        // 一次性任务
        this.periodTasksScheduler.insertPeriodTask(new RepoAppScanTask(this.appServerService, ServiceVOFieldConstant.field_type_kernel));
        this.periodTasksScheduler.insertPeriodTask(new RepoAppScanTask(this.appServerService, ServiceVOFieldConstant.field_type_system));
        this.periodTasksScheduler.insertPeriodTask(new RepoAppScanTask(this.appServerService, ServiceVOFieldConstant.field_type_service));

        // 一次性任务
        this.periodTasksScheduler.insertPeriodTask(new JarFileScanTask(this.compScanner, this.logger));
    }
}
