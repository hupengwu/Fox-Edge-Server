package cn.foxtech.channel.udp.client.entity;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * TCP客户端Socket
 */
@Getter(value = AccessLevel.PUBLIC)
@Component
public class ChannelSocket {
    @Value("${spring.channel.updsocket.port}")
    private Integer port = 0;

    /**
     * socket
     */
    private DatagramSocket socket = null;


    public boolean isOpen() {
        return this.socket != null;
    }

    /**
     * 打开socket
     *
     * @throws SocketException
     */
    public void open() throws SocketException {
        // 检查：socket是否已经实例化
        if (this.socket != null) {
            return;
        }

        // 建立socket连接
        this.socket = new DatagramSocket(this.port);
    }

    /**
     * 发送数据
     *
     * @param remoteAddress 目标IP
     * @param port          目标端口
     * @param buffer        待发送数据，不能超过以太网的包长度
     * @throws IOException
     */
    public void trySend(InetAddress remoteAddress, int port, byte[] buffer) throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, remoteAddress, port);
        this.socket.send(packet);
    }

    public DatagramPacket tryRecvData(int timeout) throws IOException {
        //2.创建一个数据包对象接收数据
        byte[] buf = new byte[1024 * 16];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);

        this.socket.setSoTimeout(timeout);
        this.socket.receive(packet);

        return packet;

    }
}
