package cn.foxtech.channel.common.linker;

import cn.foxtech.common.utils.scheduler.multitask.PeriodTask;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LinkerDeleteLinkerPeriodTask extends PeriodTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkerDeleteLinkerPeriodTask.class);

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
        return 5;
    }

    /**
     * 待周期性执行的操作
     */
    public void execute() {
        // 查询：已经完成了启动链路的Channel
        List<LinkerEntity> entities = LinkerManager.queryEntityListByLinkStatus(true);
        if (entities.isEmpty()) {
            return;
        }

        // 创建连接
        for (LinkerEntity entity : entities) {
            this.deleteLink(entity);
        }
    }

    private void deleteLink(LinkerEntity entity) {
        if (entity.getFailActive() < 5) {
            return;
        }

        // 标识链路失效
        LOGGER.info("链路断开:" + entity.getChannelName());
        LinkerManager.updateEntity4LinkStatus(entity.getChannelName(), true);
    }
}
