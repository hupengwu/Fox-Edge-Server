package cn.foxtech.common.utils.niosocket.multithread.handler;

import cn.foxtech.common.utils.niosocket.multithread.thread.ReadThread;

import java.nio.channels.SelectionKey;
import java.util.Vector;


/**
 * 捕获数据的数据接口
 *
 * @author h00442047
 * @since 2019年12月10日
 */
public interface ISocketL1Handler {
    /**
     * 运行状态
     * 对象：NioServerSocket/NioClientSocket
     *
     * @return 是否运行的标记
     */
    boolean isRunning();

    /**
     * 设置读取线程：由启动阶段创建线程后，返回给ISocketL1Handler的实例
     * 对象：NioServerSocket/NioClientSocket
     *
     * @param readThreads 读线程列表
     */
    void setReadThreads(Vector<ReadThread> readThreads);

    /**
     * 处理Accept消息：当一个客户端接入服务端的时候，会捕获到这个动作
     * 对象：NioServerSocket
     *
     * @param key SelectionKey
     */
    void handleAccept(SelectionKey key);

    /**
     * 处理Read消息：当接收到客户端发送过来的数据时，会捕获到这个动作
     * 对象：NioServerSocket/NioClientSocket
     *
     * @param key SelectionKey
     */
    void handleRead(SelectionKey key);

    /**
     * 处理Write消息：当可以给客户端发送数据的时候，会捕获到这个动作
     * 对象：NioServerSocket/NioClientSocket
     *
     * @param key SelectionKey
     */
    void handleWrite(SelectionKey key);

    /**
     * 处理连接上了消息：当客户端接入到服务器的时候，会捕获到这个动作
     * 对象：NioClientSocket
     *
     * @param key SelectionKey
     */
    void handleConnect(SelectionKey key);
}
