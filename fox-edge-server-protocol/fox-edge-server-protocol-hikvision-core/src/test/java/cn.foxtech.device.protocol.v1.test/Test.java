package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.hikvision.core.entity.AduEntity;
import cn.foxtech.device.protocol.v1.hikvision.core.entity.TcpPduEntity;
import cn.foxtech.device.protocol.v1.hikvision.core.entity.infobj.*;
import cn.foxtech.device.protocol.v1.hikvision.core.enums.AduType;
import cn.foxtech.device.protocol.v1.hikvision.core.enums.CmdType;
import cn.foxtech.device.protocol.v1.hikvision.core.utils.AddressUtil;

public class Test {
    public static void main(String[] args) {
        sysStatus();
        compStatus();
        deviceStatus();
        deviceOperate();
        syncClock();
        setInspection();
    }

    public static void sysStatus() {
        byte[] data = new byte[10];

        // 设备请求
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(CmdType.confirm.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(AduType.systemStatus);
        device.getAduEntity().getInfObjEntities().add(new InfObjSysStatusEntity());
        device.getAduEntity().getInfObjEntities().add(new InfObjSysStatusEntity());
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);

        // 平台应答
        TcpPduEntity platform = new TcpPduEntity();
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(device.getCtrlEntity().getSrcAddr());
        platform.setSn(device.getSn());
        platform.getCtrlEntity().setProtocolVersion(device.getCtrlEntity().getProtocolVersion());
        platform.getCtrlEntity().setCmd(CmdType.confirm.getCmd());
        data = TcpPduEntity.encodeEntity(platform);

        // 自我验证
        platform = TcpPduEntity.decodeEntity(data);
    }

    public static void compStatus() {
        byte[] data = new byte[10];

        // 设备请求
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(CmdType.confirm.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(AduType.compStatus);
        device.getAduEntity().getInfObjEntities().add(new InfObjCompStatusEntity());
        device.getAduEntity().getInfObjEntities().add(new InfObjCompStatusEntity());
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);
    }

    public static void deviceStatus() {
        byte[] data = new byte[10];

        // 设备请求
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(CmdType.confirm.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(AduType.deviceStatus);
        device.getAduEntity().getInfObjEntities().add(new InfObjDeviceStatusEntity());
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);
    }

    public static void deviceOperate() {
        byte[] data = new byte[10];

        // 设备请求
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(CmdType.confirm.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(AduType.deviceOperate);
        device.getAduEntity().getInfObjEntities().add(new InfObjDeviceOperateEntity());
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);
    }

    public static void syncClock() {
        byte[] data = new byte[10];

        // 设备请求
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(CmdType.confirm.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(AduType.syncClock);
        device.getAduEntity().getInfObjEntities().add(new InfObjSyncClockEntity());
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);
    }

    public static void setInspection() {
        byte[] data = new byte[10];

        // 设备请求
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(CmdType.confirm.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(AduType.setInspection);
        device.getAduEntity().getInfObjEntities().add(new InfObjSetInspectionEntity());
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);
    }


}
