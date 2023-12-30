package cn.foxtech.device.protocol.v1.dahua.fire.core.entity;

import cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj.*;
import cn.foxtech.device.protocol.v1.dahua.fire.core.enums.CmdType;

import java.util.HashMap;
import java.util.Map;

public class AduInfObjMap {
    private static final Map<CmdType, Class> cmdTypeInfObjMap = new HashMap<>();

    /**
     * 获得Class
     *
     * @param cmdType 命令字类型
     * @return 具体对应的InfObjEntity的Class
     */
    public static Class getInfObjClass(CmdType cmdType) {
        return inst().get(cmdType);
    }

    private static Map<CmdType, Class> inst() {
        if (!cmdTypeInfObjMap.isEmpty()) {
            return cmdTypeInfObjMap;
        }

        cmdTypeInfObjMap.put(CmdType.register, InfObjRegisterEntity.class);
        cmdTypeInfObjMap.put(CmdType.registerEx, InfObjRegisterExEntity.class);
        cmdTypeInfObjMap.put(CmdType.syncClock, InfObjSyncClockEntity.class);
        cmdTypeInfObjMap.put(CmdType.sysStatus, InfObjSysStatusEntity.class);
        cmdTypeInfObjMap.put(CmdType.sysAnalog, InfObjSysAnalogEntity.class);
        cmdTypeInfObjMap.put(CmdType.compStatus, InfObjCompStatusEntity.class);
        cmdTypeInfObjMap.put(CmdType.compStatusEx, InfObjCompStatusExEntity.class);
        cmdTypeInfObjMap.put(CmdType.compAnalog, InfObjCompAnalogEntity.class);
        cmdTypeInfObjMap.put(CmdType.compAnalogEx, InfObjCompAnalogExEntity.class);
        cmdTypeInfObjMap.put(CmdType.syncParamFix, InfObjSyncParamFixEntity.class);
        cmdTypeInfObjMap.put(CmdType.syncParamVar, InfObjSyncParamVarEntity.class);
        cmdTypeInfObjMap.put(CmdType.generalData, InfObjGeneralDataEntity.class);


        return cmdTypeInfObjMap;
    }
}
