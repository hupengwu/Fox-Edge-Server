package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.modbus.ModBusProtocolReadRegisters;
import cn.foxtech.device.protocol.v1.modbus.ModBusProtocolReadStatus;
import cn.foxtech.device.protocol.v1.modbus.ModBusProtocolWriteRegisters;
import cn.foxtech.device.protocol.v1.utils.BitsUtils;

import java.util.HashMap;
import java.util.Map;

public class TestUtils {
    public static void main(String[] args) throws Exception {
        JHoldingRegistersTest2();
    }

    public static void JHoldingRegistersTest2() {
        try {
            String hexString = "01 04 0c 01 1a 01 ba 41 e1 fa 9c 42 30 ed cb ef ca ";

            byte[] ttt = new byte[4];
            ttt[0] = (byte)0x41;
            ttt[1] = (byte)0xe2;
            ttt[2] = (byte)0x8e;
            ttt[3] = (byte)0x44;

            float v = BitsUtils.bitsToFloat(ttt);

            ttt[0] = (byte)0x42;
            ttt[1] = (byte)0x30;
            ttt[2] = (byte)0xf7;
            ttt[3] = (byte)0x2b;

            float v1 = BitsUtils.bitsToFloat(ttt);




            Map<String, Object> param = new HashMap<>();
            param.put("devAddr", 1);
            param.put("regAddr", 0);
            param.put("regCnt", 6);
            param.put("modbusMode", "RTU");
            param.put("operateName", "Read Input Register");
            param.put("templateName", "temperature-sensor-read-input-status-table");
            param.put("tableName", "zhongshengkeji-temperature-sensor-modbus/1.0.0/1.temperature-sensor-read-input-status-table.csv");
            Map<String, Object> value = new HashMap<>();
            value = ModBusProtocolReadRegisters.unpackReadHoldingRegister(hexString, param);
            value = ModBusProtocolReadRegisters.unpackReadHoldingRegister(hexString, param);
       //     value = ModBusProtocolReadHoldingRegisters.unpackReadHoldingRegister(hexString, param);

            String data = ModBusProtocolWriteRegisters.packWriteHoldingRegister(param);
            String ss = value.toString();

            param.putAll(value);
            param.put("逆变器11输出电流",5.1);
            param.put("逆变器12输出电流",5.2);
            param.put("逆变器13输出电流",5.3);
            param.put("逆变器14输出电流",5.4);
            param.put("逆变器15输出电流",5.5);

   //         hexString =  ModBusProtocolReadHoldingRegisters.packReadHoldingRegister(param);
  //          hexString =  ModBusProtocolReadHoldingRegisters.packReadHoldingRegister(param);

            param.put("device_addr", 1);
            param.put("reg_addr", "00 00");
            param.put("reg_cnt", 10);
            param.put("modbus_mode", "TCP");
            param.put("operate_name", "Read Coil Status");
            param.put("template_name", "Read Coil Status Table");
            param.put("table_name", "102.CETUPS_Read Coil Status Table.csv");
//            hexString =  ModBusProtocolReadCoilStatus.packReadCoilStatus(param);
//            hexString =  ModBusProtocolReadCoilStatus.packReadCoilStatus(param);
            value = ModBusProtocolReadStatus.unpackReadCoilStatus("03 e5 00 00 00 05 01 01 02 52 01 ",param);
            value = ModBusProtocolReadStatus.unpackReadCoilStatus("03 e5 00 00 00 05 01 01 02 52 01 ",param);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //方法1：  //(3412) 小端交换字节模式
    private float big2Little(float big){
        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(big);

        byte[] b = new byte[4];
        b[0] = (byte) (fbit >> 16);
        b[1] = (byte) (fbit);

        int l;
        l = b[0];
        l &= 0xff;
        l |= ((long) b[2] << 16);
        float little = Float.intBitsToFloat(l);
        return little;
    }


}
