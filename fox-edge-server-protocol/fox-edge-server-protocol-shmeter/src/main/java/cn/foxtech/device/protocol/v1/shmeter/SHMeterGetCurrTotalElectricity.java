package cn.foxtech.device.protocol.v1.shmeter;


import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.telecom.core.TelecomProtocol;

import java.util.Map;

@FoxEdgeDeviceType(value = "ShangHai Electricity Meter", manufacturer = "上海电表厂")
public class SHMeterGetCurrTotalElectricity extends SHMeterProtocolFrame {

    /**
     * 获取当前有功电量集合—总
     *
     * @param param
     * @return
     */
    @FoxEdgeOperate(name = "获取当前有功电量集合—总", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static String packCmdGetCurrTotalElectricity(Map<String, Object> param) {
        pretreatParam(param);

        byte fun = 0x01;
        byte[] arrData = new byte[2];
        arrData[0] = (byte) (0x20 + 0x33);
        arrData[1] = (byte) (0x90 + 0x33);

        param.put(FUN, 0x01);
        param.put(DAT, arrData);


        return HexUtils.byteArrayToHexString(TelecomProtocol.packCmd4Map(param));
    }

    /**
     * 获取当前有功电量集合—总
     *
     * @param hexString
     * @param value
     * @return
     */
    @FoxEdgeOperate(name = "获取当前有功电量集合—总", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> unPackCmdGetCurrTotalElectricity(String hexString, Map<String, Object> param, Map<String, Object> value) {
        byte[] arrCmd = HexUtils.hexStringToByteArray(hexString);

        Map<String, Object> tmpValue = unPackCmd2Map(arrCmd);
        if (tmpValue == null) {
            return null;
        }

        //命令编码byCmd的判断
        //if((byCmd != 0x81)||(byCmd!=0xA1)||(byCmd!=0xC1))    //是否有&&判断？？？？？？？？
        if (!tmpValue.get(FUN).equals((byte) 0x81)) {
            return null;
        }
        byte byCmd = (byte) tmpValue.get(FUN);

        //数据部分大小的判断
        byte[] arrData = (byte[]) tmpValue.get(DAT);
        if (arrData.length != 6) {
            return null;
        }


        //数据部分第一第二字节的判断（标识符）
        if ((arrData[0] != 0x53) || arrData[1] != 0xC3) {
            return null;
        }
        //从站异常应答帧 功能：从站收到非法的数据请求或无此数据
        if (byCmd == (byte) 0xC1) {
            return null;
        }
        //从站正常应答
        else if (byCmd == 0x81 || byCmd == 0xA1) {
            //C=81H，无后续数据帧
            if (byCmd == 0x81) {
                String temp = String.format("%x%x%x.%x", arrData[5] - 0x33, arrData[4] - 0x33, arrData[3] - 0x33, arrData[2] - 0x33);
                value.put("当前有功电量集合—总", Float.valueOf(temp));
                return null;
            }
            //C=A1H，有后续数据帧
            if (byCmd != 0xA1) {
                return value;
            }
            return null;
        }

        return value;
    }
}
