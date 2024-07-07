package cn.foxtech.channel.udp.server.service;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.Map;
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

    private final Map<String, Long> key2tik = new ConcurrentHashMap<>();

    public void setServiceKey(InetSocketAddress skt, ChannelHandlerContext ctx, String serviceKey) {
        this.skt2key.put(skt, serviceKey);
        this.key2ctx.put(serviceKey, ctx);
        this.key2skt.put(serviceKey, skt);
        this.key2tik.put(serviceKey, System.currentTimeMillis());
    }

    public void setTime(String serviceKey) {
        this.key2tik.put(serviceKey, System.currentTimeMillis());
    }

    public void removeTimeout(long timeout) {
        Long currTime = System.currentTimeMillis();
        for (String key : this.key2tik.keySet()) {
            Long active = this.key2tik.get(key);

            // 检查：是否超时
            if (currTime - active < timeout) {
                continue;
            }

            InetSocketAddress skt = this.key2skt.get(key);
            if (skt != null) {
                this.skt2key.remove(skt);
            }

            this.key2ctx.remove(key);
            this.key2skt.remove(key);
            this.key2tik.remove(key);
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
