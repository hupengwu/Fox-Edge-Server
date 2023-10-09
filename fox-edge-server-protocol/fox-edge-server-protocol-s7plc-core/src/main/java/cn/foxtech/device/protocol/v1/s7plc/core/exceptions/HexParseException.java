package cn.foxtech.device.protocol.v1.s7plc.core.exceptions;

/**
 * 16进制解析异常
 *
 * @author ShuangPC
 */
public class HexParseException extends RuntimeException {

    public HexParseException() {
        super();
    }

    public HexParseException(String message) {
        super(message);
    }

    public HexParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public HexParseException(Throwable cause) {
        super(cause);
    }

    protected HexParseException(String message, Throwable cause,
                                boolean enableSuppression,
                                boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
