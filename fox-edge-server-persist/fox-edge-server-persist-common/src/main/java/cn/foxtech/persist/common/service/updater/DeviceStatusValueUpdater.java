package cn.foxtech.persist.common.service.updater;

import cn.foxtech.common.entity.constant.DeviceMapperVOFieldConstant;
import cn.foxtech.common.entity.entity.*;
import cn.foxtech.common.entity.manager.EntityPublishManager;
import cn.foxtech.common.utils.pair.Pair;
import cn.foxtech.persist.common.history.IDeviceHistoryUpdater;
import cn.foxtech.persist.common.service.DeviceObjectMapper;
import cn.foxtech.persist.common.service.EntityManageService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DeviceStatusValueUpdater {
    private static final Logger logger = Logger.getLogger(DeviceStatusValueUpdater.class);
    /**
     * 实体发布
     */
    @Autowired
    protected EntityPublishManager entityPublishManager;
    /**
     * 实体管理
     */
    @Autowired
    private EntityManageService entityManageService;
    @Autowired
    private IDeviceHistoryUpdater deviceHistoryEntityUpdater;
    @Autowired
    private DeviceObjectMapper deviceObjectMapper;

    /**
     * 更新值状态数据
     *
     * @param deviceEntity 设备实体信息
     * @param statusValues 状态类数据信息
     */
    public void updateDeviceStatusValue(DeviceEntity deviceEntity, Map<String, Object> statusValues) {
        if (statusValues == null || statusValues.isEmpty()) {
            return;
        }


        try {
            // 预处理：对数值进行映射的处理
            statusValues = this.mappingStatusValues(deviceEntity, statusValues);
            // 预处理：对预处理的结果，再进行一次预处理，达到二次映射的效果。
            statusValues = this.mappingStatusValues(deviceEntity, statusValues);

            // 保存设备类型的结构化息
            this.saveObjInfEntity(deviceEntity, statusValues);

            // 构建数值实体
            DeviceValueEntity valueEntity = this.buildValueEntity(deviceEntity, statusValues);

            // 从redis读取原有的数值实体
            Map<String, Object> existMap = this.entityManageService.readHashMap(valueEntity.makeServiceKey(), DeviceValueEntity.class);
            DeviceValueEntity existEntity = DeviceValueEntity.buildValueEntity(existMap);

            // 步骤1：将数据保存到redis，同时也生成设备对象，保存到mysql数据库中
            this.saveValueEntity(existEntity, valueEntity);

            // 步骤2：将数值保存到历史记录
            this.deviceHistoryEntityUpdater.saveHistoryEntity(existEntity, statusValues);

        } catch (Exception e) {
            logger.error(e);
        }
    }

    /**
     * 对数据进行映射处理
     *
     * @param deviceEntity 设备对象
     * @param statusValues 原始值
     * @return 加工的数值
     */
    private Map<String, Object> mappingStatusValues(DeviceEntity deviceEntity, Map<String, Object> statusValues) {
        if (this.deviceObjectMapper.getValue(deviceEntity.getManufacturer(), deviceEntity.getDeviceType()) == null) {
            return statusValues;
        }


        Map<String, Object> result = new HashMap<>();
        for (String key : statusValues.keySet()) {
            Pair<String, Integer> mapper = this.deviceObjectMapper.getValue(deviceEntity.getManufacturer(), deviceEntity.getDeviceType(), key);

            // 如果没有该配置，那么沿用原来的数值
            if (mapper == null) {
                result.put(key, statusValues.get(key));
                continue;
            }

            // 场景1：保留原始值
            if (mapper.getValue().equals(DeviceMapperVOFieldConstant.mapper_mode_original)) {
                result.put(key, statusValues.get(key));
                continue;
            }

            // 场景2：采用一个新值
            if (mapper.getValue().equals(DeviceMapperVOFieldConstant.mapper_mode_replace)) {
                result.put(mapper.getKey(), statusValues.get(key));
                continue;
            }

            // 场景3：保留原始值，并复制一个新值
            if (mapper.getValue().equals(DeviceMapperVOFieldConstant.mapper_mode_duplicate)) {
                result.put(key, statusValues.get(key));
                result.put(mapper.getKey(), statusValues.get(key));
                continue;
            }

            // 场景4：剔除该值
            if (mapper.getValue().equals(DeviceMapperVOFieldConstant.mapper_mode_filter)) {
                continue;
            }

            // 场景5：展开
            if (mapper.getValue().equals(DeviceMapperVOFieldConstant.mapper_mode_expend)) {
                Object value = statusValues.get(key);
                if (value instanceof Map) {
                    // 对Map的递归展开
                    this.expendMapper((Map<String, Object>) statusValues.get(key), key, result);
                } else {
                    result.put(key, statusValues.get(key));
                }
                continue;
            }
        }

        return result;
    }

    public void expendMapper(Map<String, Object> map, String path, Map<String, Object> expendMap) {
        for (String key : map.keySet()) {
            Object value = map.get(key);

            String subKey = path + "_" + key;

            if (value instanceof Map) {
                expendMapper((Map<String, Object>) value, subKey, expendMap);
            } else {
                expendMap.put(subKey, value);
            }

        }
    }

    /**
     * 构建数值实体
     *
     * @param deviceEntity 设备实体
     * @param statusValues 数值状态
     * @return 数值实体
     */
    private DeviceValueEntity buildValueEntity(DeviceEntity deviceEntity, Map<String, Object> statusValues) {
        Long time = System.currentTimeMillis();

        // 构造更新数值
        DeviceValueEntity valueEntity = new DeviceValueEntity();
        valueEntity.setId(deviceEntity.getId());
        valueEntity.setDeviceName(deviceEntity.getDeviceName());
        valueEntity.setDeviceType(deviceEntity.getDeviceType());
        valueEntity.setManufacturer(deviceEntity.getManufacturer());
        for (String key : statusValues.keySet()) {
            DeviceObjectValue deviceObjectValue = new DeviceObjectValue();
            deviceObjectValue.setValue(statusValues.get(key));
            deviceObjectValue.setTime(time);
            valueEntity.getParams().put(key, deviceObjectValue);
        }

        return valueEntity;
    }

    /**
     * 保存设备的对象信息
     *
     * @param deviceEntity 设备实体
     * @param statusValues 设备的数值
     */
    private void saveObjInfEntity(DeviceEntity deviceEntity, Map<String, Object> statusValues) {
        if (deviceEntity == null) {
            return;
        }

        // 将新增的数据，作为对象保存到数据库
        for (String key : statusValues.keySet()) {
            DeviceObjInfEntity entity = new DeviceObjInfEntity();
            entity.setDeviceType(deviceEntity.getDeviceType());
            entity.setManufacturer(deviceEntity.getManufacturer());
            entity.setObjectName(key);

            Object value = statusValues.get(key);
            if (value != null) {
                entity.setValueType(value.getClass().getSimpleName());
            }

            // 检查：是否已经存在
            if (this.entityManageService.hasEntity(entity.makeServiceKey(), DeviceObjInfEntity.class)) {
                continue;
            }

            // 保存数据
            this.entityManageService.insertRDEntity(entity);
        }
    }

    private void saveValueEntity(DeviceValueEntity existEntity, DeviceValueEntity valueEntity) {
        // 步骤2：将数据保存到redis，同时也生成设备对象，保存到mysql数据库中
        if (existEntity == null) {
            // 将新增的数据，作为对象保存到数据库
            this.saveObjectEntities(valueEntity);


            // 保存数据到redis
            Long time = System.currentTimeMillis();
            valueEntity.setCreateTime(time);
            valueEntity.setUpdateTime(time);
            this.entityManageService.writeEntity(valueEntity);
        } else {
            // 将新增的数据，作为对象保存到数据库
            for (String key : valueEntity.getParams().keySet()) {
                if (!existEntity.getParams().containsKey(key)) {
                    this.saveObjectEntity(valueEntity.getDeviceName(), valueEntity.getManufacturer(), valueEntity.getDeviceType(), key);
                }
            }

            // 合并数据
            for (String key : existEntity.getParams().keySet()) {
                if (!valueEntity.getParams().containsKey(key)) {
                    valueEntity.getParams().put(key, existEntity.getParams().get(key));
                }
            }

            // 更新
            Long time = System.currentTimeMillis();
            valueEntity.setUpdateTime(time);
            this.entityManageService.writeEntity(valueEntity);
        }
    }

    private void saveObjectEntities(DeviceValueEntity valueEntity) {
        DeviceObjectEntity deviceObjectEntity = new DeviceObjectEntity();
        deviceObjectEntity.setDeviceName(valueEntity.getDeviceName());

        // 数据库侧的Keys:通过查询设备级别的数据，来构造一个整个设备的对象列表
        Set<String> dbServiceKeys = new HashSet<>();
        List<BaseEntity> entityList = this.entityManageService.getDeviceObjectEntityService().selectEntityList((QueryWrapper) deviceObjectEntity.makeDeviceWrapperKey());
        for (BaseEntity entity : entityList) {
            deviceObjectEntity = (DeviceObjectEntity) entity;
            deviceObjectEntity.setDeviceName(valueEntity.getDeviceName());
            deviceObjectEntity.setManufacturer(valueEntity.getManufacturer());
            deviceObjectEntity.setDeviceType(valueEntity.getDeviceType());
            deviceObjectEntity.setObjectName(deviceObjectEntity.getObjectName());
            dbServiceKeys.add(deviceObjectEntity.makeServiceKey());
        }

        // 将新增的数据，作为对象保存到数据库
        for (String key : valueEntity.getParams().keySet()) {
            deviceObjectEntity = new DeviceObjectEntity();
            deviceObjectEntity.setDeviceName(valueEntity.getDeviceName());
            deviceObjectEntity.setManufacturer(valueEntity.getManufacturer());
            deviceObjectEntity.setDeviceType(valueEntity.getDeviceType());
            deviceObjectEntity.setObjectName(key);

            String serviceKey = deviceObjectEntity.makeServiceKey();
            if (dbServiceKeys.contains(serviceKey)) {
                continue;
            }


            // 写入数据库
            this.entityManageService.getDeviceObjectEntityService().insertEntity(deviceObjectEntity);

            // 更新数据库变更的时间
            this.entityPublishManager.setPublishEntityUpdateTime(DeviceObjectEntity.class.getSimpleName(), System.currentTimeMillis());
        }
    }

    private void saveObjectEntity(String deviceName, String manufacturer, String deviceType, String objectName) {
        DeviceObjectEntity deviceObjectEntity = new DeviceObjectEntity();
        deviceObjectEntity.setDeviceName(deviceName);
        deviceObjectEntity.setManufacturer(manufacturer);
        deviceObjectEntity.setDeviceType(deviceType);
        deviceObjectEntity.setObjectName(objectName);

        DeviceObjectEntity existEntity = (DeviceObjectEntity) this.entityManageService.getDeviceObjectEntityService().selectEntity((QueryWrapper) deviceObjectEntity.makeWrapperKey());
        if (existEntity != null) {
            return;
        }

        // 写入数据库
        this.entityManageService.getDeviceObjectEntityService().insertEntity(deviceObjectEntity);

        // 更新数据库变更的时间
        this.entityPublishManager.setPublishEntityUpdateTime(DeviceObjectEntity.class.getSimpleName(), System.currentTimeMillis());

    }

    public void deleteValueEntity(String deviceName, Collection<String> objectNameList) {
        try {
            // 从redis中读取deviceValue数据
            DeviceValueEntity find = new DeviceValueEntity();
            find.setDeviceName(deviceName);
            DeviceValueEntity existEntity = this.entityManageService.readEntity(find.makeServiceKey(), DeviceValueEntity.class);

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
                DeviceObjectEntity deviceObjectEntity = new DeviceObjectEntity();
                deviceObjectEntity.setDeviceName(deviceName);
                deviceObjectEntity.setObjectName(objectName);

                // 写入数据库
                this.entityManageService.getDeviceObjectEntityService().deleteEntity(deviceObjectEntity);

                // 更新数据库变更的时间
                this.entityPublishManager.setPublishEntityUpdateTime(DeviceObjectEntity.class.getSimpleName(), System.currentTimeMillis());
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
