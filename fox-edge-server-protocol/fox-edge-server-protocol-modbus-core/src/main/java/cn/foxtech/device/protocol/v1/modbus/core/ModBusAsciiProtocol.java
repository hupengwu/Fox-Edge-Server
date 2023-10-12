package cn.foxtech.device.protocol.v1.modbus.core;


import cn.foxtech.device.protocol.v1.core.reference.ByteRef;
import cn.foxtech.device.protocol.v1.core.reference.BytesRef;
import cn.foxtech.device.protocol.v1.core.reference.IntegerRef;

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
public class ModBusAsciiProtocol extends ModBusProtocol {
    /**
     * 16进制转换为ASCII格式
     *
     * @param byAt          16进制
     * @param chAsciiHValue 高位ASCII
     * @param chAsciiLValue 低位ASCII
     */
    private static void hexToAscii(byte byAt, ByteRef chAsciiHValue, ByteRef chAsciiLValue) {
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
                return;
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
                return;
        }

        chAsciiHValue.setValue(chAsciiH);
        chAsciiLValue.setValue(chAsciiL);
        return;
    }

    /**
     * 生成CRC8编码
     *
     * @param arrCmd     报文
     * @param wLRC8Value CRC8编码
     * @return
     */
    private static boolean getLRC8(byte[] arrCmd, IntegerRef wLRC8Value) {
        int iSize = arrCmd.length - 5;
        if (iSize < 4) {
            return false;
        }
        if (iSize % 2 != 0) {
            return false;
        }

        // byte *byAt = arrCmd.GetData()+1;
        byte byAt = 1;

        byte chHigh, chLow;
        ByteRef byHexAt = new ByteRef();

        int uchLRC = 0; // LRC char initialized

        do  // pass through message buffer
        {
            chHigh = arrCmd[byAt++];
            chLow = arrCmd[byAt++];
            if (!asciiToHex(chHigh, chLow, byHexAt)) {
                return false;
            }

            uchLRC += byHexAt.getValue(); // add buffer byte without carry

            iSize -= 2;
        } while (iSize > 0);

        uchLRC &= 0xff;

        byte byLRC = (byte) (-((char) uchLRC));

        ByteRef chHighValue = new ByteRef();
        ByteRef chLowValue = new ByteRef();
        hexToAscii(byLRC, chHighValue, chLowValue);

        wLRC8Value.setValue((short) ((((short) chHighValue.getValue()) << 8) | chLowValue.getValue()));

        return true;
    }

    /**
     * ASCII转16进制
     *
     * @param chAsciiH 高位ASCII
     * @param chAsciiL 低位ASCII
     * @param byAt     16进制编码
     * @return 是否成功
     */
    private static boolean asciiToHex(byte chAsciiH, byte chAsciiL, ByteRef byAt) {
        byte byAtH = 0x00;
        byte byAtL = 0x00;

        if ((chAsciiH >= 0x30) && (chAsciiH <= 0x39)) {
            byAtH = (byte) (chAsciiH - 0x30);
        } else if ((chAsciiH >= 0x41) && (chAsciiH <= 0x46)) {
            byAtH = (byte) (chAsciiH - 0x37);
        } else {
            return false;
        }

        if ((chAsciiL >= 0x30) && (chAsciiL <= 0x39)) {
            byAtL = (byte) (chAsciiL - 0x30);
        } else if ((chAsciiL >= 0x41) && (chAsciiL <= 0x46)) {
            byAtL = (byte) (chAsciiL - 0x37);
        } else {
            return false;
        }

        byAt.setValue((byte) ((byAtH << 4) + byAtL));
        return true;
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
     * 包装成另一种格式
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
     * 打包成ASCII格式
     *
     * @return 是否打包成功
     */
    @Override
    public byte[] packCmd4Entity(ModBusEntity entity) {
        int iDataSize = entity.getData().length;

        byte[] arrCmd = new byte[2 * iDataSize + 9];

        // Slave Address 11
        // Function 01
        // Starting Address Hi 00
        // Starting Address Lo 13
        // No. of Points Hi 00
        // No. of Points Lo 25
        // Error Check (LRC or CRC) ––

        ByteRef chHighAsciiValue = new ByteRef();
        ByteRef chLowAsciiValue = new ByteRef();
        // byte chHighAscii, chLowAscii;
        byte byAt = 0;

        // 头标
        arrCmd[byAt++] = 0x3A;

        // 地址码
        hexToAscii(entity.getDevAddr(), chHighAsciiValue, chLowAsciiValue);
        arrCmd[byAt++] = chHighAsciiValue.getValue();
        arrCmd[byAt++] = chLowAsciiValue.getValue();

        // 功能码
        hexToAscii(entity.getFunc(), chHighAsciiValue, chLowAsciiValue);
        arrCmd[byAt++] = chHighAsciiValue.getValue();
        arrCmd[byAt++] = chLowAsciiValue.getValue();

        // 数据域
        // byte *byDat = arrData.GetData();
        for (int i = 0; i < iDataSize; i++) {
            hexToAscii(entity.getData()[i], chHighAsciiValue, chLowAsciiValue);
            arrCmd[byAt++] = chHighAsciiValue.getValue();
            arrCmd[byAt++] = chLowAsciiValue.getValue();
        }

        // 校验CRC
        IntegerRef wLRC8 = new IntegerRef();
        if (!getLRC8(arrCmd, wLRC8)) {
            return null;
        }
        arrCmd[byAt++] = (byte) (wLRC8.getValue() / 0x100);
        arrCmd[byAt++] = (byte) (wLRC8.getValue() % 0x100);

        // 结束符
        arrCmd[byAt++] = 0x0D;
        arrCmd[byAt++] = 0x0A;

        return arrCmd;
    }

    /**
     * 包装成map参数格式
     *
     * @param param
     * @param arrCmd
     * @return
     */
    public byte[] packCmd4Map(Map<String, Object> param, BytesRef arrCmd) {
        // 检查参数是否完备
        if (!checkParam(param)) {
            return null;
        }

        byte byAddr = Integer.decode(param.get(ModBusConstants.ADDR).toString()).byteValue();
        byte byFunc = Integer.decode(param.get(ModBusConstants.FUNC).toString()).byteValue();
        byte[] arrData = ((byte[]) param.get(ModBusConstants.DATA));

        ModBusEntity entity = new ModBusEntity();
        entity.setDevAddr(byAddr);
        entity.setFunc(byFunc);
        entity.setData(arrData);

        return packCmd4Entity(entity);
    }

    @Override
    public ModBusEntity unPackCmd2Entity(byte[] arrCmd) {
        ModBusEntity entity = new ModBusEntity();

        // Slave Address 11
        // Function 01
        // Byte Count 05
        // Data (Coils 27–20) CD
        // Data (Coils 35–28) 6B
        // Data (Coils 43–36) B2
        // Data (Coils 51–44) 0E
        // Data (Coils 56–52) 1B
        // Error Check (LRC or CRC) ––

        int iSize = arrCmd.length;
        if (iSize < 9) {
            return null;
        }
        if ((iSize % 2) != 1) {
            return null;
        }

        // byte* byArrCmd = arrCmd.GetData()+1;
        byte byArrCmd = 1;
        // byte* byAt = NULL;

        byte chHigh = 0x00;
        byte chLow = 0x00;

        // 起始位
        if (arrCmd[0] != 0x3A) {
            return null;
        }

        // 结束码
        if (arrCmd[iSize - 2] != 0x0D) {
            return null;
        }
        if (arrCmd[iSize - 1] != 0x0A) {
            return null;
        }

        // 设备地址ADR
        chHigh = arrCmd[byArrCmd++];
        chLow = arrCmd[byArrCmd++];
        ByteRef byAddrValue = new ByteRef();
        if (!asciiToHex(chHigh, chLow, byAddrValue)) {
            return null;
        }
        entity.setDevAddr(byAddrValue.getValue());

        // 功能码
        chHigh = arrCmd[byArrCmd++];
        chLow = arrCmd[byArrCmd++];
        ByteRef byFunValue = new ByteRef();
        if (!asciiToHex(chHigh, chLow, byFunValue)) {
            return null;
        }
        entity.setFunc(byFunValue.getValue());

        // 初始化数据域
        int iDataSize = (iSize - 9) / 2;
        entity.setData(new byte[iDataSize]);
        byte[] arrData = entity.getData();
        byte byAt = 0;
        ByteRef byAtValue = new ByteRef();
        for (int i = 0; i < iDataSize; i++) {
            chHigh = arrCmd[byArrCmd++];
            chLow = arrCmd[byArrCmd++];
            if (!asciiToHex(chHigh, chLow, byAtValue)) {
                return null;
            }
            arrData[byAt++] = byAtValue.getValue();
        }

        // 校验
        int wVfy = arrCmd[byArrCmd++] * 0x100;
        wVfy += arrCmd[byArrCmd++];

        // 检查:校验和
        IntegerRef wVfyOKValue = new IntegerRef();
        if (!getLRC8(arrCmd, wVfyOKValue)) {
            return null;
        }
        if (wVfyOKValue.getValue() != wVfy) {
            return null;
        }

        return entity;
    }
}
