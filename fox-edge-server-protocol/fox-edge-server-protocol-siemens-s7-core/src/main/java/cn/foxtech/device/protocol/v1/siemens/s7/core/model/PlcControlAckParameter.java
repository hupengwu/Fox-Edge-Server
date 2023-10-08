package cn.foxtech.device.protocol.v1.siemens.s7.core.model;


import cn.foxtech.device.protocol.v1.siemens.s7.core.exceptions.S7CommException;
import cn.foxtech.device.protocol.v1.siemens.s7.core.common.IObjectByteArray;
import cn.foxtech.device.protocol.v1.siemens.s7.core.common.buff.ByteReadBuff;
import cn.foxtech.device.protocol.v1.siemens.s7.core.common.buff.ByteWriteBuff;
import cn.foxtech.device.protocol.v1.siemens.s7.core.enums.EFunctionCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 启动参数
 *
 * @author xingshuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PlcControlAckParameter extends Parameter implements IObjectByteArray {

    /**
     * 未知字节
     */
    private byte unknownByte;

    public PlcControlAckParameter() {
        this.functionCode = EFunctionCode.PLC_CONTROL;
    }

    @Override
    public int byteArrayLength() {
        return 2;
    }

    @Override
    public byte[] toByteArray() {
        return ByteWriteBuff.newInstance(2)
                .putByte(this.functionCode.getCode())
                .putByte(this.unknownByte)
                .getData();
    }

    /**
     * 字节数组数据解析
     *
     * @param data 字节数组数据
     * @return PlcControlAckParameter
     */
    public static PlcControlAckParameter fromBytes(final byte[] data) {
        if (data.length < 1) {
            throw new S7CommException("PlcControlAckParameter解析有误，PlcControlAckParameter字节数组长度 < 1");
        }
        ByteReadBuff buff = new ByteReadBuff(data);
        PlcControlAckParameter parameter = new PlcControlAckParameter();
        parameter.functionCode = EFunctionCode.from(buff.getByte());
        parameter.unknownByte = buff.getByte();
        return parameter;
    }
}
