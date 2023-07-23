package cn.foxtech.manager.system.service;

import cn.foxtech.common.entity.entity.*;
import cn.foxtech.common.utils.ContainerUtils;
import cn.foxtech.common.utils.DifferUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class Method2EntityService {
    private static final Logger logger = Logger.getLogger(Method2EntityService.class);


    @Autowired
    private EntityManageService entityManageService;

    /**
     * 同步实体：
     * 1、将Device服务的OperateMethodEntity同步到System服务的OperateEntity
     * 2、将Trigger服务的TriggerMethodEntity同步到System服务的TriggerEntity
     */
    public void syncEntity() {
        try {
            // 将OperateMethodEntity实体同步到OperateEntity实体
            Method dstOperateMethod = OperateEntity.getInitMethod();
            this.syncEntity(OperateMethodEntity.class, OperateEntity.class, dstOperateMethod);

            // 将TriggerMethodEntity实体同步到TriggerEntity实体
            Method dstTriggerMethod = TriggerEntity.getInitMethod();
            this.syncEntity(TriggerMethodEntity.class, TriggerEntity.class, dstTriggerMethod);

            // 将TriggerMethodEntity实体同步到TriggerEntity实体
            Method dstDeviceObjInfMethod = DeviceMapperEntity.getInitMethod();
            this.syncEntity(DeviceObjInfEntity.class, DeviceMapperEntity.class, dstDeviceObjInfMethod);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public <SRC, DST> void syncEntity(Class<SRC> srcClass, Class<DST> dstClass, Method dstMethod) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        List<BaseEntity> srcEntityList = this.entityManageService.getEntityList(srcClass);
        List<BaseEntity> dscEntityList = this.entityManageService.getEntityList(dstClass);

        Map<String, BaseEntity> srcEntityMap = ContainerUtils.buildMapByKey(srcEntityList, BaseEntity::makeServiceKey);
        Map<String, BaseEntity> dstEntityMap = ContainerUtils.buildMapByKey(dscEntityList, BaseEntity::makeServiceKey);

        Set<String> addList = new HashSet<>();
        Set<String> delList = new HashSet<>();
        Set<String> eqlList = new HashSet<>();
        DifferUtils.differByValue(dstEntityMap.keySet(), srcEntityMap.keySet(), addList, delList, eqlList);

        // 只新增，不主动删除和修改。因为operate是用户手动维护的
        for (String key : addList) {
            BaseEntity srcEntity = srcEntityMap.get(key);
            BaseEntity dstEntity = (BaseEntity) dstClass.newInstance();
            dstMethod.invoke(dstEntity, srcEntity);

            this.entityManageService.insertEntity(dstEntity);
        }
    }
}
