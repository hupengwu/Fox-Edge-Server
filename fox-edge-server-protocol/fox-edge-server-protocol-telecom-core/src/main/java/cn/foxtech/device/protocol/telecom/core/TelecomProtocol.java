package cn.foxtech.device.protocol.telecom.core;

import cn.foxtech.device.protocol.core.reference.ByteRef;
import cn.foxtech.device.protocol.core.reference.BytesRef;
import cn.foxtech.device.protocol.core.reference.IntegerRef;

import java.util.HashMap;
import java.util.Map;

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
public class TelecomProtocol {
    /**
     * 协议版本
     */
    public static final String VER = "VER";
    /**
     * 设备地址
     */
    public static final String ADR = "ADR";
    /**
     * 标识码1
     */
    public static final String CID1 = "CID1";
    /**
     * 标识码2
     */
    public static final String CID2 = "CID2";
    /**
     * 数据
     */
    public static final String INFO = "INFO";

    /**
     * Ascii转Hex编码
     *
     * @param chAsciiH
     * @param chAsciiL
     * @param byAt
     * @return
     */
    public static boolean asciiToHex(byte chAsciiH, byte chAsciiL, ByteRef byAt) {
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
                return false;
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
                return false;
            }
        }

        byAt.setValue((byte) ((byAtH << 4) + byAtL));
        return true;
    }

    /**
     * 计算校验码
     *
     * @param arrCmd 数据报
     * @return 16位无符号的校验码
     */
    public static int getUnPackCmdVfyCode(byte[] arrCmd) {
        int iSize = arrCmd.length - 6;
        if (iSize < 12) {
            return 0;
        }

        int index = 1;
        int wVfy = 0x00;

        for (int i = 0; i < iSize; i++) {
            wVfy += (arrCmd[index++] & 0xff);
        }

        wVfy = (~wVfy & 0xffff);
        wVfy++;

        return wVfy;
    }

    /**
     * Hex转Ascii
     *
     * @param byAt
     * @param chAsciiHRef
     * @param chAsciiLRef
     */
    private static void hexToAscii(int byAt, ByteRef chAsciiHRef, ByteRef chAsciiLRef) {
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

        chAsciiHRef.setValue(chAsciiH);
        chAsciiLRef.setValue(chAsciiL);
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

    /**
     * 计算校验码
     *
     * @param arrCmd
     * @return 16位的Hex格式编码
     */
    private static int getPackCmdVfyCode(byte[] arrCmd) {
        int iSize = arrCmd.length - 6;
        if (iSize < 12) {
            return 0;
        }

        int wVfy = 0x00;
        for (int i = 0; i < iSize; i++) {
            wVfy += arrCmd[i + 1] & 0xff;
        }

        wVfy = (~wVfy & 0xffff);
        wVfy++;

        return wVfy;
    }

    private static boolean chkLenCode(int wLenCode, IntegerRef wLen) {
        // 计算校验和
        int sum = 0x00;
        sum = wLenCode & 0x0F;
        sum += (wLenCode >> 4) & 0x0F;
        sum += (wLenCode >> 8) & 0x0F;
        sum = (~sum) + 1;
        int wLenTemp = ((sum & 0xF) << 12) + (wLenCode & 0xFFF);
        if (wLenTemp != wLenCode) {
            return false;
        }

        wLenCode &= 0x0FFF;
        wLen.setValue(wLenCode);

        return true;
    }

    /**
     * 数据编码打包
     *
     * @return
     */
    public static byte[] packCmd4Entity(TelecomEntity entity) {
        // 检查:数据域长度
        int iDataSize = entity.getData().length;
        if (iDataSize > 255) {
            return null;
        }

        // 初始化:命令长度
        //    arrCmd.SetSize(2 + (8 + iDataSize) * 2);
        byte[] arrCmd = new byte[2 + (8 + iDataSize) * 2];


        // 发送命令的协议结构
//SOI	   VER	   ADR		  CID1	    CID2	 LENGTH	 INFO	 CHKSUM		EOI
//起始位 版本号	 设备地址    标识码    标识码    长度	 数据	  校验    结束码
// 1	   1		1		  1		     1		   2	  n         2		 1


        int index = 0;
        ByteRef chASCIIH = new ByteRef();
        ByteRef chASCIIL = new ByteRef();

        // 起始位SOI
        arrCmd[index++] = 0x7E;

        // 版本号VER
        hexToAscii(entity.getVer(), chASCIIH, chASCIIL);
        arrCmd[index++] = chASCIIH.getValue();
        arrCmd[index++] = chASCIIL.getValue();

        // 地址码ADR
        hexToAscii(entity.getAddr(), chASCIIH, chASCIIL);
        arrCmd[index++] = chASCIIH.getValue();
        arrCmd[index++] = chASCIIL.getValue();

        // 标识码CID1
        hexToAscii(entity.getCID1(), chASCIIH, chASCIIL);
        arrCmd[index++] = chASCIIH.getValue();
        arrCmd[index++] = chASCIIL.getValue();

        // 标识码CID2
        hexToAscii(entity.getCID2(), chASCIIH, chASCIIL);
        arrCmd[index++] = chASCIIH.getValue();
        arrCmd[index++] = chASCIIL.getValue();


        // 长度LENGTH
        int wLen = getLenCode(iDataSize * 2);
        hexToAscii((byte) ((wLen >> 8) & 0xff), chASCIIH, chASCIIL);
        arrCmd[index++] = chASCIIH.getValue();
        arrCmd[index++] = chASCIIL.getValue();
        hexToAscii((byte) (wLen & 0xff), chASCIIH, chASCIIL);
        arrCmd[index++] = chASCIIH.getValue();
        arrCmd[index++] = chASCIIL.getValue();


        // 数据
        for (int i = 0; i < iDataSize; i++) {
            hexToAscii(entity.getData()[i], chASCIIH, chASCIIL);
            arrCmd[index++] = chASCIIH.getValue();
            arrCmd[index++] = chASCIIL.getValue();
        }

        // 校验
        int wVfy = getPackCmdVfyCode(arrCmd);
        hexToAscii((byte) ((wVfy >> 8) & 0xff), chASCIIH, chASCIIL);
        arrCmd[index++] = chASCIIH.getValue();
        arrCmd[index++] = chASCIIL.getValue();
        hexToAscii(((byte) (wVfy & 0xff)), chASCIIH, chASCIIL);
        arrCmd[index++] = chASCIIH.getValue();
        arrCmd[index++] = chASCIIL.getValue();


        // 包尾
        arrCmd[arrCmd.length - 1] = 0x0D;

        return arrCmd;
    }

    /**
     * 解码函数
     *
     * @param arrCmd  数据报
     * @param byVer   版本号
     * @param byAddr  设备地址
     * @param byCID1  标识码1
     * @param byCID2  标识码2
     * @param arrData 数据段
     * @return 是否解码正确
     */
    private static boolean unPackCmd(byte[] arrCmd, ByteRef byVer, ByteRef byAddr, ByteRef byCID1, ByteRef byCID2, BytesRef arrData) {
        int iSize = arrCmd.length;
        if (iSize < 18) {
            return false;
        }

        int index = 0;
        byte chHigh = 0;
        byte chLow = 0;

        // 起始位SOI
        if (arrCmd[index++] != 0x7E) {
            return false;
        }

        // 结束码EOI
        if ((arrCmd[iSize - 1] != 0x0D)) {
            return false;
        }


        // 版本号VER
        chHigh = arrCmd[index++];
        chLow = arrCmd[index++];
        if (!asciiToHex(chHigh, chLow, byVer)) {
            return false;
        }

        // 设备地址ADR
        chHigh = arrCmd[index++];
        chLow = arrCmd[index++];
        if (!asciiToHex(chHigh, chLow, byAddr)) {
            return false;
        }

        // 标识码CID1
        chHigh = arrCmd[index++];
        chLow = arrCmd[index++];
        if (!asciiToHex(chHigh, chLow, byCID1)) {
            return false;
        }

        // 标识码CID2
        chHigh = arrCmd[index++];
        chLow = arrCmd[index++];
        if (!asciiToHex(chHigh, chLow, byCID2)) {
            return false;
        }

        // 长度LENGTH10f0
        int wLen = 0;
        chHigh = arrCmd[index++];
        chLow = arrCmd[index++];
        ByteRef byRef = new ByteRef();
        if (!asciiToHex(chHigh, chLow, byRef)) {
            return false;
        }
        wLen = (byRef.getValue() & 0xff) * 0x100;
        chHigh = arrCmd[index++];
        chLow = arrCmd[index++];
        if (!asciiToHex(chHigh, chLow, byRef)) {
            return false;
        }
        wLen += (byRef.getValue() & 0xff);


        // 检查:帧长度编码
        IntegerRef wLenRef = new IntegerRef();
        if (!chkLenCode(wLen, wLenRef)) {
            return false;
        }

        // 编码前的长度，转换为实际长度
        wLen &= 0x0FFF;

        // 检查:命令长度
        if (arrCmd.length != 18 + wLen) {
            return false;
        }

        // 初始化数据域
        int iDataSize = wLen / 2;
        arrData.setValue(new byte[iDataSize]);


        // 数据INFO
        for (int i = 0; i < iDataSize; i++) {
            chHigh = arrCmd[index++];
            chLow = arrCmd[index++];
            if (!asciiToHex(chHigh, chLow, byRef)) {
                return false;
            }

            arrData.getValue()[i] = byRef.getValue();
        }

        // 校验
        int wVfy = 0;
        chHigh = arrCmd[index++];
        chLow = arrCmd[index++];
        if (!asciiToHex(chHigh, chLow, byRef)) {
            return false;
        }
        wVfy = (byRef.getValue() & 0xff) * 0x100;

        chHigh = arrCmd[index++];
        chLow = arrCmd[index++];
        if (!asciiToHex(chHigh, chLow, byRef)) {
            return false;
        }
        wVfy += (byRef.getValue() & 0xff);


        // 检查:校验和
        int wVfyOK = getUnPackCmdVfyCode(arrCmd);
        return wVfyOK == wVfy;
    }

    /**
     * 包装成另一种格式
     *
     * @param arrCmd
     * @return
     */
    public static TelecomEntity unPackCmd2Entity(byte[] arrCmd) {
        ByteRef byVer = new ByteRef();
        ByteRef byAddr = new ByteRef();
        ByteRef byCID1 = new ByteRef();
        ByteRef byCID2 = new ByteRef();
        BytesRef arrData = new BytesRef();
        if (!TelecomProtocol.unPackCmd(arrCmd, byVer, byAddr, byCID1, byCID2, arrData)) {
            return null;
        }

        TelecomEntity entity = new TelecomEntity();
        entity.setVer(byVer.getValue());
        entity.setAddr(byAddr.getValue());
        entity.setCID1(byCID1.getValue());
        entity.setCID2(byCID2.getValue());
        entity.setData(arrData.getValue());
        return entity;
    }

    /**
     * 包装成另一种格式
     *
     * @param arrCmd
     * @return
     */
    public static Map<String, Object> unPackCmd2Map(byte[] arrCmd) {
        Map<String, Object> value = new HashMap<>();

        TelecomEntity entity = TelecomProtocol.unPackCmd2Entity(arrCmd);


        value.put(VER, entity.getVer());
        value.put(ADR, entity.getAddr());
        value.put(CID1, entity.getCID1());
        value.put(CID2, entity.getCID2());
        value.put(INFO, entity.getData());
        return value;
    }

    /**
     * 检查参数是否完备
     *
     * @param param
     * @return
     */
    public static boolean checkParam(Map<String, Object> param) {
        if (!param.containsKey(VER) || !(param.get(VER) instanceof Byte || param.get(VER) instanceof Integer)) {
            return false;
        }

        if (!param.containsKey(ADR) || !(param.get(ADR) instanceof Byte || param.get(ADR) instanceof Integer)) {
            return false;
        }

        if (!param.containsKey(CID1) || !(param.get(CID1) instanceof Byte || param.get(CID1) instanceof Integer)) {
            return false;
        }

        if (!param.containsKey(CID2) || !(param.get(CID2) instanceof Byte || param.get(CID2) instanceof Integer)) {
            return false;
        }

        return param.containsKey(INFO) && param.get(INFO) instanceof byte[];
    }

    /**
     * 包装成map参数格式
     *
     * @param param
     * @return
     */
    public static byte[] packCmd4Map(Map<String, Object> param) {
        // 检查参数是否完备
        if (!checkParam(param)) {
            return null;
        }


        TelecomEntity entity = new TelecomEntity();
        entity.setVer(Byte.decode(param.get(VER).toString()));
        entity.setAddr(Byte.decode(param.get(ADR).toString()));
        entity.setCID1(Byte.decode(param.get(CID1).toString()));
        entity.setCID2(Byte.decode(param.get(CID2).toString()));
        entity.setData((byte[]) param.get(INFO));

        return packCmd4Entity(entity);
    }
}

