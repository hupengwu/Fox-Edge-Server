package cn.foxtech.manager.system.controller;


import cn.foxtech.common.constant.HttpStatus;
import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.domain.constant.RestFulManagerVOConstant;
import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.domain.vo.RestFulRespondVO;
import cn.foxtech.common.entity.constant.DeviceValueVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceObjectEntity;
import cn.foxtech.common.entity.entity.DeviceObjectValue;
import cn.foxtech.common.entity.entity.DeviceValueEntity;
import cn.foxtech.common.entity.service.deviceobject.DeviceObjectEntityMapper;
import cn.foxtech.common.entity.service.redis.RedisReader;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.ExtendUtils;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.pair.Pair;
import cn.foxtech.common.utils.redis.topic.service.RedisTopicPublisher;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.manager.system.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/kernel/manager/device/value")
public class DeviceValueManageController {
    @Autowired
    private DeviceObjectEntityMapper mapper;

    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private RedisTopicPublisher publisher;


    @PostMapping("page")
    public AjaxResult selectEntityListByPage(@RequestBody Map<String, Object> body) {
        // 提取业务参数
        String deviceName = (String) body.get(DeviceValueVOFieldConstant.field_device_name);
        String deviceType = (String) body.get(DeviceValueVOFieldConstant.field_device_type);
        String manufacturer = (String) body.get(DeviceValueVOFieldConstant.field_manufacturer);
        String objectName = (String) body.get(DeviceValueVOFieldConstant.field_object_name);
        Integer pageNum = (Integer) body.get(DeviceValueVOFieldConstant.field_page_num);
        Integer pageSize = (Integer) body.get(DeviceValueVOFieldConstant.field_page_size);

        // 简单校验参数
        if (MethodUtils.hasNull(pageNum, pageSize)) {
            return AjaxResult.error("参数不能为空:pageNum, pageSize");
        }

        StringBuilder sb = new StringBuilder();
        if (deviceName != null) {
            sb.append(" (device_name = '").append(deviceName).append("') AND");
        }
        if (deviceType != null) {
            sb.append(" (device_type = '").append(deviceType).append("') AND");
        }
        if (manufacturer != null) {
            sb.append(" (manufacturer = '").append(manufacturer).append("') AND");
        }
        if (objectName != null) {
            sb.append(" (object_name = '").append(objectName).append("') AND");
        }
        String filter = sb.toString();
        if (!filter.isEmpty()) {
            filter = filter.substring(0, filter.length() - "AND".length());
        }

        return this.selectEntityListPage(filter, "ASC", pageNum, pageSize);
    }


