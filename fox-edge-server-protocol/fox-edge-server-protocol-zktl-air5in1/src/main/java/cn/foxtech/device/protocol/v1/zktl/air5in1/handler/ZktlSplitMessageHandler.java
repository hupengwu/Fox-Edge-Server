/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.zktl.air5in1.handler;

import cn.foxtech.device.protocol.v1.utils.netty.SplitMessageHandler;

/**
 * 分包处理器：头标识（'2424'）+通信类型（x）+设备（x）+报文长度（xx），共8字节
 */
public class ZktlSplitMessageHandler extends SplitMessageHandler {
    public ZktlSplitMessageHandler() {
        this.header = new int[8];
    }

    /**
     * 是否为非法报文：通过检查报文头部，这些协议中约定的起始标记，判定该报文是否为合法的报文
     *
     * @return 非法报文
     */
    @Override
    public boolean isInvalidPack() {
        // 报头是否为非法字符
        if (this.header[0] != '2') {
            return true;
        }
        if (this.header[1] != '4') {
            return true;
        }
        if (this.header[2] != '2') {
            return true;
        }
        return this.header[3] != '4';
    }

    /**
     * 从minPack数组中，取出报文长度信息
     *
     * @return
     */
    @Override
    public int getPackLength() {
        byte ch = (byte) this.header[6];
        byte cl = (byte) this.header[7];

        Byte value = this.asciiToHex(ch, cl);
        if (value == null) {
            return -1;
        }

        return value.intValue() + 8;
    }

    public int hexToAscii(byte byAt) {
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

        return ((chAsciiH << 8) & 0xff00) + (chAsciiL & 0xff);
    }

    public Byte asciiToHex(byte chAscii) {
        byte byAtH;
        if ((chAscii >= 0x30) && (chAscii <= 0x39)) {
            return (byte) (chAscii - 0x30);
        } else if ((chAscii >= 0x41) && (chAscii <= 0x46)) {
            return (byte) (chAscii - 0x37);
        } else {
            return null;
        }
    }

    public Byte asciiToHex(byte chAsciiH, byte chAsciiL) {
        Byte byAtH = asciiToHex(chAsciiH);
        Byte byAtL = asciiToHex(chAsciiL);

        if (byAtH == null || byAtL == null) {
            return null;
        }

        return (byte) ((byAtH.byteValue() << 4) + byAtL.byteValue());
    }
}
