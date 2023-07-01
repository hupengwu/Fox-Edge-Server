package cn.foxtech.common.utils.niosocket.multithread.socket;


import cn.foxtech.common.utils.niosocket.multithread.handler.ISocketL1Handler;
import cn.foxtech.common.utils.niosocket.multithread.handler.level1.SocketL1Handler;
import cn.foxtech.common.utils.niosocket.multithread.thread.ListenerThread;
import cn.foxtech.common.utils.niosocket.multithread.thread.ReadThread;
import cn.foxtech.common.utils.niosocket.multithread.thread.WriteThread;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.UUID;
import java.util.Vector;

/**
 * 服务端Socket:多线程响应的socket
 * 结构：一个监听线程，多个数据读线程，一个数据写线程
 *
 * @author h00442047
 * @since 2019年12月2日
 */
public class NioServerSocket {
    /**
     * 监听者线程
     */
    private ListenerThread listenerThread;

    /**
     * 一级Handler
     */
    private ISocketL1Handler socketHandler = new SocketL1Handler();

    /**
     * 服务端口
     */
    private Integer port;

    /**
     * 读线程的数量
     */
    private Integer readThreadCount = 16;

    /**
     * 写线程
     */
    private WriteThread writeThread;

    public ISocketL1Handler getSocketHandler() {
        return socketHandler;
    }

    public void setSocketHandler(ISocketL1Handler socketHandler) {
        this.socketHandler = socketHandler;
    }

    public Integer getReadThreadCount() {
        return readThreadCount;
    }

    public void setReadThreadCount(Integer readThreadCount) {
        this.readThreadCount = readThreadCount;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * 启动线程：监听线程/读线程/写线程
     */
    public void start() throws IOException {
        String uuid = UUID.randomUUID().toString().replace("-", "").toLowerCase();

        // 启动监听线程
        String threadName = "Thread" + "-" + uuid + "-ThreadListener";
        this.listenerThread = new ListenerThread(threadName, this.socketHandler, this.port);
        this.listenerThread.start();

        // 生成读线程
        Vector<ReadThread> readThreads = new Vector<ReadThread>();
        for (int i = 0; i < this.readThreadCount; i++) {
            threadName = "Thread" + "-" + uuid + "-ReadThread-" + i;
            ReadThread readThread = new ReadThread(threadName, this.socketHandler);
            readThread.start();
            readThreads.add(readThread);
        }

        // 启动响应线程
        threadName = "Thread" + "-" + uuid + "-WriteThread";
        this.writeThread = new WriteThread(threadName, this.socketHandler);
        this.writeThread.start();

        // 将线程对象传递给socketHandler
        this.socketHandler.setReadThreads(readThreads);
    }

    /**
     * 发送数据到客户端
     *
     * @param channel 客户端channel
     * @param data 待发送的数据
     * @throws IOException 异常信息
     */
    public void writeChannel(SocketChannel channel, byte[] data) throws IOException {
        this.writeThread.offerData(channel, data);
    }
}
