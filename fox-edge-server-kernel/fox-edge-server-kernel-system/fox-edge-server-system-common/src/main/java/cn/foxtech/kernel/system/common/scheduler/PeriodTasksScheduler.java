package cn.foxtech.kernel.system.common.scheduler;


import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * 后台定时任务调度器：一个线程调度多个任务，所以后台任务不要阻塞，也不能去响应很及时的任务
 */
@Component
public class PeriodTasksScheduler extends PeriodTaskScheduler {

}
