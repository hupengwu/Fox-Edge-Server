/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.test;


import cn.foxtech.device.protocol.v1.hj212_2017.entity.PduEntity;

import java.io.UnsupportedEncodingException;

public class TestPduEntity {
    public static void main(String[] args) throws UnsupportedEncodingException {
        String str = "##0457QN=20210320163101890;ST=32;CN=2011;PW=123456;MN=81733553213013;Flag=4;CP=&&DataTime=20210320013400;w00000-Rtd=181.682,w00000-Flag=N;w21001-SampleTime=20210320005400,w21001-Rtd=45.160,w21001-Flag=N;w21011-SampleTime=20210320013400,w21011-Rtd=1.970,w21011-Flag=N;w21003-SampleTime=20210320013400,w21003-Rtd=53.131,w21003-Flag=N;w01018-SampleTime=20210320013400,w01018-Rtd=194.200,w01018-Flag=N;w01001-SampleTime=20210320013406,w01001-Rtd=7.496,w01001-Flag=N&&6E80\r\n";
        PduEntity pduEntity = PduEntity.decodePdu(str);
        str = PduEntity.encodePdu(pduEntity);
        pduEntity = PduEntity.decodePdu(str);

    }


}
