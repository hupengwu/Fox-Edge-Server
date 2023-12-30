package cn.foxtech.device.protocol.v1.dahua.fire.core.enums;

import lombok.Getter;

public enum DataType {
    register(0, "注册包"),//
    event(1, "事件"),//
    analog(2, "模拟量"),//
    paramFix(3, "参数（定长）"),//
    paramVar(4, "参数（不定长）"),//
    operate(5, "操作"),//
    upgrade(6, "升级"),//
    function(7, "能力"),//
    ;//
    @Getter
    private final int value;


    @Getter
    private final String msg;

    DataType(int value, String msg) {
        this.value = value;
        this.msg = msg;
    }

    public static DataType getEnum(int value) {
        for (DataType type : DataType.values()) {
            if (type.value == value) {
                return type;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.msg;
    }
}
