package cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj.object;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class AnalogObject {
    /**
     * 模拟量类型（1 字节）
     */
    private int type = 0;
    /**
     * 模拟量数值（2 字节）
     */
    private int value = 0;
}
