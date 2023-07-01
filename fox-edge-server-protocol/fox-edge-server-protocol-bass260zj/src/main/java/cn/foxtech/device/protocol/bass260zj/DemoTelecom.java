package cn.foxtech.device.protocol.bass260zj;

import cn.foxtech.device.protocol.core.utils.HexUtils;
import cn.foxtech.device.protocol.telecom.core.TelecomEntity;
import cn.foxtech.device.protocol.telecom.core.TelecomProtocol;

public class DemoTelecom {

    public static void main(String[] args) {
        byte[] arrCmd = HexUtils.hexStringToByteArray("7E323030323431343430303030464441460D");
        TelecomEntity entity = TelecomProtocol.unPackCmd2Entity(arrCmd);
        BASS260ZJGetCardAlarm.unpackCmdGetAlarmStatus("7E323030323431303034303043303030322020303020203030464239450D", null);

    }
}
