package cn.foxtech.channel.serialport.entity;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * 数据流实体
 */
@Getter(value = AccessLevel.PUBLIC)
public class SerialStreamEntity {
    /**
     * 128K缓存，用来拼接数据
     */
    private final byte[] buff = new byte[1024 * 1024];
    /**
     * 尾部位置
     */
    private int end = 0;

    public synchronized void addTail(byte[] data) {
        // 检查：是否会溢出，如果要发生溢出，说明这个数据可能已经异常了，没有人能处理了，直接废弃吧
        if (this.end + data.length > this.buff.length) {
            this.end = 0;
        }

        System.arraycopy(data, 0, this.buff, this.end, data.length);
        this.end += data.length;
    }

    public synchronized void movHead(byte[] data) {
        System.arraycopy(buff, data.length, buff, 0, buff.length - data.length);
        this.end -= data.length;
    }
}
