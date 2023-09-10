package cn.foxtech.device.protocol.v1.cetups;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.modbus.core.ModBusConstants;
import cn.foxtech.device.protocol.v1.modbus.core.ModBusReadRegistersRequest;

import java.util.Map;

@FoxEdgeDeviceType(value = "CE+T UPS", manufacturer = "深圳安圣电气有限公司")
public class CETUPSProtocolGetSystemMeasures extends CETUPSProtocolFrame {

    /**
     * get Read System Measures Table
     *
     * @param param
     * @return
     */
    @FoxEdgeOperate(name = "Read System Measures Table", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    public static String packCmdGetSystemMeasures(Map<String, Object> param) {
        CETUPSProtocolFrame.pretreatParam(param);


        ModBusReadRegistersRequest request = new ModBusReadRegistersRequest();
        request.setMemAddr(0x42E);
        request.setCount(0x45);


        return HexUtils.byteArrayToHexString(CETUPSProtocolFrame.protocol.packCmdReadHoldingRegisters4Request(request));
    }

    /**
     * get Alarms and events table
     *
     * @param hexString
     * @return
     */
    @FoxEdgeOperate(name = "Read System Measures Table", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    public static Map<String, Object> unPackCmdGetSystemMeasures(String hexString, Map<String, Object> param) {
        byte[] arrCmd = HexUtils.hexStringToByteArray(hexString);

        Map<String, Object> value = CETUPSProtocolFrame.protocol.unPackCmdReadHoldingRegisters2Map(arrCmd);
        if (value == null) {
            return null;
        }


        byte byAddr = (byte) value.get(ModBusConstants.ADDR);
        int[] arrStatus = (int[]) value.get(ModBusConstants.REG_HOLD_STATUS);

        //检查地址
        if (byAddr != 0x01) {
            return null;
        }

        //检查数据域长度:
        if (arrStatus.length != 69) {
            return null;
        }

        int index = 0;

        value.put("系统输出电压", arrStatus[index++]);
        value.put("系统输出电流", (float) arrStatus[index++] / 10);
        value.put("系统输出功率", arrStatus[index++]);
        value.put("系统输出频率", (float) arrStatus[index++] / 10);
        value.put("负载比", arrStatus[index++]);
        value.put("组1输入电压", (float) arrStatus[index++] / 10);
        value.put("组2输入电压", (float) arrStatus[index++] / 10);
        value.put("组3输入电压", (float) arrStatus[index++] / 10);
        value.put("组4输入电压", (float) arrStatus[index++] / 10);

        //=====================
        //1079 spare
        //=====================
        index++;


        //===================================================
        //1080  Iout inverter 0
        //1081  Pout inverter 0
        //1082  dissipator temperature inverter 0
        //……
        //1125  Iout inverter 15
        //1126  Pout inverter 15
        //1127  dissipator temperature inverter 15
        //===================================================
        for (int i = 0; i < 16; i++) {
            value.put(String.format("逆变器%02d输出电流", i + 1), arrStatus[index++] / 10);
            value.put(String.format("逆变器%02d输出功率", i + 1), arrStatus[index++]);
            value.put(String.format("逆变器%02d温度", i + 1), arrStatus[index++]);
        }

        value.remove(ModBusConstants.REG_HOLD_STATUS);
        return value;
    }
}
