package cn.foxtech.device.protocol.v1.telecom.core.entity;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 电信行业通信协议：通信报文特征：7E开头，0D结尾
 * 电信总局协议是三大电信的设备供应商们的通信协议
 * 电信总局的协议框架：电信行业的大量电源设备，都在这个格式框架的基础上，自定义扩展
 * 《通信电源、机房空调集中监控管理系统暂行规定》
 * 协议格式如下：
 * SOI	   VER	   ADR		  CID1	    CID2	 LENGTH	 INFO	 CHKSUM		EOI
 * 起始位 版本号	 设备地址    标识码    标识码    长度	 数据	  校验    结束码
 * 1	   1		1		  1		     1		   2	  n         2		 1
 * 范例：
 * 7E 32 30 30 32 34 31 30 30 30 30 30 30 46 44 42 37 0D
 * <p>
 * 其他各个厂商的同类设备，可以从该类型派生子类，使用时候，先使用packCmd和unPackCmd进行数据报级别的解码，
 * 后续再对各字段进行相应处理
 */
@Setter(value = AccessLevel.PUBLIC)
@Getter(value = AccessLevel.PUBLIC)
public class PduEntity {
    /**
     * 版本
     */
    private int ver = 0;
    /**
     * 地址
     */
    private int addr = 0;
    /**
     * CID1
     */
    private int cid1 = 0;
    /**
     * CID2
     */
    private int cid2 = 0;
    /**
     * 数据域
     */
    private byte[] data = new byte[0];

    /**
     * 数据编码打包
     *
     * @param entity 实体 数据编码
     * @return 数据编码
     */
    public static byte[] encodePdu(PduEntity entity) {
        // 检查:数据域长度
        if (entity.data.length > 255) {
            throw new ProtocolException("数据长度超过255");
        }

        // 初始化:命令长度
        //    arrCmd.SetSize(2 + (8 + iDataSize) * 2);
        byte[] pdu = new byte[2 + (8 + entity.data.length) * 2];


        // 发送命令的协议结构
//SOI	   VER	   ADR		  CID1	    CID2	 LENGTH	 INFO	 CHKSUM		EOI
//起始位 版本号	 设备地址    标识码    标识码    长度	 数据	  校验    结束码
// 1	   1		1		  1		     1		   2	  n         2		 1


        int index = 0;

        int value = 0;

        // 起始位SOI
        pdu[index++] = 0x7E;

        // 版本号VER
        value = hexToAscii(entity.ver);
        pdu[index++] = (byte) (value >> 8);
        pdu[index++] = (byte) (value & 0xff);

        // 地址码ADR
        value = hexToAscii(entity.addr);
        pdu[index++] = (byte) (value >> 8);
        pdu[index++] = (byte) (value & 0xff);


        // 标识码CID1
        value = hexToAscii(entity.cid1);
        pdu[index++] = (byte) (value >> 8);
        pdu[index++] = (byte) (value & 0xff);


        // 标识码CID2
        value = hexToAscii(entity.cid2);
        pdu[index++] = (byte) (value >> 8);
        pdu[index++] = (byte) (value & 0xff);


        // 长度LENGTH
        int wLen = getLenCode(entity.data.length * 2);
        value = hexToAscii((byte) ((wLen >> 8) & 0xff));
        pdu[index++] = (byte) (value >> 8);
        pdu[index++] = (byte) (value & 0xff);

        value = hexToAscii((byte) (wLen & 0xff));
        pdu[index++] = (byte) (value >> 8);
        pdu[index++] = (byte) (value & 0xff);


        // 数据
        for (int i = 0; i < entity.data.length; i++) {
            value = hexToAscii(entity.data[i]);
            pdu[index++] = (byte) (value >> 8);
            pdu[index++] = (byte) (value & 0xff);
        }

        // 校验
        int wVfy = getVfyCode(pdu);
        value = hexToAscii((byte) ((wVfy >> 8) & 0xff));
        pdu[index++] = (byte) (value >> 8);
        pdu[index++] = (byte) (value & 0xff);

        value = hexToAscii(((byte) (wVfy & 0xff)));
        pdu[index++] = (byte) (value >> 8);
        pdu[index++] = (byte) (value & 0xff);


        // 包尾
        pdu[pdu.length - 1] = 0x0D;

        return pdu;
    }

    /**
     * 报文解码
     *
     * @param pdu 报文
     * @return 实体
     */
    public static PduEntity decodePdu(byte[] pdu) {
        return decodePdu(pdu, false, (byte) 0x00);
    }

