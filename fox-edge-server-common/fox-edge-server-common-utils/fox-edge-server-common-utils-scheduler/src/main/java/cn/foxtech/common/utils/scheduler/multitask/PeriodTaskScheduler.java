/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.scheduler.multitask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 后台线程
 */
public class PeriodTaskScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodTaskScheduler.class);

    private final PeriodTaskManager taskManager = new PeriodTaskManager();

    /**
     * 建立连接线程
     */
    public void schedule() {
        this.newShareSchedule();
        this.newAloneSchedule();
    }

    /**
     * 创建一个共享的线程：各共享类型的任务，在这个线程中执行
     * 这些任务，允许发生堆积
     */
    private void newShareSchedule() {
        PeriodTaskManager taskManager = this.taskManager;
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1 * 1000);

                        // 执行一次性的临时任务
                        executeOnceTask();

                        // 执行周期性任务
                        executeShareTask();
                    } catch (Throwable e) {
                        LOGGER.error("调度任务：", e);
                    }
                }
            }
        }, 0, TimeUnit.MILLISECONDS);
        scheduledExecutorService.shutdown();
    }

    /**
     * 执行共享该线程周期性任务
     */
    private void executeShareTask() {
        // 查询：到期的周期任务
        List<PeriodTask> periodTaskList = taskManager.queryShareTask(System.currentTimeMillis());
        for (PeriodTask periodTask : periodTaskList) {
            try {
                // 执行任务
                periodTask.execute();
            } catch (Throwable e) {
                LOGGER.error(periodTask.getClass().getSimpleName(), e);
            }

            // 更新时间
            taskManager.updateShareTask(periodTask, System.currentTimeMillis());
        }
    }
    /**
     * 执行单次任务
     */
    private void executeOnceTask() {
        // 查询：收到的临时性任务
        List<PeriodTask> tempTaskList = taskManager.queryOnceTask();
        for (PeriodTask periodTask : tempTaskList) {
            try {
                // 执行任务
                periodTask.execute();
            } catch (Throwable e) {
                LOGGER.error(periodTask.getClass().getSimpleName(), e);
            }
        }

        tempTaskList.clear();
    }

    /**
     * 为每个独立任务创建独立线程
     * 这些独立任务是不允许被其他任务堵塞的
     */
    private void newAloneSchedule() {
        PeriodTaskManager taskManager = this.taskManager;

        List<PeriodTask> aloneTaskList = taskManager.queryAloneTask();

        for (PeriodTask task : aloneTaskList) {

            // 为每一个任务创建一个定时执行的线程
            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
            scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    try {
                        task.execute();
                    } catch (Throwable e) {
                        LOGGER.error("调度任务：", e);
                    }
                }
            }, 0, task.getSchedulePeriod(), TimeUnit.SECONDS);
        }


    }

    public void insertPeriodTask(PeriodTask periodTask) {
        this.taskManager.insertTask(periodTask);
    }
}
