package cn.foxtech.device.protocol.v1.zxdu58;


import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.telecom.core.TelecomProtocol;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

import java.util.HashMap;
import java.util.Map;

@FoxEdgeDeviceType(value = "ZXDU58", manufacturer = "中兴通讯")
public class ZXDU58ProtocolGetACAlarm extends ZXDU58ProtocolFrame {

    /**
     * 获取交流系统告警状态 40H 44H
     *
     * @param param
     * @return
     */
    @FoxEdgeOperate(name = "获取交流系统告警状态", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static String packCmdGetACData(Map<String, Object> param) {
        ZXDU58ProtocolFrame.pretreatParam(param);

        byte[] arrData = new byte[1];
        arrData[0] = 0x00;

        param.put(CID1, 0x40);
        param.put(CID2, 0x44);
        param.put(INFO, arrData);

        return HexUtils.byteArrayToHexString(TelecomProtocol.packCmd4Map(param));
    }

    /**
     * 获取交流系统模拟量量化数据(浮点数)40H 41H
     *
     * @param hexString
     * @return
     */
    @FoxEdgeOperate(name = "获取交流系统告警状态", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> unPackCmdGetACData(String hexString, Map<String, Object> param) {
        byte[] arrCmd = HexUtils.hexStringToByteArray(hexString);

        Map<String, Object> value = ZXDU58ProtocolFrame.unPackCmd4Map(arrCmd);
        if (value == null) {
            return null;
        }

        if (!value.get(CID1).equals((byte) 0x40)) {
            return null;
        }
        if (!value.get(CID2).equals((byte) 0x00)) {
            return null;
        }
        byte[] arrData = (byte[]) value.get(INFO);

        if (arrData.length != 16) {
            return null;
        }


        int index = 2;

        Map<String, Object> result = new HashMap<>();

        result.put("交流输入电压L1告警", arrData[index++] != 0x00);
        result.put("交流输入电压L2告警", arrData[index++] != 0x00);
        result.put("交流输入电压L3告警", arrData[index++] != 0x00);

        // 不需要的数据
        index += 3;

        result.put("交流输入主空开告警", arrData[index++] != 0x00);
        result.put("交流停电告警", arrData[index++] != 0x00);
        result.put("C级防雷器坏告警", arrData[index++] != 0x00);
        result.put("D级防雷器坏告警", arrData[index++] != 0x00);
        result.put("交流辅助输出开关断告警", arrData[index++] != 0x00);
        result.put("输出电流L1告警", arrData[index++] != 0x00);
        result.put("输出电流L2告警", arrData[index++] != 0x00);
        result.put("输出电流L3告警", arrData[index++] != 0x00);

        return result;
    }
}
