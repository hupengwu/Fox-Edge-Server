package cn.foxtech.common.utils.niosocket.singlethread.socket;

import cn.foxtech.common.utils.niosocket.singlethread.impl.NioServerSocketL1HandlerImpl;
import cn.foxtech.common.utils.niosocket.singlethread.handler.ISocketL1Handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;


/**
 * 相关文章:https://blog.csdn.net/u011381576/article/details/79876754</br>
 * @author h00442047
 * @since 2019-09-20 11:10:45
 *
 */
public class NioServerSocket {

    /**
     * 消息处理者
     */
    private ISocketL1Handler socketHandler = new NioServerSocketL1HandlerImpl();

    /**
     * 端口号
     */
    private int port = 8080;

    /**
     * 通信超时
     */
    private int timeOut = 3 * 1000;

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    /**
     * 获取端口号
     * @return 端口号
     */
    public int getPort() {
        return port;
    }

    /**
     * 设置端口号
     * @param port 端口号
     */
    public void setPort(int port) {
        this.port = port;
    }

    public ISocketL1Handler getSocketHandler() {
        return socketHandler;
    }

    public void setSocketHandler(ISocketL1Handler socketHandler) {
        this.socketHandler = socketHandler;
    }

    /**
     * selector的消息响应
     */
    public void selector() {
        Selector selector = null;
        ServerSocketChannel ssc = null;
        try {
            // 初始化
            selector = Selector.open();// 创建selector
            ssc = ServerSocketChannel.open();// 创建通道
            ssc.socket().bind(new InetSocketAddress(this.port));// 绑定端口
            ssc.configureBlocking(false);// 指明为非阻塞模式
            ssc.register(selector, SelectionKey.OP_ACCEPT);// 注册ACCEPT事件

            while (true) {
                // 检查:是否有消息过来
                if (selector.select(this.timeOut) == 0) {
                    // System.out.println("==");
                    continue;
                }

                // 处理消息
                Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();

                    // 1.Accept消息
                    if (key.isAcceptable()) {
                        socketHandler.handleAccept(key);
                    }

                    // 2.Read消息
                    if (key.isValid() && key.isReadable()) {
                        socketHandler.handleRead(key);
                    }

                    // 3.Write消息
                    if (key.isValid() && key.isWritable()) {
                        socketHandler.handleWrite(key);
                    }

                    // 4.Connectable消息
                    if (key.isValid() && key.isConnectable()) {
                        socketHandler.handleConnectable(key);
                    }

                    iter.remove();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            try {
                if (selector != null) {
                    selector.close();
                }
                if (ssc != null) {
                    ssc.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
