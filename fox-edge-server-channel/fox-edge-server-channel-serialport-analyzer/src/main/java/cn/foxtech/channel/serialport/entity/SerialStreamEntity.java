package cn.foxtech.channel.serialport.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 数据流实体
 */
@Getter(value = AccessLevel.PUBLIC)
public class SerialStreamEntity {
    public final static int max = 1024 * 1024;
    /**
     * 128K缓存，用来拼接数据
     */
    private byte[] buff = new byte[max];
    /**
     * 尾部位置
     */
    private int end = 0;

    public synchronized void addTail(byte[] data){
        System.arraycopy(data, 0, this.buff, this.end, data.length);
        this.end += data.length;
    }

    public synchronized void movHead(byte[] data){
        System.arraycopy(buff,data.length,buff,0,buff.length-data.length);
        this.end -= data.length;
    }

    public synchronized void clear(){
        this.end = 0;
    }
}
