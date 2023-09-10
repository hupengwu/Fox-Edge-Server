package cn.foxtech.device.protocol.v1.cetups;


import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperateParam;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.modbus.core.ModBusConstants;

import java.util.Map;

@FoxEdgeDeviceType(value = "CE+T UPS", manufacturer = "深圳安圣电气有限公司")
public class CETUPSProtocolGetAlarmsAndEvents extends CETUPSProtocolFrame {

    /**
     * 获取告警和事件信息
     *
     * @param param 输入参数，其中注解指明必须携带"设备地址"的参数，它的缺省值=1
     * @return 编码是否成功
     */
    @FoxEdgeOperate(name = "Read Alarms And Events Table", polling = true, type = FoxEdgeOperate.encoder, timeout = 2000)
    @FoxEdgeOperateParam(names = {"设备地址"}, values = {"1"})
    public static String packCmdGetAlarmsAndEvents(Map<String, Object> param) {
        pretreatParam(param);


        param.put(ModBusConstants.REG_ADDR, 0x41F);
        param.put(ModBusConstants.REG_CNT, 0x07);
        param.put(ModBusConstants.MODE, ModBusConstants.MODE_RTU);

        return HexUtils.byteArrayToHexString(protocol.packCmdReadHoldingRegisters4Map(param));
    }

    /**
     * get Alarms and events table
     *
     * @return
     */
    @FoxEdgeOperate(name = "Read Alarms And Events Table", polling = true, type = FoxEdgeOperate.decoder, timeout = 2000)
    @FoxEdgeOperateParam(names = {"设备地址"}, values = {"1"})
    public static Map<String, Object> unPackCmdGetAlarmsAndEvents(String hexString, Map<String, Object> param) {
        byte[] arrCmd = HexUtils.hexStringToByteArray(hexString);

        Map<String, Object> value = protocol.unPackCmdReadHoldingRegisters2Map(arrCmd);
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
        if (arrStatus.length != 7) {
            return null;
        }

        int index = 0;


        value.put("48V直流熔丝断告警状态", ((arrStatus[index]) & 0x02) == 0x02);
        value.put("用户交流熔丝断告警状态", ((arrStatus[index]) & 0x04) == 0x04);
        value.put("辅助交流熔丝断告警状态", ((arrStatus[index]) & 0x08) == 0x08);
        value.put("直流电压超限告警状态", ((arrStatus[index]) & 0x10) == 0x10);
        value.put("输出电压超限告警状态", ((arrStatus[index]) & 0x40) == 0x40);
        value.put("逆变器故障告警状态", ((arrStatus[index]) & 0x0300) != 0x00);
        value.put("是否有逆变器手动停", ((arrStatus[index]) & 0x0800) != 0x00);
        value.put("不同步告警状态", ((arrStatus[index]) & 0x1000) != 0x00);
        value.put("系统紧急告警状态", ((arrStatus[index]) & 0x4000) != 0x00);
        value.put("系统非紧急告警状态", ((arrStatus[index]) & 0x8000) != 0x00);

        index++;

        //================================================
        //1056
        //================================================

        value.put("逆变器限流告警状态", ((arrStatus[index]) & 0x0001) != 0x00);
        value.put("逆变器超温告警状态", ((arrStatus[index]) & 0x0008) != 0x00);

        index++;

        //==========================================
        //1057
        //==========================================


        int wValue = 0x01;
        for (int i = 0; i < 16; i++) {
            value.put(String.format("逆变器%2d传输故障告警状态", i), ((arrStatus[index]) & wValue) != 0x00);

            wValue = wValue << 1;
        }

        index++;

        //=====================================
        //1058
        //======================================
        index++;

        //=====================================
        //1059
        //======================================
        index++;

        //=====================================
        //1060
        //===================================

        value.put("过载告警状态", ((arrStatus[index]) & 0x0004) != 0x00);
        value.put("是否工作在在线方式", ((arrStatus[index]) & 0x0020) != 0x00);
        value.put("是否工作在逆变方式", ((arrStatus[index]) & 0x0040) != 0x00);
        value.put("是否工作在交流方式", ((arrStatus[index]) & 0x0080) != 0x00);
        value.put("负载是否由逆变器供电", ((arrStatus[index]) & 0x0100) != 0x00);
        value.put("负载是否由市电供电", ((arrStatus[index]) & 0x0200) != 0x00);
        value.put("是否由逆变切换到手动旁路", ((arrStatus[index]) & 0x1000) != 0x00);
        value.put("是否由市电切换到手动旁路", ((arrStatus[index]) & 0x2000) != 0x00);

        value.remove(ModBusConstants.REG_HOLD_STATUS);
        return value;
    }
}
