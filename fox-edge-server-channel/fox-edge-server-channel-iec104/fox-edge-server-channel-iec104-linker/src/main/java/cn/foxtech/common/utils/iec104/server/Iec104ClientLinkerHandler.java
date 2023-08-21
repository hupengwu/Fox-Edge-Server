package cn.foxtech.common.utils.iec104.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Iec104ClientLinkerHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(Iec104ClientLinkerHandler.class);

    /**
     * socket连接建立
     * @param sessionEntity 会话实体
     */
    public void socketConnected(Iec104ClientLinkerEntity sessionEntity) {
        LOGGER.info("TCP连接建立:" + sessionEntity.getRemoteAddress());
    }

    /**
     * socket连接断开
     * @param sessionEntity 会话实体
     */
    public void socketDisconnected(Iec104ClientLinkerEntity sessionEntity) {
        LOGGER.info("TCP连接断开:" + sessionEntity.getRemoteAddress());
    }

    /**
     * IEC104链路建立
     * @param sessionEntity 会话实体
     */
    public void linkConnected(Iec104ClientLinkerEntity sessionEntity) {
        LOGGER.info("IEC104链路连接:" + sessionEntity.getRemoteAddress());
    }

    /**
     * IEC104链路断开
     * @param sessionEntity 会话实体
     */
    public void linkDisconnected(Iec104ClientLinkerEntity sessionEntity) {
        LOGGER.info("IEC104链路断开:" + sessionEntity.getRemoteAddress());
    }

    /**
     * 会话结束
     *
     * @param sessionEntity 会话实体
     */
    public void finishedRespond(Iec104ClientLinkerEntity sessionEntity) {
        LOGGER.info("完成一个会话:" + sessionEntity.getRemoteAddress());
    }
}
