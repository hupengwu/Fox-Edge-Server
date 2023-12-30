package cn.foxtech.device.protocol.v1.dahua.fire.core.enums;

import lombok.Getter;

public enum Upgrade {
    upgradeOk(0, "升级成功"),//
    illegalNode(1, "非法节点"),//
    commTimeout(2, "通信超时"),//
    underVoltage(3, "节点欠压"),//
    disconnect(4, "节点失联"),//
    powerFailure(5, "电源故障"),//
    verifyError(6, "校验错误"),//
    ;
    @Getter
    private final int value;


    @Getter
    private final String name;

    Upgrade(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public static Upgrade getEnum(int value) {
        for (Upgrade type : Upgrade.values()) {
            if (type.value == value) {
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
