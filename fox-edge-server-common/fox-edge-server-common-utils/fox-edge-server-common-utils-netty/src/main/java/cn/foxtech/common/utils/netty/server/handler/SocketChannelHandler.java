package cn.foxtech.common.utils.netty.server.handler;

import io.netty.channel.ChannelHandlerContext;

public class SocketChannelHandler {

    /**
     * 客户端与服务端第一次建立连接时 执行
     *
     * @param ctx 上下文
     * @throws Exception 异常
     */
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
    }

    /**
     * 从客户端收到新的数据时，这个方法会在收到消息时被调用
     *
     * @param ctx 上下文
     * @param msg 消息
     */
    public void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    }

    /**
     * 客户端与服务端 断连时 执行
     *
     * @param ctx 上下文
     */
    public void channelInactive(final ChannelHandlerContext ctx) {
    }

    /**
     * 当出现 Throwable 对象才会被调用，即当 Netty 由于 IO 错误或者处理器在处理事件时抛出的异常时
     *
     * @param ctx 上下文
     * @param cause 异常源头
     */
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    }
}
