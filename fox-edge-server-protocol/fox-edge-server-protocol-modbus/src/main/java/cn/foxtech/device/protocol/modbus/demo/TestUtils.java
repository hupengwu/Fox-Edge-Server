package cn.foxtech.device.protocol.modbus.demo;

import cn.foxtech.device.protocol.modbus.ModBusProtocolReadRegisters;
import cn.foxtech.device.protocol.modbus.ModBusProtocolReadStatus;
import cn.foxtech.device.protocol.modbus.ModBusProtocolWriteRegisters;

import java.util.HashMap;
import java.util.Map;

public class TestUtils {
    public static void main(String[] args) throws Exception {
        JHoldingRegistersTest2();
    }

    public static void JHoldingRegistersTest2() {
        try {
            String hexString = "01 03 8A 00 E5 00 35 03 79 01 F4 00 09 02 1D 00 00 00 00 00 00 00 00 00 11 01 30 00 00 00 15 01 30 00 3B 00 0F 01 19 00 00 00 00 00 00 00 00 00 01 00 00 00 00 00 00 00 01 00 00 00 00 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 AC 63 ";


            Map<String, Object> param = new HashMap<>();
            param.put("device_addr", 1);
            param.put("reg_addr", 1070);
            param.put("reg_cnt", 69);
            param.put("modbus_mode", "RTU");
            param.put("operate_name", "Read Holding Register");
            param.put("template_name", "Read System Measures Table");
            param.put("table_name", "101.CETUPS_Read System Measures Table.csv");
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
}
