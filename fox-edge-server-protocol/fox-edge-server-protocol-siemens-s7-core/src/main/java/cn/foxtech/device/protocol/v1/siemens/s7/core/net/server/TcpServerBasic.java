package cn.foxtech.device.protocol.v1.siemens.s7.core.net.server;


import cn.foxtech.device.protocol.v1.siemens.s7.core.exceptions.SocketRuntimeException;
import cn.foxtech.device.protocol.v1.siemens.s7.core.net.SocketUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCP socket服务端的基础类
 *
 * @author xingshuang
 */
//@Slf4j
public class TcpServerBasic {

    /**
     * 服务器对象
     */
    private ServerSocket serverSocket;

    /**
     * 端口号
     */
    protected int port = 8088;

    //region 服务端

    /**
     * 启动
     */
    public void start() {
        this.start(this.port);
    }

    /**
     * 启动
     *
     * @param port 端口号
     */
    public void start(int port) {
        try {
            this.port = port;
            this.stop();
            this.serverSocket = new ServerSocket(port);
            Thread thread = new Thread(this::waitForClients);
            thread.setDaemon(true);
            thread.start();
        } catch (IOException e) {
            throw new SocketRuntimeException(e);
        }
    }

    /**
     * 停止
     */
    public void stop() {
        try {
            if (this.isAlive()) {
                this.serverSocket.close();
              //  log.debug("关闭服务端，端口号[{}]", this.port);
            }
        } catch (IOException e) {
            throw new SocketRuntimeException(e);
        }
    }

    /**
     * 是否活跃着
     *
     * @return ture：活跃着，false：死了
     */
    public boolean isAlive() {
        return this.serverSocket != null && !this.serverSocket.isClosed();
    }

    /**
     * 等待客户端连入
     */
    protected void waitForClients() {
      //  log.debug("开启等待客户端线程，端口号[{}]", this.port);
        while (this.isAlive()) {
            try {
                Socket client = this.serverSocket.accept();
                if (!this.checkClientValid(client)) {
                    SocketUtils.close(client);
                }
                Thread thread = new Thread(() -> this.doClientConnected(client));
                thread.setDaemon(true);
                thread.start();
            } catch (IOException e) {
                if (this.isAlive()) {
                //    log.error(e.getMessage());
                }
            }
        }
    }

    //endregion

    //region 客户端

    /**
     * 校验客户端是否允许连入
     *
     * @param client 客户端
     * @return true:验证成功，false：验证失败
     * @throws IOException IO异常
     */
    protected boolean checkClientValid(Socket client) throws IOException {
        return true;
    }

    /**
     * 客户端连入后要做的业务
     *
     * @param client 客户端
     */
    protected void doClientConnected(Socket client) {
      //  log.debug("有客户端[{}]连入", client.getRemoteSocketAddress());
        this.clientConnected(client);
        try {
            if (this.checkHandshake(client)) {
                while (SocketUtils.isConnected(client)) {
                    this.doClientHandle(client);
                }
            }
        } catch (Exception e) {
            if (SocketUtils.isConnected(client)) {
        //        log.error(e.getMessage());
            }
        } finally {
            try {
                SocketUtils.close(client);
            } catch (Exception ex) {
                // NOOP
            }
        }

        this.clientDisconnected(client);
   //     log.debug("有客户端[{}]断开", client.getRemoteSocketAddress());
    }

    /**
     * 客户端连入
     *
     * @param socket 客户端
     */
    protected void clientConnected(Socket socket) {
        // NOOP
    }

    /**
     * 客户端断开
     *
     * @param socket 客户端
     */
    protected void clientDisconnected(Socket socket) {
        // NOOP
    }

    /**
     * 握手校验
     *
     * @param socket 客户端
     * @return 校验结果，true：成功，false：失败
     */
    protected boolean checkHandshake(Socket socket) {
        return true;
    }

    /**
     * 执行客户端的业务，可重写
     *
     * @param socket 客户端的socket对象
     */
    protected void doClientHandle(Socket socket) {
        byte[] data = this.readClientData(socket);
   //     log.debug(new String(data));
    }

    /**
     * 读取客户端数据
     *
     * @param socket 客户端socket对象
     * @return 读取的字节数据
     */
    protected byte[] readClientData(Socket socket) {
        try {
            InputStream in = socket.getInputStream();
            int firstByte = in.read();
            if (firstByte == -1) {
                SocketUtils.close(socket);
                throw new SocketRuntimeException("客户端主动断开");
            }
            byte[] data = new byte[in.available() + 1];
            data[0] = (byte) firstByte;
            this.read(socket, data, 1, data.length - 1, 1024);
            return data;
        } catch (IOException e) {
            throw new SocketRuntimeException(e);
        }
    }

    /**
     * 写数据
     *
     * @param socket socket
     * @param data   字节数组数据
     */
    protected void write(final Socket socket, final byte[] data) {
        try {
            SocketUtils.write(socket, data);
        } catch (IOException e) {
            throw new SocketRuntimeException(e);
        }
    }

    /**
     * 读数据
     *
     * @param socket socket
     * @param data   字节数组数据
     * @return 读取个数
     */
    protected int read(final Socket socket, final byte[] data) {
        return this.read(socket, data, 0, data.length, 1024);
    }

    /**
     * 读取数据
     *
     * @param socket socket对象
     * @param data   字节数组
     * @param offset 偏移量
     * @param length 写入长度
     * @return 返回读取的数据长度
     */
    private int read(final Socket socket, final byte[] data, final int offset, final int length) {
        return this.read(socket, data, offset, length, 1024);
    }

    /**
     * 读取数据
     *
     * @param socket    socket对象
     * @param data      字节数组
     * @param offset    偏移量
     * @param length    写入长度
     * @param maxLength 单次通信允许的对最大长度
     * @return 返回读取的数据长度
     */
    protected int read(final Socket socket, final byte[] data, final int offset, final int length, final int maxLength) {
        try {
            return SocketUtils.read(socket, data, offset, length, maxLength);
        } catch (IOException e) {
            throw new SocketRuntimeException(e);
        }
    }


    //endregion
}
