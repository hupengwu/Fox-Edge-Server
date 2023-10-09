package cn.foxtech.device.protocol.v1.s7plc.core.model;


import cn.foxtech.device.protocol.v1.s7plc.core.enums.EFunctionCode;
import cn.foxtech.device.protocol.v1.s7plc.core.common.IObjectByteArray;
import cn.foxtech.device.protocol.v1.s7plc.core.enums.EMessageType;
import cn.foxtech.device.protocol.v1.s7plc.core.exceptions.S7CommException;
import lombok.Data;

/**
 * 数据
 *
 * @author xingshuang
 */
@Data
public class Datum implements IObjectByteArray {

    /**
     * 根据消息类型和功能码，对字节数组数据进行解析
     *
     * @param data         字节数组数据
     * @param messageType  头部的消息类型
     * @param functionCode 参数部分的功能码
     * @return Datum
     */
    public static Datum fromBytes(final byte[] data, EMessageType messageType, EFunctionCode functionCode) {

        switch (functionCode) {
            case READ_VARIABLE:
            case WRITE_VARIABLE:
                return ReadWriteDatum.fromBytes(data, messageType, functionCode);
            case DOWNLOAD:
            case UPLOAD:
                return UpDownloadDatum.fromBytes(data, messageType);
            default:
                throw new S7CommException("数据部分无法解析");
        }
    }

    @Override
    public int byteArrayLength() {
        return 0;
    }

    @Override
    public byte[] toByteArray() {
        return new byte[0];
    }
}
