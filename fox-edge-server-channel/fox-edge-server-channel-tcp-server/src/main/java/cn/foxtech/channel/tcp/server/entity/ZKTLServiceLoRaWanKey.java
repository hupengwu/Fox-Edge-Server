package cn.foxtech.channel.tcp.server.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZKTLServiceLoRaWanKey  extends ZKTLServiceKey{
    public String getServiceKey() {
        return super.getCommType() + ":" + super.getDeviceType();
    }
}