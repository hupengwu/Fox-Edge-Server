package cn.foxtech.device.protocol.v1.hikvision.fire.core.entity;

import cn.foxtech.device.protocol.v1.hikvision.fire.core.entity.infobj.*;
import cn.foxtech.device.protocol.v1.hikvision.fire.core.enums.AduType;

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

        aduTypeInfObjMap.put(AduType.systemStatus, InfObjSysStatusEntity.class);
        aduTypeInfObjMap.put(AduType.compStatus, InfObjCompStatusEntity.class);
        aduTypeInfObjMap.put(AduType.deviceStatus, InfObjDeviceStatusEntity.class);
        aduTypeInfObjMap.put(AduType.deviceOperate, InfObjDeviceOperateEntity.class);
        aduTypeInfObjMap.put(AduType.deviceSoftVer, InfObjDeviceSoftVerEntity.class);
        aduTypeInfObjMap.put(AduType.syncClock, InfObjSyncClockEntity.class);
        aduTypeInfObjMap.put(AduType.setInspection, InfObjSetInspectionEntity.class);


        return aduTypeInfObjMap;
    }
}
