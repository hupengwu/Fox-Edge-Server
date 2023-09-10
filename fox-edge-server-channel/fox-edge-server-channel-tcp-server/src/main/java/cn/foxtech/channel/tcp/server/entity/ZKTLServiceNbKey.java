package cn.foxtech.channel.tcp.server.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZKTLServiceNbKey extends ZKTLServiceKey{
    /**
     * IMEI
     */
    private final String imei = "";
    /**
     * ICCID
     */
    private final String iccid = "";


    public String getServiceKey() {
        return super.getCommType() + ":" + super.getDeviceType() + ":" + this.imei + ":" + this.iccid;
    }
}
