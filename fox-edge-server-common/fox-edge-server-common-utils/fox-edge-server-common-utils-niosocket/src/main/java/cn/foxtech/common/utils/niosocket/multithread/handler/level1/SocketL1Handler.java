package cn.foxtech.common.utils.niosocket.multithread.handler.level1;


import cn.foxtech.common.utils.niosocket.multithread.handler.ISocketL1Handler;
import cn.foxtech.common.utils.niosocket.multithread.handler.ISocketL2Handler;
import cn.foxtech.common.utils.niosocket.multithread.handler.level2.*;
import cn.foxtech.common.utils.niosocket.multithread.thread.ReadThread;
import cn.foxtech.common.utils.niosocket.multithread.handler.level2.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Vector;

/**
 * 默认的响应接口实现体
 *
 * @author h00442047
 * @since 2019年12月13日
 */
public class SocketL1Handler implements ISocketL1Handler {
    /**
     * 各线程共享的是否运行/退出的变量
     */
    private volatile boolean running = true;

    /**
     * 读取器数组（线程安全）
     */
    private Vector<ReadThread> readThreads;

    /**
     * 客户端channel（线程安全）
     */
    private Vector<SocketChannel> socketChannels = new Vector<SocketChannel>();

    /**
     * 读缓存的大小
     */
    private int buffSize = 4096;

    /**
     * 客户端接入handler
     */
    private ISocketL2Handler accpetHandler = new SocketAccpetHandler();

    /**
     * 接收客户端数据的handler
     */
    private ISocketL2Handler readHandler = new SocketReadHandler();

    /**
     * 返回客户端数据的handler
     */
    private ISocketL2Handler writeHandler = new SocketWriteHandler();

    /**
     * 客户端关闭的handler
     */
    private ISocketL2Handler closeHandler = new SocketCloseHandler();

    /**
     * 客户端连接的handler
     */
    private ISocketL2Handler connetHandler = new SocketConnectHandler();

    public ISocketL2Handler getConnetHandler() {
        return connetHandler;
    }

    public void setConnetHandler(ISocketL2Handler connetHandler) {
        this.connetHandler = connetHandler;
    }

    public Vector<SocketChannel> getSocketChannels() {
        return socketChannels;
    }

    public void setSocketChannels(Vector<SocketChannel> socketChannels) {
        this.socketChannels = socketChannels;
    }

    public int getBuffSize() {
        return buffSize;
    }

    public void setBuffSize(int buffSize) {
        this.buffSize = buffSize;
    }

    public ISocketL2Handler getAccpetHandler() {
        return accpetHandler;
    }

    public void setAccpetHandler(ISocketL2Handler accpetHandler) {
        this.accpetHandler = accpetHandler;
    }

    public ISocketL2Handler getCloseHandler() {
        return closeHandler;
    }

    public void setCloseHandler(ISocketL2Handler closeHandler) {
        this.closeHandler = closeHandler;
    }

    public ISocketL2Handler getReadHandler() {
        return readHandler;
    }

    public void setReadHandler(ISocketL2Handler readHandler) {
        this.readHandler = readHandler;
    }

    public ISocketL2Handler getWriteHandler() {
        return writeHandler;
    }

    public void setWriteHandler(ISocketL2Handler writeHandler) {
        this.writeHandler = writeHandler;
    }

    /**
     * 设置读取线程：由NioServerSocket启动阶段创建线程后，返回给ISocketL1Handler的实例
     */
    public void setReadThreads(Vector<ReadThread> readThreads) {
        this.readThreads = readThreads;
    }

    /**
     * 运行状态
     *
     * @return 是否运行的标记
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * 响应监听到的消息：启动一个读线程去处理该消息
     *
     * @param key 监听到的SelectionKey
     * @throws IOException 异常
     */
    public void handleAccept(SelectionKey key) {
        try {
            // 取出服务端的channel
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

            // accept客户端的连接，得到客户端的channel
            SocketChannel socketChannel;
            while ((socketChannel = serverSocketChannel.accept()) != null) {
                // 配置成非阻塞模式
                try {
                    socketChannel.configureBlocking(false);
                    socketChannel.socket().setTcpNoDelay(true);
                    socketChannel.socket().setKeepAlive(true);
                } catch (IOException e) {
                    socketChannel.close();
                    throw e;
                }

                // 为该socket绑定一个读线程
                SelectionKey readKey = this.bindReadThread(socketChannel);
                if (readKey == null) {
                    // 已经没有读线程可以用来响应了，主动踢掉客户端的接入
                    socketChannel.close();
                    continue;
                }

                // 通知Accpet消息
                this.accpetHandler.handle(readKey);
            }

        } catch (Exception e) {
            e.toString();
        }
    }

