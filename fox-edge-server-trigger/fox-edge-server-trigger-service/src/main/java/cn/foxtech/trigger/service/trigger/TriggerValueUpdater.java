package cn.foxtech.trigger.service.trigger;

import cn.foxtech.common.entity.entity.*;
import cn.foxtech.common.entity.manager.EntityPublishManager;
import cn.foxtech.common.entity.service.redis.RedisReader;
import cn.foxtech.common.entity.service.redis.RedisWriter;
import cn.foxtech.common.utils.ContainerUtils;
import cn.foxtech.common.utils.DifferUtils;
import cn.foxtech.trigger.logic.common.FoxEdgeTrigger;
import cn.foxtech.trigger.logic.common.FoxEdgeTriggerTemplate;
import cn.foxtech.trigger.logic.common.ObjectValue;
import cn.foxtech.trigger.service.service.EntityManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import cn.foxtech.common.entity.entity.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 触发器数据数值的更新
 * 业务逻辑：
 * 1、根据到达的DeviceValue，准备一个Finder
 * 2、使用Finder找到跟DeviceValue相关的触发器配置
 * 3、将DeviceValue追加到TriggerValue中
 * 4、对DeviceValue和Finder中的DeviceValue、TriggerConfig，使用TriggerMethod进行数据加工，得到结果TriggerValueEntity
 * 5、将TriggerValueEntity保存到redis中，分享给需要的人
 */
@Component
public class TriggerValueUpdater {
    private static final Logger logger = Logger.getLogger(TriggerValueUpdater.class);
    /**
     * 发布注册服务
     */
    @Autowired
    protected EntityPublishManager entityPublishManager;
    /**
     * 实体管理服务
     */
    @Autowired
    private EntityManageService entityManageService;
    @Autowired
    private TriggerValueManager triggerValueManager;

    public void update(BaseEntity entity) {
        try {
            // 检查：这个设备的数据，是否为空
            DeviceValueEntity deviceValueEntity = (DeviceValueEntity) entity;
            if (deviceValueEntity.getParams().isEmpty()) {
                return;
            }

            // 查找跟这个设备值相关的触发器配置
            TriggerConfigFinder finder = new TriggerConfigFinder(deviceValueEntity);
            this.entityManageService.foreachFinder(TriggerConfigEntity.class, finder);

            // 检查：是否查询到了相关配置
            if (finder.getMap().isEmpty()) {
                return;
            }

            // 追加数据到末尾
            this.triggerValueManager.appendValue(finder);

            // 对触发器进行触发
            Set<TriggerConfigEntity> triggerConfigEntityList = finder.getTriggerConfig();
            List<TriggerStatusEntity> triggerStatusList = this.invokeTrigger(deviceValueEntity, triggerConfigEntityList);
            if (triggerStatusList == null || triggerStatusList.isEmpty()) {
                return;
            }

            // 将触发状态，构建成触发值
            Map<String, TriggerValueEntity> triggerValueList = this.buildTriggerValue(triggerStatusList);

            // 把触发状态更新到redis：关心中间状态的人，可以看这部分数据
            //this.updateTriggerStatus(triggerStatusList);

            // 把触发值更新到redis：关心结果状态的人，可以看这部分数据
            this.updateTriggerValue(triggerValueList.values());

        } catch (Exception e) {
            logger.warn(e);
        }
    }

    /**
     * 使用全体DeviceValue初始化TriggerValue
     */
    public void initialize() {
        logger.info("------------------Initialize开始！------------------");

        // 验证mysql的object与redis的value之间的一致性，并进行预处理
        this.verifyValueAndObject();

        logger.info("------------------Initialize开始！------------------");
    }

    /**
     * 验证措施：验证mysql的object与redis的value之间的一致性，并进行预处理
     * 预处理方式：以mysql侧的object数据未基准，删除多余的redis数据，后面的update阶段会自动补齐新增的redis数据
     */
    private void verifyValueAndObject() {
        try {
            List<BaseEntity> objectEntityList = this.entityManageService.getTriggerObjectEntityService().selectEntityList();
            RedisReader redisReader = this.entityManageService.getRedisReader(TriggerValueEntity.class);
            RedisWriter redisWriter = this.entityManageService.getRedisWriter(TriggerValueEntity.class);

            List<BaseEntity> valueEntityList = redisReader.readEntityList();


            Map<String, Set<String>> mysqlMap = new HashMap<>();
            for (BaseEntity entity : objectEntityList) {
                TriggerObjectEntity triggerObjectEntity = (TriggerObjectEntity) entity;

                TriggerValueEntity triggerValueEntity = new TriggerValueEntity();
                triggerValueEntity.setTriggerConfigName(triggerObjectEntity.getTriggerConfigName());
                triggerValueEntity.setDeviceName(triggerObjectEntity.getDeviceName());
                Set<String> objectNames = mysqlMap.computeIfAbsent(triggerValueEntity.makeServiceKey(), k -> new HashSet<>());
                objectNames.add(triggerObjectEntity.getObjectName());
            }

            Map<String, Set<String>> redisMap = new HashMap<>();
            for (BaseEntity entity : valueEntityList) {
                TriggerValueEntity triggerValueEntity = (TriggerValueEntity) entity;

                Set<String> objectNames = redisMap.computeIfAbsent(triggerValueEntity.makeServiceKey(), k -> new HashSet<>());
                objectNames.addAll(triggerValueEntity.getParams().keySet());
            }

            Set<String> addList = new HashSet();
            Set<String> delList = new HashSet();
            Set<String> eqlList = new HashSet();
            DifferUtils.differByValue(redisMap.keySet(), mysqlMap.keySet(), addList, delList, eqlList);

            // 删除设备级别的多余redis数据。mysql的多余数据会自行根据redis缓存比较，所以不需要删除
            redisWriter.deleteEntity(delList);

            // 删除对象不一致的redis数据
            for (String serviceKey : eqlList) {
                Set<String> redis = redisMap.get(serviceKey);
                Set<String> mysql = mysqlMap.get(serviceKey);

                // 比较对象级别是否存在差异
                if (DifferUtils.differByValue(redis, mysql)) {
                    continue;
                }

                redisWriter.deleteEntity(serviceKey);
            }
        } catch (Exception e) {
            logger.error(e);
        }

    }


