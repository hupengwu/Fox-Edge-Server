package cn.foxtech.device.protocol.core.utils;

public class AsciiUtils {
    /**
     * 16进制转换为ASCII格式
     *
     * @param byAt byAt 16进制
     * @return 返回ascii
     */
    public static int hexToAscii(byte byAt) {
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

    /**
     * 单字节的ASCII转16进制
     *
     * @param chAscii ASCII
     * @return HEX数值，如果转换失败则未null
     */
    public static Byte asciiToHex(byte chAscii) {
        byte byAtH;
        if ((chAscii >= 0x30) && (chAscii <= 0x39)) {
            return (byte) (chAscii - 0x30);
        } else if ((chAscii >= 0x41) && (chAscii <= 0x46)) {
            return (byte) (chAscii - 0x37);
        } else {
            return null;
        }
    }

    /**
     * ASCII转16进制
     *
     * @param chAsciiH 高位ASCII
     * @param chAsciiL 低位ASCII
     * @return 成功返回16进制编码，失败返回为null
     */
    public static Byte asciiToHex(byte chAsciiH, byte chAsciiL) {
        Byte byAtH = asciiToHex(chAsciiH);
        Byte byAtL = asciiToHex(chAsciiL);

        if (byAtH == null || byAtL == null) {
            return null;
        }

        return (byte) ((byAtH.byteValue() << 4) + byAtL.byteValue());
    }
}