    /**
     * 绑定一个读线程
     * @param socketChannel 客户端连接
     * @return 绑定是否成功
     * @throws IOException 异常信息
     */
    private SelectionKey bindReadThread(SocketChannel socketChannel) throws IOException {
        // 申请一个读取线程去读取数据（简单的自定义线程池）
        ReadThread readThread = this.allocatReadThread();
        if (readThread == null) {
            return null;
        }

        try {
            // 标识为初始化阶段
            readThread.startInit();

            // 注册:让读取线程内的selector，响应处理READ消息
            ByteBuffer buffer = ByteBuffer.allocateDirect(this.buffSize);
            SelectionKey readKey = readThread.registerChannel(socketChannel, buffer);

            this.socketChannels.add(socketChannel);
            return readKey;
        } finally {
            // 标识为初始化结束
            readThread.finishInit();
        }
    }

    /**
     * 分配一个读线程处理
     *
     * @return 处理数据的读线程
     */
    private ReadThread allocatReadThread() {
        if (this.readThreads == null) {
            return null;
        }

        // 找出负荷最小的线程：处理socket数量最少的线程
        ReadThread minThread = null;
        int minKeysSize = Integer.MAX_VALUE;
        for (ReadThread readThread : this.readThreads) {
            if (readThread.getKeys().size() < minKeysSize) {
                minKeysSize = readThread.getKeys().size();
                minThread = readThread;
            }
        }

        return minThread;
    }

    /**
     * 释放读取数据线程
     *
     * @param key 读数据的key
     */
    private void releaseReadThread(SelectionKey key) {
        if (this.readThreads == null) {
            return;
        }

        for (ReadThread readThread : this.readThreads) {
            readThread.getKeys().remove(key);
        }
    }

    /**
     * 处理Read消息
     *
     * @param key SelectionKey
     * @throws IOException 异常
     */
    public void handleRead(SelectionKey key) {
        // 取出channel
        SocketChannel sc = (SocketChannel) key.channel();

        try {
            // 取出附件:缓存
            ByteBuffer buf = (ByteBuffer) key.attachment();

            // 从channel读取数据到缓存
            long bytesRead = sc.read(buf);
            while (bytesRead > 0) {
                // 翻转:将缓存的读取位置设置为首部
                buf.flip();

                // 通知其他操作
                this.readHandler.handle(key);

                // 重置:将缓存清空（重置各个位置，并不是物理释放缓存）
                buf.clear();

                // 读取下一个数据
                bytesRead = sc.read(buf);
            }
            // 客户端关闭了socket：此时读的数据长度为-1，需要关闭channel(比如客户端主动断开)
            if (bytesRead == -1) {
                // 关闭key
                this.close(key);
            }
        } catch (IOException e) {
            this.readHandler.handleException(key, e);

            // 关闭key
            this.close(key);
        }
    }

    /**
     * 关闭socket：关闭channel/socket,释放读线程
     *
     * @param key 客户端的key
     */
    private void close(SelectionKey key) {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        // 远端SOCKET关闭了，服务端也相应关闭SOKCET
        try {
            // 通知外部：准备关闭
            this.closeHandler.handle(key);

            // 依次进行注册取消、关闭socket、关闭通道操作
            key.cancel();
            socketChannel.socket().close();
            socketChannel.close();
            this.releaseReadThread(key);

            this.socketChannels.remove(socketChannel);
        } catch (IOException ioe) {
            ioe.toString();
        }
    }

    /**
     * 处理Write消息
     *
     * @param key SelectionKey
     * @throws IOException 异常
     */
    public void handleWrite(SelectionKey key) {
        try {
            // 通知其他操作
            this.writeHandler.handle(key);

            // 写完成以后，发出不再需要后续需要写的通知
            key.interestOps(0);
        } catch (Exception e) {
            this.writeHandler.handleException(key, e);
        }
    }

    /**
     * 处理连接上了消息
     *
     * @param key SelectionKey
     */
    public void handleConnect(SelectionKey key) {
        try {
            SocketChannel socketChannel = (SocketChannel) key.channel();

            // 通知其他操作
            this.connetHandler.handle(key);
            // 绑定一个读线程
            this.bindReadThread(socketChannel);
        } catch (Exception e) {
            this.connetHandler.handleException(key, e);
        }
    }

}
