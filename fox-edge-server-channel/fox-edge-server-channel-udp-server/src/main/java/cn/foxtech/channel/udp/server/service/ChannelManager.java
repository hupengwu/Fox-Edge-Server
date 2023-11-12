package cn.foxtech.channel.udp.server.service;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 连接状态的管理
 * 设备主动上行连接的时候，它能够直接提供的是自己的IP+PORT信息，所以用SocketAddress来查询信息
 */
@Component
public class ChannelManager {
    private final Map<InetSocketAddress, String> skt2key = new ConcurrentHashMap<>();
    private final Map<String, ChannelHandlerContext> key2ctx = new ConcurrentHashMap<>();
    private final Map<String, InetSocketAddress> key2skt = new ConcurrentHashMap<>();

    public void register(InetSocketAddress skt, ChannelHandlerContext ctx, String serviceKey) {
        this.skt2key.put(skt, serviceKey);
        this.key2ctx.put(serviceKey, ctx);
        this.key2skt.put(serviceKey, skt);
    }

    /**
     * 清理不在serviceKeys中的数据
     * @param serviceKeys 来自channel管理器的serviceKeys信息
     */
    public void clearLifeCycle(Set<String> serviceKeys) {
        Set<String> removeKey = new HashSet<>();
        Set<InetSocketAddress> removeSkt = new HashSet<>();

        for (String key : this.key2skt.keySet()) {
            if (serviceKeys.contains(key)) {
                continue;
            }
            // 准备删除的key2skt/key2ctx
            removeKey.add(key);

            // 准备删除的skt2key
            removeSkt.add(this.key2skt.get(key));
        }

        for (String key : removeKey) {
            this.key2skt.remove(key);
            this.key2ctx.remove(key);
        }

        for (InetSocketAddress skt : removeSkt) {
            this.skt2key.remove(skt);
        }
    }

    public ChannelHandlerContext getChannel(String serviceKey) {
        return this.key2ctx.get(serviceKey);
    }

    public InetSocketAddress getAddress(String serviceKey) {
        return this.key2skt.get(serviceKey);
    }

    public String getServiceKey(InetSocketAddress skt) {
        return this.skt2key.get(skt);
    }
}
