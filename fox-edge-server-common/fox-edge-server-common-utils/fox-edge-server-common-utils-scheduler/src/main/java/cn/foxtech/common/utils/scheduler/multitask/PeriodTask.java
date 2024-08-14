/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.scheduler.multitask;

/**
 * 非阻塞性操作的异步任务
 */
public abstract class PeriodTask {
    /**
     * 任务类型
     *
     * @return 类型代码
     */
    public int getTaskType() {
        return PeriodTaskType.task_type_share;
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
     * 待周期性执行的操作：非阻塞性操作，否则会影响其他任务的执行
     */
    public abstract void execute();
}
