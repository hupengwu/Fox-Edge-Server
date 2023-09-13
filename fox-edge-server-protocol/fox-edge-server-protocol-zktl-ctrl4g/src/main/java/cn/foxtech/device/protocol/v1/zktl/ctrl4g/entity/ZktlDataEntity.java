package cn.foxtech.device.protocol.v1.zktl.ctrl4g.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ZktlDataEntity {
    /**
     * 通信类型
     */
    private int communType = 0;
    /**
     * 设备类型
     */
    private int deviceType = 0;
    /**
     * Addr
     */
    private String addr = "";
}
