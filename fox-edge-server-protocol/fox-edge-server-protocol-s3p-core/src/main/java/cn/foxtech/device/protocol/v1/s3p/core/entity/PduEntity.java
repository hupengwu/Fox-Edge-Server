package cn.foxtech.device.protocol.v1.s3p.core.entity;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.s3p.core.enums.Escape;
import cn.foxtech.device.protocol.v1.utils.Crc16Utils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class PduEntity {
    /**
     * 从机地址
     */
    private int devAddr = 0;
    /**
     * 控制字
     */
    private int cmd = 0;
    /**
     * 数据
     */
    private byte[] data = new byte[0];


    private static byte[] escapeDecode(byte[] srcData, Escape escape) {
        if (Escape.No.equals(escape)) {
            return srcData;
        }

        if (srcData.length < 7) {
            throw new ProtocolException("报文长度小于7");
        }

        if (srcData[0] != 0x7E) {
            throw new ProtocolException("包头必须为0x7E");
        }

        // 计算需要的目标空间
        int dstLength = 1;
        for (int i = 1; i < srcData.length - 2; i++) {
            if (srcData[i] != 0x7D) {
                dstLength++;
                continue;
            }


            i++;

            if (i >= srcData.length) {
                throw new ProtocolException("报文长度异常");
            }

            // 0x7D 0x5E -> 0x7E
            if (srcData[i] == 0x5E) {
                dstLength++;
                continue;
            }
            // 0x7D 0x5D -> 0x7D
            if (srcData[i] == 0x5D) {
                dstLength++;
                continue;
            }
            //	小于0x20的字符使用BIT5置1后前面加0x7D,如0x7D 0x29 -> 0x09
            if (srcData[i] >= 0x20) {
                dstLength++;
                continue;
            }

            throw new ProtocolException("报文字符编码异常");
        }
        // CRC校验，不转义
        dstLength += 2;

        byte[] dstData = new byte[dstLength];

        int index = 0;

        dstData[index++] = 0x7E;

        int length = 0;
        for (int i = 1; i < srcData.length - 2; i++) {
            if (srcData[i] != 0x7D) {
                dstData[index++] = srcData[i];
                continue;
            }


            i++;

            if (i >= srcData.length) {
                throw new ProtocolException("报文长度异常");
            }

            // 0x7D 0x5E -> 0x7E
            if (srcData[i] == 0x5E) {
                dstData[index++] = 0x7E;
                continue;
            }
            // 0x7D 0x5D -> 0x7D
            if (srcData[i] == 0x5D) {
                dstData[index++] = 0x7D;
                continue;
            }
            //	小于0x20的字符使用BIT5置1后前面加0x7D,如0x7D 0x29 -> 0x09
            if (srcData[i] >= 0x20) {
                dstData[index++] = (byte) (srcData[i] & 0x1F);

                continue;
            }

            throw new ProtocolException("报文字符编码异常");
        }
        // CRC校验，不转义
        dstData[dstData.length - 2] = srcData[srcData.length - 2];
        dstData[dstData.length - 1] = srcData[srcData.length - 1];


        return dstData;
    }

    private static byte[] escapeEncode(byte[] srcData, Escape escape) {
        if (Escape.No.equals(escape)) {
            return srcData;
        }


        if (srcData.length < 7) {
            throw new ProtocolException("报文长度小于7");
        }

        if (srcData[0] != 0x7E) {
            throw new ProtocolException("包头必须为0x7E");
        }


        int length = 1;
        for (int i = 1; i < srcData.length - 2; i++) {
            // 0x7E -> 0x7D 0x5E
            if ((srcData[i] == 0x7E) && (!Escape.No.equals(escape))) {
                length += 2;

                continue;
            }
            //	0x7D -> 0x7D 0x5D
            if ((srcData[i] == 0x7D) && (!Escape.No.equals(escape))) {
                length += 2;

                continue;
            }
            if ((srcData[i] < 0x20) && (Escape.Max.equals(escape))) {
                //	小于0x20的字符使用BIT5置1后前面加0x7D,如0x09 -> 0x7D 0x29
                length += 2;

                continue;
            }

            length++;
            continue;
        }
        // CRC校验，不转义
        length += 2;


        byte[] dstData = new byte[length];

        int index = 0;
        dstData[index++] = 0x7E;
        for (int i = 1; i < srcData.length - 2; i++) {
            if ((srcData[i] == 0x7E) && (!Escape.No.equals(escape))) {
                // 0x7E -> 0x7D 0x5E
                dstData[index++] = 0x7D;
                dstData[index++] = 0x5E;

                continue;
            }
            if ((srcData[i] == 0x7D) && (!Escape.No.equals(escape))) {
                //	0x7D -> 0x7D 0x5D
                dstData[index++] = 0x7D;
                dstData[index++] = 0x5D;

                continue;
            }
            if ((srcData[i] < 0x20) && (Escape.Max.equals(escape))) {
                //	小于0x20的字符使用BIT5置1后前面加0x7D,如0x09 -> 0x7D 0x29
                dstData[index++] = 0x7D;

                byte byAt = srcData[i];
                byAt |= 0x20;
                dstData[index++] = byAt;


                continue;
            }

            dstData[index++] = srcData[i];
            continue;
        }

        // CRC校验，不转义
        dstData[dstData.length - 2] = srcData[srcData.length - 2];
        dstData[dstData.length - 1] = srcData[srcData.length - 1];

        return dstData;
    }

    public static PduEntity decodePdu(byte[] pdu) {
        return decodePdu(pdu, Escape.Min);
    }

    public static PduEntity decodePdu(byte[] pdu, Escape escape) {
        // 转义
        pdu = escapeDecode(pdu, escape);

        int nSize = pdu.length;
        if (nSize < 7) {
            throw new ProtocolException("报文长度小于7");
        }

        int index = 0;

        // 头标(1字节)
        if (pdu[index++] != (byte) 0x7E) {
            throw new ProtocolException("包头必须为0x7E");
        }

        PduEntity entity = new PduEntity();

        // 地址域(2字节)
        entity.devAddr = pdu[index++] & 0xff + pdu[index++] << 8 & 0xff;

        // 控制域(1字节)
        entity.cmd = pdu[index++] & 0xff;

        // 帧长度(1字节)
        int length = pdu[index++] & 0xff;

        if (pdu.length < length + 7) {
            throw new ProtocolException("长度不正确!");
        }

        // CRC校验
        if (Crc16Utils.getCRC16(pdu, 0, length + 7, 0x1021, 0x0000, 0x0000, false) != 0x00) {
            throw new ProtocolException("CRC校验不正确");
        }

        // 数据域
        entity.data = new byte[length];
        System.arraycopy(pdu, 5, entity.data, 0, entity.data.length);

        return entity;
    }

    public static byte[] encodePdu(PduEntity entity, Escape escape) {
        if (entity.data.length > 0xFF) {
            throw new ProtocolException("数据长度超过了255");
        }

        byte[] pdu = new byte[entity.data.length + 7];

        int index = 0;


        // 头标域
        pdu[index++] = 0x7E;
        // 地址域
        pdu[index++] = (byte) (entity.devAddr / 0x100);
        pdu[index++] = (byte) (entity.devAddr % 0x100);
        // 控制域
        pdu[index++] = (byte) (entity.cmd);
        // 帧长度
        pdu[index++] = (byte) (entity.data.length);
        // 数据域
        System.arraycopy(entity.data, 0, pdu, index, entity.data.length);
        index += entity.data.length;
        // 校验域
        int crc = Crc16Utils.getCRC16(pdu, 0, pdu.length - 2, 0x1021, 0x0000, 0x0000, false);
        pdu[index++] = (byte) (crc >> 8 & 0xff);
        pdu[index++] = (byte) (crc >> 0 & 0xff);

        // 转义
        return PduEntity.escapeEncode(pdu, escape);
    }
}
