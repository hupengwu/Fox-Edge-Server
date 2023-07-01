package cn.foxtech.common.utils.niosocket.singlethread.handler;

import java.nio.channels.SelectionKey;

/**
 * 捕获Socket操作接口：这是一个一级接口，如果你觉得默认的L1捕获接口实现不满足诉求，你可以重新实现它
 * 基类内置了一个缺省的实现类
 * @author h00442047
 * @since 2019年11月28日
 */
public interface ISocketL1Handler {
    /**
     * 处理Accept消息
     * @param key SelectionKey
     */
    public void handleAccept(SelectionKey key);

    /**
     * 处理Read消息
     * @param key SelectionKey
     */
    public void handleRead(SelectionKey key);

    /**
     * 处理Write消息
     * @param key SelectionKey
     */
    public void handleWrite(SelectionKey key);

    /**
     * 处理Connectable消息
     * @param key
     */
    public void handleConnectable(SelectionKey key);
}
