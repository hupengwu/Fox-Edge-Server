/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.cjt188.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * CJT188的协议框架
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class CJT188Entity {
    /**
     * 仪表类型
     */
    private CJT188Type type = new CJT188Type();

    /**
     * 表地址（BCD）
     */
    private CJT188Address address = new CJT188Address();

    /**
     * 控制码
     */
    private CJT188Ctrl ctrl = new CJT188Ctrl();
    /**
     * 数据区
     */
    private byte[] data = new byte[0];

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{仪表类型=" + this.getType().getName() + "},");
        sb.append("{地址信息=" + this.getAddress().toString() + "},");
        sb.append("{控制码=" + this.getCtrl().toString() + "},");
        return sb.toString();
    }
}
