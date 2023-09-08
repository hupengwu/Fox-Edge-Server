package cn.foxtech.manager.system.task;

import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTask;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskType;
import cn.foxtech.manager.system.constants.RepositoryConstant;
import cn.foxtech.manager.system.service.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/**
 * 启动其他进程的一次性任务
 */
public class RepoStatusTask extends PeriodTask {
    private final RepositoryService service;
    private final String modelType;
    @Autowired
    private RedisConsoleService logger;


    public RepoStatusTask(RepositoryService service, String modelType) {
        this.service = service;
        this.modelType = modelType;
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
        if (RepositoryConstant.repository_type_service.equals(this.modelType)) {
            this.deleteJunkFiles(this.modelType);
        }

        // 重新扫描状态
        this.service.scanRepositoryStatus(this.modelType);
    }

    private void deleteJunkFiles(String modelType) {
        try {
            // 名单列表
            List<Map<String, Object>> localList = this.service.queryLocalListFile(modelType);
            Set<String> tables = new HashSet<>();
            for (Map<String, Object> map : localList) {
                String modelName = (String) map.getOrDefault(RepositoryConstant.filed_model_name, "");
                String modelVersion = (String) map.getOrDefault(RepositoryConstant.filed_model_version, RepositoryConstant.filed_value_model_version_default);
                String component = (String) map.getOrDefault(RepositoryConstant.filed_component, "");
                List<Map<String, Object>> versions = (List<Map<String, Object>>) map.getOrDefault(RepositoryConstant.filed_versions, new ArrayList<>());

                for (Map<String, Object> entity : versions) {
                    String version = (String) entity.get(RepositoryConstant.filed_version);
                    String stage = (String) entity.get(RepositoryConstant.filed_stage);
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
            List<Map<String, Object>> modelList = this.service.findLocalModel(modelType);
            for (Map<String, Object> map : modelList) {
                String modelName = (String) map.getOrDefault(RepositoryConstant.filed_model_name, "");
                String modelVersion = (String) map.getOrDefault(RepositoryConstant.filed_model_version, "v1");
                String component = (String) map.getOrDefault(RepositoryConstant.filed_component, "");
                String version = (String) map.getOrDefault(RepositoryConstant.filed_version, "");
                String stage = (String) map.getOrDefault(RepositoryConstant.filed_stage, "");

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
                this.service.deletePackageFile(modelType, modelName, modelVersion, version, stage, component);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
