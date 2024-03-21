package cn.foxtech.device.protocol.v1.tcl.air.adapter.entity;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * TCL空调的PDU格式
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class PduEntity {
    /**
     * 地址码
     */
    private int address = 0x0000;
    /**
     * 消息类型
     */
    private int messageType;
    /**
     * 消息的子类型
     */
    private int messageSubType;
    /**
     * 操作结果
     */
    private int result;
    /**
     * 总帧数
     */
    private int frame;
    /**
     * 帧序号
     */
    private int subFrame;

    /**
     * 消息内容
     */
    private byte[] messageData = new byte[0];

    public static byte[] encodePdu(PduEntity entity) {
        byte[] data = new byte[entity.messageData.length + 9];

        int index = 0;

        // 字节数（1）
        data[index++] = (byte) (data.length - 2);

        // 地址码（2）
        data[index++] = (byte) ((entity.address >> 8) & 0xff);
        data[index++] = (byte) ((entity.address >> 0) & 0xff);

        // 消息（N+5）
        data[index++] = (byte) entity.messageType;
        data[index++] = (byte) entity.messageSubType;
        data[index++] = (byte) entity.result;
        data[index++] = (byte) entity.frame;
        data[index++] = (byte) entity.subFrame;
        System.arraycopy(entity.messageData, 0, data, index, entity.messageData.length);
        index += entity.messageData.length;

        // 校验和（1）
        data[index++] = getVerify(data);

        return encodePdu(data);
    }

    /**
     * 解码
     *
     * @param pdu PDU报文
     * @return 实体
     */
    public static PduEntity decodePdu(byte[] pdu) {
        if (pdu == null || pdu.length < 4) {
            throw new ProtocolException("报文大小小于4");
        }

        // 寻找包头
        int headOffset = searchHead(pdu);
        if (headOffset == -1) {
            throw new ProtocolException("没有找到包头：F4 F5");
        }

        // 寻找包尾
        int tailOffset = searchTail(pdu, headOffset + 2);
        if (tailOffset == -1) {
            throw new ProtocolException("没有找到报包尾：F4 FB");
        }

        // 寻找数据内容
        byte[] data = searchData(pdu, headOffset, tailOffset);

        // 检测：数据长度
        if (data.length < 9) {
            throw new ProtocolException("消息的最小长度，不能小于9");
        }

        int index = 0;

        // 字节数（1）
        if (data[index++] != data.length - 2) {
            throw new ProtocolException("数据长度不正确!");
        }

        // 校验和
        if (data[data.length - 1] != getVerify(data)) {
            throw new ProtocolException("校验和不正确!");
        }

        PduEntity entity = new PduEntity();

        // 地址码（2）
        entity.address = (data[index++] & 0xff) * 0x100 + (data[index++] & 0xff);

        // 消息（N+5）
        entity.messageType = data[index++] & 0xff;
        entity.messageSubType = data[index++] & 0xff;
        entity.result = data[index++] & 0xff;
        entity.frame = data[index++] & 0xff;
        entity.subFrame = data[index++] & 0xff;
        entity.messageData = new byte[data.length - 9];

        // 复制数据
        System.arraycopy(data, index, entity.messageData, 0, entity.messageData.length);

        return entity;
    }


    private static int searchHead(byte[] pdu) {
        for (int i = 0; i < pdu.length - 1; i++) {
            if ((pdu[i] == (byte) 0xf4) && (pdu[i + 1] == (byte) 0xf5)) {
                return i;
            }
        }

        return -1;
    }

    private static int searchTail(byte[] pdu, int offset) {
        for (int i = offset; i < pdu.length - 1; i++) {
            // 检测：是否为两个f4
            if ((pdu[i] == (byte) 0xf4) && (pdu[i + 1] == (byte) 0xf4)) {
                i++;
                continue;
            }

            // 检测：是否为f4 fb
            if ((pdu[i] == (byte) 0xf4) && (pdu[i + 1] == (byte) 0xfb)) {
                return i;
            }
        }

        return -1;
    }

    private static byte[] searchData(byte[] pdu, int headOffset, int tailOffset) {
        // 计算数据的长度
        int length = 0;
        for (int i = headOffset + 2; i < tailOffset; i++) {
            // 检测：是否为两个f4
            if ((pdu[i] == (byte) 0xf4) && (pdu[i + 1] == (byte) 0xf4)) {
                i++;
                length++;
                continue;
            }

            length++;

        }

        // 分配空间
        byte[] data = new byte[length];

        // 复制数据
        length = 0;
        for (int i = headOffset + 2; i < tailOffset; i++) {
            // 检测：是否为两个f4
            if ((pdu[i] == (byte) 0xf4) && (pdu[i + 1] == (byte) 0xf4)) {
                i++;
                data[length++] = pdu[i];
                continue;
            }

            data[length++] = pdu[i];

        }

        return data;
    }

    private static byte[] encodePdu(byte[] data) {
        // 计算数据的长度
        int length = 0;
        for (int i = 0; i < data.length; i++) {
            length++;

            // 检测：是否为两个f4
            if (data[i] == (byte) 0xf4) {
                length++;
                continue;
            }
        }

        // 分配空间
        byte[] pdu = new byte[length + 4];

        // 包头
        pdu[0] = (byte) 0xf4;
        pdu[1] = (byte) 0xf5;

        // 数据
        for (int i = 0; i < data.length; i++) {
            pdu[i + 2] = data[i];

            // 检测：是否为两个f4
            if (pdu[i + 2] == (byte) 0xf4) {
                i++;
                pdu[i + 2] = (byte) 0xf4;
                continue;
            }
        }

        // 包尾
        pdu[pdu.length - 2] = (byte) 0xf4;
        pdu[pdu.length - 1] = (byte) 0xfb;

        return pdu;
    }

    private static byte getVerify(byte[] data) {
        int sum = 0;
        for (int i = 0; i < data.length - 1; i++) {
            sum += data[i] & 0xff;
        }

        return (byte) sum;
    }
}
