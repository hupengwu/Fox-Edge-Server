package cn.foxtech.common.utils.iec104.client.scheduler;

import cn.foxtech.device.protocol.v1.iec104.core.encoder.BasicSessionEncoder;
import cn.foxtech.common.utils.iec104.client.Iec104ClientLinkerEntity;
import cn.foxtech.common.utils.iec104.client.Iec104ClientLinkerManager;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTask;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 对完成了进行下一步的StartLink的会话，进行下一步的TestLink会话，
 */
public class Iec104PeriodTask4TestLink extends PeriodTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(Iec104PeriodTask4TestLink.class);

    @Override
    public int getTaskType(){
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
        List<Iec104ClientLinkerEntity> entities = Iec104ClientLinkerManager.queryEntityList4IsLinked();
        if (entities.isEmpty()) {
            return;
        }

        // 创建连接
        for (Iec104ClientLinkerEntity entity : entities) {
            try {
                LOGGER.info("发送测试链路指令");
                entity.getChannel().writeAndFlush(BasicSessionEncoder.encodeTESTFRByRequest());
            } catch (Exception e) {
                LOGGER.warn(this.getClass().getSimpleName(), e);
            }
        }
    }
}
