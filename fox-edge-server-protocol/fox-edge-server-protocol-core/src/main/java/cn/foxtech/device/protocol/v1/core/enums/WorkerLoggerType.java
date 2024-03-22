package cn.foxtech.device.protocol.v1.core.enums;


import lombok.Getter;


public enum WorkerLoggerType {
    send(0, "发送"),//
    recv(1, "接收"),//

    ;
    @Getter
    private final int code;

    @Getter
    private final String name;

    WorkerLoggerType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static WorkerLoggerType getEnum(Integer code) {
        for (WorkerLoggerType value : WorkerLoggerType.values()) {
            if (code.equals(value.code)) {
                return value;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
