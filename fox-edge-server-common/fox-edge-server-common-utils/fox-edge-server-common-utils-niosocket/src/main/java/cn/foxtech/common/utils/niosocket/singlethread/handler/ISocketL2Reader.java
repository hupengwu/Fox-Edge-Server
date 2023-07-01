package cn.foxtech.common.utils.niosocket.singlethread.handler;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

/**
 * 捕获Socket操作接口：这是一个二级接口，它嵌入在默认内置的INioServerSocketL1Handler实现中
 * 这个接口相对同样是L2接口的INioServerSocketL2Handler捕获范围小，只实现读取数据和关闭sock的消息捕获
 * @author h00442047
 * @since 2019年11月28日
 */
public interface ISocketL2Reader {
    /**
     * 读取数据
     * @param key 客户端的SelectionKey
     * @param buff 缓存
     */
    public void readData(SelectionKey key, ByteBuffer buff);

    /**
     * 关闭前的处理
     * @param key 客户端的SelectionKey
     */
    public void closeStart(SelectionKey key);

    /**
     * 关闭后的处理
     * @param key 客户端的SelectionKey
     */
    public void closeFinish(SelectionKey key);

    /**
     * 处理异常消息
     * @param key 客户端的SelectionKey
     * @param e 捕获到的异常信息
     */
    public void handleException(SelectionKey key, Exception e);
}
