package cn.foxtech.device.protocol.v1.zxdu58;


import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

import java.util.HashMap;
import java.util.Map;

@FoxEdgeDeviceType(value = "ZXDU58", manufacturer = "中兴通讯")
public class ZXDU58ProtocolGetACSwitch extends ZXDU58ProtocolFrame {
    /**
     * 获取交流系统模拟量量化数据(浮点数)40H 41H
     *
     * @param param
     * @return
     */
    @FoxEdgeOperate(name = "获取交流系统开关输入状态", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static String packCmdGetACSwitch(Map<String, Object> param) {
        pretreatParam(param);

        byte[] arrData = new byte[1];
        arrData[0] = 0x00;

        param.put("CID1", 0x40);
        param.put("CID2", 0x43);
        param.put("INFO", arrData);

        return HexUtils.byteArrayToHexString(packCmd4Map(param));
    }

    /**
     * 获取交流系统模拟量量化数据(浮点数)40H 41H
     *
     * @param hexString
     * @return
     */
    @FoxEdgeOperate(name = "获取交流系统开关输入状态", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> unPackCmdGetACSwitch(String hexString, Map<String, Object> param) {
        byte[] arrCmd = HexUtils.hexStringToByteArray(hexString);

        Map<String, Object> value = unPackCmd4Map(arrCmd);
        if (value == null) {
            return null;
        }

        if (!value.get("CID1").equals((byte) 0x40)) {
            return null;
        }
        if (!value.get("CID2").equals((byte) 0x00)) {
            return null;
        }
        byte[] arrData = (byte[]) value.get("INFO");

        if (arrData.length < 4) {
            return null;
        }
        // 检查:数据域长度
        int iNo = arrData[2];//用户自定义遥测数量
        int nSize = 3 + iNo;

        if (arrData.length != nSize) {
            return null;
        }

        Map<String, Object> result = new HashMap<>();

        int index = 3;
        for (int i = 0; i < iNo; i++) {
            byte byAt = arrData[index++];
            boolean bVale = false;
            switch (i) {
                case 0:
                    result.put("交流输入空开1断", byAt != 0x00);//00闭合
                    break;
                case 1:
                    result.put("交流输入空开2断", byAt != 0x00);//00闭合
                    break;
                case 2:
                    result.put("交流辅助输出开关断", byAt != 0x00);//00闭合
                    break;
                case 3:
                    String strValue = "";
                    if (byAt == 0x00) {
                        strValue = "市电";
                    } else {
                        if (byAt == 0x01) {
                            strValue = "油机";
                        } else {
                            strValue = "电池";
                        }
                    }

                    result.put("系统供电状态", strValue);
                    break;
                default:
                    return null;
            }
        }
        return result;
    }
}
