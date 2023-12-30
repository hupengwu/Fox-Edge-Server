package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.AduEntity;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.PduEntity;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj.InfObjSyncClockEntity;
import cn.foxtech.device.protocol.v1.dahua.fire.core.enums.CmdType;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.AddressUtil;

public class PlatForm2Device {
    public static void main(String[] args) {
        testSyncClock();
    }

    public static void testSyncClock() {
        byte[] data = new byte[0];

        // 平台请求
        PduEntity platform = new PduEntity();
        platform.getCtrlEntity().setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(CmdType.syncClock.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(CmdType.syncClock);
        platform.getAduEntity().getInfObjEntities().add(new InfObjSyncClockEntity());
        data = PduEntity.encodeEntity(platform);

        // 自我验证
        platform = PduEntity.decodeEntity(data);

        // 设备应答
        PduEntity device = new PduEntity();
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(platform.getCtrlEntity().getSrcAddr());
        device.getCtrlEntity().setSn(platform.getCtrlEntity().getSn());
        device.getCtrlEntity().setProtocolVersion(platform.getCtrlEntity().getProtocolVersion());
        device.getCtrlEntity().setCmd(CmdType.confirm.getCmd());
        data = PduEntity.encodeEntity(device);

        // 自我验证
        device = PduEntity.decodeEntity(data);
    }
}