    /**
     * 保存数据
     *
     * @param triggerValueEntityList
     */
    public void updateTriggerValue(Collection<TriggerValueEntity> triggerValueEntityList) {
        Long time = System.currentTimeMillis();
        for (TriggerValueEntity valueEntity : triggerValueEntityList) {
            try {
                TriggerValueEntity existEntity = this.entityManageService.readEntity(valueEntity.makeServiceKey(), TriggerValueEntity.class);
                if (existEntity == null) {
                    // 将新增的数据，作为对象保存到数据库
                    for (String key : valueEntity.getParams().keySet()) {
                        this.saveObjectEntity(valueEntity.getDeviceName(), valueEntity.getDeviceType(), valueEntity.getTriggerConfigName(), key);
                    }

                    valueEntity.setCreateTime(time);
                    valueEntity.setUpdateTime(time);
                    this.entityManageService.writeEntity(valueEntity);
                } else {
                    // 将新增的数据，作为对象保存到数据库
                    for (String key : valueEntity.getParams().keySet()) {
                        if (!existEntity.getParams().containsKey(key)) {
                            this.saveObjectEntity(valueEntity.getDeviceName(), valueEntity.getDeviceType(), valueEntity.getTriggerConfigName(), key);
                        }
                    }

                    valueEntity.setUpdateTime(time);
                    this.entityManageService.writeEntity(valueEntity);
                }
            } catch (Exception e) {
                logger.error(e);
            }
        }
    }

    private void saveObjectEntity(String deviceName, String deviceType, String triggerConfigName, String objectName) {
        TriggerObjectEntity triggerObjectEntity = new TriggerObjectEntity();
        triggerObjectEntity.setTriggerConfigName(triggerConfigName);
        triggerObjectEntity.setDeviceName(deviceName);
        triggerObjectEntity.setDeviceType(deviceType);
        triggerObjectEntity.setObjectName(objectName);

        TriggerObjectEntity existEntity = (TriggerObjectEntity) this.entityManageService.getTriggerObjectEntityService().selectEntity((QueryWrapper) triggerObjectEntity.makeWrapperKey());
        if (existEntity != null) {
            return;
        }

        // 写入数据库
        this.entityManageService.getTriggerObjectEntityService().insertEntity(triggerObjectEntity);

        // 更新数据库变更的时间
        this.entityPublishManager.setPublishEntityUpdateTime(TriggerObjectEntity.class.getSimpleName(), System.currentTimeMillis());
    }

