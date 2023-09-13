package cn.foxtech.device.protocol.v1.zktl.electric.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZktlLoRaHartDataEntity  extends ZktlDataEntity {
    /**
     * Addr
     */
    private String addr = "";
    /**
     * 包类型
     */
    private int packType = 0;
    /**
     * PM1.0
     */
    private int pm1p0 = 0;
    /**
     * PM2.5
     */
    private int pm2p5 = 0;
    /**
     * PM10
     */
    private int pm10 = 0;
    /**
     * VOC
     */
    private double odor = 0;
    /**
     * 温度
     */
    private double temp = 0;
    /**
     * 湿度
     */
    private double humidity = 0;
    /**
     * 信号强度
     */
    private int signal = 0;
    /**
     * 包序号
     */
    private int packSn = 0;
    /**
     * 预留
     */
    private int reserve = 0;

    public String getServiceKey() {
        return "electric fire=" + super.getCommunTypeName() + ":" + super.getDeviceTypeName() + ":" + this.addr;
    }
}
