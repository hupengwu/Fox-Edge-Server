/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.dahua.fire.core.enums;

import lombok.Getter;


public enum ParFmt {
    ascii("ASCII"),//
    hex("Hex"),//
    bcd("BCD"),//
    ;


    @Getter
    private final String name;

    ParFmt(String name) {
        this.name = name;
    }

    public static ParFmt getEnum(String name) {
        for (ParFmt type : ParFmt.values()) {
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
