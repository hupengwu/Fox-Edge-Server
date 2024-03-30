package cn.foxtech.device.protocol.v1.haiwu.air.v10d;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.haiwu.air.v10d.enums.Type;
import cn.foxtech.device.protocol.v1.telecom.core.entity.PduEntity;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;

import java.util.HashMap;
import java.util.Map;

@FoxEdgeDeviceType(value = "基站空调(V1.0D)", manufacturer = "广东海悟科技有限公司")
public class SetTurnOn {
    @FoxEdgeOperate(name = "遥控开机", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static String encodePdu(Map<String, Object> param) {
        // 取出设备地址
        Integer devAddr = (Integer) param.get("devAddr");
        String type = (String) param.get("type");
        Integer value = (Integer) param.get("value");

        // 检查输入参数
        if (MethodUtils.hasEmpty(devAddr, type, value)) {
            throw new ProtocolException("输入参数不能为空:devAddr, type, value");
        }

        Type typ = Type.getEnum(type);
        if (typ == null) {
            throw new ProtocolException("未定义的类型" + type);
        }

        PduEntity pduEntity = new PduEntity();
        pduEntity.setAddr(devAddr);
        pduEntity.setVer(0x10);
        pduEntity.setCid1(0x60);
        pduEntity.setCid2(0x45);
        pduEntity.setData(new byte[1]);

        byte[] data = pduEntity.getData();
        data[0] = 0x10;

        byte[] pdu = PduEntity.encodePdu(pduEntity);

        return HexUtils.byteArrayToHexString(pdu);
    }

    @FoxEdgeOperate(name = "遥控开机", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> decodePdu(String hexString, Map<String, Object> param) {
        byte[] pdu = HexUtils.hexStringToByteArray(hexString);

        PduEntity entity = PduEntity.decodePdu(pdu);

        if (entity.getCid1() != 0x60) {
            throw new ProtocolException("返回的CID1不正确!");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("return", entity.getCid2());

        return result;
    }
}
