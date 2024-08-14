/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.demo;


/**
 * 解码器范例：
 * 1、类名称要加上FoxEdgeType注解，告知扫描器，在这边读取设备类型名
 * 2、准备导出的函数，要加上FoxEdgeMethod注解，告知扫描器，在这边读取操作方法
 * 3、FoxEdgeMethod编码解码函数，比如严格参照范例的格式
 */
public class DemoProtocolFrame {

    /**
     * 报文打包
     *
     * @param cmd
     * @param dat
     * @return
     */
    public static byte[] encodePack(byte cmd, byte[] dat) {
        byte[] pack = new byte[dat.length + 5];

        pack[0] = (byte) 0xb0;
        pack[1] = cmd;
        pack[2] = (byte) dat.length;
        for (int i = 0; i < dat.length; i++) {
            pack[3 + i] = dat[i];
        }
        pack[dat.length + 3] = (byte) 0xfe;
        pack[dat.length + 4] = (byte) 0xfe;

        return pack;
    }


    /**
     * 报文解包
     *
     * @param pack
     * @return
     */
    public static DemoEntity decodePack(byte[] pack) {
        if (pack.length < 5) {
            return null;
        }

        // 报头
        if (pack[0] != (byte) 0xb0) {
            return null;
        }

        DemoEntity entity = new DemoEntity();

        // 命令字
        entity.setCmd(pack[1]);

        // 数据长度：java的byte是有符号的转变为无符号是 byte & 0xff
        if ((pack[2] & 0xff) + 5 != pack.length) {
            return null;
        }

        // 数据
        byte[] dats = new byte[pack.length - 5];
        for (int i = 0; i < dats.length; i++) {
            dats[i] = pack[3 + i];
        }
        entity.setDat(dats);

        // 结尾
        if (pack[dats.length + 3] != (byte) 0xfe) {
            return null;
        }
        if (pack[dats.length + 4] != (byte) 0xfe) {
            return null;
        }

        return entity;
    }


    /**
     * 报文打包
     *
     * @param cmd
     * @return
     */
    public static byte[] encodePack(byte cmd) {
        byte[] dat = new byte[0];
        return encodePack(cmd, dat);
    }
}
