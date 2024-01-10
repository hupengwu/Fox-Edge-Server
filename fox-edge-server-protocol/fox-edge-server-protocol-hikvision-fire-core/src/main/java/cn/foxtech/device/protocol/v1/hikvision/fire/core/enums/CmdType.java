package cn.foxtech.device.protocol.v1.hikvision.fire.core.enums;


import lombok.Getter;


/**
 * 命令字和类型定义表
 * 注意：
 * 1、一个命令字下，有多种类型标志
 * 2、类型标志，可以为null
 * 3、Sender.any是设备和平台，都是可以使用的
 */
public enum CmdType {
    control(1, "控制"),//
    send(2, "发送"),//
    confirm(3, "确认"),//
    request(4, "请求"),//
    respond(5, "应答"),//
    deny(6, "否认"),//
    ;
    @Getter
    private final int cmd;

    @Getter
    private final String description;

    CmdType(int cmd, String description) {
        this.cmd = cmd;
        this.description = description;
    }

    public static CmdType getEnum(Integer cmd) {
        for (CmdType cmdType : CmdType.values()) {
            if (cmd.equals(cmdType.cmd)) {
                return cmdType;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.description;
    }
}
