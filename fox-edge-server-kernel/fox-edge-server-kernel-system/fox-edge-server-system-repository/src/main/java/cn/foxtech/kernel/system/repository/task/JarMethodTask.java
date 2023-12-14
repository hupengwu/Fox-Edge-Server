package cn.foxtech.kernel.system.repository.task;

import cn.foxtech.common.entity.entity.*;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.ContainerUtils;
import cn.foxtech.common.utils.DifferUtils;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTask;
import cn.foxtech.common.utils.scheduler.multitask.PeriodTaskType;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import cn.foxtech.kernel.system.repository.service.RepoLocalJarFileCompScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class JarMethodTask extends PeriodTask {
    @Autowired
    private RedisConsoleService logger;

    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private RepoLocalJarFileCompScanner compScanner;

    @Override
    public int getTaskType() {
        return PeriodTaskType.task_type_share;
    }

    /**
     * 获得调度周期
     *
     * @return 调度周期，单位秒
     */
    public int getSchedulePeriod() {
        return 10;
    }

    /**
     * 待周期性执行的操作
     */
    @Override
    public void execute() {
        try {
            // 将OperateMethodEntity实体同步到OperateEntity实体
            Method dstOperateMethod = OperateEntity.getInitMethod();
            this.syncEntity(OperateMethodEntity.class, OperateEntity.class, dstOperateMethod);

            // 同步本地仓库信息
            Map<String, RepoCompEntity> fileNameMap = this.compScanner.scanRepoCompEntityByJarMethod();
            this.compScanner.scanRepoCompEntity(fileNameMap);

            // 将TriggerMethodEntity实体同步到TriggerEntity实体
            Method dstTriggerMethod = TriggerEntity.getInitMethod();
            this.syncEntity(TriggerMethodEntity.class, TriggerEntity.class, dstTriggerMethod);

            // 将DeviceObjInfEntity实体同步到DeviceMapperEntity实体
            Method dstDeviceObjInfMethod = DeviceMapperEntity.getInitMethod();
            this.syncEntity(DeviceObjInfEntity.class, DeviceMapperEntity.class, dstDeviceObjInfMethod);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private <SRC, DST> void syncEntity(Class<SRC> srcClass, Class<DST> dstClass, Method dstMethod) throws InvocationTargetException, IllegalAccessException, InstantiationException {
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