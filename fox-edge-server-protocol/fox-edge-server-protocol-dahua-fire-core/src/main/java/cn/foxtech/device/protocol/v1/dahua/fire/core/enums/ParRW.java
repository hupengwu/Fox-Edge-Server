package cn.foxtech.device.protocol.v1.dahua.fire.core.enums;

import lombok.Getter;


public enum ParRW {
    r("r"),//
    w("w"),//
    rw("r/w"),//
    ;


    @Getter
    private final String name;

    ParRW(String name) {
        this.name = name;
    }

    public static ParRW getEnum(String name) {
        for (ParRW type : ParRW.values()) {
            if (type.getName().equals(name)) {
                return type;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
