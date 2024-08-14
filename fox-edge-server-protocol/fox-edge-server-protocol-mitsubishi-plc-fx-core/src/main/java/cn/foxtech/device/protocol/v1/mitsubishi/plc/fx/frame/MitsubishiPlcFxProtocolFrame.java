/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.mitsubishi.plc.fx.frame;


import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.utils.AsciiUtils;
import cn.foxtech.device.protocol.v1.mitsubishi.plc.fx.entity.MitsubishiPlcFxDeviceReadEntity;
import cn.foxtech.device.protocol.v1.mitsubishi.plc.fx.entity.MitsubishiPlcFxDeviceWriteEntity;
import cn.foxtech.device.protocol.v1.mitsubishi.plc.fx.entity.MitsubishiPlcFxForceOffEntity;
import cn.foxtech.device.protocol.v1.mitsubishi.plc.fx.entity.MitsubishiPlcFxForceOnEntity;

/**
 * 三菱PLC-FX系列
 * 这协议比较简单，就是地址编码比较坑爹
 * 参考：
 * https://blog.csdn.net/qq_45445740/article/details/117924160
 * https://blog.csdn.net/caodunchao/article/details/51564484
 * https://www.dgjs123.com/sanlingplc/1780.htm
 */
public class MitsubishiPlcFxProtocolFrame {
    /**
     * 读取设备
     *
     * @param entity
     * @return
     */
    public static byte[] encodePack(MitsubishiPlcFxDeviceReadEntity entity) {
        byte[] pack = new byte[11];

        // 起始（STX）
        pack[0] = 0x02;

        // 命令（CMD）
        pack[1] = '0';

        // 首地址（ADDRESS）
        // 将用户地址编码为设备地址
        int devAddress = entity.encodeAddress();
        int daH = devAddress >> 8;
        int daL = devAddress & 0xff;
        int valueH = AsciiUtils.hexToAscii((byte) daH);
        int valueL = AsciiUtils.hexToAscii((byte) daL);

        pack[2] = (byte) (valueH >> 8);
        pack[3] = (byte) (valueH & 0xff);
        pack[4] = (byte) (valueL >> 8);
        pack[5] = (byte) (valueL & 0xff);


        // 字节数（BYTES）
        int value = AsciiUtils.hexToAscii((byte) entity.getCount());
        pack[6] = (byte) (value >> 8);
        pack[7] = (byte) (value & 0xff);

        // 终止（ETX）
        pack[pack.length - 3] = 0x03;

        // 校验和（SUM）
        int sum = AsciiUtils.hexToAscii((byte) getSum(pack));

        pack[pack.length - 2] = (byte) (sum >> 8);
        pack[pack.length - 1] = (byte) (sum & 0xff);

        return pack;
    }


