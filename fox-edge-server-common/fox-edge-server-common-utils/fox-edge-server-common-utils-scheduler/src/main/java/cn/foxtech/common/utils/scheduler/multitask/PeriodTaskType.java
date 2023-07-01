package cn.foxtech.common.utils.scheduler.multitask;

public class PeriodTaskType {
    /**
     * 周期任务：在公共线程，循环执行
     */
    public static int task_type_share = 1;

    /**
     * 周期任务：在独立线程中，循环执行
     */
    public static int task_type_alone = 2;

    /**
     * 临时性任务：执行一次
     */
    public static int task_type_once = 3;
}
