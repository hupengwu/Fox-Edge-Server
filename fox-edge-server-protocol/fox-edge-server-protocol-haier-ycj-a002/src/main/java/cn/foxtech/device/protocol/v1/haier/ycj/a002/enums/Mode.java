package cn.foxtech.device.protocol.v1.haier.ycj.a002.enums;

import lombok.Getter;


public enum Mode {
    value0(0, "自动模式"),//
    value1(1, "制冷模式"),//
    value2(2, "制热模式"),//
    value3(3, "通风模式"),//
    value4(4, "除湿模式"),//
    value5(5, "舒适模式"),//
    value6(6, "干衣模式"),//

    ;
    @Getter
    private final int code;

    @Getter
    private final String name;

    Mode(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Mode getEnum(Integer code) {
        for (Mode value : Mode.values()) {
            if (code.equals(value.code)) {
                return value;
            }
        }

        return null;
    }

    public static Mode getEnum(String name) {
        for (Mode value : Mode.values()) {
            if (name.equals(value.name)) {
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
