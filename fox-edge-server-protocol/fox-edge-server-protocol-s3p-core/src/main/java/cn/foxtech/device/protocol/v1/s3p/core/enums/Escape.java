package cn.foxtech.device.protocol.v1.s3p.core.enums;

import lombok.Getter;


public enum Escape {
    No(0, "No"),//
    Min(1, "Min"),//
    Max(2, "Max"),//

    ;
    @Getter
    private final int code;

    @Getter
    private final String name;

    Escape(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Escape getEnum(Integer code) {
        for (Escape value : Escape.values()) {
            if (code.equals(value.code)) {
                return value;
            }
        }

        return null;
    }

    public static Escape getEnum(String name) {
        for (Escape value : Escape.values()) {
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
