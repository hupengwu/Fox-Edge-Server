package cn.foxtech.device.protocol.v1.hikvision.fire.core.enums;

import lombok.Getter;

public enum AduType {
    systemStatus(1, "上传建筑消防设施系统状态", Sender.device),//
    compStatus(2, "上传建筑消防设施部件运行状态", Sender.device),//
    deviceStatus(21, "上传用户信息传输装置运行状态", Sender.device),//
    deviceOperate(24, "上传用户信息传输装置操作信息", Sender.device),//
    deviceSoftVer(25, "上传用户信息传输装置软件版本", Sender.device),//
    syncClock(90, "同步用户信息传输装置时钟 ", Sender.platform),//
    setInspection(91, "查岗命令", Sender.platform),//
    ;
    @Getter
    private final Integer type;

    @Getter
    private final Sender sender;

    @Getter
    private final String description;

    AduType(Integer type, String description, Sender sender) {
        this.type = type;
        this.sender = sender;
        this.description = description;
    }

    public static AduType getEnum(Integer type) {
        for (AduType cmdType : AduType.values()) {
            if (type.equals(cmdType.type)) {
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
