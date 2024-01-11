package cn.foxtech.device.protocol.v1.dahua.fire.core.enums;


import lombok.Getter;


/**
 * 命令字和类型定义表
 * 注意：
 * 1、一个命令字下，有多种类型标志
 * 2、类型标志，可以为null
 * 3、Sender.any是设备和平台，都是可以使用的
 */
public enum AduType {
    // 注册
    register(0x00, 0x00, "注册帧", Sender.device),//
    registerEx(0x00, 0x8C, "注册扩展帧", Sender.device),//

    // 时间同步
    syncClock(0x01, 0x5A, "时钟同步帧", Sender.platform),//

    // 设备应答
    sysStatus(0x02, 0x01, "系统状态帧 ", Sender.device),//
    compStatus(0x02, 0x02, "部件状态帧 ", Sender.device),//
    compAnalog(0x02, 0x03, "部件模拟量帧 ", Sender.device),//
    sysAnalog(0x02, 0x84, "系统模拟量帧 ", Sender.device),//
    compStatusEx(0x02, 0x85, "部件状态扩展帧 ", Sender.device),//
    compAnalogEx(0x02, 0x86, "部件模拟量扩展帧 ", Sender.device),//
    // termParam(0x02, 0xBF, "终端参数帧 ", Sender.device),//说明书中，终端参数帧与同步参数帧重复，可能是说明书写错了
    syncParamFix(0x02, 0xBE, "同步参数帧（定长）", Sender.device),//
    syncParamVar(0x02, 0xBF, "同步参数帧（不定长）", Sender.device),//
    generalData(0x02, 0x87, "通用数据帧", Sender.device),//
    deleteFunc(0x02, 0x8A, "能力删除帧", Sender.device),//
    upgradeStart(0x02, 0xC9, "升级开始帧", Sender.device),//
    upgradeEnd(0x02, 0xCA, "升级结束帧", Sender.device),//


    // 确认
    confirm(0x03, null, "确认帧", Sender.any),//

    // 平台下发
    setParamFix(0x04, 0x64, "设置参数帧（定长）", Sender.platform),//
    setParamVar(0x04, 0x81, "设置参数帧（不定长）", Sender.platform),//
    getParamFix(0x04, 0x82, "查询参数帧（定长）", Sender.platform),//
    getParamVar(0x04, 0x83, "查询参数帧（不定长）", Sender.platform),//
    remoteMute(0x04, 0xF9, "远程消音帧", Sender.platform),//
    generalGet(0x04, 0x88, "查询通用帧", Sender.platform),//
    generalSet(0x04, 0x89, "设置/配置通用帧", Sender.platform),//
    getFuncReq(0x04, 0x8B, "查询能力集", Sender.platform),//
    upgradeReq(0x04, 0xC8, "升级请求帧", Sender.platform),//

    // 查询应答
    getParamRspFix(0x05, 0xBE, "参数查询应答帧（定长）", Sender.device),//
    getParamRspVar(0x05, 0xBF, "参数查询应答帧（不定长）", Sender.device),//
    generalGetRsp(0x05, 0x88, "查询通用应答帧", Sender.device),//
    getFuncRsp(0x05, 0x8B, "查询能力集应答帧", Sender.device),//

    // 否认
    deny(0x06, null, "否认帧", Sender.any),//

    // 激活
    active(0x07, null, "保活帧", Sender.device),//

    // 功能配置
    setFuncReq(0x81, 0xFA, "功能配置帧", Sender.platform),//
    ;
    @Getter
    private final int cmd;

    @Getter
    private final Integer type;

    @Getter
    private final Sender sender;

    @Getter
    private final String description;

    AduType(int cmd, Integer type, String description, Sender sender) {
        this.cmd = cmd;
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

    public static AduType getEnum(Integer cmd, Integer type) {
        for (AduType cmdType : AduType.values()) {
            if (cmd.equals(cmdType.cmd) && type.equals(cmdType.type)) {
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
