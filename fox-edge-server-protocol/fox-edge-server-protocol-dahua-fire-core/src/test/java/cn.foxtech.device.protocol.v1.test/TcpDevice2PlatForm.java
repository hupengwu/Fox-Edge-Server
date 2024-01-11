package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.AduEntity;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.TcpPduEntity;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj.*;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj.object.AnalogObject;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj.object.TlvObject;
import cn.foxtech.device.protocol.v1.dahua.fire.core.enums.AduType;
import cn.foxtech.device.protocol.v1.dahua.fire.core.enums.ParType;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.AddressUtil;

public class TcpDevice2PlatForm {
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
        deleteFunc();
        getParamRspFix();
        getParamRspVar();
        getFuncRsp();
        setFunc();
        upgradeStart();
        upgradeEnd();
    }

    public static void testRegister() {
        byte[] data = new byte[0];

        // 设备请求
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(AduType.register.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(AduType.register.getType());
        device.getAduEntity().getInfObjEntities().add(new InfObjRegisterEntity());
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);

        // 平台应答
        TcpPduEntity platform = new TcpPduEntity();
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(device.getCtrlEntity().getSrcAddr());
        platform.setSn(device.getSn());
        platform.getCtrlEntity().setProtocolVersion(device.getCtrlEntity().getProtocolVersion());
        platform.getCtrlEntity().setCmd(AduType.confirm.getCmd());
        data = TcpPduEntity.encodeEntity(platform);

        // 自我验证
        platform = TcpPduEntity.decodeEntity(data);

    }

    public static void testRegisterEx() {
        byte[] data = new byte[0];

        // 设备请求
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(AduType.registerEx.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(AduType.registerEx.getType());
        device.getAduEntity().getInfObjEntities().add(new InfObjRegisterExEntity());
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);

        // 平台应答
        TcpPduEntity platform = new TcpPduEntity();
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(device.getCtrlEntity().getSrcAddr());
        platform.setSn(device.getSn());
        platform.getCtrlEntity().setProtocolVersion(device.getCtrlEntity().getProtocolVersion());
        platform.getCtrlEntity().setCmd(AduType.confirm.getCmd());
        data = TcpPduEntity.encodeEntity(platform);

        // 自我验证
        platform = TcpPduEntity.decodeEntity(data);

    }


    public static void testActive() {
        byte[] data = new byte[0];

        // 设备请求
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(AduType.active.getCmd());
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);

        // 平台应答
        TcpPduEntity platform = new TcpPduEntity();
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(device.getCtrlEntity().getSrcAddr());
        platform.setSn(device.getSn());
        platform.getCtrlEntity().setProtocolVersion(device.getCtrlEntity().getProtocolVersion());
        platform.getCtrlEntity().setCmd(AduType.confirm.getCmd());
        data = TcpPduEntity.encodeEntity(platform);

        // 自我验证
        platform = TcpPduEntity.decodeEntity(data);
    }

    public static void testSysStatus() {
        byte[] data = new byte[10];

        // 设备请求
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(AduType.sysStatus.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(AduType.sysStatus.getType());
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
        platform.getCtrlEntity().setCmd(AduType.confirm.getCmd());
        data = TcpPduEntity.encodeEntity(platform);

        // 自我验证
        platform = TcpPduEntity.decodeEntity(data);
    }

    public static void sysAnalog() {
        byte[] data = new byte[10];

        // 设备请求
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(AduType.sysAnalog.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(AduType.sysAnalog.getType());
        device.getAduEntity().getInfObjEntities().add(new InfObjSysAnalogEntity());
        device.getAduEntity().getInfObjEntities().add(new InfObjSysAnalogEntity());
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);

        // 平台应答
        TcpPduEntity platform = new TcpPduEntity();
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(device.getCtrlEntity().getSrcAddr());
        platform.setSn(device.getSn());
        platform.getCtrlEntity().setProtocolVersion(device.getCtrlEntity().getProtocolVersion());
        platform.getCtrlEntity().setCmd(AduType.confirm.getCmd());
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
        device.getCtrlEntity().setCmd(AduType.compStatus.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(AduType.compStatus.getType());
        device.getAduEntity().getInfObjEntities().add(new InfObjCompStatusEntity());
        device.getAduEntity().getInfObjEntities().add(new InfObjCompStatusEntity());
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);
    }

    public static void compStatusEx() {
        byte[] data = new byte[10];

        // 设备请求
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(AduType.compStatusEx.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(AduType.compStatusEx.getType());
        device.getAduEntity().getInfObjEntities().add(new InfObjCompStatusExEntity());
        device.getAduEntity().getInfObjEntities().add(new InfObjCompStatusExEntity());
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);
    }

    public static void compAnalog() {
        byte[] data = new byte[10];

        // 设备请求
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(AduType.compAnalog.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(AduType.compAnalog.getType());
        device.getAduEntity().getInfObjEntities().add(new InfObjCompAnalogEntity());
        device.getAduEntity().getInfObjEntities().add(new InfObjCompAnalogEntity());
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);
    }

    public static void compAnalogEx() {
        byte[] data = new byte[10];

        // 设备请求
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(AduType.compAnalogEx.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(AduType.compAnalogEx.getType());
        InfObjCompAnalogExEntity infObj = new InfObjCompAnalogExEntity();
        infObj.getAnalogs().add(new AnalogObject());
        infObj.getAnalogs().add(new AnalogObject());
        device.getAduEntity().getInfObjEntities().add(infObj);
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);
    }

    public static void syncParamFix() {
        byte[] data = new byte[10];

        // 设备请求
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(AduType.syncParamFix.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(AduType.syncParamFix.getType());
        InfObjSyncParamFixEntity infObj = new InfObjSyncParamFixEntity();
        device.getAduEntity().getInfObjEntities().add(infObj);
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);
    }

    public static void syncParamVar() {
        byte[] data = new byte[10];

        // 设备请求
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(AduType.syncParamVar.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(AduType.syncParamVar.getType());
        InfObjSyncParamVarEntity infObj = new InfObjSyncParamVarEntity();
        infObj.getParam().setType(ParType.host);
        infObj.getParam().setValue("192.168.1.21");
        device.getAduEntity().getInfObjEntities().add(infObj);
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);
    }

    public static void generalData() {
        byte[] data = new byte[10];

        // 设备请求
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(AduType.generalData.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(AduType.generalData.getType());
        InfObjGeneralDataEntity infObj = new InfObjGeneralDataEntity();
        TlvObject tlv = new TlvObject();
        tlv.setType(1);
        tlv.setValue("12345678");
        infObj.getTlvs().add(tlv);
        infObj.getTlvs().add(tlv);
        device.getAduEntity().getInfObjEntities().add(infObj);
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);
    }

    public static void deleteFunc() {
        byte[] data = new byte[10];

        // 设备请求
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(AduType.deleteFunc.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(AduType.deleteFunc.getType());
        InfObjDeleteFuncEntity infObj = new InfObjDeleteFuncEntity();
        TlvObject tlv = new TlvObject();
        tlv.setType(1);
        tlv.setValue("12345678");
        infObj.getTlvs().add(tlv);
        infObj.getTlvs().add(tlv);
        device.getAduEntity().getInfObjEntities().add(infObj);
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);
    }

    public static void getParamRspFix() {
        byte[] data = new byte[10];

        // 设备请求
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(AduType.getParamRspFix.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(AduType.getParamRspFix.getType());
        InfObjGetParamRspFixEntity infObj = new InfObjGetParamRspFixEntity();
        device.getAduEntity().getInfObjEntities().add(infObj);
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);
    }

    public static void getParamRspVar() {
        byte[] data = new byte[10];

        // 设备请求
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(AduType.getParamRspVar.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(AduType.getParamRspVar.getType());
        InfObjGetParamRspVarEntity infObj = new InfObjGetParamRspVarEntity();
        device.getAduEntity().getInfObjEntities().add(infObj);
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);
    }

    public static void getFuncRsp() {
        byte[] data = new byte[10];

        // 设备请求
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(AduType.getFuncRsp.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(AduType.getFuncRsp.getType());
        InfObjGetFuncRspEntity infObj = new InfObjGetFuncRspEntity();
        device.getAduEntity().getInfObjEntities().add(infObj);
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);
    }

    public static void setFunc() {
        byte[] data = new byte[10];

        // 设备请求
        TcpPduEntity device = new TcpPduEntity();
        device.getCtrlEntity().setSn(8);
        device.getCtrlEntity().setSrcAddr(AddressUtil.DEVICE_DEFAULT);
        device.getCtrlEntity().setDstAddr(AddressUtil.PLATFORM_DEFAULT);
        device.getCtrlEntity().setCmd(AduType.setFuncReq.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(AduType.setFuncReq.getType());
        InfObjSetFuncReqEntity infObj = new InfObjSetFuncReqEntity();
        device.getAduEntity().getInfObjEntities().add(infObj);
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);
    }

    public static void upgradeStart() {
        byte[] data = new byte[0];

        // 平台请求
        TcpPduEntity platform = new TcpPduEntity();
        platform.setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(AduType.upgradeStart.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(AduType.upgradeStart.getType());
        InfObjUpgradeStartEntity infObj = new InfObjUpgradeStartEntity();
        platform.getAduEntity().getInfObjEntities().add(infObj);
        data = TcpPduEntity.encodeEntity(platform);

        // 自我验证
        platform = TcpPduEntity.decodeEntity(data);
    }

    public static void upgradeEnd() {
        byte[] data = new byte[0];

        // 平台请求
        TcpPduEntity platform = new TcpPduEntity();
        platform.setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(AduType.upgradeEnd.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(AduType.upgradeEnd.getType());
        InfObjUpgradeEndEntity infObj = new InfObjUpgradeEndEntity();
        platform.getAduEntity().getInfObjEntities().add(infObj);
        data = TcpPduEntity.encodeEntity(platform);

        // 自我验证
        platform = TcpPduEntity.decodeEntity(data);
    }
}
