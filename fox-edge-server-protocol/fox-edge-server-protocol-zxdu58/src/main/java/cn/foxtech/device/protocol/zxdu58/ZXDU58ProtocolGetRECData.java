package cn.foxtech.device.protocol.zxdu58;

import cn.foxtech.device.protocol.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.core.utils.BitsUtils;
import cn.foxtech.device.protocol.core.utils.HexUtils;
import cn.foxtech.device.protocol.telecom.core.TelecomProtocol;

import java.util.HashMap;
import java.util.Map;

@FoxEdgeDeviceType(value = "ZXDU58", manufacturer = "中兴通讯")
public class ZXDU58ProtocolGetRECData extends ZXDU58ProtocolFrame {
    /**
     * 获取整流系统模拟量量化数据(浮点数)40H 41H
     *
     * @param param
     * @return
     */
    @FoxEdgeOperate(name = "获取整流系统模拟量量化数据", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static String packCmdGetRECData(Map<String, Object> param) {
        pretreatParam(param);

        byte[] arrData = new byte[0];

        param.put(CID1, 0x41);
        param.put(CID2, 0x41);
        param.put(INFO, arrData);

        return HexUtils.byteArrayToHexString(TelecomProtocol.packCmd4Map(param));
    }

    /**
     * 获取交流系统模拟量量化数据(浮点数)40H 41H
     *
     * @param hexString
     * @return
     */
    @FoxEdgeOperate(name = "获取整流系统模拟量量化数据", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> unPackCmdGetRECData(String hexString, Map<String, Object> param) {
        byte[] arrCmd = HexUtils.hexStringToByteArray(hexString);

        Map<String, Object> value = ZXDU58ProtocolFrame.unPackCmd4Map(arrCmd);
        if (value == null) {
            return null;
        }

        if (!value.get(CID1).equals((byte) 0x41)) {
            return null;
        }
        if (!value.get(CID2).equals((byte) 0x00)) {
            return null;
        }
        byte[] arrData = (byte[]) value.get(INFO);

        if (arrData.length < 6) {
            return null;
        }
        // 检查:数据域长度
        byte iModuleNo = arrData[5];//整流模块数
        int nSize = 6 + iModuleNo * 5;

        if (arrData.length != nSize) {
            return null;
        }


        int index = 1;
        int dwValue;


        Map<String, Object> result = new HashMap<>();
        result.put("整流模块输出电压", BitsUtils.bitsToFloat(arrData[index++], arrData[index++], arrData[index++], arrData[index++]));
        result.put("整流模块数量", arrData[index++]);


        for (int i = 0; i < iModuleNo; i++) {
            //模块i+1输出电流
            result.put(String.format("模块%02d输出电流", i + 1), BitsUtils.bitsToFloat(arrData[index++], arrData[index++], arrData[index++], arrData[index++]));

            index++;
        }

        return result;
    }
}
