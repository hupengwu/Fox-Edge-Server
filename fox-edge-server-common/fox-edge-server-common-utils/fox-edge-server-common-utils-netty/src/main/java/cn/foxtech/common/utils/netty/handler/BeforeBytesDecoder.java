package cn.foxtech.common.utils.netty.handler;

import cn.foxtech.device.protocol.v1.utils.netty.SplitMessageHandler;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.Setter;

import java.util.List;

/**
 * 拆包和沾包的预处理：解决TCP 拆包和沾包的问题
 */
public class BeforeBytesDecoder extends ByteToMessageDecoder {
    @Setter
    private SplitMessageHandler handler = new SplitMessageHandler();


    /**
     * 对原始流进行完整帧的检查
     * 这是一个同步函数，避免并发的时候，产生多线程的问题。
     *
     * @param ctx 上下文
     * @param in  輸入
     * @param out 输出
     */
    @Override
    protected synchronized void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // 可读性检查：可读性数据不构成最小包，直接退出，继续等待后续报文的到达。
        if (in.readableBytes() < this.handler.getHeaderLength()) {
            return;
        }

        // 标记当前缓存位置：因为下面试读取一部分数据后，要重新回退到这个位置
        in.markReaderIndex();
        // 试试读包头的数据
        int headerLength = this.handler.getHeaderLength();
        for (int index = 0; index < headerLength; index++) {
            this.handler.setHeaderValue(index, in.readByte() & 0xff);
        }
        // 恢复位置
        in.resetReaderIndex();

        // 检测：是否是合法的报文格式
        if (this.handler.isInvalidPack()) {
            /**
             * 非法报文：
             * 要么对端不是对应的协议的通信设备，要么对端传输出现了异常
             * 因为切包的过程都在本函数完成，从链路连接开始，不管是完整包，粘包，切包，一定是该格式约定
             * 所以，此时主动断开重连，重新开始发起正常的会话
             */
            ctx.close();
            return;
        }


        int packLen = this.handler.getPackLength();
        if (packLen == -1) {
            ctx.close();
        }

        // 检查：已经到达的数据长度，是否超过刚才试读到的长度，如果小于，则说明这个包还不完整，继续等待后续内容到达
        if (in.readableBytes() < packLen) {
            return;
        }

        // 真正读取完整帧的报文，并放入到下一环去处理，至于没读取的后半截，继续后续流程处理
        ByteBuf data = in.readBytes(packLen);
        out.add(data);
    }

}
