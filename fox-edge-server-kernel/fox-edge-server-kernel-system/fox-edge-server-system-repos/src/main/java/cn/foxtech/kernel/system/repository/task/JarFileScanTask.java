package cn.foxtech.kernel.system.repository.task;

import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTask;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskType;
import cn.foxtech.kernel.system.repository.service.RepoLocalJarFileCompScanner;

import java.util.Map;

public class JarFileScanTask extends PeriodTask {
    private final RedisConsoleService logger;

    private final RepoLocalJarFileCompScanner compScanner;

    public JarFileScanTask(RepoLocalJarFileCompScanner compScanner, RedisConsoleService logger) {
        this.logger = logger;
        this.compScanner = compScanner;
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
            Map<String, RepoCompEntity> fileNameMap = this.compScanner.scanRepoCompEntity();
            this.compScanner.scanRepoCompEntity(fileNameMap);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}