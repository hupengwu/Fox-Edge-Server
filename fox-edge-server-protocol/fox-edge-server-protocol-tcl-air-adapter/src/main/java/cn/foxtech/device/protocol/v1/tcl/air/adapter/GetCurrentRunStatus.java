/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

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
public class GetCurrentRunStatus {
    @FoxEdgeOperate(name = "查询运行状态", polling = true, type = FoxEdgeOperate.encoder, mode = FoxEdgeOperate.status, timeout = 4000)
    public static String encodePdu(Map<String, Object> param) {
        Integer devAddr = (Integer) param.get("devAddr");

        if (MethodUtils.hasEmpty(devAddr)) {
            throw new ProtocolException("参数缺失：devAddr");
        }

        MsgEntity msgEntity = new MsgEntity();
        msgEntity.setType(112);
        msgEntity.setSubType(0);
        msgEntity.setResult(0);


        PduEntity entity = new PduEntity();
        entity.setAddress(devAddr);
        entity.setData(MsgEntity.encode(msgEntity));

        byte[] pdu = PduEntity.encodePdu(entity);

        return HexUtils.byteArrayToHexString(pdu);
    }

    @FoxEdgeOperate(name = "查询运行状态", polling = true, type = FoxEdgeOperate.decoder, mode = FoxEdgeOperate.status, timeout = 4000)
    public static Map<String, Object> decodePdu(String hexString, Map<String, Object> param) {
        byte[] pdu = HexUtils.hexStringToByteArray(hexString);

        PduEntity entity = PduEntity.decodePdu(pdu);

        MsgEntity msgEntity = MsgEntity.decode(entity.getData());

        if (msgEntity.getType() != 112) {
            throw new ProtocolException("返回的messageType不匹配!");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("devAddr", entity.getAddress());
        result.put("result", msgEntity.getResult());

        if (msgEntity.getData().length != 8) {
            throw new ProtocolException("返回的messageData长度不匹配!");
        }

        byte[] data = msgEntity.getData();
        int value = 0;
        String key = "";

        key = "风量";
        value = (data[0] & 0b0000011) >> 0;
        if (value == 0) {
            result.put(key, "自动");
        }
        if (value == 1) {
            result.put(key, "高");
        }
        if (value == 2) {
            result.put(key, "中");
        }
        if (value == 3) {
            result.put(key, "低");
        }

        key = "模式";
        value = (data[0] & 0b00110000) >> 4;
        if (value == 0) {
            result.put(key, "制热");
        }
        if (value == 1) {
            result.put(key, "送风");
        }
        if (value == 2) {
            result.put(key, "制冷");
        }
        if (value == 3) {
            result.put(key, "除湿");
        }

        key = "压机状态";
        value = (data[0] & 0b01000000) >> 6;
        result.put(key, value == 1);

        key = "开关状态";
        value = (data[0] & 0b10000000) >> 7;
        result.put(key, value == 1);

        key = "设定温度";
        value = data[2] & 0xff;
        result.put(key, value);

        key = "室内温度";
        value = data[3] & 0xff;
        result.put(key, value);

        key = "管温进口AD值";
        value = data[4] & 0xff;
        result.put(key, value);

        key = "除霜出口AD值";
        value = data[5] & 0xff;
        result.put(key, value);

        key = "防冷风(停送)";
        value = (data[6] & (0x01 << 0)) & 0xff;
        result.put(key, value > 0);

        key = "防冷风(微风)";
        value = (data[6] & (0x01 << 1)) & 0xff;
        result.put(key, value > 0);

        key = "过热保护";
        value = (data[6] & (0x01 << 2)) & 0xff;
        result.put(key, value > 0);

        key = "过冷保护";
        value = (data[6] & (0x01 << 3)) & 0xff;
        result.put(key, value > 0);

        key = "电辅助加热暂停";
        value = (data[6] & (0x01 << 4)) & 0xff;
        result.put(key, value > 0);

        key = "室外机保护";
        value = (data[6] & (0x01 << 5)) & 0xff;
        result.put(key, value > 0);

        key = "除霜";
        value = (data[6] & (0x01 << 6)) & 0xff;
        result.put(key, value > 0);

        key = "过热停机";
        value = (data[6] & (0x01 << 7)) & 0xff;
        result.put(key, value > 0);

        key = "室内温度短路故障";
        value = (data[7] & (0x01 << 0)) & 0xff;
        result.put(key, value > 0);

        key = "室内温度开故障";
        value = (data[7] & (0x01 << 1)) & 0xff;
        result.put(key, value > 0);

        key = "室内管温短路故障";
        value = (data[7] & (0x01 << 2)) & 0xff;
        result.put(key, value > 0);

        key = "室内管温开路故障";
        value = (data[7] & (0x01 << 3)) & 0xff;
        result.put(key, value > 0);

        key = "室外管温短路故障";
        value = (data[7] & (0x01 << 4)) & 0xff;
        result.put(key, value > 0);

        key = "室外管温开路故障";
        value = (data[7] & (0x01 << 5)) & 0xff;
        result.put(key, value > 0);


        return result;
    }
}
