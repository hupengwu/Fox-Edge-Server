/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.channel.socket.core.service;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 连接状态的管理
 * 设备主动上行连接的时候，它能够直接提供的是自己的IP+PORT信息，所以用SocketAddress来查询信息
 */
public class ChannelManager {
    private final Map<SocketAddress, ChannelHandlerContext> skt2ctx = new ConcurrentHashMap<>();
    private final Map<SocketAddress, String> skt2key = new ConcurrentHashMap<>();
    private final Map<String, ChannelHandlerContext> key2ctx = new ConcurrentHashMap<>();

    public void insert(ChannelHandlerContext ctx) {
        this.skt2ctx.put(ctx.channel().remoteAddress(), ctx);
    }

    public Set<String> getServiceKeys() {
        return this.key2ctx.keySet();
    }

    public void setServiceKey(ChannelHandlerContext ctx, String serviceKey) {
        this.skt2key.put(ctx.channel().remoteAddress(), serviceKey);
        this.key2ctx.put(serviceKey, ctx);
    }

    public ChannelHandlerContext getContext(String serviceKey) {
        return this.key2ctx.get(serviceKey);
    }

    public String getServiceKey(SocketAddress sktAddr) {
        return this.skt2key.get(sktAddr);
    }

    public ChannelHandlerContext getContext(SocketAddress sktAddr) {
        return this.skt2ctx.get(sktAddr);
    }

    public void remove(SocketAddress sktAddr) {
        String key = this.skt2key.get(sktAddr);
        if (key != null) {
            this.key2ctx.remove(key);
        }

        this.skt2ctx.remove(sktAddr);
        this.skt2key.remove(sktAddr);
    }

    public void remove(String serviceKey) {
        ChannelHandlerContext ctx = this.key2ctx.get(serviceKey);
        if (ctx != null) {
            this.skt2ctx.remove(ctx.channel().remoteAddress());
            this.skt2key.remove(ctx.channel().remoteAddress());
        }

        this.key2ctx.remove(serviceKey);

    }

}
