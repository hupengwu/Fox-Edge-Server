/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.cjt188.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class CJT188Ctrl {
    private int value = 0;

    public int getCtrl() {
        return value & 0x7f;
    }

    public boolean getCS() {
        return (value & 0x80) == 0;
    }

    public String getName() {
        if (this.getCtrl() == 0x01) {
            return "读表计数据";
        }
        if (this.getCtrl() == 0x03) {
            return "读表计地址";
        }
        if (this.getCtrl() == 0x15) {
            return "设置表计地址";
        }
        if (this.getCtrl() == 0x2A) {
            return "控制阀门";
        }
        if (this.getCtrl() == 0x04) {
            return "控制阀门";
        }


        return "未定义的值:" + this.getCtrl();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!this.getCS()) {
            sb.append("主叫方");
        } else {
            sb.append("应答方");
        }
        sb.append(":");
        sb.append(this.getName());

        return sb.toString();
    }
}
