package cn.foxtech.common.utils.niosocket.singlethread.handler;

import java.nio.channels.SelectionKey;

/**
 * 捕获Socket操作接口：这是一个二级接口，它嵌入在默认内置的INioServerSocketL1Handler实现中
 * 这个接口相对同样是L2接口的INioServerSocketL2Reader捕获范围更大，如果需要实现更多的捕获行为，可以实现这个接口
 * @author h00442047
 * @since 2019年11月28日
 */
public interface ISocketL2Handler {
    /**
     * 处理进入消息
     * @param key key
     */
    public void handleStart(SelectionKey key);

    /**
     * 处理退出消息
     * @param key key
     */
    public void handleFinish(SelectionKey key);

    /**
     * 处理异常消息
     * @param key
     * @param e
     */
    public void handleException(SelectionKey key, Exception e);
}
