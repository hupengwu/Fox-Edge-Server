package cn.foxtech.device.protocol.v1.modbus.core;


import java.util.HashMap;
import java.util.Map;

/**
 * 工业行业通信协议
 * MODBUS是工业设备的常用标准协议
 * 背景知识：
 * 1、它的基本设计理念是把设备当作一片连续的内存表，往内存地址上读取数据，设备就返回相应的设备检测数据
 * 往内存地址上写入数据，设备就配置或者控制动作。
 * 2、ModBus设备有两种工作模式，一种是ASCII，一种是RTU，上位机要用相应的工作模式去对接
 */
public class ModBusRtuProtocol extends ModBusProtocol {
    /**
     * 校验CRC16
     *
     * @param arrCmd
     * @return
     */
    public static int getCRC16(byte[] arrCmd) {
        int iSize = arrCmd.length - 2;

        // 检查:帧长度
        if (iSize < 2) {
            return 0;
        }

        int wCrcMathematics = 0xA001;

        int usCrc16 = 0x00;

        //16位的CRC寄存器
        int byteCrc16Lo = 0xFF;
        int byteCrc16Hi = 0xFF;
        //临时变量
        int byteSaveHi = 0x00;
        int byteSaveLo = 0x00;

        //CRC多项式码的寄存器
        int byteCl = wCrcMathematics % 0x100;
        int byteCh = wCrcMathematics / 0x100;

        for (int i = 0; i < iSize; i++) {
            byteCrc16Lo &= 0xFF;
            byteCrc16Hi &= 0xFF;
            byteSaveHi &= 0xFF;
            byteSaveLo &= 0xFF;

            byteCrc16Lo ^= arrCmd[i];                    //每一个数据与CRC寄存器进行异或
            for (int k = 0; k < 8; k++) {
                byteCrc16Lo &= 0xFF;
                byteCrc16Hi &= 0xFF;

                byteSaveHi = byteCrc16Hi;
                byteSaveLo = byteCrc16Lo;
                byteCrc16Hi /= 2;                         //高位右移一位
                byteCrc16Lo /= 2;                         //低位右移一位
                if ((byteSaveHi & 0x01) == 0x01)         //如果高位字节最后一位为1
                {
                    byteCrc16Lo |= 0x80;                 //则低位字节右移后前面补1
                }                                         //否则自动补0
                if ((byteSaveLo & 0x01) == 0x01)         //如果高位字节最后一位为1，则与多项式码进行异或
                {
                    byteCrc16Hi ^= byteCh;
                    byteCrc16Lo ^= byteCl;
                }
            }
        }


        usCrc16 = (byteCrc16Hi & 0xff) * 0x100 + (byteCrc16Lo & 0xff);

        return usCrc16;
    }

    /**
     * 包装成map参数格式
     *
     * @param param
     * @return
     */
    @Override
    public byte[] packCmd4Map(Map<String, Object> param) {
        // 检查参数是否完备
        if (!super.checkParam(param)) {
            return null;
        }

        ModBusEntity entity = new ModBusEntity();
        entity.setDevAddr(Integer.decode(param.get(ModBusConstants.ADDR).toString()).byteValue());
        entity.setFunc(Integer.decode(param.get(ModBusConstants.FUNC).toString()).byteValue());
        entity.setData(((byte[]) param.get(ModBusConstants.DATA)));


        return packCmd4Entity(entity);
    }

    /**
     * 解包报文
     *
     * @param arrCmd 报文
     * @return 是否成功
     */
    @Override
    public ModBusEntity unPackCmd2Entity(byte[] arrCmd) {
        ModBusEntity entity = new ModBusEntity();

        int iSize = arrCmd.length;
        if (iSize < 4) {
            return null;
        }

        // 地址码
        byte byAddr = arrCmd[0];
        entity.setDevAddr(byAddr);

        // 功能码
        byte byFun = arrCmd[1];
        entity.setFunc(byFun);

        // 数据域
        int iDataSize = iSize - 4;
        entity.setData(new byte[iDataSize]);
        byte[] arrData = entity.getData();
        System.arraycopy(arrCmd, 2, arrData, 0, iDataSize);

        // 校验CRC
        int wCrc16OK = getCRC16(arrCmd);
        byte crcH = (byte) (wCrc16OK & 0xff);
        byte crcL = (byte) ((wCrc16OK & 0xff00) >> 8);
        if (arrCmd[arrCmd.length - 1] == crcL && arrCmd[arrCmd.length - 2] == crcH) {
            return entity;
        }

        return null;
    }

    /**
     * 解码成MAP格式
     *
     * @param arrCmd
     * @return
     */
    @Override
    public Map<String, Object> unPackCmd2Map(byte[] arrCmd) {
        ModBusEntity entity = unPackCmd2Entity(arrCmd);
        if (entity == null) {
            return null;
        }

        Map<String, Object> value = new HashMap<>();
        value.put(ModBusConstants.ADDR, entity.getDevAddr());
        value.put(ModBusConstants.FUNC, entity.getFunc());
        value.put(ModBusConstants.DATA, entity.getData());
        return value;
    }

    /**
     * 打包报文
     *
     * @return 编码是否成功
     */
    @Override
    public byte[] packCmd4Entity(ModBusEntity entity) {
        int iSize = entity.getData().length;

        byte[] arrCmd = new byte[iSize + 4];

        // Slave Address 11
        // Function 01
        // Starting Address Hi 00
        // Starting Address Lo 13
        // No. of Points Hi 00
        // No. of Points Lo 25
        // Error Check (LRC or CRC) ––

        // 地址码
        arrCmd[0] = entity.getDevAddr();

        // 功能码
        arrCmd[1] = entity.getFunc();

        // 数据域
        System.arraycopy(entity.getData(), 0, arrCmd, 2, iSize);

        // 校验CRC
        int wCrc16 = getCRC16(arrCmd);
        arrCmd[arrCmd.length - 2] = (byte) (wCrc16 % 0x100);
        arrCmd[arrCmd.length - 1] = (byte) (wCrc16 / 0x100);

        return arrCmd;
    }

}
