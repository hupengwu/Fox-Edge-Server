package cn.foxtech.common.utils.iec104.client.scheduler;

import cn.foxtech.common.utils.iec104.client.Iec104ClientLinkerFactory;
import cn.foxtech.common.utils.iec104.client.Iec104ClientLinkerManager;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTask;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.List;

/**
 * 对完成了登记步骤的会话，进行下一步的Socket Connected步骤，
 */
public class Iec104PeriodTask4SocketConnect extends PeriodTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(Iec104PeriodTask4SocketConnect.class);

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
        return 15;
    }

    /**
     * 待周期性执行的操作
     */
    public void execute() {
        List<SocketAddress> waitRemoteAddress = Iec104ClientLinkerManager.queryEntityList4WaitConnected();
        if (waitRemoteAddress == null) {
            return;
        }

        // 创建连接
        for (SocketAddress socketAddress : waitRemoteAddress) {
            Iec104ClientLinkerFactory.getInstance().createClient(socketAddress);
        }
    }
}
