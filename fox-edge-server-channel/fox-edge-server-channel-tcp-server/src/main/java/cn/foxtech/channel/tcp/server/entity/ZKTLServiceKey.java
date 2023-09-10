package cn.foxtech.channel.tcp.server.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ZKTLServiceKey {
    /**
     * 通信类型
     */
    private final String commType = "";
    /**
     * 设备类型
     */
    private final String deviceType = "";

    public abstract String getServiceKey();
}
