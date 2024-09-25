/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

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
        if (this.buff.length < data.length) {
            return;
        }

        // 寻找data在buff中的位置，因为前面可能有其他垃圾数据
        int index = -1;
        for (int i = 0; i < this.buff.length - data.length; i++) {
            boolean finded = true;
            for (int j = 0; j < data.length; j++) {
                if (data[j] == this.buff[i + j]) {
                    continue;
                }
                finded = false;
                break;
            }

            if (finded) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            index = 0;
        }

        // 截取数据：就是移动后面的数据到前面去
        System.arraycopy(this.buff, data.length + index, this.buff, 0, this.buff.length - (data.length+ index));
        this.end -= (data.length + index);
    }
}
