package cn.foxtech.common.utils.netty.client;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ChannelClientManager {

    private static final Map<SocketAddress, Channel> channelMap = new HashMap<>();


    public static synchronized List<SocketAddress> waitRemoteAddress() {
        List<SocketAddress> waitRemoteAddress = new ArrayList<>();
        for (Map.Entry<SocketAddress, Channel> enty : channelMap.entrySet()) {
            if (enty.getValue() == null) {
                waitRemoteAddress.add(enty.getKey());
            }
        }

        return waitRemoteAddress;

    }

    public static synchronized void registerRemoteAddress(String host, int port) {
        SocketAddress remoteAddress = new InetSocketAddress(host, port);
        registerRemoteAddress(remoteAddress);
    }

    public static synchronized void registerRemoteAddress(SocketAddress remoteAddress) {
        if (channelMap.containsKey(remoteAddress)) {
            return;
        }

        // 初始化记录
        channelMap.put(remoteAddress, null);
    }

    public static synchronized void channelActive(Channel channel) {
        SocketAddress remoteAddress = channel.remoteAddress();
        if (!channelMap.containsKey(remoteAddress)) {
            return;
        }

        // 记录channel
        channelMap.put(remoteAddress, channel);
    }

    public static synchronized void channelInactive(Channel channel) {
        SocketAddress remoteAddress = channel.remoteAddress();
        if (!channelMap.containsKey(remoteAddress)) {
            return;
        }

        // 注销channel
        channelMap.put(remoteAddress, null);
    }

    public static void schedule() {
        // 创建线程：后台自动连接
        createThread4Connect();
    }

    private static void createThread4Connect() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1 * 1000);
                        List<SocketAddress> waitRemoteAddress = waitRemoteAddress();
                        if (waitRemoteAddress == null) {
                            break;
                        }

                        // 创建连接
                        for (SocketAddress socketAddress : waitRemoteAddress) {
                            ChannelClientFactory.getInstance().createClient(socketAddress);
                        }

                        Thread.sleep(10 * 1000);
                    } catch (Throwable e) {
                    //    LOGGER.error("createThread4Connect", e);
                    }
                }
            }
        }, 0, TimeUnit.MILLISECONDS);
        scheduledExecutorService.shutdown();
    }


}
