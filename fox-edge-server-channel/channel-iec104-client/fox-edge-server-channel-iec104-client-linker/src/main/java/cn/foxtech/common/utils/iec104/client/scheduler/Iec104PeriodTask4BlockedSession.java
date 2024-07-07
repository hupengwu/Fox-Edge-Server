package cn.foxtech.common.utils.iec104.client.scheduler;

import cn.foxtech.device.protocol.v1.iec104.core.encoder.MessageUtils;
import cn.foxtech.device.protocol.v1.iec104.core.entity.ApduEntity;
import cn.foxtech.device.protocol.v1.iec104.core.entity.AsduEntity;
import cn.foxtech.device.protocol.v1.iec104.core.entity.IControlEntity;
import cn.foxtech.common.utils.iec104.client.Iec104ClientLinkerEntity;
import cn.foxtech.common.utils.iec104.client.Iec104ClientLinkerManager;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTask;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 会话阻塞的处理：只要是客户端发送了一些设备不支持的命令类型，设备不会在会话层响应这类命令，
 * 要清理这些占用的会话，否则会堵塞后续其他命令的执行
 */
public class Iec104PeriodTask4BlockedSession extends PeriodTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(Iec104PeriodTask4TestLink.class);

    // 判定阻塞的时间长度
    private static final int BLOCK_TIME = 5000;

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
        return 1;
    }

    /**
     * 待周期性执行的操作
     */
    public void execute() {
        // 查询：已经完成了启动链路的Channel
        List<Iec104ClientLinkerEntity> entities = Iec104ClientLinkerManager.queryEntityList4IsBlocked(BLOCK_TIME);
        if (entities.isEmpty()) {
            return;
        }

        // 清理连接
        for (Iec104ClientLinkerEntity linkerEntity : entities) {
            try {
                ApduEntity request = linkerEntity.getSession().getRequest();

                if (request.getControl() instanceof IControlEntity) {
                    IControlEntity control = (IControlEntity) request.getControl();
                    AsduEntity asduEntity = request.getAsdu();
                    LOGGER.info("会话长期未完成，自动清理：" + linkerEntity.getChannel().remoteAddress() + "Accept=" + control.getAccept() //
                            + "，Send=" + control.getSend() //
                            + "，类型标识=" + MessageUtils.getTypeIdMessage(asduEntity.getTypeId()) //
                            + "，可变结构限定词=" + asduEntity.getVsq() //
                            + "，传输原因=" + MessageUtils.getReasonMessage(asduEntity.getCot().getReason()) + " " + asduEntity.getCot() //
                            + "，公共地址=" + asduEntity.getCommonAddress() //
                    );

                    // 重置会话，为下一次会话做准备
                    linkerEntity.getSession().resetSession();
                    linkerEntity.getSession().increaseLastSend();
                }
            } catch (Exception e) {
                LOGGER.warn(this.getClass().getSimpleName(), e);
            }
        }
    }
}