    /**
     * 分页查询数据
     *
     * @param filter
     * @param order
     * @param pageNmu
     * @param pageSize
     * @return
     */
    private AjaxResult selectEntityListPage(String filter, String order, long pageNmu, long pageSize) {
        try {
            // 从数据库的deviceObject中查询总数
            String selectCount = PageUtils.makeSelectCountSQL("tb_device_object", filter);
            Integer total = this.mapper.executeSelectCount(selectCount);

            // 分页查询数据
            String selectPage = PageUtils.makeSelectSQLPage("tb_device_object", filter, order, total, pageNmu, pageSize);
            List<DeviceObjectEntity> entityList = this.mapper.executeSelectData(selectPage);

            // 从redis中直接读取真正的设备数值
            Set<Object> deviceNames = new HashSet<>();
            RedisReader redisReader = this.entityManageService.getRedisReader(DeviceValueEntity.class);
            for (DeviceObjectEntity entity : entityList) {
                DeviceValueEntity deviceValueEntity = new DeviceValueEntity();
                deviceValueEntity.setDeviceName(entity.getDeviceName());
                deviceNames.add(deviceValueEntity.makeServiceKey());
            }
            Map<String, BaseEntity> deviceValueMap = redisReader.readEntityMap(deviceNames);

            // 组织扩展信息：
            Long time = System.currentTimeMillis();
            List<Map<String, Object>> extendList = new ArrayList<>();
            for (BaseEntity entity : deviceValueMap.values()) {
                DeviceValueEntity deviceValueEntity = (DeviceValueEntity) entity;
                for (String key : deviceValueEntity.getParams().keySet()) {
                    DeviceObjectValue deviceObjectValue = deviceValueEntity.getParams().get(key);

                    Map<String, Object> map = new HashMap<>();
                    // 业务key
                    map.put(DeviceValueVOFieldConstant.field_device_name, deviceValueEntity.getDeviceName());
                    map.put(DeviceValueVOFieldConstant.field_object_name, key);
                    //业务数值
                    map.put(DeviceValueVOFieldConstant.field_object_time, (time - deviceObjectValue.getTime()) / 1000);
                    map.put(DeviceValueVOFieldConstant.field_object_value, deviceObjectValue.getValue());

                    map.put(DeviceValueVOFieldConstant.field_update_time, deviceObjectValue.getTime());

                    extendList.add(map);
                }

            }

            Map<String, Object> data = new HashMap<>();
            data.put("list", EntityVOBuilder.buildVOList(entityList));
            data.put("total", total);

            // 增加扩展数据
            List<Pair<String, String>> pairs = new ArrayList<>();
            Set<String> extend = new HashSet<>();
            pairs.add(new Pair<>(DeviceValueVOFieldConstant.field_device_name, DeviceValueVOFieldConstant.field_device_name));
            pairs.add(new Pair<>(DeviceValueVOFieldConstant.field_object_name, DeviceValueVOFieldConstant.field_object_name));
            extend.add(DeviceValueVOFieldConstant.field_object_value);
            extend.add(DeviceValueVOFieldConstant.field_object_time);
            extend.add(DeviceValueVOFieldConstant.field_update_time);
            ExtendUtils.extend((List<Map<String, Object>>) data.get("list"), extendList, pairs, extend);

            return AjaxResult.success(data);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("remove")
    public AjaxResult removeDeviceValueList(@RequestBody Map<String, Object> body) {
        // 提取业务参数
        List<Map<String, Object>> deviceValueList = (List<Map<String, Object>>) body.get(DeviceValueVOFieldConstant.field_object_names);

        // 简单校验参数
        if (MethodUtils.hasEmpty(deviceValueList)) {
            return AjaxResult.error("参数不能为空:objectNames");
        }

        try {
            // 删除设备的数值对象
            RestFulRespondVO respondVO = this.deleteDeviceValue(deviceValueList);
            if (HttpStatus.SUCCESS.equals(respondVO.getCode())) {
                return AjaxResult.success(respondVO.getData());
            } else {
                return AjaxResult.error(respondVO.getMsg());
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 删除设备的数值对象
     * 该对象的生产者是持久化服务，所以数据要发给它去删除
     *
     * @param deviceValueList
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private RestFulRespondVO deleteDeviceValue(List<Map<String, Object>> deviceValueList) throws IOException, InterruptedException {
        RestFulRequestVO requestVO = new RestFulRequestVO();
        requestVO.setUuid(UUID.randomUUID().toString());
        requestVO.setUri(RestFulManagerVOConstant.uri_device_value);
        requestVO.setMethod("delete");
        requestVO.setData(deviceValueList);


        String body = JsonUtils.buildJson(requestVO);

        // 重置信号
        SyncFlagObjectMap.inst().reset(requestVO.getUuid());

        // 发送数据
        this.publisher.sendMessage(RedisTopicConstant.topic_persist_request + RedisTopicConstant.model_manager, body);

        // 等待消息的到达：根据动态key
        RestFulRespondVO respond = (RestFulRespondVO) SyncFlagObjectMap.inst().waitDynamic(requestVO.getUuid(), 30 * 1000);
        if (respond == null) {
            throw new ServiceException("服务响应超时！");
        }

        return respond;
    }
}
