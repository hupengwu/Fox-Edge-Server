package cn.foxtech.common.utils.netty.demo1;

import io.netty.channel.Channel;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * 连接池：已经连接和等待连接的信息
 */
public class NettyClientConnectPool {
    /**
     * 已经连接成功的Channel
     */
    private final List<Channel> channelList = new ArrayList<>();

    /**
     * 等待连接的目标地址
     */
    private final List<SocketAddress> waitConnectList = new ArrayList<>();

    /**
     * 等待连接的socket
     * @param remoteAddress
     */
    public synchronized void waitConnect(SocketAddress remoteAddress) {
        waitConnectList.add(remoteAddress);
    }

    /**
     * 登记建立连接成功的channel
     *
     * @param channel
     */
    public synchronized void registerConnected(Channel channel) {
        SocketAddress remoteAddress = channel.remoteAddress();

        for (SocketAddress socketAddress : waitConnectList) {
            if (socketAddress.equals(remoteAddress)) {
                channelList.add(channel);
                waitConnectList.remove(socketAddress);
                return;
            }
        }
    }

    /**
     * 等待重新连接
     *
     * @param channel
     */
    public synchronized void waitReconnect(Channel channel) {
        SocketAddress remoteAddress = channel.remoteAddress();

        for (Channel ch : channelList) {
            if (ch.equals(channel)) {
                channelList.remove(channel);
                waitConnectList.add(remoteAddress);
                return;
            }
        }
    }
}
