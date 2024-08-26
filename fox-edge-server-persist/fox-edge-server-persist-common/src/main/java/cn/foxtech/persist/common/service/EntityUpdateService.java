/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.persist.common.service;

import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.domain.vo.RestFulRespondVO;
import cn.foxtech.common.entity.constant.DeviceVOFieldConstant;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.utils.number.NumberUtils;
import cn.foxtech.device.domain.vo.OperateRespondVO;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.persist.common.service.updater.DeviceCommStatusUpdater;
import cn.foxtech.persist.common.service.updater.DeviceRecordValueUpdater;
import cn.foxtech.persist.common.service.updater.DeviceStatusValueUpdater;
import cn.foxtech.persist.common.service.updater.OperateRecordValueUpdater;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 实体更新操作：将收集到的设备数据更新到redis和mysql
 */
@Component
public class EntityUpdateService {
    private static final Logger logger = Logger.getLogger(EntityUpdateService.class);

    /**
     * 实体管理
     */
    @Autowired
    private PersistManageService entityManageService;


    @Autowired
    private DeviceCommStatusUpdater deviceCommStatusUpdater;

    @Autowired
    private DeviceStatusValueUpdater deviceStatusValueUpdater;

    @Autowired
    private DeviceRecordValueUpdater deviceRecordValueUpdater;

    @Autowired
    private OperateRecordValueUpdater operateRecordValueUpdater;


    /**
     * 将设备响应的信息更新到shujk和redis
     *
     * @param operateRespondVO 设备返回的操作数据
     */
    @SuppressWarnings("unchecked")
    public void updateDeviceRespond(OperateRespondVO operateRespondVO, String clientName) {
        try {
            String deviceName = operateRespondVO.getDeviceName();
            String deviceType = operateRespondVO.getDeviceType();
            String manufacturer = operateRespondVO.getManufacturer();

            DeviceEntity deviceEntity = new DeviceEntity();
            deviceEntity.setDeviceName(deviceName);
            deviceEntity.setDeviceType(deviceType);
            deviceEntity.setManufacturer(manufacturer);

            // 从redis直接读取设备实体：使用readHashMap是避免JSON转换，JSON的开销太大了，这里是高并发操作，难以忍受
            Map<String, Object> deviceMap = this.entityManageService.readHashMap(deviceEntity.makeServiceKey(), DeviceEntity.class);
            if (deviceMap == null) {
                return;
            }


            // 取出ID，后面需要用到
            deviceEntity.setId(NumberUtils.makeLong(deviceMap.get(DeviceVOFieldConstant.field_id)));

            // 数据1: 设备通信状态
            Map<String, Object> commStatus = (Map<String, Object>) operateRespondVO.getData().get(OperateRespondVO.data_comm_status);
            this.deviceCommStatusUpdater.updateStatusEntity(deviceEntity.getId(), commStatus);

            // 提取数值
            Map<String, Object> deviceValues = (Map<String, Object>) operateRespondVO.getData().get(OperateRespondVO.data_value);
            if (deviceValues == null) {
                return;
            }

            // 数据2: 设备的状态类型数据
            Map<String, Object> statusValues = (Map<String, Object>) deviceValues.get(FoxEdgeOperate.status);
            this.deviceStatusValueUpdater.updateDeviceStatusValue(deviceEntity, statusValues);

            // 数据3: 设备的记录类数据
            List<Map<String, Object>> recordList = (List<Map<String, Object>>) deviceValues.get(FoxEdgeOperate.record);
            this.deviceRecordValueUpdater.updateDeviceRecordValue(deviceName, manufacturer, deviceType, recordList);

            // 数据4: 用户的操作记录类数据
            this.operateRecordValueUpdater.updateOperateRecordValue(clientName, operateRespondVO);
        } catch (Exception e) {
            logger.warn(e);
        }
    }

    public RestFulRespondVO deleteValueEntity(RestFulRequestVO requestVO) {
        try {
            List<Map<String, Object>> mapList = (List<Map<String, Object>>) requestVO.getData();

            Map<String, Set<String>> deviceValues = new HashMap<>();
            for (Map<String, Object> map : mapList) {
                String deviceName = (String) map.get("deviceName");
                String objectName = (String) map.get("objectName");

                if (!deviceValues.containsKey(deviceName)) {
                    deviceValues.put(deviceName, new HashSet<>());
                }
                Set<String> objectNames = deviceValues.get(deviceName);

                objectNames.add(objectName);
            }

            for (String deviceName : deviceValues.keySet()) {
                this.deviceStatusValueUpdater.deleteValueEntity(deviceName, deviceValues.get(deviceName));
            }

            return RestFulRespondVO.success(requestVO);

        } catch (Exception e) {
            return RestFulRespondVO.error(requestVO, e.getMessage());
        }
    }
}
