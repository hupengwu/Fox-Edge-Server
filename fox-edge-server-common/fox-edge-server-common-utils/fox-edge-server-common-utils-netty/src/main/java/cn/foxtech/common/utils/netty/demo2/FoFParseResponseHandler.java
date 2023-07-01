package cn.foxtech.common.utils.netty.demo2;

import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: CopyRight (c) 2020-2035</p>
 * <p>Company: lehoon Co. LTD.</p>
 * <p>Author: lehoon</p>
 * <p>Date: 2021/12/3 14:47</p>
 */
public class FoFParseResponseHandler extends ChannelInboundHandlerAdapter {
    private String userId;
    private IMessageListener messageListener;

    public FoFParseResponseHandler() {
    }

    public FoFParseResponseHandler(String userId) {
        this.userId = userId;
    }

    public FoFParseResponseHandler(String userId, IMessageListener messageListener) {
        this.userId = userId;
        this.messageListener = messageListener;
    }
//
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        if (messageListener != null) messageListener.onConnect(userId);
//        if (StringUtils.isBlank(userId)) {
//            ctx.close();
//        }
//
//        String message = MessageHelper.emailParseRequest(userId);
//        ByteBuf byteBuf = Unpooled.copiedBuffer(message.getBytes());
//        ctx.writeAndFlush(byteBuf);
//        if (messageListener != null) messageListener.onWrite(message.getBytes(), message.getBytes().length);
//    }
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);
//        EmailParseResponse response = MessageHelper.emailParseResponse((String) msg);
//        if (response == null) {
//            return;
//        }
//
//        if (messageListener == null) return;
//        if(messageListener.onRead(userId, response) == MessageCallBackAction.SHUTDOWN) {
//            ctx.close();
//        }
//    }
//
//    @Override
//    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//        super.exceptionCaught(ctx, cause);
//        FoFParseClientFactory.getInstance().disConnect(ctx.channel());
//    }
//
//    @Override
//    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
//        super.handlerRemoved(ctx);
//        if (messageListener != null) {
//            messageListener.onClose(userId);
//        }
//        FoFParseClientFactory.getInstance().disConnect(ctx.channel());
//    }

    public void setMessageListener(IMessageListener messageListener) {
        this.messageListener = messageListener;
    }
}
