package cn.foxtech.common.utils.scheduler.multitask;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class PeriodTaskManager {
    /**
     * 周期性任务：循环执行
     */
    private final Map<PeriodTask, Long> shareTaskList = new ConcurrentHashMap<>();

    /**
     * 独立线程
     */
    private final List<PeriodTask> aloneTaskList = new CopyOnWriteArrayList<>();

    /**
     * 临时性任务：只执行一次
     */
    private final List<PeriodTask> onceTaskList = new CopyOnWriteArrayList<>();


    public synchronized List<PeriodTask> queryAloneTask() {
        return this.aloneTaskList;
    }

    public synchronized List<PeriodTask> queryShareTask(long currentTime) {
        List<PeriodTask> result = new ArrayList<>();
        for (Map.Entry<PeriodTask, Long> entry : this.shareTaskList.entrySet()) {
            PeriodTask periodTask = entry.getKey();
            long lastUpdateTime = entry.getValue();

            if (currentTime > periodTask.getSchedulePeriod() * 1000L + lastUpdateTime) {
                result.add(periodTask);
            }
        }

        return result;
    }

    public synchronized List<PeriodTask> queryOnceTask() {
        List<PeriodTask> result = new ArrayList<>();
        result.addAll(this.onceTaskList);
        this.onceTaskList.clear();
        return result;
    }

    public synchronized void insertTask(PeriodTask periodTask) {
        if (periodTask.getTaskType() == PeriodTaskType.task_type_share) {
            this.shareTaskList.put(periodTask, 0L);
        }
        if (periodTask.getTaskType() == PeriodTaskType.task_type_alone) {
            this.aloneTaskList.add(periodTask);
        }
        if (periodTask.getTaskType() == PeriodTaskType.task_type_once) {
            this.onceTaskList.add(periodTask);
        }
    }

    public synchronized void updateShareTask(PeriodTask periodTask, long time) {
        if (periodTask.getTaskType() == PeriodTaskType.task_type_share) {
            this.shareTaskList.put(periodTask, time);
        }
    }
}
