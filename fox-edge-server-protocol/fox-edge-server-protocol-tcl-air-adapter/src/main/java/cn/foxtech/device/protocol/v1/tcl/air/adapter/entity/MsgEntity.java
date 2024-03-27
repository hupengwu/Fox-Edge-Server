package cn.foxtech.device.protocol.v1.tcl.air.adapter.entity;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class MsgEntity {
    /**
     * 消息类型
     */
    private int type = 0;
    /**
     * 消息子类型
     */
    private int subType = 0;
    /**
     * 操作结果
     */
    private int result = 0;
    /**
     * 数据
     */
    private byte[] data = new byte[0];

    public static byte[] encode(MsgEntity entity) {
        byte[] msg = new byte[entity.data.length + 3];
        msg[0] = (byte) entity.type;
        msg[1] = (byte) entity.subType;
        msg[2] = (byte) entity.result;
        System.arraycopy(entity.data, 0, msg, 3, entity.data.length);
        return msg;
    }

    public static MsgEntity decode(byte[] msg) {
        if (msg.length < 3) {
            throw new ProtocolException("消息长度小于3");
        }

        MsgEntity entity = new MsgEntity();
        entity.type = msg[0] & 0xff;
        entity.subType = msg[1] & 0xff;
        entity.result = msg[2] & 0xff;

        entity.data = new byte[msg.length - 3];
        System.arraycopy(msg, 3, entity.data, 0, entity.data.length);
        return entity;
    }
}

