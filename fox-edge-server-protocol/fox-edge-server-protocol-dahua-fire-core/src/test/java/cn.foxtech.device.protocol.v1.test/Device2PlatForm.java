package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.AduEntity;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.PduEntity;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj.*;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj.object.AnalogObject;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj.object.TlvObject;
import cn.foxtech.device.protocol.v1.dahua.fire.core.enums.CmdType;
import cn.foxtech.device.protocol.v1.dahua.fire.core.enums.ParType;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.AddressUtil;

public class Device2PlatForm {
    public static void main(String[] args) {
        testRegister();
        testRegisterEx();
        testActive();
        testSysStatus();
        sysAnalog();
        compStatus();
        compStatusEx();
        compAnalog();
        compAnalogEx();
        syncParamFix();
        syncParamVar();
        generalData();
    }

    public static void testRegister() {
        byte[] data = new byte[0];

        // 设备请求
        PduEntity device = new PduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(CmdType.register.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.register);
        device.getAduEntity().getInfObjEntities().add(new InfObjRegisterEntity());
        data = PduEntity.encodeEntity(device);

        // 自我验证
        device = PduEntity.decodeEntity(data);

        // 平台应答
        PduEntity platform = new PduEntity();
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(device.getCtrlEntity().getSrcAddr());
        platform.getCtrlEntity().setSn(device.getCtrlEntity().getSn());
        platform.getCtrlEntity().setProtocolVersion(device.getCtrlEntity().getProtocolVersion());
        platform.getCtrlEntity().setCmd(CmdType.confirm.getCmd());
        data = PduEntity.encodeEntity(platform);

        // 自我验证
        platform = PduEntity.decodeEntity(data);

    }

    public static void testRegisterEx() {
        byte[] data = new byte[0];

        // 设备请求
        PduEntity device = new PduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(CmdType.registerEx.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.registerEx);
        device.getAduEntity().getInfObjEntities().add(new InfObjRegisterExEntity());
        data = PduEntity.encodeEntity(device);

        // 自我验证
        device = PduEntity.decodeEntity(data);

        // 平台应答
        PduEntity platform = new PduEntity();
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(device.getCtrlEntity().getSrcAddr());
        platform.getCtrlEntity().setSn(device.getCtrlEntity().getSn());
        platform.getCtrlEntity().setProtocolVersion(device.getCtrlEntity().getProtocolVersion());
        platform.getCtrlEntity().setCmd(CmdType.confirm.getCmd());
        data = PduEntity.encodeEntity(platform);

        // 自我验证
        platform = PduEntity.decodeEntity(data);

    }


    public static void testActive() {
        byte[] data = new byte[0];

        // 设备请求
        PduEntity device = new PduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(CmdType.active.getCmd());
        data = PduEntity.encodeEntity(device);

        // 自我验证
        device = PduEntity.decodeEntity(data);

        // 平台应答
        PduEntity platform = new PduEntity();
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(device.getCtrlEntity().getSrcAddr());
        platform.getCtrlEntity().setSn(device.getCtrlEntity().getSn());
        platform.getCtrlEntity().setProtocolVersion(device.getCtrlEntity().getProtocolVersion());
        platform.getCtrlEntity().setCmd(CmdType.confirm.getCmd());
        data = PduEntity.encodeEntity(platform);

        // 自我验证
        platform = PduEntity.decodeEntity(data);
    }

    public static void testSysStatus() {
        byte[] data = new byte[10];

        // 设备请求
        PduEntity device = new PduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(CmdType.sysStatus.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.sysStatus);
        device.getAduEntity().getInfObjEntities().add(new InfObjSysStatusEntity());
        device.getAduEntity().getInfObjEntities().add(new InfObjSysStatusEntity());
        data = PduEntity.encodeEntity(device);

        // 自我验证
        device = PduEntity.decodeEntity(data);

        // 平台应答
        PduEntity platform = new PduEntity();
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(device.getCtrlEntity().getSrcAddr());
        platform.getCtrlEntity().setSn(device.getCtrlEntity().getSn());
        platform.getCtrlEntity().setProtocolVersion(device.getCtrlEntity().getProtocolVersion());
        platform.getCtrlEntity().setCmd(CmdType.confirm.getCmd());
        data = PduEntity.encodeEntity(platform);

        // 自我验证
        platform = PduEntity.decodeEntity(data);
    }

