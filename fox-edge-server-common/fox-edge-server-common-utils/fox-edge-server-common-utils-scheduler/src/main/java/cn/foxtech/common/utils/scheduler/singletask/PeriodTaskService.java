package cn.foxtech.common.utils.scheduler.singletask;


import org.apache.log4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 周期性任务
 */
public abstract class PeriodTaskService {
    private static final Logger logger = Logger.getLogger(PeriodTaskService.class);

    /**
     * 是否已经初始化
     */
    private boolean inited = false;

    /**
     * 周期执行任务
     */
    public abstract void execute(long threadId) throws Exception;

    private boolean isInited() {
        return this.inited;
    }

    private void setInited() {
        this.inited = true;
    }

    public void schedule() {
        this.schedule(1);
    }

    public void schedule(int threadCount) {
        // 只初始化一次
        if (this.isInited()) {
            return;
        }
        this.setInited();

        PeriodTaskService service = this;

        for (int i = 0; i < threadCount; i++) {
            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
            //获取当前时间
            long cur = System.currentTimeMillis();
            scheduledExecutorService.schedule(new Runnable() {
                @Override
                public void run() {
                    long threadId = System.nanoTime();

                    //当前任务执行时候，对应的时间
                    while (true) {
                        try {
                            service.execute(threadId);
                        } catch (Throwable e) {
                            logger.error(e);
                        }
                    }
                }
            }, 0, TimeUnit.MILLISECONDS);
            scheduledExecutorService.shutdown();
        }

    }
}
