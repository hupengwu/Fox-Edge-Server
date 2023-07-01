package cn.foxtech.device.protocol.mitsubishi.plc.fx.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 三菱PLC的设备地址编码比较奇葩
 * 在用户界面输入的是，D123，M200这类TAR+UADDR的格式，然后安装一定的转换方式，转换未设备能够识别的设备地址
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class MitsubishiPlcFxEntity {
    public static final String TAR_X = "X";
    public static final String TAR_Y = "Y";
    public static final String TAR_M = "M";
    public static final String TAR_S = "S";
    public static final String TAR_T = "T";
    public static final String TAR_C = "C";
    public static final String TAR_D = "D";

    /**
     * 目标设备:X,Y,M,S,T,C,D
     */
    private String target = "";


    /**
     * 用户地址
     */
    private int address = 0;


    public int encodeAddress() {
        return 0;
    }
}