    public static void sysAnalog() {
        byte[] data = new byte[10];

        // 设备请求
        PduEntity device = new PduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(CmdType.sysAnalog.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.sysAnalog);
        device.getAduEntity().getInfObjEntities().add(new InfObjSysAnalogEntity());
        device.getAduEntity().getInfObjEntities().add(new InfObjSysAnalogEntity());
        data = PduEntity.encodeEntity(device);

        // 自我验证
        device = PduEntity.decodeEntity(data);

        // 平台应答
        PduEntity platform = new PduEntity();
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(device.getCtrlEntity().getSrcAddr());
        platform.getCtrlEntity().setSn(device.getCtrlEntity().getSn());
        platform.getCtrlEntity().setProtocolVersion(device.getCtrlEntity().getProtocolVersion());
        platform.getCtrlEntity().setCmd(CmdType.confirm.getCmd());
        data = PduEntity.encodeEntity(platform);

        // 自我验证
        platform = PduEntity.decodeEntity(data);
    }

    public static void compStatus() {
        byte[] data = new byte[10];

        // 设备请求
        PduEntity device = new PduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(CmdType.compStatus.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.compStatus);
        device.getAduEntity().getInfObjEntities().add(new InfObjCompStatusEntity());
        device.getAduEntity().getInfObjEntities().add(new InfObjCompStatusEntity());
        data = PduEntity.encodeEntity(device);

        // 自我验证
        device = PduEntity.decodeEntity(data);
    }

    public static void compStatusEx() {
        byte[] data = new byte[10];

        // 设备请求
        PduEntity device = new PduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(CmdType.compStatusEx.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.compStatusEx);
        device.getAduEntity().getInfObjEntities().add(new InfObjCompStatusExEntity());
        device.getAduEntity().getInfObjEntities().add(new InfObjCompStatusExEntity());
        data = PduEntity.encodeEntity(device);

        // 自我验证
        device = PduEntity.decodeEntity(data);
    }

    public static void compAnalog() {
        byte[] data = new byte[10];

        // 设备请求
        PduEntity device = new PduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(CmdType.compAnalog.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.compAnalog);
        device.getAduEntity().getInfObjEntities().add(new InfObjCompAnalogEntity());
        device.getAduEntity().getInfObjEntities().add(new InfObjCompAnalogEntity());
        data = PduEntity.encodeEntity(device);

        // 自我验证
        device = PduEntity.decodeEntity(data);
    }

    public static void compAnalogEx() {
        byte[] data = new byte[10];

        // 设备请求
        PduEntity device = new PduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(CmdType.compAnalogEx.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.compAnalogEx);
        InfObjCompAnalogExEntity infObj = new InfObjCompAnalogExEntity();
        infObj.getAnalogs().add(new AnalogObject());
        infObj.getAnalogs().add(new AnalogObject());
        device.getAduEntity().getInfObjEntities().add(infObj);
        data = PduEntity.encodeEntity(device);

        // 自我验证
        device = PduEntity.decodeEntity(data);
    }

    public static void syncParamFix() {
        byte[] data = new byte[10];

        // 设备请求
        PduEntity device = new PduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(CmdType.syncParamFix.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.syncParamFix);
        InfObjSyncParamFixEntity infObj = new InfObjSyncParamFixEntity();
        device.getAduEntity().getInfObjEntities().add(infObj);
        data = PduEntity.encodeEntity(device);

        // 自我验证
        device = PduEntity.decodeEntity(data);
    }

    public static void syncParamVar() {
        byte[] data = new byte[10];

        // 设备请求
        PduEntity device = new PduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(CmdType.syncParamVar.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.syncParamVar);
        InfObjSyncParamVarEntity infObj = new InfObjSyncParamVarEntity();
        infObj.getParam().setType(ParType.host);
        infObj.getParam().setValue("192.168.1.21");
        device.getAduEntity().getInfObjEntities().add(infObj);
        data = PduEntity.encodeEntity(device);

        // 自我验证
        device = PduEntity.decodeEntity(data);
    }

    public static void generalData() {
        byte[] data = new byte[10];

        // 设备请求
        PduEntity device = new PduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(CmdType.generalData.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.generalData);
        InfObjGeneralDataEntity infObj = new InfObjGeneralDataEntity();
        TlvObject tlv = new TlvObject();
        tlv.setType(1);
        tlv.setValue("12345678");
        infObj.getTlvs().add(tlv);
        infObj.getTlvs().add(tlv);
        device.getAduEntity().getInfObjEntities().add(infObj);
        data = PduEntity.encodeEntity(device);

        // 自我验证
        device = PduEntity.decodeEntity(data);
    }

}
