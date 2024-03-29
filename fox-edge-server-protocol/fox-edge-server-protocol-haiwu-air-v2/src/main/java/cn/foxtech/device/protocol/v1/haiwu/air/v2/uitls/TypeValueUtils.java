package cn.foxtech.device.protocol.v1.haiwu.air.v2.uitls;

import cn.foxtech.device.protocol.v1.haiwu.air.v2.enums.Type;

public class TypeValueUtils {
    public static Object getTypeValueDesc(Type type, int value) {
        switch (type) {
            case EC0: {//运行模式设定
                switch (value) {
                    case 0x00:
                        return "自动";
                    case 0x01:
                        return "制冷";
                    case 0x02:
                        return "除湿";
                    case 0x03:
                        return "送风";
                    case 0x04:
                        return "制热";
                    default:
                        return "";
                }
            }
            case EC1: {//内风机风速设定
                switch (value) {
                    case 0x00:
                        return "停";
                    case 0x01:
                        return "低风";
                    case 0x02:
                        return "中风";
                    case 0x03:
                        return "高风";
                    default:
                        return "";
                }
            }
            case EC2: {//摆风功能设定
                switch (value) {
                    case 0x00:
                        return "停止";
                    case 0x01:
                        return "运转";
                    default:
                        return "";
                }
            }
            case EC3: {//屏蔽本地操作
                switch (value) {
                    case 0x00:
                        return "允许";
                    case 0x01:
                        return "屏蔽";
                    default:
                        return "";
                }
            }
            default:
                return value;
        }
    }
}

