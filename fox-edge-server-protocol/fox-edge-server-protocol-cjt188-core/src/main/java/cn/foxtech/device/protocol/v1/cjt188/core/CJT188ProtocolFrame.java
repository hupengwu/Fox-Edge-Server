package cn.foxtech.device.protocol.v1.cjt188.core;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;

/**
 * CJT188三表、热量表通信协议
 * 参考文章：
 * https://blog.csdn.net/dfdffdg8l00002001/article/details/102427129
 * https://blog.csdn.net/xuehu96/article/details/112055510
 * http://biz.doczj.com/doc/8a13185026-8.html
 */
public class CJT188ProtocolFrame {
    public static byte[] encodePack(CJT188Entity entity) {
        byte[] pack = new byte[entity.getData().length + 16];

        // 前缀
        pack[0] = (byte) 0xFE;
        pack[1] = (byte) 0xFE;
        pack[2] = (byte) 0xFE;

        // 起始符
        pack[3] = (byte) 0x68;

        // 表类型
        pack[4] = (byte) entity.getType().getValue();

        // 设备地址:BCD
        String address = entity.getAddress().getValue().replace(" ", "");
        if (address == null || address.length() != 14) {
            throw new ProtocolException("地址码长度不正确，应该是14字节长度的BCD格式文本字符串");
        }
        pack[5] = (byte) encodeBcd(address.charAt(0), address.charAt(1));
        pack[6] = (byte) encodeBcd(address.charAt(2), address.charAt(3));
        pack[7] = (byte) encodeBcd(address.charAt(4), address.charAt(5));
        pack[8] = (byte) encodeBcd(address.charAt(6), address.charAt(7));
        pack[9] = (byte) encodeBcd(address.charAt(8), address.charAt(9));
        pack[10] = (byte) encodeBcd(address.charAt(10), address.charAt(11));
        pack[11] = (byte) encodeBcd(address.charAt(12), address.charAt(13));

        // 控制码
        pack[12] = (byte) entity.getCtrl().getValue();
        // 数据长度
        pack[13] = (byte) entity.getData().length;
        // 数据
        System.arraycopy(entity.getData(), 0, pack, 14, entity.getData().length);
        // 校验和
        int check = getCheck(pack, 3, entity.getData().length + 15);
        pack[pack.length - 2] = (byte) check;
        // 结束符
        pack[pack.length - 1] = (byte) 0x16;

        return pack;
    }

    /**
     * 报文解包
     *
     * @param pack
     * @return
     */
    public static CJT188Entity decodePack(byte[] pack) {
        // 检查：报文长度
        if (pack.length < 16) {
            throw new ProtocolException("报文长度不正确！");
        }

        // 寻找起始符位置
        int start = -1;
        for (int i = 0; i < pack.length; i++) {
            if (pack[i] == (byte) 0xfe) {
                continue;
            }
            if (pack[i] != (byte) 0x68) {
                throw new ProtocolException("起始符的格式不正确，正确的格式为FE FE FE 68");
            }
            start = i;
            break;
        }
        if (start == -1) {
            throw new ProtocolException("起始符的格式不正确，正确的格式为FE FE FE 68");
        }

        if (pack.length != start + 13 + (pack[start + 10] & 0xff)) {
            throw new ProtocolException("报文长度不正确！");
        }


        CJT188Entity entity = new CJT188Entity();

        // 表类型
        entity.getType().setValue(pack[start + 1] & 0xff);
        // 表地址
        StringBuilder sb = new StringBuilder();
        decodeBcd(pack[start + 2], sb);
        decodeBcd(pack[start + 3], sb);
        decodeBcd(pack[start + 4], sb);
        decodeBcd(pack[start + 5], sb);
        decodeBcd(pack[start + 6], sb);
        decodeBcd(pack[start + 7], sb);
        decodeBcd(pack[start + 8], sb);
        String address = sb.toString();
        entity.getAddress().setValue(address);

        // 控制码
        entity.getCtrl().setValue(pack[start + 9] & 0xff);

        // 数据长度
        int len = pack[start + 10] & 0xff;
        byte[] data = new byte[len];
        // 数据
        entity.setData(data);
        System.arraycopy(pack, start + 11, entity.getData(), 0, len);
        // 校验和
        byte check = (byte) getCheck(pack, start, pack.length - 2);
        if (check != (pack[pack.length - 2])) {
            throw new ProtocolException("检验码不正确！");
        }
        // 结束符
        if (pack[pack.length - 1] != (byte) 0x16) {
            throw new ProtocolException("结束符不正确！");
        }

        return entity;
    }

    private static int getCheck(byte[] pack, int start, int end) {
        int check = 0;
        for (int i = start; i < end; i++) {
            check += pack[i] & 0xff;
        }

        check &= 0xff;

        return check;
    }


    public static int encodeBcd(char ch, char cl) {
        int value = 0;
        if ('0' <= ch && ch <= '9') {
            value += ((ch - '0') << 4);
        } else if ('a' <= ch && ch <= 'f') {
            value += ((ch - 'a') << 4) + 0xA0;
        } else if ('A' <= ch && ch <= 'F') {
            value += ((ch - 'A') << 4) + 0xA0;
        } else {
            throw new ProtocolException("地址码格式不正确，应该是0~10，A~F的字符!");
        }

        if ('0' <= cl && cl <= '9') {
            value += (cl - '0');
        } else if ('a' <= cl && cl <= 'f') {
            value += (cl - 'a') + 0x0A;
        } else if ('A' <= cl && cl <= 'F') {
            value += (cl - 'A') + 0x0A;
        } else {
            throw new ProtocolException("地址码格式不正确，应该是0~10，A~F的字符!");
        }

        return value;
    }

    private static void decodeBcd(byte by, StringBuilder sb) {
        int ch = (by & 0xf0);
        int cl = (by & 0x0f);

        if (0x00 <= ch && ch <= 0x90) {
            sb.append((char) ((ch >> 4) + '0'));
        } else if (0xa0 <= ch && ch <= 0xf0) {
            sb.append((char) ((ch >> 4) - 0xa + 'A'));
        } else if (0xA0 <= ch && ch <= 0xF0) {
            sb.append((char) ((ch >> 4) - 0xa + 'A'));
        } else {
            throw new ProtocolException("地址码格式不正确，应该是0~10，A~F的字符!");
        }

        if (0x00 <= cl && cl <= 0x09) {
            sb.append((char) (cl + '0'));
        } else if (0x0a <= cl && cl <= 0x0f) {
            sb.append((char) (cl - 0xa + 'A'));
        } else if (0x0A <= cl && cl <= 0x0F) {
            sb.append((char) (cl - 0xa + 'A'));
        } else {
            throw new ProtocolException("地址码格式不正确，应该是0~10，A~F的字符!");
        }
    }
}
