package cn.foxtech.device.protocol.v1.s7plc.core.model;


import cn.foxtech.device.protocol.v1.s7plc.core.enums.EFunctionCode;
import cn.foxtech.device.protocol.v1.s7plc.core.common.IObjectByteArray;
import cn.foxtech.device.protocol.v1.s7plc.core.common.buff.ByteReadBuff;
import cn.foxtech.device.protocol.v1.s7plc.core.common.buff.ByteWriteBuff;
import cn.foxtech.device.protocol.v1.s7plc.core.exceptions.S7CommException;
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
}
