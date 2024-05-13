package cn.foxtech.kernel.system.repository.task;

import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTask;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskType;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import cn.foxtech.kernel.system.repository.service.RepoCloudCacheService;
import cn.foxtech.kernel.system.repository.service.RepoCloudInstallService;
import cn.foxtech.kernel.system.repository.service.RepoCloudInstallStatus;
import cn.foxtech.kernel.system.repository.service.RepoLocalPathNameService;

import java.util.*;

/**
 * 启动其他进程的一次性任务
 */
public class RepoScanStatusTask extends PeriodTask {
    private final RepoCloudInstallService installService;

    private final RepoCloudInstallStatus installStatus;

    private final RepoLocalPathNameService pathNameService;
    private final String modelType;
    private final RepoCloudCacheService cacheService;
    private final RedisConsoleService logger;


    public RepoScanStatusTask(RepoCloudInstallService installService, RepoCloudInstallStatus installStatus, RepoCloudCacheService cacheService, RepoLocalPathNameService pathNameService, RedisConsoleService logger, String modelType) {
        this.installService = installService;
        this.installStatus = installStatus;
        this.cacheService = cacheService;
        this.pathNameService = pathNameService;
        this.modelType = modelType;
        this.logger = logger;
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
        // 删除垃圾文件
        if (RepoCompConstant.repository_type_service.equals(this.modelType)) {
            this.deleteJunkFiles(this.modelType);
        }

        // 重新扫描状态
        this.installStatus.scanRepositoryStatus(this.modelType);
    }

    private void deleteJunkFiles(String modelType) {
        try {
            // 名单列表
            List<Map<String, Object>> localList = this.cacheService.queryLocalListFile(modelType);
            Set<String> tables = new HashSet<>();
            for (Map<String, Object> map : localList) {
                String modelName = (String) map.getOrDefault(RepoCompConstant.filed_model_name, "");
                String modelVersion = (String) map.getOrDefault(RepoCompConstant.filed_model_version, RepoCompConstant.filed_value_model_version_default);
                String component = (String) map.getOrDefault(RepoCompConstant.filed_component, "");
                List<Map<String, Object>> versions = (List<Map<String, Object>>) map.getOrDefault(RepoCompConstant.filed_versions, new ArrayList<>());

                for (Map<String, Object> entity : versions) {
                    String version = (String) entity.get(RepoCompConstant.filed_version);
                    String stage = (String) entity.get(RepoCompConstant.filed_stage);
                    if (MethodUtils.hasEmpty(version, stage)) {
                        continue;
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append(modelName);
                    sb.append(modelVersion);
                    sb.append(component);
                    sb.append(version);
                    sb.append(stage);

                    tables.add(sb.toString());
                }

            }

            // 文件列表
            List<Map<String, Object>> modelList = this.pathNameService.findRepoLocalModel(modelType);
            for (Map<String, Object> map : modelList) {
                String modelName = (String) map.getOrDefault(RepoCompConstant.filed_model_name, "");
                String modelVersion = (String) map.getOrDefault(RepoCompConstant.filed_model_version, RepoCompConstant.filed_value_model_version_default);
                String component = (String) map.getOrDefault(RepoCompConstant.filed_component, "");
                String version = (String) map.getOrDefault(RepoCompConstant.filed_version, "");
                String stage = (String) map.getOrDefault(RepoCompConstant.filed_stage, "");

                StringBuilder sb = new StringBuilder();
                sb.append(modelName);
                sb.append(modelVersion);
                sb.append(component);
                sb.append(version);
                sb.append(stage);

                // 根据本地文件是否存在与远端再本地的列表中，判定是否为垃圾文件
                if (tables.contains(sb.toString())) {
                    continue;
                }

                // 删除本地垃圾文件
                this.installService.deletePackageFile(modelType, modelName, modelVersion, version, stage, component);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