    public static void decodePack(byte[] pack, MitsubishiPlcFxDeviceReadEntity entity) {
        // 验证帧格式：最小长度、头、尾、校验和，是否正确
        verifyHeadAndTail(pack);


        // 检查数据长度
        byte[] data = new byte[pack.length - 4];
        if (data.length % 4 != 0) {
            throw new ProtocolException("返回数据长度必须是4的整数倍");
        }

        // 数据部分
        for (int i = 0; i < data.length / 4; i++) {
            data[i * 4 + 0] = pack[1 + i * 4 + 2];
            data[i * 4 + 1] = pack[1 + i * 4 + 3];
            data[i * 4 + 2] = pack[1 + i * 4 + 0];
            data[i * 4 + 3] = pack[1 + i * 4 + 1];
        }

        // 转换字符串:3412CDAB
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            char ch = (char) data[i];
            sb.append(ch);
        }
        entity.setData(sb.toString());
    }

    /**
     * 读取设备操作
     *
     * @param entity
     * @return
     */
    public static byte[] encodePack(MitsubishiPlcFxDeviceWriteEntity entity) {
        int length = entity.getData().length();
        if (length % 4 != 0) {
            throw new ProtocolException("字符串长度必须是4的整数倍");
        }

        byte[] pack = new byte[11 + entity.getData().length()];

        // 起始（STX）
        pack[0] = 0x02;

        // 命令（CMD）
        pack[1] = '1';

        // 首地址（ADDRESS）
        // 将用户地址编码为设备地址
        int devAddress = entity.encodeAddress();
        int daH = devAddress >> 8;
        int daL = devAddress & 0xff;
        int valueH = AsciiUtils.hexToAscii((byte) daH);
        int valueL = AsciiUtils.hexToAscii((byte) daL);

        pack[2] = (byte) (valueH >> 8);
        pack[3] = (byte) (valueH & 0xff);
        pack[4] = (byte) (valueL >> 8);
        pack[5] = (byte) (valueL & 0xff);


        // 字节数（BYTES）
        int value = AsciiUtils.hexToAscii((byte) (length / 2));
        pack[6] = (byte) (value >> 8);
        pack[7] = (byte) (value & 0xff);


        // 数据部分
        byte[] data = entity.getData().getBytes();
        for (int i = 0; i < length / 4; i++) {
            pack[8 + i * 4 + 2] = data[i * 4 + 0];
            pack[8 + i * 4 + 3] = data[i * 4 + 1];
            pack[8 + i * 4 + 0] = data[i * 4 + 2];
            pack[8 + i * 4 + 1] = data[i * 4 + 3];
        }


        // 终止（ETX）
        pack[pack.length - 3] = 0x03;

        // 校验和（SUM）
        int sum = AsciiUtils.hexToAscii((byte) getSum(pack));

        pack[pack.length - 2] = (byte) (sum >> 8);
        pack[pack.length - 1] = (byte) (sum & 0xff);

        return pack;
    }

    public static String decodePackConfirm(byte[] pack) {
        if (pack.length != 1) {
            throw new ProtocolException("报文长度不正确!");
        }

        // 报文内容
        if (pack[0] == 0x06) {
            return "ASK";
        } else if (pack[0] == 0x15) {
            return "NAK";
        } else {
            throw new ProtocolException("报文不正确!");
        }
    }

    public static void decodePack(byte[] pack, MitsubishiPlcFxDeviceWriteEntity entity) {
        String result = decodePackConfirm(pack);

        entity.setResult(result);
    }

    /**
     * 设备强制置位
     *
     * @param entity
     * @return
     */
    public static byte[] encodePack(MitsubishiPlcFxForceOnEntity entity) {
        byte[] pack = new byte[9];

        // 起始（STX）
        pack[0] = 0x02;

        // 命令（CMD）
        pack[1] = '7';

        // 首地址（ADDRESS）
        // 将用户地址编码为设备地址
        int devAddress = entity.encodeAddress();
        int daH = devAddress >> 8;
        int daL = devAddress & 0xff;
        int valueH = AsciiUtils.hexToAscii((byte) daH);
        int valueL = AsciiUtils.hexToAscii((byte) daL);

        pack[2] = (byte) (valueH >> 8);
        pack[3] = (byte) (valueH & 0xff);
        pack[4] = (byte) (valueL >> 8);
        pack[5] = (byte) (valueL & 0xff);


        // 终止（ETX）
        pack[pack.length - 3] = 0x03;

        // 校验和（SUM）
        int sum = AsciiUtils.hexToAscii((byte) getSum(pack));

        pack[pack.length - 2] = (byte) (sum >> 8);
        pack[pack.length - 1] = (byte) (sum & 0xff);

        return pack;
    }

    public static void decodePack(byte[] pack, MitsubishiPlcFxForceOnEntity entity) {
        String result = decodePackConfirm(pack);

        entity.setResult(result);
    }

    public static byte[] encodePack(MitsubishiPlcFxForceOffEntity entity) {
        byte[] pack = new byte[9];

        // 起始（STX）
        pack[0] = 0x02;

        // 命令（CMD）
        pack[1] = '8';

        // 首地址（ADDRESS）
        // 将用户地址编码为设备地址
        int devAddress = entity.encodeAddress();
        int daH = devAddress >> 8;
        int daL = devAddress & 0xff;
        int valueH = AsciiUtils.hexToAscii((byte) daH);
        int valueL = AsciiUtils.hexToAscii((byte) daL);

        pack[2] = (byte) (valueH >> 8);
        pack[3] = (byte) (valueH & 0xff);
        pack[4] = (byte) (valueL >> 8);
        pack[5] = (byte) (valueL & 0xff);


        // 终止（ETX）
        pack[pack.length - 3] = 0x03;

        // 校验和（SUM）
        int sum = AsciiUtils.hexToAscii((byte) getSum(pack));

        pack[pack.length - 2] = (byte) (sum >> 8);
        pack[pack.length - 1] = (byte) (sum & 0xff);

        return pack;
    }

    public static void decodePack(byte[] pack, MitsubishiPlcFxForceOffEntity entity) {
        String result = decodePackConfirm(pack);
        entity.setResult(result);
    }


    /**
     * 验证头尾
     *
     * @param pack
     */
    private static void verifyHeadAndTail(byte[] pack) {
        if (pack.length < 4) {
            throw new ProtocolException("长度不正确!");
        }

        // 起始（STX）
        if (pack[0] != 0x02) {
            throw new ProtocolException("STX不正确!");
        }

        // 终止（ETX）
        if (pack[pack.length - 3] != 0x03) {
            throw new ProtocolException("ETX不正确!");
        }

        // 校验和（SUM）
        Byte byH = AsciiUtils.asciiToHex(pack[pack.length - 2]);
        Byte byL = AsciiUtils.asciiToHex(pack[pack.length - 1]);
        int sum1 = ((byH << 4) & 0xff) + byL;
        if (getSum(pack) != sum1) {
            throw new ProtocolException("SUM不正确!");
        }
    }

    private static int getSum(byte[] pack) {
        int sum = 0;
        for (int i = 1; i < pack.length - 2; i++) {
            sum += pack[i] & 0xff;
        }

        return sum & 0xff;
    }

}
