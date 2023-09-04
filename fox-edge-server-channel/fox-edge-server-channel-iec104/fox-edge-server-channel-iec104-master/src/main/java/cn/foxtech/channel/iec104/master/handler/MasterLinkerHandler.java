package cn.foxtech.channel.iec104.master.handler;

import cn.foxtech.common.utils.iec104.server.Iec104ClientLinkerEntity;
import cn.foxtech.common.utils.iec104.server.Iec104ClientLinkerHandler;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import cn.foxtech.device.protocol.v1.iec104.core.encoder.MessageUtils;
import cn.foxtech.device.protocol.v1.iec104.core.entity.ApduEntity;
import cn.foxtech.device.protocol.v1.iec104.core.entity.IControlEntity;
import cn.foxtech.device.protocol.v1.iec104.core.entity.SControlEntity;
import cn.foxtech.device.protocol.v1.iec104.core.enums.FrameTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MasterLinkerHandler extends Iec104ClientLinkerHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(MasterLinkerHandler.class);


    /**
     * IEC104链路建立
     */
    @Override
    public void linkConnected(Iec104ClientLinkerEntity linkerEntity) {
        LOGGER.info("IEC104链路连接:" + linkerEntity.getChannel().remoteAddress());
    }

    /**
     * 会话结束
     *
     * @param linkerEntity
     */
    @Override
    public void finishedRespond(Iec104ClientLinkerEntity linkerEntity) {
        LOGGER.info("完成一个会话:" + linkerEntity.getChannel().remoteAddress());

        List<ApduEntity> responds = linkerEntity.getSession().getResponds();
        if (responds.isEmpty()) {
            return;
        }

        String key = linkerEntity.getSession().getUuid();
        FrameTypeEnum waitFrameType = linkerEntity.getSession().getWaitFrameType();
        ApduEntity request = linkerEntity.getSession().getRequest();
        ApduEntity respond = responds.get(responds.size() - 1);
        if (request == null) {
            return;
        }

        // 场景1：等待I帧结束
        if (FrameTypeEnum.I_FORMAT.equals(waitFrameType)) {
            if (!(request.getControl() instanceof IControlEntity)) {
                return;
            }
            if (!(respond.getControl() instanceof IControlEntity)) {
                return;
            }

            LOGGER.info("会话结束：Accept=" + ((IControlEntity) respond.getControl()).getAccept() //
                    + "，Send=" + ((IControlEntity) respond.getControl()).getSend() //
                    + "，类型标识=" + MessageUtils.getTypeIdMessage(respond.getAsdu().getTypeId()) //
                    + "，可变结构限定词=" + respond.getAsdu().getVsq() //
                    + "，传输原因=" + MessageUtils.getReasonMessage(respond.getAsdu().getCot().getReason()) + " " + respond.getAsdu().getCot() //
                    + "，公共地址=" + respond.getAsdu().getCommonAddress() //
            );
        }

        // 场景2：等待S帧结束
        if (FrameTypeEnum.S_FORMAT.equals(waitFrameType)) {
            if (!(request.getControl() instanceof IControlEntity)) {
                return;
            }
            if (!(respond.getControl() instanceof SControlEntity)) {
                return;
            }

            LOGGER.info("会话结束：Accept=" + ((SControlEntity) respond.getControl()).getAccept());
        }

        // 将数据转移到队列中，通知发送者查询
        SyncFlagObjectMap.inst().notifyDynamic(key, linkerEntity.getSession().getResponds());
    }
}
