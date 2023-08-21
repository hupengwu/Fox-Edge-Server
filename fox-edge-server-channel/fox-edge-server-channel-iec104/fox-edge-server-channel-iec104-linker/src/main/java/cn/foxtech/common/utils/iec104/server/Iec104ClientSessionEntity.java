package cn.foxtech.common.utils.iec104.server;

import cn.foxtech.device.protocol.iec104.core.entity.ApduEntity;
import cn.foxtech.device.protocol.iec104.core.entity.IControlEntity;
import cn.foxtech.device.protocol.iec104.core.entity.SControlEntity;
import cn.foxtech.device.protocol.iec104.core.enums.FrameTypeEnum;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * IEC104网络层次模型：
 * 1、连接层：首先建立TCP连接，后面需要TCP层心跳来维持Socket连接
 * 2、IEC104链路层：然后建立链路，后面需要Linker层的心跳来维持Linker连接
 * 3、IEC104会话层：真正跟从站进行信息交换的操作，特点是一问多答，双向问答
 * 方案：因为IEC104的上述会话方式，才会用会话实体来记录一次对话
 */
@Data
public class Iec104ClientSessionEntity {
    /**
     * 会话的UUID
     */
    private String uuid;

    /**
     * 主站自己的发送序号
     */
    private short lastRequest = 0;

    // 最近收到响应报文时间
    private long lastRespond = 0;

    /**
     * 发送的会话
     */
    private ApduEntity request;

    /**
     * 等待的I帧结束还是S帧结束:true为等待I帧结束，false为等待S帧结束
     */
    private FrameTypeEnum waitFrameType = FrameTypeEnum.I_FORMAT;

    /**
     * 等待的I帧结束原因:出现这些原因之一时，判定为流程终止
     */
    private Set<Integer> endFlag = new CopyOnWriteArraySet<>();

    /**
     * 接收到的一组会话
     */
    private List<ApduEntity> responds = new CopyOnWriteArrayList<>();


    public void increaseLastSend() {
        this.lastRequest++;
    }

    /**
     * 复位会话，为下一次会话做准备
     */
    public List<ApduEntity> resetSession() {
        List<ApduEntity> result = this.responds;

        this.request = null;
        this.responds = new CopyOnWriteArrayList<>();
        this.endFlag.clear();

        return result;
    }

    /**
     * 会话是否空闲，也就是可以发送对话
     *
     * @return 是否空闲
     */
    public boolean isIdle() {
        return this.request == null && this.responds.isEmpty();
    }

    public void respondSession(ApduEntity entity) throws Exception {
        // S帧结束的场景
        if (FrameTypeEnum.S_FORMAT.equals(this.waitFrameType)) {
            SControlEntity control = (SControlEntity) entity.getControl();
            short accept = control.getAccept();

            // 初始化会话列表
            if (this.request == null) {
                throw new Exception("该Send未被使用！");
            }

            // 初始帧：尚未使用，则直接添加到末尾
            this.responds.add(entity);
            this.lastRespond = System.currentTimeMillis();
            return;
        }

        // 检查：是否为I帧
        if (FrameTypeEnum.I_FORMAT.equals(this.waitFrameType)) {
            IControlEntity control = (IControlEntity) entity.getControl();
            short accept = control.getAccept();
            short send = control.getSend();

            // 初始化会话列表
            if (this.request == null) {
                throw new Exception("该Send未被使用！");
            }

            // 初始帧：尚未使用，则直接添加到末尾
            if (this.responds.isEmpty()) {
                this.responds.add(entity);
                this.lastRespond = System.currentTimeMillis();
                return;
            }


            // 末尾帧：检查send的连续性，然后追加到末尾
            ApduEntity lastEntity = this.responds.get(this.responds.size() - 1);
            IControlEntity lastControl = (IControlEntity) lastEntity.getControl();
            if (lastControl.getSend() + 1 != send) {
                throw new Exception("跟上一帧的Send不连续，Send=" + control.getSend());
            }

            this.responds.add(entity);
            this.lastRespond = System.currentTimeMillis();
            return;
        }
    }
}
