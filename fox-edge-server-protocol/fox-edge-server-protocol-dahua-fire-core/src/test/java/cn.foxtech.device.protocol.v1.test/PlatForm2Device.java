package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.AduEntity;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.PduEntity;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj.*;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj.object.TlvObject;
import cn.foxtech.device.protocol.v1.dahua.fire.core.enums.CmdType;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.AddressUtil;

public class PlatForm2Device {
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

    public static void getParamFix() {
        byte[] data = new byte[0];

        // 平台请求
        PduEntity platform = new PduEntity();
        platform.getCtrlEntity().setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(CmdType.getParamFix.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(CmdType.getParamFix);
        platform.getAduEntity().getInfObjEntities().add(new InfObjGetParamFixEntity());
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

    public static void getParamVar() {
        byte[] data = new byte[0];

        // 平台请求
        PduEntity platform = new PduEntity();
        platform.getCtrlEntity().setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(CmdType.getParamVar.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(CmdType.getParamVar);
        platform.getAduEntity().getInfObjEntities().add(new InfObjGetParamVarEntity());
        data = PduEntity.encodeEntity(platform);

        // 自我验证
        platform = PduEntity.decodeEntity(data);
    }

    public static void setParamFix() {
        byte[] data = new byte[0];

        // 平台请求
        PduEntity platform = new PduEntity();
        platform.getCtrlEntity().setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(CmdType.setParamFix.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(CmdType.setParamFix);
        platform.getAduEntity().getInfObjEntities().add(new InfObjSetParamFixEntity());
        data = PduEntity.encodeEntity(platform);

        // 自我验证
        platform = PduEntity.decodeEntity(data);
    }

    public static void setParamVar() {
        byte[] data = new byte[0];

        // 平台请求
        PduEntity platform = new PduEntity();
        platform.getCtrlEntity().setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(CmdType.setParamVar.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(CmdType.setParamVar);
        platform.getAduEntity().getInfObjEntities().add(new InfObjSetParamVarEntity());
        data = PduEntity.encodeEntity(platform);

        // 自我验证
        platform = PduEntity.decodeEntity(data);
    }

    public static void remoteMute() {
        byte[] data = new byte[0];

        // 平台请求
        PduEntity platform = new PduEntity();
        platform.getCtrlEntity().setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(CmdType.remoteMute.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(CmdType.remoteMute);
        platform.getAduEntity().getInfObjEntities().add(new InfObjRemoteMuteEntity());
        data = PduEntity.encodeEntity(platform);

        // 自我验证
        platform = PduEntity.decodeEntity(data);
    }

    public static void generalGet() {
        byte[] data = new byte[0];

        // 平台请求
        PduEntity platform = new PduEntity();
        platform.getCtrlEntity().setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(CmdType.generalGet.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(CmdType.generalGet);
        InfObjGeneralGetEntity infObj = new InfObjGeneralGetEntity();
        TlvObject tlv = new TlvObject();
        tlv.setType(1);
        tlv.setValue("12345678");
        infObj.getTlvs().add(tlv);
        infObj.getTlvs().add(tlv);
        platform.getAduEntity().getInfObjEntities().add(infObj);
        data = PduEntity.encodeEntity(platform);

        // 自我验证
        platform = PduEntity.decodeEntity(data);
    }

    public static void generalSet() {
        byte[] data = new byte[0];

        // 平台请求
        PduEntity platform = new PduEntity();
        platform.getCtrlEntity().setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(CmdType.generalSet.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(CmdType.generalSet);
        InfObjGeneralSetEntity infObj = new InfObjGeneralSetEntity();
        TlvObject tlv = new TlvObject();
        tlv.setType(1);
        tlv.setValue("12345678");
        infObj.getTlvs().add(tlv);
        infObj.getTlvs().add(tlv);
        platform.getAduEntity().getInfObjEntities().add(infObj);
        data = PduEntity.encodeEntity(platform);

        // 自我验证
        platform = PduEntity.decodeEntity(data);
    }

    public static void getFuncReq() {
        byte[] data = new byte[0];

        // 平台请求
        PduEntity platform = new PduEntity();
        platform.getCtrlEntity().setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(CmdType.getFuncReq.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(CmdType.getFuncReq);
        InfObjGetFuncReqEntity infObj = new InfObjGetFuncReqEntity();
        TlvObject tlv = new TlvObject();
        tlv.setType(1);
        tlv.setValue("12345678");
        infObj.getTlvs().add(tlv);
        infObj.getTlvs().add(tlv);
        platform.getAduEntity().getInfObjEntities().add(infObj);
        data = PduEntity.encodeEntity(platform);

        // 自我验证
        platform = PduEntity.decodeEntity(data);
    }

    public static void setFuncReq() {
        byte[] data = new byte[0];

        // 平台请求
        PduEntity platform = new PduEntity();
        platform.getCtrlEntity().setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(CmdType.setFuncReq.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(CmdType.setFuncReq);
        InfObjSetFuncReqEntity infObj = new InfObjSetFuncReqEntity();
        platform.getAduEntity().getInfObjEntities().add(infObj);
        data = PduEntity.encodeEntity(platform);

        // 自我验证
        platform = PduEntity.decodeEntity(data);
    }

    public static void upgradeReq() {
        byte[] data = new byte[0];

        // 平台请求
        PduEntity platform = new PduEntity();
        platform.getCtrlEntity().setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(CmdType.upgradeReq.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(CmdType.upgradeReq);
        InfObjUpgradeReqEntity infObj = new InfObjUpgradeReqEntity();
        platform.getAduEntity().getInfObjEntities().add(infObj);
        data = PduEntity.encodeEntity(platform);

        // 自我验证
        platform = PduEntity.decodeEntity(data);
    }


}