    public void deleteValueEntity(String deviceName, String triggerConfigName, Collection<String> objectNameList) {
        try {
            // 从redis中读取deviceValue数据
            TriggerValueEntity find = new TriggerValueEntity();
            find.setDeviceName(deviceName);
            TriggerValueEntity existEntity = this.entityManageService.readEntity(find.makeServiceKey(), TriggerValueEntity.class);

            // 步骤1：删除redis的deviceValue.param数据
            if (existEntity != null) {

                // 删除副本中的对象
                for (String objectName : objectNameList) {
                    existEntity.getParams().remove(objectName);
                }

                // 把副本更新到redis中
                Long time = System.currentTimeMillis();
                existEntity.setUpdateTime(time);
                this.entityManageService.writeEntity(existEntity);
            }


            // 步骤2：删除mysql中的deviceObject数据
            for (String objectName : objectNameList) {
                // 删除对象
                TriggerObjectEntity objectEntity = new TriggerObjectEntity();
                objectEntity.setDeviceName(deviceName);
                objectEntity.setTriggerConfigName(triggerConfigName);
                objectEntity.setObjectName(objectName);

                // 写入数据库
                this.entityManageService.getTriggerObjectEntityService().deleteEntity(objectEntity);

                // 更新数据库变更的时间
                this.entityPublishManager.setPublishEntityUpdateTime(TriggerObjectEntity.class.getSimpleName(), System.currentTimeMillis());
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }


    /**
     * 对对设备数据进行触发器处理
     * @param deviceValueEntity 设备数值
     * @param triggerConfigEntityList 触发器配置列表
     * @return 触发器加工值
     */
    public List<TriggerStatusEntity> invokeTrigger(DeviceValueEntity deviceValueEntity, Set<TriggerConfigEntity> triggerConfigEntityList) {
        try {
            Long time = System.currentTimeMillis();

            List<TriggerStatusEntity> result = new CopyOnWriteArrayList<>();

            for (TriggerConfigEntity triggerConfigEntity : triggerConfigEntityList) {
                // 查询触发器需要的相关值
                Map<String, List<ObjectValue>> deviceTriggerValue = this.triggerValueManager.selectDeviceTriggerValue(deviceValueEntity.getDeviceName(), triggerConfigEntity);
                if (deviceTriggerValue == null || deviceTriggerValue.isEmpty()) {
                    continue;
                }

                // 获得触发器
                FoxEdgeTrigger trigger = FoxEdgeTriggerTemplate.inst().getEdgeTrigger(triggerConfigEntity.getTriggerModelName(), triggerConfigEntity.getTriggerMethodName());
                if (trigger == null) {
                    continue;
                }


                // 调用触发器：（对象的历史值，触发器的参数），返回加工后的数据
                Map<String, Object> triggerValue = (Map<String, Object>) trigger.getMethod().invoke(this, deviceTriggerValue, triggerConfigEntity.getParams());
                if (triggerValue == null || triggerValue.isEmpty()) {
                    continue;
                }

                if (triggerValue == null || triggerValue.isEmpty()) {
                    continue;
                }


                TriggerStatusEntity triggerStatusEntity = new TriggerStatusEntity();
                triggerStatusEntity.setId(deviceValueEntity.getId());
                triggerStatusEntity.setDeviceName(deviceValueEntity.getDeviceName());
                triggerStatusEntity.setDeviceType(deviceValueEntity.getDeviceType());
                triggerStatusEntity.setTriggerConfigId(triggerConfigEntity.getId());
                triggerStatusEntity.setObjectRange(triggerConfigEntity.getObjectRange());
                triggerStatusEntity.setTriggerConfigName(triggerConfigEntity.getTriggerConfigName());
                triggerStatusEntity.setTriggerModelName(triggerConfigEntity.getTriggerModelName());
                triggerStatusEntity.setTriggerMethodName(triggerConfigEntity.getTriggerMethodName());
                triggerStatusEntity.setParams(triggerConfigEntity.getParams());
                for (String key : triggerValue.keySet()) {
                    DeviceObjectValue deviceObjectValue = new DeviceObjectValue();
                    deviceObjectValue.setTime(time);
                    deviceObjectValue.setValue(triggerValue.get(key));
                    triggerStatusEntity.getValues().put(key, deviceObjectValue);
                }

                result.add(triggerStatusEntity);
            }

            return result;
        } catch (Exception e) {
            logger.warn(e);
            return null;
        }
    }


    /**
     * 生成组合名的设备数据
     *
     * @param entityList
     * @return
     */
    private Map<String, TriggerValueEntity> buildTriggerValue(List<TriggerStatusEntity> entityList) {
        // 分组数据
        Map<String, List<TriggerStatusEntity>> rangeList = ContainerUtils.buildMapByTypeAndFinalMethod(entityList, TriggerStatusEntity::getObjectRange, String.class);

        // 合并数据到TriggerValueEntity
        Map<String, TriggerValueEntity> mergeValue = new HashMap<>();
        this.mergeTriggerValue(rangeList.get(TriggerConfigEntity.GlobalLevel), mergeValue);
        this.mergeTriggerValue(rangeList.get(TriggerConfigEntity.DeviceLevel), mergeValue);

        return mergeValue;
    }

    private void mergeTriggerValue(List<TriggerStatusEntity> entityList, Map<String, TriggerValueEntity> mergeValue) {
        if (entityList == null || entityList.isEmpty()) {
            return;
        }

        TriggerValueEntity finder = new TriggerValueEntity();
        for (TriggerStatusEntity entity : entityList) {
            finder.setDeviceName(entity.getDeviceName());
            finder.setTriggerConfigName(entity.getTriggerConfigName());

            // 分配对象
            TriggerValueEntity triggerValueEntity = mergeValue.get(finder.makeServiceKey());
            if (triggerValueEntity == null) {
                triggerValueEntity = new TriggerValueEntity();
                triggerValueEntity.setId(entity.getId());
                triggerValueEntity.setDeviceName(entity.getDeviceName());
                triggerValueEntity.setTriggerConfigName(entity.getTriggerConfigName());
                triggerValueEntity.setDeviceType(entity.getDeviceType());
                mergeValue.put(triggerValueEntity.makeServiceKey(), triggerValueEntity);
            }

            triggerValueEntity.getParams().putAll(entity.getValues());
        }
    }
}
