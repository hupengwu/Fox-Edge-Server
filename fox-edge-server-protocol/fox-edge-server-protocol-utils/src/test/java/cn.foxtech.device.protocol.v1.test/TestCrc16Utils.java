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


import cn.foxtech.device.protocol.v1.utils.Crc16Utils;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.device.protocol.v1.utils.enums.CrcType;

public class TestCrc16Utils {
    public static void main(String[] args) {
        byte[] pdu = HexUtils.hexStringToByteArray("01 7F 03 01 81 1F E1");
       // int crc = Crc16Utils.getCRC16(pdu, 0, pdu.length - 2, CrcType.CRC16MODBUS);
        int crc = Crc16Utils.getCRC16(pdu, 0, pdu.length - 2, 0x8005,0xFFFF,0x0000,true);

        byte[] crcs = new byte[2];
        crcs[0] = (byte) (crc >> 8 & 0xff);
        crcs[1] = (byte) (crc >> 0 & 0xff);

        byte[] pdu1 = HexUtils.hexStringToByteArray("01 02 02 02 01 00 5C FE");
        int sum11 = (pdu1[pdu1.length - 1] & 0xff) + (pdu1[pdu1.length - 2] & 0xff) * 0x100;
        int sum12 = (pdu1[pdu1.length - 2] & 0xff) + (pdu1[pdu1.length - 1] & 0xff) * 0x100;

        byte[] pdu2 = HexUtils.hexStringToByteArray("01 7F 03 01 81 1F E1");
        int sum21 = (pdu2[pdu2.length - 1] & 0xff) + (pdu2[pdu2.length - 2] & 0xff) * 0x100;
        int sum22 = (pdu2[pdu2.length - 2] & 0xff) + (pdu2[pdu2.length - 1] & 0xff) * 0x100;

        byte[] pdu3 = HexUtils.hexStringToByteArray("01 02 02 05 8B 00 00 80 00 79 DA");
        int sum31 = (pdu3[pdu3.length - 1] & 0xff) + (pdu3[pdu3.length - 2] & 0xff) * 0x100;
        int sum32 = (pdu3[pdu3.length - 2] & 0xff) + (pdu3[pdu3.length - 1] & 0xff) * 0x100;


        // int polynomial = 0x8005;
        //  int initial = 0xFFFF;
        for (int polynomial = 0; polynomial < 0x10000; polynomial++) {
            for (int initial = 0; initial < 0x10000; initial++) {
                for (int xorOut = 0; xorOut < 0x10000; xorOut++) {
                    int value = Crc16Utils.getCRC16(pdu1, 0, pdu1.length - 2, polynomial, initial, xorOut, false);
                    if (!(value == sum11 || sum12 == value)) {
                        break;
                    }

                    value = Crc16Utils.getCRC16(pdu2, 0, pdu2.length - 2, polynomial, initial, xorOut, false);
                    if (!(value == sum21 || sum22 == value)) {
                        break;
                    }

                    value = Crc16Utils.getCRC16(pdu3, 0, pdu3.length - 2, polynomial, initial, xorOut, false);
                    if (!(value == sum31 || sum32 == value)) {
                        break;
                    }

                    System.out.println(Integer.toString(polynomial, 16) + "," + Integer.toString(initial, 16));
                }
            }
        }
    }

}
