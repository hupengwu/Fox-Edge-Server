package cn.foxtech.device.protocol.v1.s7plc.core.exceptions;

/**
 * 认证的运行异常
 *
 * @author xingshuang
 */
public class AuthenticationException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AuthenticationException() {
        super();
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(Throwable cause) {
        super(cause);
    }

}
