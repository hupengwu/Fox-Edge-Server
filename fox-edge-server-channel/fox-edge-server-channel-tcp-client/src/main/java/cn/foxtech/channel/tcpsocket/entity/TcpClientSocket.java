package cn.foxtech.channel.tcpsocket.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * TCP客户端Socket
 */
public class TcpClientSocket {
    /**
     * socket
     */
    private Socket socket = null;
    /**
     * 目标主机的地址
     */
    @Getter(value = AccessLevel.PUBLIC)
    @Setter(value = AccessLevel.PUBLIC)
    private String host = "localhost";
    /**
     * 目标主机的端口号
     */
    @Getter(value = AccessLevel.PUBLIC)
    @Setter(value = AccessLevel.PUBLIC)
    private int port = 502;


    /**
     * 关闭socket
     */
    public void close() {
        if (this.socket == null) {
            return;
        }

        if (this.socket.isConnected()) {
            try {
                this.socket.shutdownOutput();
                this.socket.shutdownInput();
                this.socket.close();
            } catch (IOException e1) {
                this.socket = null;
            } finally {
                this.socket = null;
            }
        }
    }

    /**
     * 尝试连接
     *
     * @throws IOException 连接异常
     */
    private void tryConnect() throws IOException {
        // 检查：socket是否已经实例化
        if (this.socket != null && this.socket.isConnected()) {
            return;
        }

        // 建立socket连接
        try {
            this.socket = new Socket();
            SocketAddress endpoint = new InetSocketAddress(this.host, this.port);
            this.socket.connect(endpoint);
            this.socket.setKeepAlive(true);

        } catch (IOException e) {
            if (this.socket.isConnected()) {
                this.socket.close();
            }
        }
    }

    /**
     * 尝试发送数据
     *
     * @param sendBuff 发送数据
     * @throws IOException 操作异常
     */
    private void trySendData(byte[] sendBuff) throws IOException {
        OutputStream os = this.socket.getOutputStream();
        os.write(sendBuff);
        os.flush();
    }

    /**
     * 发送数据
     *
     * @param sendBuff 发送数据
     * @throws IOException 操作异常
     */
    public void sendData(byte[] sendBuff) throws IOException {
        try {
            // 尝试建立连接
            this.tryConnect();

            // 在TCP连接上，尝试发送一次数据
            this.trySendData(sendBuff);
        } catch (IOException e) {
            // 出现异常，最大的可能是长期没有心跳，而导致旧的连接断开
            this.close();

            // 重新尝试建立连接
            this.tryConnect();

            // 重新尝试发送数据
            this.trySendData(sendBuff);
        }
    }

    /**
     * 尝试接收数据
     *
     * @param recvBuff 接受缓存
     * @param timeout  通信超时
     * @return 受到的数据量，没有收到数据时返回-1
     * @throws IOException 操作是否异常
     */
    private int tryRecvData(byte[] recvBuff, int timeout) throws IOException {
        if (this.socket == null) {
            return -1;
        }

        this.socket.setSoTimeout(timeout);

        InputStream is = this.socket.getInputStream();
        BufferedInputStream recv = new BufferedInputStream(is);
        return recv.read(recvBuff, 0, recvBuff.length);
    }

    /**
     * 接收数据
     *
     * @param recvBuff 接受缓存
     * @param timeout  通信超时
     * @return 受到的数据量，没有收到数据时返回-1
     */
    public int recvData(byte[] recvBuff, int timeout) {
        try {
            return this.tryRecvData(recvBuff, timeout);
        } catch (IOException e) {
            // 说明连接实际上已经断开，那么要关闭老的连接
            this.close();
            return -1;
        }
    }
}
