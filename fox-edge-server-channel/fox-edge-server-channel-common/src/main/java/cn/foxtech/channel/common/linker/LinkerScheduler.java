package cn.foxtech.channel.common.linker;

import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LinkerScheduler extends PeriodTaskScheduler {

    /**
     * 建立链路任务
     */
    @Autowired
    private LinkerCreateLinkerPeriodTask createLinkTask;

    /**
     * 心跳链路任务
     */
    @Autowired
    private LinkerActiveLinkerPeriodTask activeLinkTask;

    /**
     * 删除失效链路任务
     */
    @Autowired
    private LinkerDeleteLinkerPeriodTask deleteLinkTask;

    public void initialize() {
        this.insertPeriodTask(this.createLinkTask);
        this.insertPeriodTask(this.activeLinkTask);
        this.insertPeriodTask(this.deleteLinkTask);
    }
}
