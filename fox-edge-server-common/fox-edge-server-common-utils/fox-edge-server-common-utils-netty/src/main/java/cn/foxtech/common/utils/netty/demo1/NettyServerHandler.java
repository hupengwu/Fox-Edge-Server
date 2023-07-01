package cn.foxtech.common.utils.netty.demo1;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

public class NettyServerHandler extends SimpleChannelInboundHandler<MessageProtocol> {

    private int count;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageProtocol msg) throws Exception {

        String message = new String(msg.getContent(), CharsetUtil.UTF_8);
        System.out.println("服务端接收到数据" + message);
        System.out.println("服务端接收数据长度" + msg.getLen());
        System.out.println("接收次数" + (++this.count));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
