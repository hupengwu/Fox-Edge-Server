package cn.foxtech.device.protocol.iec104.core.entity;

import lombok.Data;

/**
 * 可变结构限定词
 * SQ = 0 ：信息对象的地址不连续（意思就是每个信息对象都会一个对象地址）
 * SQ = 1 ： 信息对象的地址连续 （只有第一个信息对象有地址，其他对象的地址就是累加1）
 * Tips：总召唤时，为了压缩信息传输时间SQ=1；而在从站主动上传变化数据时，因为地址不连续，采用SQ=0；
 */
@Data
public class VsqEntity {

    /**
     * 地址是否连续
     */
    boolean sq = false;
    /**
     * 当地址为连续时，信息体元素地址数量   0-7位
     */
    int num = 0;
}
