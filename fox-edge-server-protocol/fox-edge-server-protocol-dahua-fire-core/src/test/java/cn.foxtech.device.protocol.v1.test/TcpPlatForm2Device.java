/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.AduEntity;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.TcpPduEntity;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj.*;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj.object.TlvObject;
import cn.foxtech.device.protocol.v1.dahua.fire.core.enums.AduType;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.AddressUtil;

public class TcpPlatForm2Device {
    public static void main(String[] args) {
        syncClock();
        getParamFix();
        getParamVar();
        setParamFix();
        setParamVar();
        remoteMute();
        generalGet();
        generalSet();
        getFuncReq();
        setFuncReq();
        upgradeReq();
    }

    public static void syncClock() {
        byte[] data = new byte[0];

        // 平台请求
        TcpPduEntity platform = new TcpPduEntity();
        platform.setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(AduType.syncClock.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(AduType.syncClock.getType());
        platform.getAduEntity().getInfObjEntities().add(new InfObjSyncClockEntity());
        data = TcpPduEntity.encodeEntity(platform);

        // 自我验证
        platform = TcpPduEntity.decodeEntity(data);

        // 设备应答
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(platform.getCtrlEntity().getSrcAddr());
        device.setSn(platform.getSn());
        device.getCtrlEntity().setProtocolVersion(platform.getCtrlEntity().getProtocolVersion());
        device.getCtrlEntity().setCmd(AduType.confirm.getCmd());
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);
    }

    public static void getParamFix() {
        byte[] data = new byte[0];

        // 平台请求
        TcpPduEntity platform = new TcpPduEntity();
        platform.setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(AduType.getParamFix.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(AduType.getParamFix.getType());
        platform.getAduEntity().getInfObjEntities().add(new InfObjGetParamFixEntity());
        data = TcpPduEntity.encodeEntity(platform);

        // 自我验证
        platform = TcpPduEntity.decodeEntity(data);

        // 设备应答
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(platform.getCtrlEntity().getSrcAddr());
        device.setSn(platform.getSn());
        device.getCtrlEntity().setProtocolVersion(platform.getCtrlEntity().getProtocolVersion());
        device.getCtrlEntity().setCmd(AduType.confirm.getCmd());
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);
    }

    public static void getParamVar() {
        byte[] data = new byte[0];

        // 平台请求
        TcpPduEntity platform = new TcpPduEntity();
        platform.setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(AduType.getParamVar.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(AduType.getParamVar.getType());
        platform.getAduEntity().getInfObjEntities().add(new InfObjGetParamVarEntity());
        data = TcpPduEntity.encodeEntity(platform);

        // 自我验证
        platform = TcpPduEntity.decodeEntity(data);
    }

    public static void setParamFix() {
        byte[] data = new byte[0];

        // 平台请求
        TcpPduEntity platform = new TcpPduEntity();
        platform.setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(AduType.setParamFix.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(AduType.setParamFix.getType());
        platform.getAduEntity().getInfObjEntities().add(new InfObjSetParamFixEntity());
        data = TcpPduEntity.encodeEntity(platform);

        // 自我验证
        platform = TcpPduEntity.decodeEntity(data);
    }

    public static void setParamVar() {
        byte[] data = new byte[0];

        // 平台请求
        TcpPduEntity platform = new TcpPduEntity();
        platform.setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(AduType.setParamVar.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(AduType.setParamVar.getType());
        platform.getAduEntity().getInfObjEntities().add(new InfObjSetParamVarEntity());
        data = TcpPduEntity.encodeEntity(platform);

        // 自我验证
        platform = TcpPduEntity.decodeEntity(data);
    }

    public static void remoteMute() {
        byte[] data = new byte[0];

        // 平台请求
        TcpPduEntity platform = new TcpPduEntity();
        platform.setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(AduType.remoteMute.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(AduType.remoteMute.getType());
        platform.getAduEntity().getInfObjEntities().add(new InfObjRemoteMuteEntity());
        data = TcpPduEntity.encodeEntity(platform);

        // 自我验证
        platform = TcpPduEntity.decodeEntity(data);
    }

    public static void generalGet() {
        byte[] data = new byte[0];

        // 平台请求
        TcpPduEntity platform = new TcpPduEntity();
        platform.setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(AduType.generalGet.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(AduType.generalGet.getType());
        InfObjGeneralGetEntity infObj = new InfObjGeneralGetEntity();
        TlvObject tlv = new TlvObject();
        tlv.setType(1);
        tlv.setValue("12345678");
        infObj.getTlvs().add(tlv);
        infObj.getTlvs().add(tlv);
        platform.getAduEntity().getInfObjEntities().add(infObj);
        data = TcpPduEntity.encodeEntity(platform);

        // 自我验证
        platform = TcpPduEntity.decodeEntity(data);
    }

    public static void generalSet() {
        byte[] data = new byte[0];

        // 平台请求
        TcpPduEntity platform = new TcpPduEntity();
        platform.setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(AduType.generalSet.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(AduType.generalSet.getType());
        InfObjGeneralSetEntity infObj = new InfObjGeneralSetEntity();
        TlvObject tlv = new TlvObject();
        tlv.setType(1);
        tlv.setValue("12345678");
        infObj.getTlvs().add(tlv);
        infObj.getTlvs().add(tlv);
        platform.getAduEntity().getInfObjEntities().add(infObj);
        data = TcpPduEntity.encodeEntity(platform);

        // 自我验证
        platform = TcpPduEntity.decodeEntity(data);
    }

    public static void getFuncReq() {
        byte[] data = new byte[0];

        // 平台请求
        TcpPduEntity platform = new TcpPduEntity();
        platform.setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(AduType.getFuncReq.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(AduType.getFuncReq.getType());
        InfObjGetFuncReqEntity infObj = new InfObjGetFuncReqEntity();
        TlvObject tlv = new TlvObject();
        tlv.setType(1);
        tlv.setValue("12345678");
        infObj.getTlvs().add(tlv);
        infObj.getTlvs().add(tlv);
        platform.getAduEntity().getInfObjEntities().add(infObj);
        data = TcpPduEntity.encodeEntity(platform);

        // 自我验证
        platform = TcpPduEntity.decodeEntity(data);
    }

    public static void setFuncReq() {
        byte[] data = new byte[0];

        // 平台请求
        TcpPduEntity platform = new TcpPduEntity();
        platform.setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(AduType.setFuncReq.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(AduType.setFuncReq.getType());
        InfObjSetFuncReqEntity infObj = new InfObjSetFuncReqEntity();
        platform.getAduEntity().getInfObjEntities().add(infObj);
        data = TcpPduEntity.encodeEntity(platform);

        // 自我验证
        platform = TcpPduEntity.decodeEntity(data);
    }

    public static void upgradeReq() {
        byte[] data = new byte[0];

        // 平台请求
        TcpPduEntity platform = new TcpPduEntity();
        platform.setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(AduType.upgradeReq.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(AduType.upgradeReq.getType());
        InfObjUpgradeReqEntity infObj = new InfObjUpgradeReqEntity();
        platform.getAduEntity().getInfObjEntities().add(infObj);
        data = TcpPduEntity.encodeEntity(platform);

        // 自我验证
        platform = TcpPduEntity.decodeEntity(data);
    }


}
