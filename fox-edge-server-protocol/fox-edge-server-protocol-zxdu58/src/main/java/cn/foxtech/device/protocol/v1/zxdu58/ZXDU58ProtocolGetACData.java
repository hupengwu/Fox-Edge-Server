/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.zxdu58;


import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.utils.BitsUtils;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

import java.util.HashMap;
import java.util.Map;

@FoxEdgeDeviceType(value = "ZXDU58", manufacturer = "中兴通讯")
public class ZXDU58ProtocolGetACData extends ZXDU58ProtocolFrame {

    /**
     * 获取交流系统模拟量量化数据(浮点数)40H 41H
     *
     * @param param
     * @return
     */
    @FoxEdgeOperate(name = "获取交流系统模拟量量化数据", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static String packCmdGetACData(Map<String, Object> param) {
        ZXDU58ProtocolFrame.pretreatParam(param);

        byte[] arrData = new byte[1];
        arrData[0] = 0x00;

        param.put("CID1", 0x40);
        param.put("CID2", 0x41);
        param.put("INFO", arrData);

        return HexUtils.byteArrayToHexString(ZXDU58ProtocolFrame.packCmd4Map(param));
    }

    /**
     * 获取交流系统模拟量量化数据(浮点数)40H 41H
     *
     * @param hexString
     * @return
     */
    @FoxEdgeOperate(name = "获取交流系统模拟量量化数据", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> unPackCmdGetACData(String hexString, Map<String, Object> param) {
        byte[] arrCmd = HexUtils.hexStringToByteArray(hexString);

        Map<String, Object> value = ZXDU58ProtocolFrame.unPackCmd4Map(arrCmd);
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

        if (arrData.length != 31) {
            return null;
        }


        int index = 2;

        Map<String, Object> result = new HashMap<>();

        result.put("交流输入相电压L1", BitsUtils.bitsToFloat(arrData[index++], arrData[index++], arrData[index++], arrData[index++]));
        result.put("交流输入相电压L2", BitsUtils.bitsToFloat(arrData[index++], arrData[index++], arrData[index++], arrData[index++]));
        result.put("交流输入相电压L3", BitsUtils.bitsToFloat(arrData[index++], arrData[index++], arrData[index++], arrData[index++]));

        //交流输入频率用户自定义遥测量,不检测
        index += 5;

        result.put("交流屏输出电流L1", BitsUtils.bitsToFloat(arrData[index++], arrData[index++], arrData[index++], arrData[index++]));
        result.put("交流屏输出电流L2", BitsUtils.bitsToFloat(arrData[index++], arrData[index++], arrData[index++], arrData[index++]));
        result.put("交流屏输出电流L3", BitsUtils.bitsToFloat(arrData[index++], arrData[index++], arrData[index++], arrData[index++]));


        return result;
    }
}
