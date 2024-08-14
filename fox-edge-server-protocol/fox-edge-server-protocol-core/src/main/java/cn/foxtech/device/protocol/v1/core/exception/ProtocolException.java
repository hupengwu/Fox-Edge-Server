/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.core.exception;

/**
 * 业务异常
 *
 * @author ruoyi
 */
public final class ProtocolException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 错误提示
     */
    private String message;

    /**
     * 错误明细，内部调试错误
     */
    private String detailMessage;

    /**
     * 空构造方法，避免反序列化问题
     */
    public ProtocolException() {
    }

    public ProtocolException(String message) {
        this.message = message;
    }

    public ProtocolException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    public ProtocolException setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ProtocolException setMessage(String message) {
        this.message = message;
        return this;
    }

    public Integer getCode() {
        return code;
    }
}