    /**
     * 报文解码
     *
     * @param pdu          报文
     * @param rawData      是否为原始数据
     * @param defaultValue 某些设备厂商对电信总局的数据编码理解不正确，会出现一些非法的数据，那么给一个缺省值进行替代
     * @return
     */
    public static PduEntity decodePdu(byte[] pdu, boolean rawData, byte defaultValue) {
        int iSize = pdu.length;
        if (iSize < 18) {
            throw new ProtocolException("报文长度小于18");
        }

        int index = 0;
        byte chHigh = 0;
        byte chLow = 0;

        // 起始位SOI
        if (pdu[index++] != (byte) 0x7E) {
            throw new ProtocolException("起始字符必须位7E");
        }

        // 结束码EOI
        if ((pdu[iSize - 1] != (byte) 0x0D)) {
            throw new ProtocolException("结束字符必须位0D");
        }

        PduEntity entity = new PduEntity();


        // 版本号VER
        chHigh = pdu[index++];
        chLow = pdu[index++];
        entity.ver = asciiToHex(chHigh, chLow, defaultValue);

        // 设备地址ADR
        chHigh = pdu[index++];
        chLow = pdu[index++];
        entity.addr = asciiToHex(chHigh, chLow, defaultValue);

        // 标识码CID1
        chHigh = pdu[index++];
        chLow = pdu[index++];
        entity.cid1 = asciiToHex(chHigh, chLow, defaultValue);

        // 标识码CID2
        chHigh = pdu[index++];
        chLow = pdu[index++];
        entity.cid2 = asciiToHex(chHigh, chLow, defaultValue);

        // 长度LENGTH10f0
        int wLen = 0;
        chHigh = pdu[index++];
        chLow = pdu[index++];
        byte value = asciiToHex(chHigh, chLow, defaultValue);

        wLen = (value & 0xff) * 0x100;
        chHigh = pdu[index++];
        chLow = pdu[index++];
        value = asciiToHex(chHigh, chLow, defaultValue);
        wLen += (value & 0xff);


        // 检查:帧长度编码
        if (!chkLenCode(wLen)) {
            throw new ProtocolException("帧长度编码不正确!");
        }

        // 编码前的长度，转换为实际长度
        wLen &= 0x0FFF;

        // 检查:命令长度
        if (pdu.length != 18 + wLen) {
            throw new ProtocolException("帧长度校验不正确!");
        }

        if (rawData) {
            // 原始数据模式：某些设备厂家，没有按电信总局的要求，按原始数据格式进行编码
            entity.data = new byte[wLen];
            for (int i = 0; i < wLen; i++) {
                entity.data[i] = pdu[index++];
            }
        } else {
            // 电信总局的要求：数据双字节的编码
            int iDataSize = wLen / 2;
            entity.data = new byte[iDataSize];
            for (int i = 0; i < iDataSize; i++) {
                chHigh = pdu[index++];
                chLow = pdu[index++];

                entity.data[i] = asciiToHex(chHigh, chLow, defaultValue);
            }
        }

        // 校验
        int wVfy = 0;
        chHigh = pdu[index++];
        chLow = pdu[index++];
        value = asciiToHex(chHigh, chLow, defaultValue);
        wVfy = (value & 0xff) * 0x100;

        chHigh = pdu[index++];
        chLow = pdu[index++];
        value = asciiToHex(chHigh, chLow, defaultValue);
        wVfy += (value & 0xff);


        // 检查:校验和
        int wVfyOK = getVfyCode(pdu);
        if (wVfyOK != wVfy) {
            throw new ProtocolException("校验和不正确!");
        }

        return entity;
    }

    /**
     * 计算Length的值
     *
     * @param iLen
     * @return 16位的Hex格式编码
     */
    private static int getLenCode(int iLen) {
        byte byLCHK = (byte) (((iLen & 0xF00) >> 8) + ((iLen & 0x0F0) >> 4) + (iLen & 0x00F));
        byLCHK &= 0x0F;
        byLCHK = (byte) ((byte) (~byLCHK) & 0xff);
        byLCHK &= 0x0F;
        byLCHK++;
        int wLen = byLCHK;
        wLen = (wLen << 12) + iLen;

        return wLen;
    }


