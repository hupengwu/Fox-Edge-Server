/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.dahua.fire.core.entity;

import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj.*;
import cn.foxtech.device.protocol.v1.dahua.fire.core.enums.AduType;

import java.util.HashMap;
import java.util.Map;

public class AduInfObjMap {
    private static final Map<AduType, Class> aduTypeInfObjMap = new HashMap<>();

    /**
     * 获得Class
     *
     * @param aduType 命令字类型
     * @return 具体对应的InfObjEntity的Class
     */
    public static Class getInfObjClass(AduType aduType) {
        return inst().get(aduType);
    }

    private static Map<AduType, Class> inst() {
        if (!aduTypeInfObjMap.isEmpty()) {
            return aduTypeInfObjMap;
        }

        aduTypeInfObjMap.put(AduType.register, InfObjRegisterEntity.class);
        aduTypeInfObjMap.put(AduType.registerEx, InfObjRegisterExEntity.class);
        aduTypeInfObjMap.put(AduType.syncClock, InfObjSyncClockEntity.class);
        aduTypeInfObjMap.put(AduType.sysStatus, InfObjSysStatusEntity.class);
        aduTypeInfObjMap.put(AduType.sysAnalog, InfObjSysAnalogEntity.class);
        aduTypeInfObjMap.put(AduType.compStatus, InfObjCompStatusEntity.class);
        aduTypeInfObjMap.put(AduType.compStatusEx, InfObjCompStatusExEntity.class);
        aduTypeInfObjMap.put(AduType.compAnalog, InfObjCompAnalogEntity.class);
        aduTypeInfObjMap.put(AduType.compAnalogEx, InfObjCompAnalogExEntity.class);
        aduTypeInfObjMap.put(AduType.syncParamFix, InfObjSyncParamFixEntity.class);
        aduTypeInfObjMap.put(AduType.syncParamVar, InfObjSyncParamVarEntity.class);
        aduTypeInfObjMap.put(AduType.generalData, InfObjGeneralDataEntity.class);
        aduTypeInfObjMap.put(AduType.deleteFunc, InfObjDeleteFuncEntity.class);
        aduTypeInfObjMap.put(AduType.getParamFix, InfObjGetParamFixEntity.class);
        aduTypeInfObjMap.put(AduType.getParamRspFix, InfObjGetParamRspFixEntity.class);
        aduTypeInfObjMap.put(AduType.getParamVar, InfObjGetParamVarEntity.class);
        aduTypeInfObjMap.put(AduType.getParamRspVar, InfObjGetParamRspVarEntity.class);
        aduTypeInfObjMap.put(AduType.setParamFix, InfObjSetParamFixEntity.class);
        aduTypeInfObjMap.put(AduType.setParamVar, InfObjSetParamVarEntity.class);
        aduTypeInfObjMap.put(AduType.remoteMute, InfObjRemoteMuteEntity.class);
        aduTypeInfObjMap.put(AduType.generalGet, InfObjGeneralGetEntity.class);
        aduTypeInfObjMap.put(AduType.generalSet, InfObjGeneralSetEntity.class);
        aduTypeInfObjMap.put(AduType.getFuncReq, InfObjGetFuncReqEntity.class);
        aduTypeInfObjMap.put(AduType.getFuncRsp, InfObjGetFuncRspEntity.class);
        aduTypeInfObjMap.put(AduType.setFuncReq, InfObjSetFuncReqEntity.class);
        aduTypeInfObjMap.put(AduType.upgradeReq, InfObjUpgradeReqEntity.class);
        aduTypeInfObjMap.put(AduType.upgradeStart, InfObjUpgradeStartEntity.class);
        aduTypeInfObjMap.put(AduType.upgradeEnd, InfObjUpgradeEndEntity.class);


        return aduTypeInfObjMap;
    }
}
