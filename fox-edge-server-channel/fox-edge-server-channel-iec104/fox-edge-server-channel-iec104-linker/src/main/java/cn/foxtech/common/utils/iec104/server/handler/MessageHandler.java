package cn.foxtech.common.utils.iec104.server.handler;

import cn.foxtech.common.utils.iec104.server.Iec104ClientLinkerEntity;
import cn.foxtech.common.utils.iec104.server.Iec104ClientLinkerManager;
import cn.foxtech.device.protocol.iec104.core.encoder.BasicSessionEncoder;
import cn.foxtech.device.protocol.iec104.core.encoder.MessageUtils;
import cn.foxtech.device.protocol.iec104.core.entity.*;
import cn.foxtech.device.protocol.iec104.core.enums.CotReasonEnum;
import cn.foxtech.device.protocol.iec104.core.enums.FrameTypeEnum;
import cn.foxtech.device.protocol.iec104.core.enums.UControlTypeEnum;
import cn.foxtech.device.protocol.iec104.core.entity.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息处理器
 */
public class MessageHandler extends SimpleChannelInboundHandler<ApduEntity> {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ApduEntity msg) throws Exception {
        try {
            if (msg.getControl() instanceof UControlEntity) {
                this.receiveUFrame(ctx, msg);
            }
            if (msg.getControl() instanceof SControlEntity) {
                this.receiveSFrame(ctx, msg);
            }
            if (msg.getControl() instanceof IControlEntity) {
                this.receiveIFrame(ctx, msg);
            }
        } catch (Exception e) {
            LOGGER.info("消息处理异常：" + e.getMessage());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    /**
     * I帧格式的处理
     *
     * @param ctx
     * @param apduEntity
     * @throws Exception
     */
    private void receiveIFrame(ChannelHandlerContext ctx, ApduEntity apduEntity) throws Exception {
        IControlEntity control = (IControlEntity) apduEntity.getControl();
        AsduEntity asdu = apduEntity.getAsdu();
        LOGGER.info("收到I帧，控制域：Accept=" + control.getAccept() //
                + "，Send=" + control.getSend() //
                + "，类型标识=" + MessageUtils.getTypeIdMessage(asdu.getTypeId()) //
                + "，可变结构限定词=" + asdu.getVsq() //
                + "，传输原因=" + MessageUtils.getReasonMessage(asdu.getCot().getReason()) + " " + asdu.getCot() //
                + "，公共地址=" + asdu.getCommonAddress() //
        );

        // 发送确认报文：告诉从站，这帧数据，已经收到
        ctx.channel().writeAndFlush(BasicSessionEncoder.encodeSFrameRespond(control.getSend()));

        // 查询链路实体
        Iec104ClientLinkerEntity linkerEntity = Iec104ClientLinkerManager.queryEntity(ctx.channel().remoteAddress());
        if (linkerEntity == null) {
            return;
        }

        // 查询：是否是等待I帧结束会话
        if (!FrameTypeEnum.I_FORMAT.equals(linkerEntity.getSession().getWaitFrameType())) {
            return;
        }

        // 检查：会话是否结束，打印日志
        if (CotReasonEnum.isEnd(apduEntity.getAsdu().getCot().getReason())) {
            int typeId = apduEntity.getAsdu().getTypeId();
            CotEntity cot = apduEntity.getAsdu().getCot();
            LOGGER.info("I帧会话[" + MessageUtils.getTypeIdMessage(typeId) + "]返回结束，" + cot.toString());
        }

        // 保存收到的会话报文
        Iec104ClientLinkerManager.insertApdu4IFrameRespond(ctx.channel(), apduEntity);
    }

    /**
     * U帧格式的处理
     *
     * @param ctx
     * @param apduEntity
     */
    private void receiveUFrame(ChannelHandlerContext ctx, ApduEntity apduEntity) throws Exception {
        UControlEntity control = (UControlEntity) apduEntity.getControl();
        LOGGER.info("收到U帧:Accept=" + control.getValue());

        if (control.getValue() == UControlTypeEnum.TESTFR_YES) {
            LOGGER.info("收到测试确认指令");

            // 更新链路的心跳时间
            Iec104ClientLinkerManager.updateEntity4HeartBeat(ctx.channel());
            return;
        }

        if (control.getValue() == UControlTypeEnum.STOPDT_YES) {
            LOGGER.info("收到停止确认指令");

            // 标识：IEC104的链路层已经断开
            Iec104ClientLinkerManager.updateEntity4IsNotLinked(ctx.channel());
            return;
        }

        if (control.getValue() == UControlTypeEnum.STARTDT_YES) {
            LOGGER.info("收到启动指令确认指令");

            // 标识：IEC104的链路层已经建立
            Iec104ClientLinkerManager.updateEntity4IsLinked(ctx.channel());
            return;
        }
    }

    /**
     * S帧格式的处理
     *
     * @param ctx
     * @param apduEntity
     */
    private void receiveSFrame(ChannelHandlerContext ctx, ApduEntity apduEntity) throws Exception {
        SControlEntity control = (SControlEntity) apduEntity.getControl();
        LOGGER.info("收到S帧:Accept=" + control.getAccept());

        // 查询链路实体
        Iec104ClientLinkerEntity linkerEntity = Iec104ClientLinkerManager.queryEntity(ctx.channel().remoteAddress());
        if (linkerEntity == null) {
            return;
        }

        // 查询：是否是等待S帧结束会话
        if (!FrameTypeEnum.S_FORMAT.equals(linkerEntity.getSession().getWaitFrameType())) {
            return;
        }

        // 保存收到的会话报文
        Iec104ClientLinkerManager.insertApdu4SFrameRespond(ctx.channel(), apduEntity);
    }
}
