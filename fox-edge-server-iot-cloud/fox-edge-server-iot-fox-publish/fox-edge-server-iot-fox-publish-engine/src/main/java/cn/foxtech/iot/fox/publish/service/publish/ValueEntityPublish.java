/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.iot.fox.publish.service.publish;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.ExtendConfigEntity;
import cn.foxtech.common.entity.utils.ExtendConfigUtils;
import cn.foxtech.common.utils.SplitUtils;
import cn.foxtech.common.utils.bean.BeanMapUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.string.StringUtils;
import cn.foxtech.iot.common.remote.RemoteMqttService;
import cn.foxtech.iot.common.service.EntityManageService;
import cn.foxtech.iot.fox.publish.service.service.IotFoxPublishService;
import cn.foxtech.iot.fox.publish.service.service.RedisEntityService;
import cn.foxtech.iot.fox.publish.service.service.TimeIntervalService;
import cn.foxtech.iot.fox.publish.service.vo.EntityChangedNotifyVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 设备数值的推送：DeviceValueEntity/DeviceValueExEntity
 */
@Component
public class ValueEntityPublish {
    @Autowired
    private TimeIntervalService timeIntervalService;

    @Autowired
    private RemoteMqttService remoteMqttService;

    @Autowired
    private IotFoxPublishService iotFoxPublishService;

    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private RedisEntityService redisEntityService;

    public void publish(String entityType) throws JsonProcessingException {
        // 检查：是否到了执行的时间间隔
        if (!this.timeIntervalService.testLastTime(StringUtils.camelName(entityType))) {
            return;
        }

        // 弹出全部数据
        List<EntityChangedNotifyVO> voList = this.redisEntityService.queryNotify(entityType);

        // 分拆为10个数据为一组
        List<List<EntityChangedNotifyVO>> lists = SplitUtils.split(voList, 10);

        // 分批发送
        String topicType = entityType.toLowerCase();
        for (List<EntityChangedNotifyVO> list : lists) {
            List<Map<String, Object>> mapList = this.extendDeviceParam(list);
            String body = JsonUtils.buildJson(mapList);
            this.remoteMqttService.getClient().publish(this.iotFoxPublishService.getPublish() + "/" + topicType, body.getBytes(StandardCharsets.UTF_8));
        }
    }


    private List<Map<String, Object>> extendDeviceParam(List<EntityChangedNotifyVO> entityList) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        List<BaseEntity> extendConfigList = this.entityManageService.getEntityList(ExtendConfigEntity.class);
        Map<String, ExtendConfigEntity> extendMap = ExtendConfigUtils.getExtendConfigList(extendConfigList, DeviceEntity.class);

        for (Object object : entityList) {
            EntityChangedNotifyVO entityChangedNotifyVO = (EntityChangedNotifyVO) object;
            if (entityChangedNotifyVO.getEntity() == null) {
                continue;
            }

            Map<String, Object> result = JsonUtils.buildObjectWithoutException(entityChangedNotifyVO,Map.class);

            // 查询相关设备
            DeviceEntity exist = this.entityManageService.getEntity(entityChangedNotifyVO.getEntity().makeServiceKey(), DeviceEntity.class);
            if (exist == null) {
                continue;
            }

            Map<String, Object> deviceMap = JsonUtils.buildObjectWithoutException(exist,Map.class);
            if (deviceMap == null) {
                continue;
            }

            // 扩展配置
            ExtendConfigUtils.extendMapList(deviceMap, extendMap);

            // 从扩展配置中，取出是否上传的标记
            Map<String,Object> extendParam = (Map<String,Object>)deviceMap.get("extendParam");
            Object extendField = extendParam.get(this.iotFoxPublishService.getExtendField());

            // 检测：是否配置了上传属性
            if (extendField != null && !Boolean.TRUE.equals(extendField)){
                continue;
            }


            Map<String,Object> map = (Map<String,Object>)result.get("entity");
            map.put("deviceParam", deviceMap.get("deviceParam"));
            map.put("extendParam", deviceMap.get("extendParam"));

            resultList.add(result);
        }

        return resultList;
    }
}
