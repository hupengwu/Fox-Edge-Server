package cn.foxtech.device.protocol.v1.haier.ycj.a002.enums;


import lombok.Getter;


public enum Fault {
    value0(0, "无"),//
    value1(1, "室温传感器故障"),//
    value2(2, "室内管温传感器故障"),//
    value3(3, "室内制热过载保护"),//
    value4(4, "室内制冷结冰保护"),//
    value5(5, "室内、外板通讯故障"),//
    value6(6, "面板与内机通信故障"),//
    value7(7, "模块故障"),//
    value8(8, "无负载"),//
    value9(9, "压机过热"),//
    value10(10, "CT电流异常"),//
    value11(11, "外环传感器故障"),//
    value12(12, "外热交传感器故障"),//
    value13(13, "电源超、欠压保护"),//
    value14(14, "压力保护"),//
    value15(15, "外蒸发传感器故障"),//
    value16(16, "制冷过载"),//
    value17(17, "EEPROM故障"),//
    value18(18, "外回气传感器故障"),//
    value19(19, "压机传感器故障"),//
    value20(20, "室内蒸发温度"),//
    value21(21, "排水故障"),//
    value22(22, "电源三相故障"),//
    value23(23, "湿度传感器故障"),//
    value24(24, "室内风机故障"),//
    value25(25, "室外风机故障"),//
    value26(26, "压力保护"),//
    value27(27, "电子膨胀阀故障"),//
    value28(28, "除尘网需清洗"),//
    value31(31, "控制系统故障"),//
    ;
    @Getter
    private final int code;

    @Getter
    private final String name;

    Fault(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Fault getEnum(Integer cmd) {
        for (Fault value : Fault.values()) {
            if (cmd.equals(value.code)) {
                return value;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
