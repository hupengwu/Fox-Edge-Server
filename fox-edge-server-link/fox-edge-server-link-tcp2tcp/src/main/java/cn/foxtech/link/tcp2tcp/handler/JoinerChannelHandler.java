package cn.foxtech.link.tcp2tcp.handler;

import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JoinerChannelHandler {
    private final Map<SocketAddress, ChannelHandlerContext> northChannel = new ConcurrentHashMap<>();

    @Getter
    private ChannelHandlerContext southChannel = null;

    public void insertNorthChannel(ChannelHandlerContext ctx) {
        this.northChannel.put(ctx.channel().remoteAddress(), ctx);
    }

    public void removeNorthChannel(ChannelHandlerContext ctx) {
        this.northChannel.remove(ctx.channel().remoteAddress());
    }

    public void insertSouthChannel(ChannelHandlerContext ctx) {
        this.southChannel = ctx;
    }

    public void removeSouthChannel(ChannelHandlerContext ctx) {
        this.southChannel = null;
    }

    public void send2South(Object msg) {
        if (this.southChannel == null) {
            return;
        }

        this.southChannel.channel().writeAndFlush(msg);
    }

    public void send2North(Object msg) {
        for (SocketAddress skt : this.northChannel.keySet()) {
            ChannelHandlerContext ctx = this.northChannel.get(skt);
            ctx.channel().writeAndFlush(msg);
        }
    }
}
