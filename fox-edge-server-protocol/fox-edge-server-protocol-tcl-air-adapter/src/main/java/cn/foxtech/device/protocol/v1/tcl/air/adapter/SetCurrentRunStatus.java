package cn.foxtech.device.protocol.v1.tcl.air.adapter;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.tcl.air.adapter.entity.MsgEntity;
import cn.foxtech.device.protocol.v1.tcl.air.adapter.entity.PduEntity;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.MethodUtils;

import java.util.HashMap;
import java.util.Map;

@FoxEdgeDeviceType(value = "柜式空调(KPRd)", manufacturer = "TCL科技集团股份有限公司")
public class SetCurrentRunStatus {
    @FoxEdgeOperate(name = "设置运行状态", polling = true, type = FoxEdgeOperate.encoder, mode = FoxEdgeOperate.status, timeout = 4000)
    public static String encodePdu(Map<String, Object> param) {
        Integer devAddr = (Integer) param.get("devAddr");
        String airVolume = (String) param.get("风量");
        Boolean open = (Boolean) param.get("运行");
        Integer tempComp = (Integer) param.get("温度补偿");
        Boolean windDirect = (Boolean) param.get("风向");
        String mode = (String) param.get("模式");
        Integer settingTemp = (Integer) param.get("设定温度");

        if (MethodUtils.hasEmpty(devAddr, airVolume, open, tempComp, windDirect, mode, settingTemp)) {
            throw new ProtocolException("参数缺失：devAddr, 风量, 运行, 温度补偿, 风向, 模式, 设定温度");
        }

        MsgEntity msgEntity = new MsgEntity();
        msgEntity.setType(111);
        msgEntity.setSubType(0);
        msgEntity.setResult(0);
        msgEntity.setData(new byte[4]);

        byte[] data = msgEntity.getData();

        int value = 0;

        // 风量
        if (airVolume.equals("自动")) {
            value = 0;
        }
        if (airVolume.equals("高")) {
            value = 1;
        }
        if (airVolume.equals("中")) {
            value = 2;
        }
        if (airVolume.equals("低")) {
            value = 3;
        }
        data[0] |= value << 0;

        // 运行/停机
        data[0] |= open ? 1 << 2 : 0;

        // 温度补偿
        if (tempComp.intValue() >= 2) {
            value = 2;
        }
        if (tempComp.intValue() == 1) {
            value = 1;
        }
        if (tempComp.intValue() == 0) {
            value = 0;
        }
        if (tempComp.intValue() == -1) {
            value = 5;
        }
        if (tempComp.intValue() == -2) {
            value = 6;
        }
        data[0] |= value << 4;

        // 风向
        data[0] |= windDirect ? 1 << 7 : 0;

        // 模式
        if (mode.equals("制热")) {
            value = 0;
        }
        if (mode.equals("自动")) {
            value = 1;
        }
        if (mode.equals("制冷")) {
            value = 2;
        }
        if (mode.equals("除湿")) {
            value = 3;
        }
        if (mode.equals("送风")) {
            value = 4;
        }
        data[1] |= value << 0;

        // 设定温度
        value = settingTemp.intValue() - 18;
        data[1] |= value << 4;


        PduEntity entity = new PduEntity();
        entity.setAddress(devAddr);
        entity.setData(MsgEntity.encode(msgEntity));
        byte[] pdu = PduEntity.encodePdu(entity);

        return HexUtils.byteArrayToHexString(pdu);
    }

    @FoxEdgeOperate(name = "设置运行状态", polling = true, type = FoxEdgeOperate.decoder, mode = FoxEdgeOperate.status, timeout = 4000)
    public static Map<String, Object> decodePdu(String hexString, Map<String, Object> param) {
        byte[] pdu = HexUtils.hexStringToByteArray(hexString);

        PduEntity entity = PduEntity.decodePdu(pdu);

        MsgEntity msgEntity = MsgEntity.decode(entity.getData());

        if (msgEntity.getType() != 111) {
            throw new ProtocolException("返回的messageType不匹配!");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("devAddr", entity.getAddress());
        result.put("result", msgEntity.getResult());

        return result;
    }
}