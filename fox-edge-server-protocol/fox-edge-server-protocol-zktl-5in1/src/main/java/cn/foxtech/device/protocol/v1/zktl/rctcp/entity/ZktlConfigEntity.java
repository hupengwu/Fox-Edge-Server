package cn.foxtech.device.protocol.v1.zktl.rctcp.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZktlConfigEntity extends ZktlDataEntity{
    private int value = 0;
    public String getServiceKey() {
        return "air6in1=" + super.getCommunTypeName() + ":" + super.getDeviceTypeName() + ":" + this.value;
    }
}
