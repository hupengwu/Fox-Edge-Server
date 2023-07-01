package cn.foxtech.channel.common.linker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkerHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(LinkerHandler.class);


    /**
     * IEC104链路建立
     */
    public void linkConnected(LinkerEntity sessionEntity) {
        LOGGER.info("链路连接:" + sessionEntity.getChannelName());
    }

    /**
     * IEC104链路断开
     */
    public void linkDisconnected(LinkerEntity sessionEntity) {
        LOGGER.info("链路断开:" + sessionEntity.getChannelName());
    }
}
