package cn.foxtech.kernel.system.repository.task;


import cn.foxtech.common.domain.constant.ServiceVOFieldConstant;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.process.ProcessUtils;
import cn.foxtech.common.utils.osinfo.OSInfo;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTask;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskType;
import cn.foxtech.kernel.system.repository.service.RepoLocalAppConfService;
import cn.foxtech.kernel.system.repository.service.RepoLocalAppLoadService;
import cn.foxtech.kernel.system.repository.service.RepoLocalAppStartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 启动其他进程的一次性任务
 */
@Component
public class ProcessStartTask extends PeriodTask {
    @Autowired
    private RedisConsoleService logger;


    /**
     * 启动配置信息服务
     */
    @Autowired
    private RepoLocalAppLoadService repoLocalAppLoadService;

    /**
     * 进程去启动服务
     */
    @Autowired
    private RepoLocalAppStartService repoLocalAppStartService;

    @Autowired
    private RepoLocalAppConfService repoLocalAppConfService;

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
        return 10;
    }

    /**
     * 待周期性执行的操作
     */
    @Override
    public void execute() {
        try {
            // windows版本没有主动拉起JAVA进程的操作
            if (OSInfo.isWindows()) {
                return;
            }

            // 从磁盘中查找所有的shell文件信息
            List<Map<String, Object>> confFileInfoList = this.repoLocalAppConfService.getConfFileInfoList();

            // 扩展数据库中的启动配置信息
            this.repoLocalAppLoadService.extendStartConfig(confFileInfoList);

            // 扩展相关的进程状态信息
            ProcessUtils.extendAppStatus(confFileInfoList);

            for (Map<String, Object> confFileInfo : confFileInfoList) {
                String appName = (String) confFileInfo.get(ServiceVOFieldConstant.field_app_name);
                String appType = (String) confFileInfo.get(ServiceVOFieldConstant.field_app_type);
                Boolean appLoad = (Boolean) confFileInfo.get(ServiceVOFieldConstant.field_app_load);
                Long pid = (Long) confFileInfo.get(ServiceVOFieldConstant.field_pid);

                // 启动进程
                this.firstStartProcess(appName, appType, appLoad, pid);
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void firstStartProcess(String appName, String appType, Boolean appLoad, Long pid) {
        try {
            // kernel类型的进程，通过配置linux下的startup.sh来启动，service才是由manage进程来拉起
            if (ServiceVOFieldConstant.field_type_kernel.equals(appType)) {
                return;
            }

            // 配置为启动
            if (!appLoad) {
                return;
            }

            // 检查：是否已经启动
            if (pid != null) {
                return;
            }

            // 启动进程
            this.repoLocalAppStartService.restartProcess(appName, appType);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
