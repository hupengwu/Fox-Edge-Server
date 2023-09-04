package cn.foxtech.common.utils.iec104.server;

import cn.foxtech.device.protocol.v1.iec104.core.entity.ApduEntity;
import io.netty.channel.Channel;
import lombok.Data;

import java.net.SocketAddress;
import java.util.List;

/**
 * IEC104会话方式：
 * 1、全双工通信方式，即主站可以主动对从站发起问答，从站也可以对主站发起问答
 * 2、一问多答方式：即主站/从站向对方发起的一个问答，对方可以同时分多个报文顺序回答
 * 3、多任务并发方式：即主站可以同时对从站发起多个问答会话
 * 4、IEC104的网络层次模型：TCP连接层/IEC104链路层/会话层
 * 方案：因为IEC104的上述会话方式，才会添加Link记录链路状态
 */
@Data
public class Iec104ClientLinkerEntity {
    /**
     * 会话
     */
    private final Iec104ClientSessionEntity session = new Iec104ClientSessionEntity();
    /**
     * 远端地址
     */
    private SocketAddress remoteAddress;
    /**
     * channel
     */
    private Channel channel;
    /**
     * 是否建立104链路
     */
    private boolean linked = false;
    /**
     * 是上次心跳时间
     */
    private long heartbeat;

    /**
     * 会话是否空闲，也就是可以发送对话
     *
     * @return 是否成功
     */
    public boolean isIdleSession() {
        if (this.channel == null) {
            return false;
        }

        if (this.linked == false) {
            return false;
        }

        return this.session.isIdle();
    }

    public List<ApduEntity> resetSession() {
        return this.session.resetSession();
    }

    public void respondSession(ApduEntity entity) throws Exception {
        if (this.channel == null) {
            throw new Exception("socket尚未建立连接！");
        }
        if (this.linked == false) {
            throw new Exception("Link尚未建立成功！");
        }

        this.session.respondSession(entity);
    }
}
