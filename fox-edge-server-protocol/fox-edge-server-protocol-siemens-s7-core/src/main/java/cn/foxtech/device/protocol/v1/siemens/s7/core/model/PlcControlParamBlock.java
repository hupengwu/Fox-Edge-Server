package cn.foxtech.device.protocol.v1.siemens.s7.core.model;


import cn.foxtech.device.protocol.v1.siemens.s7.core.common.IObjectByteArray;

/**
 * PLC控制参数块
 *
 * @author xingshuang
 */
public class PlcControlParamBlock implements IObjectByteArray {

    @Override
    public int byteArrayLength() {
        return 0;
    }

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }
}
