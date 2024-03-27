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
     * 消息内容
     */
    private byte[] data = new byte[0];

    public static byte[] encodePdu(PduEntity entity) {
        byte[] data = new byte[entity.data.length + 4];

        int index = 0;

        // 字节数（1）
        data[index++] = (byte) (data.length - 2);

        // 地址码（2）
        data[index++] = (byte) ((entity.address >> 8) & 0xff);
        data[index++] = (byte) ((entity.address >> 0) & 0xff);

        // 数据（N）
        System.arraycopy(entity.data, 0, data, index, entity.data.length);
        index += entity.data.length;

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
        byte[] dataArea = searchData(pdu, headOffset, tailOffset);

        // 检测：数据区长度（1+2+N+1）
        if (dataArea.length < 4) {
            throw new ProtocolException("消息的最小长度，不能小于4");
        }

        int index = 0;

        // 字节数（1）
        if (dataArea[index++] != dataArea.length - 2) {
            throw new ProtocolException("数据长度不正确!");
        }

        // 校验和（1）
        if (dataArea[dataArea.length - 1] != getVerify(dataArea)) {
            throw new ProtocolException("校验和不正确!");
        }

        PduEntity entity = new PduEntity();

        // 地址码（2）
        entity.address = (dataArea[index++] & 0xff) * 0x100 + (dataArea[index++] & 0xff);

        // 消息
        entity.data = new byte[dataArea.length - 4];

        // 复制数据
        System.arraycopy(dataArea, index, entity.data, 0, entity.data.length);

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
