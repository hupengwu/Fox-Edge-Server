package cn.foxtech.channel.tcp.server.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZKTLServiceLoRaKey extends ZKTLServiceKey {
    /**
     * Addr
     */
    private final String addr = "";


    public String getServiceKey() {
        return super.getCommType() + ":" + super.getDeviceType() + ":" + this.addr;
    }
}
