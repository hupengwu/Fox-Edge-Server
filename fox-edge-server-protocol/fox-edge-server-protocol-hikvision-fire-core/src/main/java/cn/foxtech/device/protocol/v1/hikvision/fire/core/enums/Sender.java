package cn.foxtech.device.protocol.v1.hikvision.fire.core.enums;

import lombok.Getter;

public enum Sender {
    device(0, "设备"),//设备发出
    platform(1, "平台"),//平台发出
    any(2, "设备或平台");// 设备或者平台发出
    @Getter
    private final int value;


    @Getter
    private final String msg;

    Sender(int value, String msg) {
        this.value = value;
        this.msg = msg;
    }

    public static Sender getEnum(int value) {
        for (Sender type : Sender.values()) {
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
