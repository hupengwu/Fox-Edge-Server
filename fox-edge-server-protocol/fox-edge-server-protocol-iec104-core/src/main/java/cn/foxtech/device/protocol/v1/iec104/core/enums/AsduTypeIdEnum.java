/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.iec104.core.enums;


import lombok.Getter;


/**
 * Asdu的枚举值
 */
public enum AsduTypeIdEnum {

    /**
     * 单点摇信
     */
    singlePointSignal(0x01,"单点信息"),
    /**
     * 双点摇信
     */
    twoPointTeleindication(0x03,"双点信息"),
    /**
     * 双点摇信
     */
    stepPositionInformation(0x05,"步位置信息"),
    /**
     * 测量值 归一化值 遥测
     */
    normalizedTelemetry(0x07,"32比特串"),
    /**
     * 测量值 归一化值 遥测
     */
    normalizedMeasuredValue(0x09,"测量值，归一化值"),
    /**
     * 测量值  标度化值 遥测
     */
    scaledTelemetry(0x0B,"测量值，标度化值"),
    /**
     * 测量值 短浮点数 遥测   Short floating point
     */
    shortFloatingPointTelemetry(0x0D,"测量值，短浮点数"),
    /**
     * 测量值 短浮点数 遥测   Short floating point
     */
    cumulativeMeasurement(0x0F,"累计量"),
    /**
     * 测量值 短浮点数 遥测   Short floating point
     */
    groupSinglePointInformation(0x14,"具有状态变位检出的成组单点信息"),
    /**
     * 测量值 短浮点数 遥测   Short floating point
     */
    noQualityNormalizedMeasuredValue(0x15,"测量值，不带品质描述的归一化值"),
    /**
     * 摇信带时标 单点
     */
    onePointTimeWithTime(0x1E,"带时标CP56Time2a的单点信息"),
    /**
     * 摇信带时标 双点
     */
    twoPointTimeWithTime(0x1F,"带时标CP56Time2a的双点信息"),
    /**
     * 摇信带时标 双点
     */
    stepPositionInformationWithTime(0x20,"带时标CP56Time2a的步位置信息"),
    /**
     * 摇信带时标 双点
     */
    bit32StringWithTime(0x21,"带时标CP56Time2a的32位串"),
    /**
     * 摇信带时标 双点
     */
    normalizedMeasuredValueWithTime(0x22,"带时标CP56Time2a的归一化测量值"),
    /**
     * 摇信带时标 双点
     */
    scaledValueWithTime(0x23,"带时标CP56Time2a的标度化值"),
    /**
     * 摇信带时标 双点
     */
    shortFloatWithTime(0x24,"带时标CP56Time2a的短浮点数"),
    /**
     * 带时标CP56Time2a的累计值 双点
     */
    cumulativeMeasurementWithTime(0x25,"带时标CP56Time2a的累计值"),
    /**
     * 带时标CP56Time2a的继电保护装置事件
     */
    relayProtectionDeviceEventWithTime(0x26,"带时标CP56Time2a的继电保护装置事件"),
    /**
     * 带时标CP56Time2a的继电保护装置成组启动事件
     */
    relayProtectionDeviceGroupStartEventWithTime(0x27,"带时标CP56Time2a的继电保护装置成组启动事件"),
    /**
     * 带时标CP56Time2a的继电保护装置成组输出电路信息
     */
    relayProtectionDeviceGroupOutputCircuitInfoWithTime(0x28,"带时标CP56Time2a的继电保护装置成组输出电路信息"),
    /**
     * 单命令 遥控
     */
    onePointTelecontrol(0x2D,"单命令"),
    /**
     * 双命令遥控
     */
    twoPointTelecontrol(0x2E,"双命令"),
    /**
     * 读单个参数
     */
    readOneParameter(0x66,"读单个参数命令"),
    /**
     * 读多个参数
     */
    readMultipleParameter(0x84,"读多个参数命令"),
    /**
     * 预置单个参数命令
     */
    prefabActivationOneParameter(0x30,"预置/激活单个参数命令"),
    /**
     * 预置多个个参数
     */
    prefabActivationMultipleParameter(0x88,"预置/激活多个参数命令"),
    /**
     * 初始化结束
     */
    initEnd(0x46,"初始化结束"),
    /**
     * 总召唤命令
     */
    generalCall(0x64,"总召唤命令"),
    /**
     * 电能脉冲召唤命令
     */
    powerPulseCall(0x65,"电能脉冲召唤命令"),
    /**
     * 读命令
     */
    read(0x66,"读命令"),
    /**
     * 时钟同步
     */
    synchronizeDatetime(0x67,"时钟同步/读取命令"),
    /**
     * 复位进程
     */
    resetPprocess(0x69,"复位进程");

    @Getter
    private final int value;

    @Getter
    private final String msg;

    AsduTypeIdEnum(int value, String msg) {
        this.value = value;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return this.msg;
    }

    public static AsduTypeIdEnum getEnum(int value) {
        for (AsduTypeIdEnum type : AsduTypeIdEnum.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }

        return null;
    }
}
