package cn.foxtech.channel.tcp.server.service;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelManager {
    private final Map<SocketAddress, ChannelHandlerContext> skt2ctx = new ConcurrentHashMap<>();
    private final Map<SocketAddress, String> skt2key = new ConcurrentHashMap<>();
    private final Map<String,ChannelHandlerContext> key2skt = new ConcurrentHashMap<>();

    public void insert(ChannelHandlerContext ctx) {
        this.skt2ctx.put(ctx.channel().remoteAddress(), ctx);
    }

    public void setServiceKey(ChannelHandlerContext ctx, String serviceKey) {
        this.skt2key.put(ctx.channel().remoteAddress(), serviceKey);
    }

    public ChannelHandlerContext getContext(String serviceKey) {
        return this.key2skt.get(serviceKey);
    }

    public String getServiceKey(ChannelHandlerContext ctx) {
        return this.skt2key.get(ctx.channel().remoteAddress());
    }



    public void remove(ChannelHandlerContext ctx) {
        String key = this.skt2key.get(ctx.channel().remoteAddress());
        if (key != null) {
            this.key2skt.remove(key);
        }

        this.skt2ctx.remove(ctx.channel().remoteAddress());
        this.skt2key.remove(ctx.channel().remoteAddress());
    }

}
