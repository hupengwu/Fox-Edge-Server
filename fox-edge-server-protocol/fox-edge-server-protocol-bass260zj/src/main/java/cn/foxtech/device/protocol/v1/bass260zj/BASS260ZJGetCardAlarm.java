/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.bass260zj;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.telecom.core.entity.PduEntity;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

import java.util.HashMap;
import java.util.Map;

@FoxEdgeDeviceType(value = "BASS260ZJ", manufacturer = "广东高新兴")
public class BASS260ZJGetCardAlarm {
    /**
     * 读取刷卡记录
     *
     * @param param
     * @return
     */
    @FoxEdgeOperate(name = "读取告警状态", polling = true, type = FoxEdgeOperate.encoder, mode = FoxEdgeOperate.status, timeout = 2000)
    public static String packCmdGetAlarmStatus(Map<String, Object> param) {
        // 7E323030323431343430303030464441460D

        PduEntity entity = new PduEntity();
        entity.setVer((byte) 0x20);
        entity.setAddr((byte) 0x02);
        entity.setCid1((byte) 0x41);// 设备分类码 开关电源系统（整流器）
        entity.setCid2((byte) 0x44);// 获取告警状态

        byte[] data = new byte[0];
        entity.setData(data);
        byte[] arrCmd = PduEntity.encodePdu(entity);

        return HexUtils.byteArrayToHexString(arrCmd);
    }

    @FoxEdgeOperate(name = "读取告警状态", polling = true, type = FoxEdgeOperate.decoder, mode = FoxEdgeOperate.status, timeout = 2000)
    public static Map<String, Object> unpackCmdGetAlarmStatus(String hexString, Map<String, Object> param) {
        // 7E323030323431303034303043303030322020303020203030464239450D

        byte[] arrCmd = HexUtils.hexStringToByteArray(hexString);

        PduEntity entity = PduEntity.decodePdu(arrCmd);
        byte[] dat = entity.getData();

        // 检查:数据域长度
        if (dat.length < 2) {
            throw new ProtocolException("返回的data长度不正确！");
        }

        int size = (dat[0] & 0xff) * 0x100 + (dat[1] & 0xff);
        if (dat.length != size * 2 + 2) {
            throw new ProtocolException("返回的data长度不正确！");
        }

        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < size; i++) {
            int status = (dat[i * 2 + 2] & 0xff) * 0x100 + (dat[i * 2 + 3] & 0xff);

            String key = String.format("整流模块%02d", i + 1);
            String alarm = "";
            if (status == 0x01) {
                alarm = "故障告警";
            }
            if (status == 0xE3) {
                alarm = "断电告警";
            }
            if (status == 0xE5) {
                alarm = "高温告警";
            }
            if (status == 0xE9) {
                alarm = "过压告警";
            }
            if (status == 0xEF) {
                alarm = "损坏故障告警";
            }

            if (alarm.isEmpty()) {
                continue;
            }

            result.put(key + alarm, true);

        }


        return result;
    }
}
