package cn.foxtech.device.protocol.v1.haiwu.air.v2;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.haiwu.air.v2.enums.Type;
import cn.foxtech.device.protocol.v1.haiwu.air.v2.uitls.SetParamUtils;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;

import java.util.Map;

@FoxEdgeDeviceType(value = "海悟空调(V2.0)", manufacturer = "海悟空调有限公司")
public class SetParam {
    @FoxEdgeOperate(name = "设定系统参数（定点数）", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
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

        return HexUtils.byteArrayToHexString(SetParamUtils.encode(devAddr, typ.getCode(), value));
    }

    @FoxEdgeOperate(name = "设定系统参数（定点数）", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> decodePdu(String hexString, Map<String, Object> param) {
        byte[] pdu = HexUtils.hexStringToByteArray(hexString);
        return SetParamUtils.decode(pdu);
    }
}
