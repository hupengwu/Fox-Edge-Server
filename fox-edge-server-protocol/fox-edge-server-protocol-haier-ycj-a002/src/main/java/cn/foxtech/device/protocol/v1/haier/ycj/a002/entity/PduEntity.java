package cn.foxtech.device.protocol.v1.haier.ycj.a002.entity;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 海尔空调的PDU格式
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class PduEntity {
    /**
     * 上位机地址：默认0X00FF
     */
    private int hostAddr = 0xff;
    /**
     * 目的地址：00代表总线上的全体设备
     */
    private int devAddr = 0;
    /**
     * 命令字
     */
    private int cmd = 0;
    /**
     * 数据
     */
    private byte[] data = new byte[0];

    public static byte[] encodePdu(PduEntity entity) {
        byte[] pdu = new byte[entity.data.length + 10];

        int index = 0;

        // 报头
        pdu[index++] = (byte) 0xf4;
        pdu[index++] = (byte) 0xf5;

        // 长度
        pdu[index++] = (byte) (entity.data.length + 6);

        // 源端地址
        pdu[index++] = (byte) ((entity.hostAddr >> 8) & 0xff);
        pdu[index++] = (byte) ((entity.hostAddr >> 0) & 0xff);

        // 宿端地址
        pdu[index++] = (byte) ((entity.devAddr >> 8) & 0xff);
        pdu[index++] = (byte) ((entity.devAddr >> 0) & 0xff);

        // 命令字
        pdu[index++] = (byte) entity.cmd;

        // 数据
        System.arraycopy(entity.data, 0, pdu, index, entity.data.length);
        index += entity.data.length;

        // 校验和
        pdu[index++] = getVerify(pdu);

        // 包尾
        pdu[index++] = (byte) 0xfb;


        return pdu;
    }

    public static PduEntity decodePdu(byte[] pdu) {
        if (pdu == null || pdu.length < 10) {
            throw new ProtocolException("报文大小小于10");
        }


        // 报头
        if ((pdu[0] != (byte) 0xf4) || (pdu[1] != (byte) 0xf5)) {
            throw new ProtocolException("包头必须为 F4 F5");
        }

        // 长度
        int length = pdu[2] & 0xff;
        if (pdu.length < length + 4 || length < 6) {
            throw new ProtocolException("帧长度不正确");
        }

        // 报尾
        if (pdu[length + 3] != (byte) 0xfb) {
            throw new ProtocolException("包尾必须为 FB");
        }

        // 预分配空间
        PduEntity entity = new PduEntity();
        entity.setData(new byte[length - 6]);

        // 源端地址
        entity.hostAddr = (pdu[3] & 0xff) * 0x100 + (pdu[4] & 0xff);

        // 宿端地址
        entity.devAddr = (pdu[5] & 0xff) * 0x100 + (pdu[6] & 0xff);

        // 命令字
        entity.cmd = pdu[7] & 0xff;

        // 数据
        System.arraycopy(pdu, 8, entity.data, 0, entity.data.length);

        // 校验和
        if (pdu[length + 2] != getVerify(pdu)) {
            throw new ProtocolException("校验和不正确");
        }


        return entity;
    }

    private static byte getVerify(byte[] pdu) {
        int sum = 0;
        for (int i = 0; i < pdu.length - 4; i++) {
            sum += pdu[i + 2] & 0xff;
        }

        return (byte) sum;
    }
}
