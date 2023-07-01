package cn.foxtech.device.protocol.lrw;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class LRWEntity {
    /**
     * 命令字
     */
    private byte cmd = 0;

    /**
     * 数据区
     */
    private byte[] dat = new byte[0];
}
