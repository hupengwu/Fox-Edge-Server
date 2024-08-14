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

import cn.foxtech.device.protocol.v1.mitsubishi.plc.fx.PlcFxProtocolDeviceRead;
import cn.foxtech.device.protocol.v1.mitsubishi.plc.fx.entity.MitsubishiPlcFxDeviceReadEntity;
import cn.foxtech.device.protocol.v1.mitsubishi.plc.fx.entity.MitsubishiPlcFxDeviceWriteEntity;
import cn.foxtech.device.protocol.v1.mitsubishi.plc.fx.entity.MitsubishiPlcFxEntity;
import cn.foxtech.device.protocol.v1.mitsubishi.plc.fx.entity.MitsubishiPlcFxForceOnEntity;
import cn.foxtech.device.protocol.v1.mitsubishi.plc.fx.frame.MitsubishiPlcFxProtocolFrame;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

import java.util.HashMap;
import java.util.Map;

public class TestUtils {
    public static void main(String[] args) throws Exception {
        test1();
        test2();
    }

    public static void test1() {
        try {
            // 读取数据
            MitsubishiPlcFxDeviceReadEntity readEntity = new MitsubishiPlcFxDeviceReadEntity();
            readEntity.setTarget(MitsubishiPlcFxEntity.TAR_D);
            readEntity.setAddress(123);
            readEntity.setCount(4);
            String hexString = HexUtils.byteArrayToHexString(MitsubishiPlcFxProtocolFrame.encodePack(readEntity));
            MitsubishiPlcFxProtocolFrame.decodePack(HexUtils.hexStringToByteArray("02 33 34 31 32 43 44 41 42 03 44 37 "), readEntity);

            MitsubishiPlcFxDeviceWriteEntity writeEntity = new MitsubishiPlcFxDeviceWriteEntity();
            writeEntity.setTarget(MitsubishiPlcFxEntity.TAR_D);
            writeEntity.setAddress(123);
            writeEntity.setData("1234ABCD");
            hexString = HexUtils.byteArrayToHexString(MitsubishiPlcFxProtocolFrame.encodePack(writeEntity));
            MitsubishiPlcFxProtocolFrame.decodePack(HexUtils.hexStringToByteArray("15"), writeEntity);


            MitsubishiPlcFxForceOnEntity forceOnEntity = new MitsubishiPlcFxForceOnEntity();
            forceOnEntity.setTarget(MitsubishiPlcFxEntity.TAR_C);
            forceOnEntity.setAddress(123);
            hexString = HexUtils.byteArrayToHexString(MitsubishiPlcFxProtocolFrame.encodePack(forceOnEntity));
            MitsubishiPlcFxProtocolFrame.decodePack(HexUtils.hexStringToByteArray("15 "), forceOnEntity);


            hexString = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void test2() {
        try {
            String hexString = "01 03 8A 00 E5 00 35 03 79 01 F4 00 09 02 1D 00 00 00 00 00 00 00 00 00 11 01 30 00 00 00 15 01 30 00 3B 00 0F 01 19 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 28 4F";


            Map<String, Object> param = new HashMap<>();
            param.put("target", "D");
            param.put("address", 100);
            param.put("count", 4);
            param.put("templateName", "ms-plc-fx");
            param.put("tableName", "101.MB_PLC_FX_Address_Default_Table.csv");
            Map<String, Object> value = new HashMap<>();
            value = PlcFxProtocolDeviceRead.unpackReadHoldingRegister("02 33 34 31 32 43 44 41 42 03 44 37", param);
            //     value = ModBusProtocolReadHoldingRegisters.unpackReadHoldingRegister(hexString, param);

            //       String data = ModBusProtocolWriteRegisters.packWriteHoldingRegister(param);
            String ss = value.toString();

            param.putAll(value);
            param.put("逆变器11输出电流", 5.1);
            param.put("逆变器12输出电流", 5.2);
            param.put("逆变器13输出电流", 5.3);
            param.put("逆变器14输出电流", 5.4);
            param.put("逆变器15输出电流", 5.5);

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
            //     value = ModBusProtocolReadStatus.unpackReadCoilStatus("03 e5 00 00 00 05 01 01 02 52 01 ",param);
            //     value = ModBusProtocolReadStatus.unpackReadCoilStatus("03 e5 00 00 00 05 01 01 02 52 01 ",param);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
