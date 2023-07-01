package cn.foxtech.common.utils.netty.demo2;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.LineEncoder;
import io.netty.handler.codec.string.LineSeparator;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: CopyRight (c) 2020-2035</p>
 * <p>Company: lehoon Co. LTD.</p>
 * <p>Author: lehoon</p>
 * <p>Date: 2021/12/3 14:35</p>
 */
public class FoFNewNioChannelInitializer extends ChannelInitializer<NioSocketChannel> {
    private String userId;
    private IMessageListener messageListener;

    public FoFNewNioChannelInitializer() {
    }

    public FoFNewNioChannelInitializer(String userId) {
        this.userId = userId;
    }

    public FoFNewNioChannelInitializer(String userId, IMessageListener messageListener) {
        this.userId = userId;
        this.messageListener = messageListener;
    }

    @Override
    protected void initChannel(NioSocketChannel ch) throws Exception {
        ch.pipeline().addLast(new LineEncoder(LineSeparator.WINDOWS));
        ch.pipeline().addLast(new StringEncoder(CharsetUtil.UTF_8));
        ch.pipeline().addLast(new StringDecoder(CharsetUtil.UTF_8));
        ch.pipeline().addLast(new FoFParseResponseHandler(userId, messageListener));
    }
}
