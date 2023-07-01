package cn.foxtech.common.utils.niosocket.singlethread.impl;

import cn.foxtech.common.utils.niosocket.singlethread.handler.ISocketL1Handler;
import cn.foxtech.common.utils.niosocket.singlethread.handler.ISocketL2Handler;
import cn.foxtech.common.utils.niosocket.singlethread.handler.ISocketL2Reader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;



/**
 * 实现Socket的消息处理
 * @author h00442047
 * @since 2019年11月26日
 */
public class NioServerSocketL1HandlerImpl implements ISocketL1Handler {
    /**
     * 缓存大小
     */
    private int buffSize = 4096;

    private ISocketL2Handler iAccpetHandler = new NioServerSocketL2HandlerImpl();

    private ISocketL2Handler iReadHandler = new NioServerSocketL2HandlerImpl();

    private ISocketL2Handler iWriteHandler = new NioServerSocketL2HandlerImpl();

    private ISocketL2Handler iConnectableHandler = new NioServerSocketL2HandlerImpl();

    private ISocketL2Reader iReader = new NioServerSocketL2ReaderImpl();

    public int getBuffSize() {
        return buffSize;
    }

    public void setBuffSize(int buffSize) {
        this.buffSize = buffSize;
    }

    public ISocketL2Handler getiAccpetHandler() {
        return iAccpetHandler;
    }

    public void setiAccpetHandler(ISocketL2Handler iAccpetHandler) {
        this.iAccpetHandler = iAccpetHandler;
    }

    public ISocketL2Handler getiReadHandler() {
        return iReadHandler;
    }

    public void setiReadHandler(ISocketL2Handler iReadHandler) {
        this.iReadHandler = iReadHandler;
    }

    public ISocketL2Handler getiWriteHandler() {
        return iWriteHandler;
    }

    public void setiWriteHandler(ISocketL2Handler iWriteHandler) {
        this.iWriteHandler = iWriteHandler;
    }

    public ISocketL2Handler getiConnectableHandler() {
        return iConnectableHandler;
    }

    public void setiConnectableHandler(ISocketL2Handler iConnectableHandler) {
        this.iConnectableHandler = iConnectableHandler;
    }

    public ISocketL2Reader getiReader() {
        return iReader;
    }

    public void setiReader(ISocketL2Reader iReader) {
        this.iReader = iReader;
    }

    /**
     * 处理Accept消息
     * @param key SelectionKey
     * @param buffSize 缓存大小
     * @throws IOException 异常
     */
    public void handleAccept(SelectionKey key) {
        try {
            // 通知其他操作
            this.iAccpetHandler.handleStart(key);

            // 取出channel
            ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();

            // accept客户端的连接
            SocketChannel sc = ssChannel.accept();

            // 配置成非阻塞模式
            sc.configureBlocking(false);

            // 注册:准备处理READ消息
            sc.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocateDirect(buffSize));

            // 通知其他操作
            this.iAccpetHandler.handleFinish(key);

        } catch (Exception e) {
            this.iAccpetHandler.handleException(key, e);
        }
    }

    /**
     * 处理Read消息
     * @param key SelectionKey
     * @throws IOException 异常
     */
    public void handleRead(SelectionKey key) {
        // 取出channel
        SocketChannel sc = (SocketChannel) key.channel();

        try {
            // 通知其他操作
            this.iReadHandler.handleStart(key);

            // 取出附件:缓存
            ByteBuffer buf = (ByteBuffer) key.attachment();

            // 从channel读取数据到缓存
            long bytesRead = sc.read(buf);
            while (bytesRead > 0) {
                // 翻转:将缓存的读取位置设置为首部
                buf.flip();

                this.iReader.readData(key, buf);

                // 重置:将缓存清空（重置各个位置，并不是物理释放缓存）
                buf.clear();

                // 读取下一个数据
                bytesRead = sc.read(buf);
            }
            // 读取完毕，关闭channel(比如客户端主动断开)
            if (bytesRead == -1) {
                this.iReader.closeStart(key);
                sc.close();
                this.iReader.closeFinish(key);
            }

            // 通知其他操作
            this.iReadHandler.handleFinish(key);
        } catch (IOException e) {
            this.iReader.handleException(key, e);
            this.iReadHandler.handleException(key, e);

            // 远端SOCKET关闭了，服务端也相应关闭SOKCET
            try {
                // 通知外部：准备关闭
                this.iReader.closeStart(key);

                // 依次进行消息取消、关闭socket、关闭通道操作
                key.cancel();
                sc.socket().close();
                key.channel().close();

                // 通知外部：关闭完毕
                this.iReader.closeFinish(key);
            } catch (IOException ioe) {
                ioe.toString();
            }
        }

    }

    /**
     * 处理Write消息
     * @param key SelectionKey
     * @throws IOException 异常
     */
    public void handleWrite(SelectionKey key) {
        try {
            // 通知其他操作
            this.iWriteHandler.handleStart(key);

            // 取出附件:缓存
            ByteBuffer buf = (ByteBuffer) key.attachment();

            // 取出channel
            SocketChannel sc = (SocketChannel) key.channel();

            // 翻转:将缓存的读取位置设置为首部
            buf.flip();

            // 发送数据:将缓存中的数据写入channel
            while (buf.hasRemaining()) {
                sc.write(buf);
            }

            // 对缓存进行compact
            buf.compact();

            // 通知其他操作
            this.iWriteHandler.handleFinish(key);
        } catch (IOException e) {
            this.iWriteHandler.handleException(key, e);
        }
    }

    /**
     * 处理Connectable消息
     * @param key
     * @throws IOException
     */
    public void handleConnectable(SelectionKey key) {
        // 通知其他操作
        this.iConnectableHandler.handleStart(key);
        // 通知其他操作
        this.iConnectableHandler.handleFinish(key);
    }
}
