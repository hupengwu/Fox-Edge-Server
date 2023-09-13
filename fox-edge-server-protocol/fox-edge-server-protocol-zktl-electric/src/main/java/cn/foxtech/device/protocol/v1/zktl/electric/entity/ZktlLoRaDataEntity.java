package cn.foxtech.device.protocol.v1.zktl.electric.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ZktlLoRaDataEntity extends ZktlDataEntity {
    /**
     * Addr
     */
    private String addr = "";
    /**
     * 包类型
     */
    private int packType = 0;
    /**
     * 消音状态
     */
    private int silencerStatus = 0;
    /**
     * 电压状态
     */
    private int voltageStatus = 0;
    /**
     * 电流状态
     */
    private int currentStatus = 0;
    /**
     * 温度状态
     */
    private int tempStatus = 0;
    /**
     * A 相电压
     */
    private int voltageA = 0;
    /**
     * B 相电压
     */
    private int voltageB = 0;
    /**
     * C 相电压
     */
    private int voltageC = 0;
    /**
     * A 线电流
     */
    private double currentA = 0;
    /**
     * B 线电流
     */
    private double currentB = 0;
    /**
     * C 线电流
     */
    private double currentC = 0;
    /**
     * 漏电流
     */
    private int currentLeakage = 0;
    /**
     * A 线温
     */
    private int lineTempeA = 0;
    /**
     * B 线温
     */
    private int lineTempeB = 0;
    /**
     * C 线温
     */
    private int lineTempeC = 0;
    /**
     * 箱温
     */
    private int boxTemp = 0;
    /**
     * 电网频率
     */
    private int gridFrequency = 0;
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
