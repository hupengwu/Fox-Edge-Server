package cn.foxtech.device.protocol.v1.zktl.air6in1.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZktlNbDataEntity extends ZktlDataEntity {
    /**
     * IMEI
     */
    private String imei = "";
    /**
     * ICCID
     */
    private String iccid = "";
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
    private double voc = 0;
    /**
     * 温度
     */
    private double temp = 0;
    /**
     * 湿度
     */
    private double humidity = 0;
    /**
     * 包类型
     */
    private String packType = "";
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
        return super.getCommunType() + ":" + super.getDeviceType() + ":" + this.imei + ":" + this.iccid;
    }
}
