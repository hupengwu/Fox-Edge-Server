package cn.foxtech.device.protocol.v1.test;

import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.AduEntity;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.TcpPduEntity;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj.*;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj.object.AnalogObject;
import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj.object.TlvObject;
import cn.foxtech.device.protocol.v1.dahua.fire.core.enums.CmdType;
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
        device.getCtrlEntity().setCmd(CmdType.register.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.register);
        device.getAduEntity().getInfObjEntities().add(new InfObjRegisterEntity());
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);

        // 平台应答
        TcpPduEntity platform = new TcpPduEntity();
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(device.getCtrlEntity().getSrcAddr());
        platform.getCtrlEntity().setSn(device.getCtrlEntity().getSn());
        platform.getCtrlEntity().setProtocolVersion(device.getCtrlEntity().getProtocolVersion());
        platform.getCtrlEntity().setCmd(CmdType.confirm.getCmd());
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
        device.getCtrlEntity().setCmd(CmdType.registerEx.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.registerEx);
        device.getAduEntity().getInfObjEntities().add(new InfObjRegisterExEntity());
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);

        // 平台应答
        TcpPduEntity platform = new TcpPduEntity();
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(device.getCtrlEntity().getSrcAddr());
        platform.getCtrlEntity().setSn(device.getCtrlEntity().getSn());
        platform.getCtrlEntity().setProtocolVersion(device.getCtrlEntity().getProtocolVersion());
        platform.getCtrlEntity().setCmd(CmdType.confirm.getCmd());
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
        device.getCtrlEntity().setCmd(CmdType.active.getCmd());
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);

        // 平台应答
        TcpPduEntity platform = new TcpPduEntity();
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(device.getCtrlEntity().getSrcAddr());
        platform.getCtrlEntity().setSn(device.getCtrlEntity().getSn());
        platform.getCtrlEntity().setProtocolVersion(device.getCtrlEntity().getProtocolVersion());
        platform.getCtrlEntity().setCmd(CmdType.confirm.getCmd());
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
        device.getCtrlEntity().setCmd(CmdType.sysStatus.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.sysStatus);
        device.getAduEntity().getInfObjEntities().add(new InfObjSysStatusEntity());
        device.getAduEntity().getInfObjEntities().add(new InfObjSysStatusEntity());
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);

        // 平台应答
        TcpPduEntity platform = new TcpPduEntity();
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(device.getCtrlEntity().getSrcAddr());
        platform.getCtrlEntity().setSn(device.getCtrlEntity().getSn());
        platform.getCtrlEntity().setProtocolVersion(device.getCtrlEntity().getProtocolVersion());
        platform.getCtrlEntity().setCmd(CmdType.confirm.getCmd());
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
        device.getCtrlEntity().setCmd(CmdType.sysAnalog.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.sysAnalog);
        device.getAduEntity().getInfObjEntities().add(new InfObjSysAnalogEntity());
        device.getAduEntity().getInfObjEntities().add(new InfObjSysAnalogEntity());
        data = TcpPduEntity.encodeEntity(device);

        // 自我验证
        device = TcpPduEntity.decodeEntity(data);

        // 平台应答
        TcpPduEntity platform = new TcpPduEntity();
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(device.getCtrlEntity().getSrcAddr());
        platform.getCtrlEntity().setSn(device.getCtrlEntity().getSn());
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
        device.getCtrlEntity().setCmd(CmdType.compStatus.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.compStatus);
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
        device.getCtrlEntity().setCmd(CmdType.compStatusEx.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.compStatusEx);
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
        device.getCtrlEntity().setCmd(CmdType.compAnalog.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.compAnalog);
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
        device.getCtrlEntity().setCmd(CmdType.compAnalogEx.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.compAnalogEx);
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
        device.getCtrlEntity().setCmd(CmdType.syncParamFix.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.syncParamFix);
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
        device.getCtrlEntity().setCmd(CmdType.syncParamVar.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.syncParamVar);
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
        device.getCtrlEntity().setCmd(CmdType.deleteFunc.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.deleteFunc);
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
        device.getCtrlEntity().setCmd(CmdType.getParamRspFix.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.getParamRspFix);
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
        device.getCtrlEntity().setCmd(CmdType.getParamRspVar.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.getParamRspVar);
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
        device.getCtrlEntity().setCmd(CmdType.getFuncRsp.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.getFuncRsp);
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
        device.getCtrlEntity().setCmd(CmdType.setFuncReq.getCmd());
        device.setAduEntity(new AduEntity());
        device.getAduEntity().setType(CmdType.setFuncReq);
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
        platform.getCtrlEntity().setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(CmdType.upgradeStart.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(CmdType.upgradeStart);
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
        platform.getCtrlEntity().setSn(8);
        platform.getCtrlEntity().setSrcAddr(AddressUtil.PLATFORM_DEFAULT);
        platform.getCtrlEntity().setDstAddr(AddressUtil.DEVICE_DEFAULT);
        platform.getCtrlEntity().setCmd(CmdType.upgradeEnd.getCmd());
        platform.setAduEntity(new AduEntity());
        platform.getAduEntity().setType(CmdType.upgradeEnd);
        InfObjUpgradeEndEntity infObj = new InfObjUpgradeEndEntity();
        platform.getAduEntity().getInfObjEntities().add(infObj);
        data = TcpPduEntity.encodeEntity(platform);

        // 自我验证
        platform = TcpPduEntity.decodeEntity(data);
    }
}
