package cn.foxtech.common.utils.iec104.server;

import cn.foxtech.common.utils.iec104.server.scheduler.Iec104PeriodTask4BlockedSession;
import cn.foxtech.common.utils.iec104.server.scheduler.Iec104PeriodTask4SocketConnect;
import cn.foxtech.common.utils.iec104.server.scheduler.Iec104PeriodTask4StartLink;
import cn.foxtech.common.utils.iec104.server.scheduler.Iec104PeriodTask4TestLink;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskScheduler;

public class Iec104ClientLinkerScheduler extends PeriodTaskScheduler {
    private static Iec104ClientLinkerScheduler instance;

    public static Iec104ClientLinkerScheduler getInstance() {
        if (instance == null) {
            instance = new Iec104ClientLinkerScheduler();
            instance.initialize();
        }
        return instance;
    }

    private void initialize() {
        this.insertPeriodTask(new Iec104PeriodTask4SocketConnect());
        this.insertPeriodTask(new Iec104PeriodTask4StartLink());
        this.insertPeriodTask(new Iec104PeriodTask4TestLink());
        this.insertPeriodTask(new Iec104PeriodTask4BlockedSession());
    }
}
