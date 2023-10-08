package cn.foxtech.device.protocol.v1.siemens.s7.core.model;


import cn.foxtech.device.protocol.v1.siemens.s7.core.common.IObjectByteArray;
import cn.foxtech.device.protocol.v1.siemens.s7.core.common.buff.ByteReadBuff;
import cn.foxtech.device.protocol.v1.siemens.s7.core.common.buff.ByteWriteBuff;
import cn.foxtech.device.protocol.v1.siemens.s7.core.enums.EFunctionCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 设置通信参数
 *
 * @author xingshuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SetupComParameter extends Parameter implements IObjectByteArray {

    public static final int BYTE_LENGTH = 8;

    /**
     * 预留 <br>
     * 字节大小：1 <br>
     * 字节序数：1
     */
    private byte reserved = (byte) 0x00;

    /**
     * Ack队列的大小（主叫）（大端）<br>
     * 字节大小：2 <br>
     * 字节序数：2-3
     */
    private int maxAmqCaller = 0x0001;

    /**
     * Ack队列的大小（被叫）（大端）<br>
     * 字节大小：2 <br>
     * 字节序数：4-5
     */
    private int maxAmqCallee = 0x0001;

    /**
     * PDU长度（大端）<br>
     * 字节大小：2 <br>
     * 字节序数：6-7
     */
    private int pduLength = 0x0000;

    @Override
    public int byteArrayLength() {
        return BYTE_LENGTH;
    }

    @Override
    public byte[] toByteArray() {
        return ByteWriteBuff.newInstance(BYTE_LENGTH)
                .putByte(this.functionCode.getCode())
                .putByte(this.reserved)
                .putShort(this.maxAmqCaller)
                .putShort(this.maxAmqCallee)
                .putShort(this.pduLength)
                .getData();
    }

    /**
     * 字节数组数据解析
     *
     * @param data 字节数组数据
     * @return SetupComParameter
     */
    public static SetupComParameter fromBytes(final byte[] data) {
        ByteReadBuff buff = new ByteReadBuff(data);
        SetupComParameter setupComParameter = new SetupComParameter();
        setupComParameter.functionCode = EFunctionCode.from(buff.getByte());
        setupComParameter.reserved = buff.getByte();
        setupComParameter.maxAmqCaller = buff.getUInt16();
        setupComParameter.maxAmqCallee = buff.getUInt16();
        setupComParameter.pduLength = buff.getUInt16();
        return setupComParameter;
    }

    /**
     * 创建默认的设置通信参数，默认最大PDU长度240
     *
     * @param pduLength PDU长度
     * @return SetupComParameter
     */
    public static SetupComParameter createDefault(int pduLength) {
        SetupComParameter parameter = new SetupComParameter();
        parameter.functionCode = EFunctionCode.SETUP_COMMUNICATION;
        parameter.reserved = (byte) 0x00;
        parameter.maxAmqCaller = 1;
        parameter.maxAmqCallee = 1;
        // 默认最大PDU长度240
        parameter.pduLength = pduLength;
        return parameter;
    }
}
