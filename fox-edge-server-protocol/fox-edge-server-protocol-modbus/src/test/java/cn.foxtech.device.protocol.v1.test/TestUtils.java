/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 *
 *     This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * --------------------------------------------------------------------------- */
 
package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.modbus.ModBusProtocolReadRegisters;
import cn.foxtech.device.protocol.v1.modbus.ModBusProtocolReadStatus;
import cn.foxtech.device.protocol.v1.modbus.ModBusProtocolWriteRegisters;
import cn.foxtech.device.protocol.v1.modbus.core.ModBusConstants;
import cn.foxtech.device.protocol.v1.modbus.core.ModBusEntity;
import cn.foxtech.device.protocol.v1.modbus.core.ModBusProtocol;
import cn.foxtech.device.protocol.v1.modbus.core.ModBusProtocolFactory;
import cn.foxtech.device.protocol.v1.utils.BitsUtils;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

import java.util.HashMap;
import java.util.Map;

public class TestUtils {
    public static void main(String[] args) throws Exception {

        // 45 0f 60 00
        float ss =  BitsUtils.bitsToFloat((byte)0x00,(byte)0x60,  (byte)0x0f,(byte) 0x45);
        ss =  BitsUtils.bitsToFloat((byte)0xC3, (byte)0xf5, (byte)0xFa, (byte)0x41);

        JHoldingRegistersTest2();
    }

    public static void test1(){
        byte[] pdu = HexUtils.hexStringToByteArray("01 03 00 00 00 01 84 0A");

        ModBusProtocol modBusProtocol = ModBusProtocolFactory.createProtocol(ModBusConstants.MODE_RTU);
        ModBusEntity modBusEntity = modBusProtocol.unPackCmd2Entity(pdu);
    }

    public static void JHoldingRegistersTest2() {
        try {
            String hexString = "32 03 04 45 0f 60 00 f4 3f ";

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
            param.put("regAddr", 8198);
            param.put("regCnt", 18);
            param.put("modbusMode", "RTU");
            param.put("operateName", "Read Input Register");
            param.put("templateName", "chint-dt-su666 Read Holding Registers");
            param.put("tableName", "chint-dt-su666/v1/Holding Registers.csv");
            Map<String, Object> value = new HashMap<>();
            value = ModBusProtocolReadRegisters.unpackReadHoldingRegister("32 04 24 45 66 30 00 45 71 f0 00 45 68 d0 00 44 ff 20 00 45 0a 30 00 45 0d 30 00 44 80 c0 00 42 3c 00 00 43 4d 00 00 44 20 ", param);
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
