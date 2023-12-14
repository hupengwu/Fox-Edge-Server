package cn.foxtech.kernel.system.repository.task;

import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTask;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskType;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import cn.foxtech.kernel.system.repository.service.RepoCloudInstallService;
import cn.foxtech.kernel.system.repository.service.RepoCloudInstallStatus;
import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 异步下载仓库安装包任务
 */

@Setter(value = AccessLevel.PUBLIC)
public class RepoDownLoadTask extends PeriodTask {
    @Autowired
    private RedisConsoleService logger;

    private String modelType;
    private String modelName;
    private String modelVersion;
    private String version;
    private String stage;
    private String pathName;
    private String component;

    private RepoCloudInstallService installService;

    private RepoCloudInstallStatus installStatus;

    public RepoDownLoadTask(RepoCloudInstallService installService, RepoCloudInstallStatus installStatus,String modelType, String modelName, String modelVersion, String version, String stage, String pathName, String component) {
        this.installService = installService;
        this.installStatus = installStatus;
        this.modelType = modelType;
        this.modelName = modelName;
        this.modelVersion = modelVersion;
        this.version = version;
        this.stage = stage;
        this.pathName = pathName;
        this.component = component;
    }

    /**
     * 重载：指明是一次性的排队任务
     *
     * @return 一次性排队任务
     */
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
            // 简单验证
            if (MethodUtils.hasEmpty(modelType, modelName, modelVersion, version, stage, pathName, component)) {
                throw new ServiceException("参数不能为空: modelType, modelName, modelVersion, version, stage, pathName, component");
            }

            // 删除旧的下载文件
            if (RepoCompConstant.repository_type_service.equals(modelType) || RepoCompConstant.repository_type_decoder.equals(modelType) || RepoCompConstant.repository_type_webpack.equals(modelType) || RepoCompConstant.repository_type_template.equals(modelType)) {
                this.installService.deletePackageFile(modelType, modelName, modelVersion, version, stage, component);
            }

            // 重新下载新的安装文件
            this.installService.downloadFile(modelType, modelName, modelVersion, version, stage, pathName, component);

            // 下载完成后，验证状态
            this.installStatus.scanLocalStatus(modelType, modelName, modelVersion, version, stage, component);
            this.installStatus.scanLocalMd5(modelType, modelName, modelVersion, version, stage, component);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
