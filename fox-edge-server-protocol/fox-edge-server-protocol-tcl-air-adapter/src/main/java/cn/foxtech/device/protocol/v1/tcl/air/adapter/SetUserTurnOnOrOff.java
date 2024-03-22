package cn.foxtech.device.protocol.v1.tcl.air.adapter;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.tcl.air.adapter.entity.PduEntity;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;

import java.util.HashMap;
import java.util.Map;

@FoxEdgeDeviceType(value = "柜式空调(KPRd)", manufacturer = "TCL科技集团股份有限公司")
public class SetUserTurnOnOrOff {
    @FoxEdgeOperate(name = "用户开关空调", polling = true, type = FoxEdgeOperate.encoder, mode = FoxEdgeOperate.status, timeout = 4000)
    public static String encodePdu(Map<String, Object> param) {
        Integer devAddr = (Integer) param.get("devAddr");
        Boolean open = (Boolean) param.get("运行");


        if (MethodUtils.hasEmpty(devAddr, open)) {
            throw new ProtocolException("参数缺失：devAddr, 运行");
        }

        PduEntity entity = new PduEntity();
        entity.setAddress(devAddr);
        entity.setMessageType(13);
        entity.setMessageSubType(open ? 1 : 0);

        byte[] pdu = PduEntity.encodePdu(entity);

        return HexUtils.byteArrayToHexString(pdu);
    }

    @FoxEdgeOperate(name = "用户开关空调", polling = true, type = FoxEdgeOperate.decoder, mode = FoxEdgeOperate.status, timeout = 4000)
    public static Map<String, Object> decodePdu(String hexString, Map<String, Object> param) {
        byte[] pdu = HexUtils.hexStringToByteArray(hexString);

        PduEntity entity = PduEntity.decodePdu(pdu);
        if (entity.getMessageType() != 13) {
            throw new ProtocolException("返回的messageType不匹配!");
        }

        Map<String, Object> result = new HashMap<>();

        result.put("devAddr", entity.getAddress());
        result.put("运行", entity.getMessageSubType() == 1);
        result.put("result", entity.getResult());


        return result;
    }
}
