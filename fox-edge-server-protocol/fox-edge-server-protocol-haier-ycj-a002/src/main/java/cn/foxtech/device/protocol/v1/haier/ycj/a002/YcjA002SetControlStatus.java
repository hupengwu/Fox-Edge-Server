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
public class YcjA002SetControlStatus {
    @FoxEdgeOperate(name = "控制", polling = true, type = FoxEdgeOperate.encoder, mode = FoxEdgeOperate.status, timeout = 2000)
    public static String encodePdu(Map<String, Object> param) {
        String mode = (String) param.getOrDefault("mode", "自动模式");
        Integer temp = (Integer) param.getOrDefault("temp", 0);
        Boolean open = (Boolean) param.getOrDefault("open", false);
        Boolean damper = (Boolean) param.getOrDefault("damper", false);
        String speed = (String) param.getOrDefault("speed", "超高速");
        Integer devAddr = (Integer) param.getOrDefault("devAddr", 0);

        if (MethodUtils.hasEmpty(mode, temp, open, damper, speed, devAddr)) {
            throw new ProtocolException("参数缺失：mode, temp, open, damper, speed, devAddr");
        }

        PduEntity entity = new PduEntity();

        // 命令字：0x01
        entity.setCmd((byte) 0x01);
        entity.setDevAddr(devAddr);
        entity.setData(new byte[2]);

        byte dat0 = 0;
        byte dat1 = 0;

        // 设定温度
        dat0 = (byte) (temp & 0x0f);

        // 模式
        Mode md = Mode.getEnum(mode);
        if (md != null) {
            dat0 |= md.getCode();
        }

        // 开关
        if (open) {
            dat0 |= (byte) 0x80;
        }

        // 风速
        Speed sp = Speed.getEnum(speed);
        if (sp == null) {
            sp = Speed.value0;
        }
        dat1 |= (byte) sp.getCode();

        // 挡风板
        if (damper) {
            dat1 |= (byte) 0x08;
        }

        entity.getData()[0] = dat0;
        entity.getData()[1] = dat1;

        byte[] pdu = PduEntity.encodePdu(entity);

        return HexUtils.byteArrayToHexString(pdu);
    }

    @FoxEdgeOperate(name = "控制", polling = true, type = FoxEdgeOperate.decoder, mode = FoxEdgeOperate.status, timeout = 2000)
    public static Map<String, Object> decodePdu(String hexString, Map<String, Object> param) {
        byte[] pdu = HexUtils.hexStringToByteArray(hexString);

        PduEntity entity = PduEntity.decodePdu(pdu);

        Map<String, Object> result = new HashMap<>();

        result.put("devAddr", entity.getDevAddr());

        if (entity.getCmd() == 0x10) {
            result.put("success", true);
        } else {
            result.put("success", false);
        }

        return result;
    }
}
