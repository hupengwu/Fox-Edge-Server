package cn.foxtech.device.protocol.v1.gdana.digester;


import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;

import java.util.ArrayList;
import java.util.List;

/**
 * 解码器范例：
 * 1、类名称要加上FoxEdgeType注解，告知扫描器，在这边读取设备类型名
 * 2、准备导出的函数，要加上FoxEdgeMethod注解，告知扫描器，在这边读取操作方法
 * 3、FoxEdgeMethod编码解码函数，比如严格参照范例的格式
 */
public class DigesterProtocolFrame {

    public static byte[] encodeBroadcast() {
        byte[] pack = new byte[10];

        // 设备地址:BCD
        pack[0] = (byte) 0xff;
        pack[1] = (byte) 0xff;
        pack[2] = (byte) 0xff;
        // 子设备地址:BCD
        pack[3] = (byte) 0xff;
        // 占位符
        pack[4] = (byte) 0xff;
        pack[5] = (byte) 0xff;
        // 功能号
        pack[6] = (byte) 0x00;
        // 数据长度
        pack[7] = (byte) 0x00;
        // 数据
        // CRC16
        pack[8] = (byte) 0x51;
        pack[9] = (byte) 0x0b;

        return pack;
    }

    /**
     * 报文打包
     *
     * @return
     */
    public static byte[] encodePack(DigesterEntity entity) {
        byte[] pack = new byte[entity.getData().length + 10];

        // 设备地址:BCD
        pack[0] = (byte) encodeBcd(entity.getAddr() % 1000000 / 10000);
        pack[1] = (byte) encodeBcd(entity.getAddr() % 10000 / 100);
        pack[2] = (byte) encodeBcd(entity.getAddr() % 100 / 1);
        // 子设备地址:BCD
        pack[3] = (byte) encodeBcd(entity.getSubAddr());
        // 占位符
        pack[4] = (byte) 0x00;
        pack[5] = (byte) 0x00;
        // 功能号
        pack[6] = (byte) entity.getFunc();
        // 数据长度
        pack[7] = (byte) entity.getData().length;
        // 数据
        System.arraycopy(entity.getData(), 0, pack, 8, entity.getData().length);
        // CRC16
        int crc16 = getCRC16(pack);
        pack[pack.length - 2] = (byte) ((crc16 >> 0) & 0xff);
        pack[pack.length - 1] = (byte) ((crc16 >> 8) & 0xff);

        return pack;
    }

    /**
     * 报文解包
     *
     * @param pack
     * @return
     */
    public static DigesterEntity decodePack(byte[] pack) {
        if (pack.length < 10) {
            return null;
        }

        DigesterEntity entity = new DigesterEntity();

        // 设备地址:BCD
        int adr = 0;
        adr = adr * 100 + decodeBcd(pack[0]);
        adr = adr * 100 + decodeBcd(pack[1]);
        adr = adr * 100 + decodeBcd(pack[2]);
        entity.setAddr(adr);
        // 子设备地址
        entity.setSubAddr(decodeBcd(pack[3]));
        // 功能号
        entity.setFunc(pack[6] & 0xff);
        // 数据长度
        entity.setData(new byte[pack.length - 10]);
        // 数据
        System.arraycopy(pack, 8, entity.getData(), 0, entity.getData().length);

        // CRC16
        int crc16 = getCRC16(pack);
        byte crcL = (byte) ((crc16 >> 0) & 0xff);
        byte crcH = (byte) ((crc16 >> 8) & 0xff);
        if ((pack[pack.length - 2] == crcL) && (pack[pack.length - 2] == crcL)) {
            return entity;
        }

        return null;
    }

    public static List<DigesterEntity> decodeStickPack(byte[] pack) {
        List<DigesterEntity> entityList = new ArrayList<>();
        if (pack.length < 10) {
            throw new ProtocolException("解码失败!");
        }

        // 一个包的最小长度
        if (pack.length >= 10) {

            // 刚好1个包的长度
            int datLen1 = pack[7];
            int packLen1 = datLen1 + 10;
            if (pack.length == packLen1) {
                DigesterEntity entity = decodePack(pack);
                if (entity == null) {
                    throw new ProtocolException("解码失败!");
                }

                entityList.add(entity);
                return entityList;
            }

            // 两包的最小长度
            if (pack.length >= packLen1 + 10) {

                // 刚好2个包的长度
                int datLen2 = pack[packLen1 + 7];
                int packLen2 = datLen2 + 10;
                if (pack.length == packLen1 + packLen2) {
                    byte[] pack1 = new byte[packLen1];
                    byte[] pack2 = new byte[packLen2];
                    System.arraycopy(pack, 0, pack1, 0, pack1.length);
                    System.arraycopy(pack, pack1.length, pack2, 0, pack2.length);

                    DigesterEntity entity = decodePack(pack1);
                    if (entity == null) {
                        throw new ProtocolException("解码失败!");
                    }
                    entityList.add(entity);

                    entity = decodePack(pack2);
                    if (entity == null) {
                        throw new ProtocolException("解码失败!");
                    }

                    entityList.add(entity);
                    return entityList;
                }
            }
        }


        throw new ProtocolException("解码失败!");
    }

    public static int decodeBcd(byte bcd) {
        return ((bcd & 0xff) >> 4) * 10 + (bcd & 0x0f);
    }

    public static int encodeBcd(int dec) {
        return (dec % 10) + ((dec % 100 / 10) << 4);
    }

    /**
     * CRC16计算器
     *
     * @param arrCmd
     * @return
     */
    private static int getCRC16(byte[] arrCmd) {
        int iCount = arrCmd.length - 2;

        int save_hi, save_lo;
        byte i, n;
        int CRC16Lo = 0xFF;
        int CRC16Hi = 0xFF;
        for (n = 0; n < iCount; n++) {
            CRC16Lo = CRC16Lo ^ (arrCmd[n] & 0xff);//每一个数据与CRC寄存器进行异或
            for (i = 0; i < 8; i++) {
                save_hi = CRC16Hi;
                save_lo = CRC16Lo;
                CRC16Hi = CRC16Hi >> 1;// 高位右移一位
                CRC16Lo = CRC16Lo >> 1;// 低位右移一位
                if ((save_hi & 0x01) != 0)   //如果高位字节最后一位为1
                {
                    CRC16Lo = CRC16Lo | 0x80;//则低位字节右移后前面补1
                }
                //否则自动补0
                if ((save_lo & 0x01) != 0)   //如果LSB为1，则与多项式码进行异或
                {
                    CRC16Hi = CRC16Hi ^ 0x84;
                    CRC16Lo = CRC16Lo ^ 0x08;
                }
            }
        }
        return CRC16Hi * 256 + CRC16Lo;

    }

    /**
     * 命令字：发送0x05命令，返回的是0x85命令
     *
     * @param cmd
     * @return
     */
    public static byte decodeCmd(byte cmd) {
        return (byte) (cmd & 0x0f);
    }
}
