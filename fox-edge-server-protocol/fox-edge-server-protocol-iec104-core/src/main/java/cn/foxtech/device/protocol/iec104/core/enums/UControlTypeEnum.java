package cn.foxtech.device.protocol.iec104.core.enums;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * U帧的控制字枚举类型，U帧用于链路需要的建立/拆除/心跳
 */
public enum UControlTypeEnum {
    /**
     * 测试命令
     */
    TESTFR(0x43000000),
    /**
     * 测试确认指令
     */
    TESTFR_YES(0x83000000),
    /**
     * 停止指令
     */
    STOPDT(0x13000000),
    /**
     * 停止确认
     */
    STOPDT_YES(0x23000000),
    /**
     * 启动命令
     */
    STARTDT(0x07000000),
    /**
     * 启动确认命令
     */
    STARTDT_YES(0x0B000000);

    @Getter
    private final int value;

    UControlTypeEnum(int value) {
        this.value = value;
    }


    public static Set<Integer> getTypes() {
        Set<Integer> cmds = new HashSet<>();
        for (UControlTypeEnum type : UControlTypeEnum.values()) {
            cmds.add((type.getValue() >> 24) & 0xff);
        }

        return cmds;
    }
}
