package cn.foxtech.common.utils.niosocket.multithread.handler.level2;

import java.nio.channels.SelectionKey;

import cn.foxtech.common.utils.niosocket.multithread.handler.ISocketL2Handler;

/**
 * 默认的L2 关闭接口响应实现体：它是放在L1接口实现体里面的默认空操作，目的是保证SocketL1Handler有一个SocketL2Handler实例，不会出现空操作
 * 处理连接上了消息：当客户端接入到服务器的时候，会捕获到这个动作
 *  对象：NioClientSocket
 *
 * @author h00442047
 * @since 2019年12月13日
 */
public class SocketConnectHandler implements ISocketL2Handler {
    /**
     * 处理进入消息
     *
     * @param key key
     */
    public void handle(SelectionKey key) {
    }

    /**
     * 处理异常消息
     *
     * @param key key
     */
    public void handleException(SelectionKey key, Exception e) {
    }
}
