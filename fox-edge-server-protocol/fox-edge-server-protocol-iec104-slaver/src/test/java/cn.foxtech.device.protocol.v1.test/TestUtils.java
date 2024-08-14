/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.core.utils.JsonUtils;
import cn.foxtech.device.protocol.v1.iec104.core.builder.ApduVOBuilder;
import cn.foxtech.device.protocol.v1.iec104.core.encoder.ApduEncoder;
import cn.foxtech.device.protocol.v1.iec104.core.encoder.ValueEncoder;
import cn.foxtech.device.protocol.v1.iec104.core.entity.ApduEntity;
import cn.foxtech.device.protocol.v1.iec104.core.vo.ApduVO;
import cn.foxtech.device.protocol.v1.iec104.slaver.template.Iec104Template;
import cn.foxtech.device.protocol.v1.iec104.slaver.template.JReadDataTemplate;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestUtils {
    public static void main(String[] args) throws Exception {
        test1();
        JHoldingRegistersTest2();
    }

    public static void test1() {
        try {
            ApduEntity apduEntity1 = ApduEncoder.decodeApdu(HexUtils.hexStringToByteArray("68  14  08 00 04 00 67 01 06 00 01 00 00 00 00 A4 92 06 0F EA 05 0F"));
            ApduEntity apduEntity2 = ApduEncoder.decodeApdu(HexUtils.hexStringToByteArray("68  14 0A 00 04 00 67 01 07 00 01 00 00 00 00 A4 92 06 0F EA 05 0F"));
            ApduEntity apduEntity3 = ApduEncoder.decodeApdu(HexUtils.hexStringToByteArray("680401007A00"));
            ApduEntity apduEntity4 = ApduEncoder.decodeApdu(HexUtils.hexStringToByteArray("680401000200"));
            ApduEntity apduEntity5 = ApduEncoder.decodeApdu(HexUtils.hexStringToByteArray("68A37C0002000BB2140001005B4A00020100050400328900323200323200989800243400232300244500331200020100050400328900323200323200989800243400232300244500331200020100050400328900323200323200989800243400232300244500331200020100050400328900323200323200989800243400232300244500331200020100050400328900323200323200989800243400232300244500331200"));
            ApduEntity apduEntity6 = ApduEncoder.decodeApdu(HexUtils.hexStringToByteArray("680401007C00"));

            ApduEntity apduEntity7 = ApduEncoder.decodeApdu(HexUtils.hexStringToByteArray("68 0e 8a 00 00 00 64 01 0a 00 01 00 14 00 00 14"));

            ApduVO apduVO1 = ApduVOBuilder.buildVO(apduEntity1);
            ApduVO apduVO2 = ApduVOBuilder.buildVO(apduEntity2);
            apduVO2.getWaitEndFlag().add(6);
            apduVO2.getWaitEndFlag().add(10);

            String json1 = JsonUtils.buildJson(apduVO1);
            String json2 = JsonUtils.buildJson(apduVO2);

            byte[] CP56Time2a = Arrays.copyOfRange(apduEntity1.getAsdu().getData(), 3, 3 + 7);
            Date date = ValueEncoder.decodeCP56Time2a(CP56Time2a);
            date.toString();

            int end = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void JHoldingRegistersTest2() {
        try {
            String hexString = "01 03 8A 00 E5 00 35 03 79 01 F4 00 09 02 1D 00 00 00 00 00 00 00 00 00 11 01 30 00 00 00 15 01 30 00 3B 00 0F 01 19 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 28 4F";

            Map<String, Object> param = new HashMap<>();
            param.put("device_addr", 1);
            param.put("reg_addr", "04 1F");
            param.put("reg_cnt", 7);
            param.put("modbus_mode", "RTU");
            param.put("operate_name", "Read  Single Point Signal");
            param.put("template_name", "Read  Single Point Signal Table");
            param.put("table_name", "101.CETUPS_Read  Single Point Signal Table.csv");

            JReadDataTemplate template = Iec104Template.newInstance().getTemplate("总召唤", "Read Single Point Signal Table", 1, "101.IEC104_Read Single Point Signal Table.csv", JReadDataTemplate.class);
            // template.decode();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
