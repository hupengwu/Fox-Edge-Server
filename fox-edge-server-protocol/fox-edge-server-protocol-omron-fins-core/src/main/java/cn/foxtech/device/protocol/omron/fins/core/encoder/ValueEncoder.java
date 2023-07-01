package cn.foxtech.device.protocol.omron.fins.core.encoder;

import cn.foxtech.device.protocol.core.exception.ProtocolException;

public class ValueEncoder {
    public static float getFloat(byte[] pack, int index) {
        int bits = 0;
        bits += (pack[1] & 0xff) << 0;
        bits += (pack[0] & 0xff) << 8;
        bits += (pack[3] & 0xff) << 16;
        bits += (pack[2] & 0xff) << 24;
        return Float.intBitsToFloat(bits);
    }


    public static long getDWord(byte[] pack, int index) {
        long value = 0;
        value += (pack[index + 1] & 0xff) * 0x01;
        value += (pack[index + 0] & 0xff) * 0x0100;
        value += (pack[index + 3] & 0xff) * 0x010000;
        value += (pack[index + 2] & 0xff) * 0x01000000;

        return value;
    }

    public static void setDword(byte[] pack, int index, long value) {
        pack[index + 1] = (byte) (value & 0xff);
        pack[index + 0] = (byte) ((value >> 8) & 0xff);
        pack[index + 3] = (byte) ((value >> 16) & 0xff);
        pack[index + 2] = (byte) ((value >> 24) & 0xff);
    }

    public static int getWord(byte[] pack, int index) {
        int value = 0;
        value += (pack[index + 1] & 0xff) * 0x01;
        value += (pack[index + 0] & 0xff) * 0x0100;

        return value;
    }

    public static void setWord(byte[] pack, int index, int value) {
        pack[index + 1] = (byte) (value & 0xff);
        pack[index + 0] = (byte) ((value >> 8) & 0xff);
    }

    public static Object decodeReadData(byte[] value, int count, String clazz) {
        if (value.length < 2) {
            throw new ProtocolException("这不是一个合法数据！");
        }

        if (String.class.getSimpleName().equals(clazz)) {
            // 48 4A 4B 31 32 33 34 35 36 37 38 00 ----HJK12345678
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < value.length; i++) {
                char ch = (char) value[i];
                if (ch == 0) {
                    break;
                }

                sb.append((char) value[i]);
            }

            return sb.toString();
        }
        if (Integer.class.getSimpleName().equals(clazz)) {
            if (value.length == 4) {
                // 00 0C 3F 7B ----1065025548
                return ValueEncoder.getDWord(value, 0);
            }
        }
        if (Short.class.getSimpleName().equals(clazz)) {
            if (value.length == 2) {
                // 00 0C ---12
                return ValueEncoder.getWord(value, 0);
            }
        }
        if (Float.class.getSimpleName().equals(clazz)) {
            if (value.length == 4) {
                //   00 00 3F C0   1.5
                return ValueEncoder.getFloat(value, 0);
            }
        }


        return null;
    }
    public static <T> Object decodeReadData(byte[] value, int count, Class<T> clazz) {
        if (value.length < 2) {
            throw new ProtocolException("这不是一个合法数据！");
        }

        if (String.class.equals(clazz)) {
            // 48 4A 4B 31 32 33 34 35 36 37 38 00 ----HJK12345678
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < value.length; i++) {
                char ch = (char) value[i];
                if (ch == 0) {
                    break;
                }

                sb.append((char) value[i]);
            }

            return sb.toString();
        }
        if (Integer.class.equals(clazz)) {
            if (value.length == 4) {
                // 00 0C 3F 7B ----1065025548
                return ValueEncoder.getDWord(value, 0);
            }
        }
        if (Short.class.equals(clazz)) {
            if (value.length == 2) {
                // 00 0C ---12
                return ValueEncoder.getWord(value, 0);
            }
        }
        if (Float.class.equals(clazz)) {
            if (value.length == 4) {
                //   00 00 3F C0   1.5
                return ValueEncoder.getFloat(value, 0);
            }
        }


        return null;
    }

}