    private static int hexToAscii(int byAt) {
        byte chAsciiH = 0x00;
        byte chAsciiL = 0x00;

        switch (byAt & 0xF0) {
            case 0x00:
                chAsciiH = 0x30;
                break;
            case 0x10:
                chAsciiH = 0x31;
                break;
            case 0x20:
                chAsciiH = 0x32;
                break;
            case 0x30:
                chAsciiH = 0x33;
                break;
            case 0x40:
                chAsciiH = 0x34;
                break;
            case 0x50:
                chAsciiH = 0x35;
                break;
            case 0x60:
                chAsciiH = 0x36;
                break;
            case 0x70:
                chAsciiH = 0x37;
                break;
            case 0x80:
                chAsciiH = 0x38;
                break;
            case 0x90:
                chAsciiH = 0x39;
                break;
            case 0xA0:
                chAsciiH = 0x41;
                break;
            case 0xB0:
                chAsciiH = 0x42;
                break;
            case 0xC0:
                chAsciiH = 0x43;
                break;
            case 0xD0:
                chAsciiH = 0x44;
                break;
            case 0xE0:
                chAsciiH = 0x45;
                break;
            case 0xF0:
                chAsciiH = 0x46;
                break;
            default:
                break;
        }

        switch (byAt & 0x0F) {
            case 0x00:
                chAsciiL = 0x30;
                break;
            case 0x01:
                chAsciiL = 0x31;
                break;
            case 0x02:
                chAsciiL = 0x32;
                break;
            case 0x03:
                chAsciiL = 0x33;
                break;
            case 0x04:
                chAsciiL = 0x34;
                break;
            case 0x05:
                chAsciiL = 0x35;
                break;
            case 0x06:
                chAsciiL = 0x36;
                break;
            case 0x07:
                chAsciiL = 0x37;
                break;
            case 0x08:
                chAsciiL = 0x38;
                break;
            case 0x09:
                chAsciiL = 0x39;
                break;
            case 0x0A:
                chAsciiL = 0x41;
                break;
            case 0x0B:
                chAsciiL = 0x42;
                break;
            case 0x0C:
                chAsciiL = 0x43;
                break;
            case 0x0D:
                chAsciiL = 0x44;
                break;
            case 0X0E:
                chAsciiL = 0x45;
                break;
            case 0x0F:
                chAsciiL = 0x46;
                break;
            default:
                break;
        }

        return chAsciiH << 8 | chAsciiL & 0xff;
    }

    /**
     * 计算校验码
     *
     * @param pdu
     * @return 16位的Hex格式编码
     */
    private static int getVfyCode(byte[] pdu) {
        int iSize = pdu.length - 6;
        if (iSize < 12) {
            return 0;
        }

        int wVfy = 0x00;
        for (int i = 0; i < iSize; i++) {
            wVfy += pdu[i + 1] & 0xff;
        }

        wVfy = (~wVfy & 0xffff);
        wVfy++;

        return wVfy;
    }

    private static byte asciiToHex(byte chAsciiH, byte chAsciiL, byte defaultValue) {
        byte byAtH = 0x00;
        byte byAtL = 0x00;

        if (chAsciiH == 0x20) {
            byAtH = 0x00;
        } else {
            if ((chAsciiH >= 0x30) && (chAsciiH <= 0x39)) {
                byAtH = (byte) (chAsciiH - 0x30);
            } else if ((chAsciiH >= 0x41) && (chAsciiH <= 0x46)) {
                byAtH = (byte) (chAsciiH - 0x37);
            } else {
                return defaultValue;
            }
        }

        if (chAsciiL == 0x20) {
            byAtL = 0x00;
        } else {
            if ((chAsciiL >= 0x30) && (chAsciiL <= 0x39)) {
                byAtL = (byte) (chAsciiL - 0x30);
            } else if ((chAsciiL >= 0x41) && (chAsciiL <= 0x46)) {
                byAtL = (byte) (chAsciiL - 0x37);
            } else {
                return defaultValue;
            }
        }

        return (byte) ((byAtH << 4) + byAtL);
    }


    private static boolean chkLenCode(int wLenCode) {
        // 计算校验和
        int sum = 0x00;
        sum = wLenCode & 0x0F;
        sum += (wLenCode >> 4) & 0x0F;
        sum += (wLenCode >> 8) & 0x0F;
        sum = (~sum) + 1;
        int wLenTemp = ((sum & 0xF) << 12) + (wLenCode & 0xFFF);
        return wLenTemp == wLenCode;
    }

}
