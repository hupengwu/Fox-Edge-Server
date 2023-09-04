package cn.foxtech.common.utils.iec104.server.handler;

import cn.foxtech.device.protocol.v1.iec104.core.common.IEC104Constant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 拆包和沾包的预处理：解决TCP 拆包和沾包的问题
 */
public class BeforePackHandler extends ByteToMessageDecoder {
    /**
     * 最小长度(1+1+4) 1：起始字符，1：后续长度 4：后续内容
     */
    private static final int MIN_LENGTH = 6;

    /**
     * @param ctx 上下文
     * @param in 輸入
     * @param out 输出
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out){
        // 可读性检查：可读性数据不构成最小包，直接退出，继续等待后续报文的到达。
        if (in.readableBytes() < MIN_LENGTH) {
            return;
        }

        // 标记当前缓存位置：因为下面试读取一部分数据后，要重新回退到这个位置
        in.markReaderIndex();
        // 试试读包头和长度
        int head = in.readByte() & 0xff;
        int length = in.readByte() & 0xff;
        // 恢复位置
        in.resetReaderIndex();


        // 检查：刚才试读到的头是否为指定字符0x68
        if (head != IEC104Constant.HEAD_DATA) {
            /**
             * 缓冲区不以0x68开头，说明要么对端不是IEC104的通信设备，要么对端传输出现了异常
             * 因为切包的过程都在本函数完成，从链路连接开始，不管是完整包，粘包，切包，一定是以0x68开头
             * 所以，此时主动断开重连，重新开始发起正常的IEC104会话
             */
            ctx.close();
            return;
        }

        // 检查：已经到达的数据长度，是否超过刚才试读到的长度+2，如果小于，则说明这个包还不完整，继续等待后续内容到达
        if (in.readableBytes() < length + 2) {
            return;
        }

        // 真正读取完整帧的报文，并放入到下一环去处理，至于没读取的后半截，继续后续流程处理
        ByteBuf data = in.readBytes(length + 2);
        out.add(data);
    }

}
