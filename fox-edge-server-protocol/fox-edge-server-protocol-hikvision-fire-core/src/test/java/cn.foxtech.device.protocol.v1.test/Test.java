/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.hikvision.fire.core.entity.AduEntity;
import cn.foxtech.device.protocol.v1.hikvision.fire.core.entity.TcpPduEntity;
import cn.foxtech.device.protocol.v1.hikvision.fire.core.entity.infobj.*;
import cn.foxtech.device.protocol.v1.hikvision.fire.core.enums.AduType;
import cn.foxtech.device.protocol.v1.hikvision.fire.core.enums.CmdType;
import cn.foxtech.device.protocol.v1.hikvision.fire.core.utils.AddressUtil;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

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
        String t = "{ctrlEntity={sn=54, protocolVersion=1.01, time=2021-10-28 10:35:44, srcAddr=F6380C000000, dstAddr=000000000000, aduLength=48, cmd=2}, aduEntity={type=2, infObjEntities=[{sysType=1, sysAddress=0, compType=0, compCirc=35579, compNode=1, compStatus=3, compDescription=\u0002, time=2021-10-28 10:35:44}]}, sn=54}";

        // 设备发送报文
        byte[] data = HexUtils.hexStringToByteArray("4040360001012C230A1C0A15F6380C0000000000000000003000020201010000FB8A01000300020000000000000000000000000000000000000000000000000000000000002C230A1C0A155B2323");

        TcpPduEntity device1 = TcpPduEntity.decodeEntity(data);
        device1.getAduEntity();

        // 上位机确认报文
        data = HexUtils.hexStringToByteArray("404013000101341a100c071300000000000040e201000000000003bf2323");
        device1 = TcpPduEntity.decodeEntity(data);
        device1.getAduEntity();

        // 设备请求
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(CmdType.confirm.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(AduType.systemStatus.getType());
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
        device.getAduEntity().setType(AduType.compStatus.getType());
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
        device.getAduEntity().setType(AduType.deviceStatus.getType());
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
        device.getAduEntity().setType(AduType.deviceOperate.getType());
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
        device.getAduEntity().setType(AduType.syncClock.getType());
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
        device.getAduEntity().setType(AduType.setInspection.getType());
        device.getAduEntity().getInfObjEntities().add(new InfObjSetInspectionEntity());
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);
    }


}
