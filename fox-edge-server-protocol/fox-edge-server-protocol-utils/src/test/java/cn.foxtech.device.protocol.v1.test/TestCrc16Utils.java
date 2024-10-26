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

public class TestCrc16Utils {
    public static void main(String[] args) {
        test();

        byte[] pdu = HexUtils.hexStringToByteArray("FC CF 01 05 00 00 02 C0 30 C3");
        int sum = 0;
        for (int i = 0; i < pdu.length - 1; i++) {
            sum += pdu[i] & 0xff;
        }

        // int crc = Crc16Utils.getCRC16(pdu, 0, pdu.length - 2, CrcType.CRC16MODBUS);
        int crc = Crc16Utils.getCRC16(pdu, 0, pdu.length - 2, 0x8005, 0xFFFF, 0x0000, true);

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

    private static void test() {
        try {


            byte[] pdu = HexUtils.hexStringToByteArray("30 36 8D 32 37 8E 31 36 3A 35 37 20 20 39 31 3331 39 37 0D 04 00 0F 60 04 04 FE 01 00 00 FF 0909 09 49 89 7F 00 00 00 00 81 40 20 10 0C 03 0003 0C 10 20 40 C0 40 00 04 03 00 FF 00 41 44 4444 7F 44 44 46 64 40 00 00 00 00 00 00 00 00 0000 00 00 00 00 00 00 00 08 1C 0B 08 08 8A 5C 201C 03 40 80 40 3F 00 00 00 00 00 FF 00 00 00 00FF 08 08 08 0C 08 00 00 0D 0F 60 10 22 64 0C 8000 E2 2C 20 3F 28 24 E2 00 00 00 00 00 80 70 0000 00 FF 00 80 40 20 30 00 00 00 10 10 D0 FF 9050 20 50 4C C3 4C 50 50 20 20 00 00 00 00 00 0000 00 00 00 00 00 00 00 00 00 00 20 24 24 E4 2424 20 10 10 FF 10 10 10 F8 10 00 80 40 20 F8 8740 30 0F F8 88 88 C8 88 0C 08 00 0D 04 03 0D 0400 0F 70 07 0F 08 08 08 0F 07 00 00 00 00 00 0000 00 00 02 0F 0F 02 0F 0F 02 00 00 00 00 00 0000 00 00 0E 0F 09 08 08 0C 0C 00 00 00 00 00 0000 00 00 10 10 17 12 12 12 12 FF 12 12 12 12 1318 10 00 20 60 3F 10 10 00 3F 40 40 5F 44 48 4740 70 00 00 00 00 00 00 00 FF 00 00 00 00 01 0000 00 00 00 00 00 00 00 00 00 00 00 00 00 00 0000 00 00 0D 0F 70 F0 F8 4C 44 44 C0 80 00 00 0000 00 00 00 00 00 20 F8 F8 20 F8 F8 20 00 00 0000 00 00 00 00 00 08 0C 84 C4 64 3C 18 00 00 0000 00 00 00 00 00 00 00 F8 49 4A 4C 48 F8 48 4C4A 49 FC 08 00 00 40 40 FE 40 40 80 FC 40 40 FF20 20 F0 20 00 00 02 02 02 02 02 02 FE 02 22 4282 82 02 03 02 00 00 00 00 00 00 00 00 00 00 0000 00 00 00 00 00 0D 04 03 0D  ");

            String text = new String(pdu, "ASCII");
            System.out.println(text);
        } catch (Exception e) {
            e.getMessage();
        }
    }

}
