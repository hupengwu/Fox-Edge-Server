package cn.foxtech.device.protocol.v1.zktl.air6in1.entity;

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

    public abstract String getServiceKey();

    public String getCommunTypeName(){
        if (this.communType==0){
            return "NB";
        }
        if (this.communType==1){
            return "LoRa";
        }
        if (this.communType==2){
            return "LoRaWAN";
        }
        if (this.communType==4){
            return "LoRa_1freq_Mode";
        }

        return "未知通信类型";
    }

    public String getDeviceTypeName(){
        if (this.deviceType==1){
            return "消火栓";
        }
        if (this.deviceType==2){
            return "水压";
        }
        if (this.deviceType==3){
            return "水位";
        }
        if (this.deviceType==4){
            return "门磁";
        }
        if (this.deviceType==5){
            return "水浸";
        }
        if (this.deviceType==6){
            return "销售烟感";
        }
        if (this.deviceType==7){
            return "演示烟感";
        }
        if (this.deviceType==8){
            return "六合一空气监测";
        }

        return "未知设备类型";
    }
}
