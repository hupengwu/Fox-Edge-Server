package cn.foxtech.common.utils.niosocket.multithread.socket;


import cn.foxtech.common.utils.niosocket.multithread.handler.ISocketL1Handler;
import cn.foxtech.common.utils.niosocket.multithread.handler.level1.SocketL1Handler;
import cn.foxtech.common.utils.niosocket.multithread.thread.ReadThread;
import cn.foxtech.common.utils.niosocket.multithread.thread.WriteThread;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;

/**
 * 客户端socket：多线程响应的socket
 * 结构：多个数据读线程，一个数据写线程
 * socketChannel管理：
 * 1.可以通过connect成功，来知道创建了哪些socketChannel;
 * 2.可以通过自定义的SocketCloseHandler，捕获哪些socketChannel关闭;
 * 3.可以通过isConnected，来指导当前socketChannel中途是否失效
 * 4.可以通过SocketConnectHandler，捕获哪些socketChannel连接上了;
 *
 * @author h00442047
 * @since 2019年12月25日
 */
public class NioClientSocket {
    /**
     * 选择器
     */
    private Selector selector;

    /**
     * 一级Handler
     */
    private ISocketL1Handler socketHandler = new SocketL1Handler();

    /**
     * 读线程的数量
     */
    private Integer readThreadCount = 5;

    /**
     * 写线程
     */
    private WriteThread writeThread;

    public Integer getReadThreadCount() {
        return readThreadCount;
    }

    public void setReadThreadCount(Integer readThreadCount) {
        this.readThreadCount = readThreadCount;
    }

    public ISocketL1Handler getSocketHandler() {
        return socketHandler;
    }

    public void setSocketHandler(ISocketL1Handler socketHandler) {
        this.socketHandler = socketHandler;
    }

    /**
     * 构造函数
     *
     */
    public NioClientSocket() {
        try {
            // 打开选择器
            this.selector = Selector.open();
        } catch (IOException e) {
            e.toString();
        }
    }

    /**
     * 连接目标服务器
     *
     * @param remoteAddress 远端服务器
     * @throws IOException 异常信息
     */
    public SocketChannel connect(SocketAddress remoteAddress) throws IOException, ConnectException {
        // 打开一个socket
        SocketChannel socketChannel = SocketChannel.open();
        // 指明为非阻塞模式
        socketChannel.configureBlocking(false);

        // 连接目标
        socketChannel.connect(remoteAddress);
        // 注册监听OP_CONNECT消息
        socketChannel.register(this.selector, SelectionKey.OP_CONNECT);

        // 检查:是否有消息过来
        this.selector.select();

        try {
            // 遍历消息SelectionKey：标识连接已经完成
            Iterator<SelectionKey> it = this.selector.selectedKeys().iterator();
            while (it.hasNext()) {
                SelectionKey key = it.next();
                it.remove();

                if (key.isValid() && key.isConnectable()) {
                    socketChannel.finishConnect();

                    // 通知连接成功
                    this.socketHandler.handleConnect(key);
                }
            }
        } catch (ConnectException e) {
            socketChannel.close();
            throw new ConnectException();
        }

        return socketChannel;
    }

    /**
     * 启动线程：读线程/写线程
     *
     * @return 操作是否成功
     * @throws IOException 异常信息
     */
    public boolean start() throws IOException {
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();

        // 生成读线程
        Vector<ReadThread> readThreads = new Vector<ReadThread>();
        for (int i = 0; i < this.readThreadCount; i++) {
            String threadName = "Thread" + "-" + uuid + "-ReadThread-" + i;

            ReadThread readThread = new ReadThread(threadName, this.socketHandler);
            readThread.start();
            readThread.startInit();
            readThread.finishInit();
            readThreads.add(readThread);
        }

        // 启动响应线程
        String threadName = "Thread-" + uuid + "-WriteThread";
        this.writeThread = new WriteThread(threadName, this.socketHandler);
        this.writeThread.start();

        // 将线程对象传递给socketHandler

        this.socketHandler.setReadThreads(readThreads);
        return true;
    }

    /**
     * 发送数据到客户端
     *
     * @param data 待发送的数据
     */
    public void writeChannel(SocketChannel socketChannel, byte[] data) {
        this.writeThread.offerData(socketChannel, data);
    }

    /**
     * 是否跟服务器保持连接
     *
     * @return 是否保持连接
     */
    public boolean isConnected(SocketChannel socketChannel) {
        if (socketChannel == null) {
            return false;
        }
        if (!socketChannel.isOpen()) {
            return false;
        }
        if (!socketChannel.isConnected()) {
            return false;
        }
        return true;
    }
}
