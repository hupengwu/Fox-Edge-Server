package cn.foxtech.device.protocol.v1.haiwu.air.v100.enums;

import lombok.Getter;


public enum Type {
    I80(0x80, "空调开机温度"),// 80H 2 制冷运行开机温度
    I81(0x81, "空调关机温度"),// 81H 2 制冷运行关机温度
    I82(0x82, "回风温度上限"),// 82H 2 室内高温报警温度设定值
    I83(0x83, "回风温度下限"),// 83H 2 室内低温报警温度设定值
    I84(0x84, "回风湿度上限"),// 84H 2 室内高湿报警设定值
    I85(0x85, "回风湿度下限"),// 85H 2 室内低湿报警设定值
    I86(0x86, "制冷模式温度设定值"),// 86H 2 制冷模式空调设定温度
    EC0(0xC0, "运行模式设定"),// C0H 2 00H:自动；01H:制冷；02H:除湿；03H:送风；04H:制热；
    EC1(0xC1, "内风机风速设定"),// C1H 2 00H：停；01H：低风，02H：中风，03H：高风，
    EC2(0xC2, "摆风功能设定"),// C2H 2 01H：运转， 00H:停止
    EC3(0xC3, "屏蔽本地操作"),// 屏蔽本地操作 C3H 2 01H：屏蔽本地操作， 00H:允许本地操作
    IC4(0xC4, "双机备份切换时间"),// C4H 2 1~24小时设定有效，0：表示不启用双机备份切换功能
    IC5(0xC5, "高温同开温度设定"),// C5H 2 空调双机备份高温同开温度设定值
    IC6(0xC6, "制热模式温度设定值"),// C6H 2 制热模式空调设定温度
    IC7(0xC7, "制冷温控精度"),// C7H 2 制冷时温度控制的精度
    IC8(0xC8, "制热温控精度"),// C8H 2 制热时温度控制的精度

    ;
    @Getter
    private final int code;

    @Getter
    private final String name;

    Type(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static Type getEnum(Integer code) {
        for (Type value : Type.values()) {
            if (code.equals(value.code)) {
                return value;
            }
        }

        return null;
    }

    public static Type getEnum(String name) {
        for (Type value : Type.values()) {
            if (name.equals(value.name)) {
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
