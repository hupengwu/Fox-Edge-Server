package cn.foxtech.kernel.system.repository.task;


import cn.foxtech.common.domain.constant.ServiceVOFieldConstant;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.process.ProcessUtils;
import cn.foxtech.common.utils.Maps;
import cn.foxtech.common.utils.osinfo.OSInfo;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTask;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 周期性GC任务
 */
@Component
public class ProcessGcTask extends PeriodTask {
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
            // windows版本没有GC JAVA进程的操作
            if (OSInfo.isWindows()){
                return;
            }

            // 通过PS命令获得进程列表
            List<Map<String, Object>> processList = ProcessUtils.getProcess();

            // 逐个对进程进行GC操作
            for (Map<String, Object> map : processList) {
                try {
                    Long pid = (Long) map.get(ServiceVOFieldConstant.field_pid);
                    Long rss = (Long) map.get(ServiceVOFieldConstant.field_rss);

                    // 取出上次保存的进程内存大小
                    Long rssLast = (Long) Maps.getOrDefault(this.statusMap, "Process GC", ServiceVOFieldConstant.field_rss, pid, 0L);

                    // 检查：内存的膨胀状况，是否超过50M内存
                    if (rss < rssLast + 50 * 1000) {
                        continue;
                    }

                    // 膨胀过大的进程，进行GC回收过期内存
                    ProcessUtils.gcProcess(pid);

                    // 获得GC后进程的内存占用大小
                    Long rssNew = this.getRSS(pid);
                    if (rssNew == null) {
                        continue;
                    }

                    // 将本次内存大小保存下来
                    Maps.setValue(this.statusMap, "Process GC", ServiceVOFieldConstant.field_rss, pid, rssNew);
                } catch (Exception e) {
                    logger.error("GC进程失败：" + e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private Long getRSS(Long processId) throws IOException, InterruptedException {
        // 获得进程列表信息
        List<Map<String, Object>> processList = ProcessUtils.getProcess();

        // 找到指定PID的RSS
        for (Map<String, Object> map : processList) {
            Long pid = (Long) map.get(ServiceVOFieldConstant.field_pid);
            Long rss = (Long) map.get(ServiceVOFieldConstant.field_rss);

            if (processId.equals(pid)) {
                return rss;
            }
        }

        return null;
    }
}
