package cn.foxtech.device.protocol.iec104.core.entity;

/**
 * 控制域实体：
 * I帧：用于信息传递和控制
 * S帧：用于对I帧控制的确认
 * U帧：用于链路层的维护
 * 所以，U帧可以由链路层自动维护，会话层I帧发送S帧应答，应用层应用到的是I帧发送无应答
 */
public abstract class ControlEntity {
}
