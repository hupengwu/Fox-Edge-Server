package cn.foxtech.device.protocol.v1.haier.ycj.a002;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.haier.ycj.a002.entity.PduEntity;
import cn.foxtech.device.protocol.v1.haier.ycj.a002.enums.Mode;
import cn.foxtech.device.protocol.v1.haier.ycj.a002.enums.Speed;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;

import java.util.HashMap;
import java.util.Map;

@FoxEdgeDeviceType(value = "海尔空调-YCJ-A000", manufacturer = "海尔集团公司")
public class YcjA002GetControlStatus {
    @FoxEdgeOperate(name = "查询控制状态监视", polling = true, type = FoxEdgeOperate.encoder, mode = FoxEdgeOperate.status, timeout = 2000)
    public static String encodePdu(Map<String, Object> param) {
        Integer devAddr = (Integer) param.get("devAddr");

        if (MethodUtils.hasEmpty(devAddr)) {
            throw new ProtocolException("参数缺失：devAddr");
        }

        PduEntity entity = new PduEntity();

        entity.setCmd((byte) 0x3D);
        entity.setDevAddr(devAddr);

        byte[] pdu = PduEntity.encodePdu(entity);

        return HexUtils.byteArrayToHexString(pdu);
    }

    @FoxEdgeOperate(name = "查询控制状态监视", polling = true, type = FoxEdgeOperate.decoder, mode = FoxEdgeOperate.status, timeout = 2000)
    public static Map<String, Object> decodePdu(String hexString, Map<String, Object> param) {
        byte[] pdu = HexUtils.hexStringToByteArray(hexString);

        PduEntity entity = PduEntity.decodePdu(pdu);

        if (entity.getCmd() != 0x3D) {
            throw new ProtocolException("返回的命令字不正确!");
        }
        if (entity.getData().length != 4) {
            throw new ProtocolException("返回的数据长度不正确!");
        }


        Map<String, Object> result = new HashMap<>();
        result.put("devAddr", entity.getDevAddr());

        byte dat0 = entity.getData()[0];

        // 设定温度
        result.put("设定温度", dat0 & 0x0f + 16);

        // 运行模式
        Mode mode = Mode.getEnum((dat0 >> 4) & 0x07);
        if (mode == null) {
            mode = Mode.value0;
        }
        result.put("运行模式", mode.getName());

        // 主机开/关机检测
        result.put("开关机标志", (dat0 & 0x80) != 0);


        // 主机室内温度传感器
        byte dat1 = entity.getData()[1];

        // 风速设定
        Speed speed = Speed.getEnum(dat1 & 0x07);
        if (speed == null) {
            speed = Speed.value0;
        }
        result.put("运行模式", speed.getName());

        // 风门摆动标志
        result.put("风门摆动标志", (dat1 & 0x08) != 0);


        byte dat2 = entity.getData()[2];

        // 换新风
        if ((dat2 & 0b11) == 0) {
            result.put("换新风", "无");
        }
        if ((dat2 & 0b11) == 1) {
            result.put("换新风", "自动");
        }
        if ((dat2 & 0b11) == 2) {
            result.put("换新风", "连续");
        }
        if ((dat2 & 0b11) == 3) {
            result.put("换新风", "无");
        }

        // 强力/安静指示
        if (((dat2 >> 2) & 0b11) == 0) {
            result.put("强力安静指示", "无");
        }
        if (((dat2 >> 2) & 0b11) == 1) {
            result.put("强力安静指示", "强力");
        }
        if (((dat2 >> 2) & 0b11) == 2) {
            result.put("强力安静指示", "安静");
        }
        if (((dat2 >> 2) & 0b11) == 3) {
            result.put("强力安静指示", "无");
        }

        // 加湿
        result.put("加湿", ((dat2 >> 4) & 0b1) == 1);


        // 辅助电加热
        result.put("辅助电加热", ((dat2 >> 5) & 0b1) == 1);


        // 空气清新
        result.put("空气清新", ((dat2 >> 6) & 0b1) == 1);


        // 健康
        result.put("健康", ((dat2 >> 7) & 0b1) == 1);


        byte dat3 = entity.getData()[3];


        // 湿度设定
        result.put("湿度设定", ((dat3 >> 0) & 0b111111) + 29);

        // 立体送风
        if (((dat3 >> 6) & 0b11) == 0) {
            result.put("立体送风", "停止");
        }
        if (((dat3 >> 6) & 0b11) == 1) {
            result.put("立体送风", "上下摆风");
        }
        if (((dat3 >> 6) & 0b11) == 2) {
            result.put("立体送风", "左右摆风");
        }
        if (((dat3 >> 6) & 0b11) == 3) {
            result.put("立体送风", "立体摆风");
        }

        return result;
    }

}
