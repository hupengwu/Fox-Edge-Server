package cn.foxtech.device.protocol.iec104.core.enums;

import lombok.Getter;

/**
 * 传送原因的枚举值
 */
public enum CotReasonEnum {
    /**
     *
     */
    c0(0, "未用", true, true),
    /**
     *
     */
    c1(1, "周期、循环", true, false),
    /**
     *
     */
    c2(2, "背景扫描", true, false),
    /**
     *
     */
    c3(3, "突发", true, false),
    /**
     *
     */
    c4(4, "初始化完成", true, false),
    /**
     *
     */
    c5(5, "请求或者被请求", true, true),
    /**
     *
     */
    active(6, "激活", false, true),
    /**
     *
     */
    activeConfirmed(7, "激活确认", true, false),
    /**
     *
     */
    c8(8, "停止激活", false, true),
    /**
     *
     */
    stopActiveConfirmed(9, "停止激活确认", true, false),
    /**
     *
     */
    activeEnded(10, "激活终止", true, false),
    /**
     *
     */
    c11(11, "远方命令引起的返送信息", true, false),
    /**
     *
     */
    c12(12, "当地命令引起的返送信息", true, false),
    /**
     *
     */
    responseStationCall(20, "响应站召唤", true, false),
    /**
     *
     */
    c21(21, "响应第1组召唤", true, false),
    /**
     *
     */
    c22(22, "响应第2组召唤", true, false),
    /**
     *
     */
    c23(23, "响应第3组召唤", true, false),
    /**
     *
     */
    c24(24, "响应第4组召唤", true, false),
    /**
     *
     */
    c25(25, "响应第5组召唤", true, false),
    /**
     *
     */
    c26(26, "响应第6组召唤", true, false),
    /**
     *
     */
    c27(27, "响应第7组召唤", true, false),
    /**
     *
     */
    c28(28, "响应第8组召唤", true, false),
    /**
     *
     */
    c29(29, "响应第9组召唤", true, false),
    /**
     *
     */
    c30(30, "响应第10组召唤", true, false),
    /**
     *
     */
    c31(31, "响应第11组召唤", true, false),
    /**
     *
     */
    c32(32, "响应第12组召唤", true, false),
    /**
     *
     */
    c33(33, "响应第13组召唤", true, false),
    /**
     *
     */
    c34(34, "响应第14组召唤", true, false),
    /**
     *
     */
    c35(35, "响应第15组召唤", true, false),
    /**
     *
     */
    c36(36, "响应第36组召唤", true, false),
    /**
     *
     */
    c37(37, "响应累积量站召唤", true, false),
    /**
     *
     */
    c38(38, "响应第1组累积量召唤", true, false),
    /**
     *
     */
    c39(39, "响应第2组累积量召唤", true, false),
    /**
     *
     */
    c40(40, "响应第3组累积量召唤", true, false),
    /**
     *
     */
    c41(41, "响应第4组累积量召唤", true, false),
    /**
     *
     */
    unknownTypeId(44, "未知的类型标识", true, false),
    /**
     *
     */
    unknownTransferReason(45, "未知的传送原因", true, false),
    /**
     *
     */
    unknownAsduAddress(46, "未知的应用服务数据单元公共地址", true, false),
    /**
     *
     */
    unknownInfoObjectAddress(47, "未知的信息对象地址", true, false),
    /**
     *
     */
    c48(48, "遥控执行软压板状态错误", true, false),
    /**
     *
     */
    c49(49, "遥控执行时间戳错误", true, false),
    /**
     *
     */
    c50(50, "遥控执行数字签名认证错误", true, false),
    ;

    @Getter
    private final int value;

    @Getter
    private final String msg;

    @Getter
    private final boolean up;
    @Getter
    private final boolean down;


    CotReasonEnum(int value, String msg, boolean up, boolean down) {
        this.value = value;
        this.msg = msg;
        this.up = up;
        this.down = down;
    }

    public static CotReasonEnum getEnum(int value) {
        for (CotReasonEnum type : CotReasonEnum.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return this.msg;
    }


    /**
     * 对话级的终止：也就是一问多答中，某个答的终止，它可能不是全流程的终止
     *
     * @param value 数值
     * @return 是否为结束符
     */
    public static boolean isEnd(int value) {
        // 激活确认
        if (value == CotReasonEnum.activeConfirmed.getValue()) {
            return true;
        }
        // 停止激活确认
        if (value == CotReasonEnum.stopActiveConfirmed.getValue()) {
            return true;
        }
        // 激活终止
        if (value == CotReasonEnum.activeEnded.getValue()) {
            return true;
        }
        // 未知的类型标识
        if (value == CotReasonEnum.unknownTypeId.getValue()) {
            return true;
        }
        // 未知的传送原因
        if (value == CotReasonEnum.unknownTransferReason.value) {
            return true;
        }
        // 未知的应用服务数据单元公共地址
        if (value == CotReasonEnum.unknownAsduAddress.value) {
            return true;
        }
        // 未知的信息对象地址
        return value == CotReasonEnum.unknownInfoObjectAddress.value;

        // 其他都是非结束原因
    }
}
