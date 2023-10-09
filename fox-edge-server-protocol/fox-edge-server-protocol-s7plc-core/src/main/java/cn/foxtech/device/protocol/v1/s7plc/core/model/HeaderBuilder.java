package cn.foxtech.device.protocol.v1.s7plc.core.model;


import cn.foxtech.device.protocol.v1.s7plc.core.enums.EMessageType;
import cn.foxtech.device.protocol.v1.s7plc.core.exceptions.S7CommException;

/**
 * Header构建器
 *
 * @author xingshuang
 */
public class HeaderBuilder {

    private HeaderBuilder() {
        // NOOP
    }

    /**
     * 字节数组数据解析
     *
     * @param data 字节数组数据
     * @return Header
     */
    public static Header fromBytes(final byte[] data) {
        EMessageType messageType = EMessageType.from(data[1]);

        switch (messageType) {
            case JOB:
                return Header.fromBytes(data);
            case ACK:
            case ACK_DATA:
                return AckHeader.fromBytes(data);
            case USER_DATA:
                return null;
            default:
                throw new S7CommException("COTP的pduType数据类型无法解析");
        }
    }
}
