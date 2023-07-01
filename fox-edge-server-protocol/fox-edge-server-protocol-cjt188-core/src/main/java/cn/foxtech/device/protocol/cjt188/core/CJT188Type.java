package cn.foxtech.device.protocol.cjt188.core;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 仪表类型
 * 广播类型: AA
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class CJT188Type {
    private int value = 0;

    public String getName() {
        if (value == (0xAA & 0xff)) {
            return "广播类型:AA";
        }

        if (value == (0x10 & 0xff)) {
            return "冷水水表";
        }
        if (value == (0x11 & 0xff)) {
            return "热水水表";
        }
        if (value == (0x12 & 0xff)) {
            return "直饮水水表";
        }
        if (value == (0x20 & 0xff)) {
            return "热量表 (记热量)";
        }
        if (value == (0x21 & 0xff)) {
            return "热量表 (记冷量)";
        }
        if (value == (0x10 & 0xff)) {
            return "冷水水表";
        }
        if (value == (0x30 & 0xff)) {
            return "燃气表";
        }
        if (value == (0x40 & 0xff)) {
            return "电度表";
        }
        return "未知类型:" + value;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
