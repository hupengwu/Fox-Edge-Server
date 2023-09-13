package cn.foxtech.device.protocol.v1.zktl.ctrl4g.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZktlHartDataEntity extends ZktlDataEntity {
    /**
     * 预留
     */
    private int reserve = 0;
}
