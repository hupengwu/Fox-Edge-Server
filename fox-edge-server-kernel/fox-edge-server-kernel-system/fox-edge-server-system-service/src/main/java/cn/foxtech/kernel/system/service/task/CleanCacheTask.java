package cn.foxtech.kernel.system.service.task;


import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.MapUtils;
import cn.foxtech.common.utils.osinfo.OSInfo;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTask;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskType;
import cn.foxtech.common.utils.shell.ShellUtils;
import cn.foxtech.kernel.common.utils.OSInfoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 周期性清理缓存任务
 */
@Component
public class CleanCacheTask extends PeriodTask {
    private final Map<String, Object> statusMap = new HashMap<>();
    @Autowired
    private RedisConsoleService logger;

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
        return 60;
    }

    /**
     * 待周期性执行的操作
     */
    @Override
    public void execute() {
        try {
            // windows版本没有对操作系统进行CleanCache的操作
            if (OSInfo.isWindows()) {
                return;
            }

            // 取出上次保存的进程内存大小
            Double buffCacheLast = (Double) MapUtils.getOrDefault(this.statusMap, "OS buff/cache", 0.0d);

            // 检查：内存的膨胀状况，是否缓超过了512M
            if (buffCacheLast > 5.12E8) {
                return;
            }

            // 释放缓存
            ShellUtils.executeShell("echo 1 > /proc/sys/vm/drop_caches");

            // 获得释放后的缓存占用状况
            Double buffCacheNew = (Double) OSInfoUtils.getMemInfo().get("ramBuffCache");

            // 将本次占用状况保存下来
            MapUtils.setValue(this.statusMap, "OS buff/cache", buffCacheNew);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}
