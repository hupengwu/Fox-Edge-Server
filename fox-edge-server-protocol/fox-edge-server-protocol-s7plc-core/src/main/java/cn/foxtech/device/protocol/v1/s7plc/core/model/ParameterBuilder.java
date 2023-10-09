package cn.foxtech.device.protocol.v1.s7plc.core.model;


import cn.foxtech.device.protocol.v1.s7plc.core.enums.EFunctionCode;
import cn.foxtech.device.protocol.v1.s7plc.core.enums.EMessageType;
import cn.foxtech.device.protocol.v1.s7plc.core.exceptions.S7CommException;

/**
 * @author xingshuang
 */
public class ParameterBuilder {

    private ParameterBuilder() {
        // NOOP
    }

    /**
     * 字节数组数据解析
     *
     * @param data        字节数组数据
     * @param messageType 消息类型
     * @return Parameter
     */
    public static Parameter fromBytes(final byte[] data, EMessageType messageType) {
        EFunctionCode functionCode = EFunctionCode.from(data[0]);

        switch (functionCode) {
            case CPU_SERVICES:
                return null;
            case READ_VARIABLE:
            case WRITE_VARIABLE:
                return ReadWriteParameter.fromBytes(data);
            case START_DOWNLOAD:
                return messageType == EMessageType.ACK_DATA ? new Parameter(EFunctionCode.START_DOWNLOAD) : StartDownloadParameter.fromBytes(data);
            case DOWNLOAD:
                return messageType == EMessageType.ACK_DATA ? new Parameter(EFunctionCode.DOWNLOAD) : DownloadParameter.fromBytes(data);
            case END_DOWNLOAD:
                return messageType == EMessageType.ACK_DATA ? new Parameter(EFunctionCode.END_DOWNLOAD) : EndDownloadParameter.fromBytes(data);
            case START_UPLOAD:
                return messageType == EMessageType.ACK_DATA ? StartUploadAckParameter.fromBytes(data) : StartUploadParameter.fromBytes(data);
            case UPLOAD:
                return messageType == EMessageType.ACK_DATA ? UploadAckParameter.fromBytes(data) : UploadParameter.fromBytes(data);
            case END_UPLOAD:
                return messageType == EMessageType.ACK_DATA ? new Parameter(EFunctionCode.END_UPLOAD) : EndUploadParameter.fromBytes(data);
            case PLC_CONTROL:
                if (messageType == EMessageType.ACK) {
                    return PlcControlAckParameter.fromBytes(data);
                }
                return messageType == EMessageType.ACK_DATA ? new Parameter(EFunctionCode.PLC_CONTROL) : PlcControlParameter.fromBytes(data);
            case PLC_STOP:
                return messageType == EMessageType.ACK_DATA ? new Parameter(EFunctionCode.PLC_STOP) : PlcStopParameter.fromBytes(data);
            case SETUP_COMMUNICATION:
                return SetupComParameter.fromBytes(data);
            default:
                throw new S7CommException("Parameter的功能码不存在");
        }
    }
}
