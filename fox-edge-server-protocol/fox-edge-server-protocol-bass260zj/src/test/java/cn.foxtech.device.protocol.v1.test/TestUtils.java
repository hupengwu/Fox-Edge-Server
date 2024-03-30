package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.bass260zj.BASS260ZJGetCardAlarm;
import cn.foxtech.device.protocol.v1.telecom.core.entity.PduEntity;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

public class TestUtils {
    public static void main(String[] args) {
        byte[] arrCmd = HexUtils.hexStringToByteArray("7E323030323431343430303030464441460D");
        PduEntity entity = PduEntity.decodePdu(arrCmd);
        BASS260ZJGetCardAlarm.unpackCmdGetAlarmStatus("7E323030323431303034303043303030322020303020203030464239450D", null);

    }
}
