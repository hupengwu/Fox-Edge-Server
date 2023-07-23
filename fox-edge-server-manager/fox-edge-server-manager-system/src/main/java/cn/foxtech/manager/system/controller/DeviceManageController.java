package cn.foxtech.manager.system.controller;


import cn.foxtech.common.entity.constant.DeviceStatusVOFieldConstant;
import cn.foxtech.common.entity.constant.DeviceVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.DevicePo;
import cn.foxtech.common.entity.entity.DeviceStatusEntity;
import cn.foxtech.common.entity.service.device.DeviceEntityMaker;
import cn.foxtech.common.entity.service.device.DeviceEntityService;
import cn.foxtech.common.entity.service.redis.RedisReader;
import cn.foxtech.common.entity.service.redis.RedisWriter;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.number.NumberUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.manager.system.service.EntityManageService;
import com.fasterxml.jackson.core.JsonParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kernel/manager/device")
public class DeviceManageController {
    @Autowired
    protected DeviceEntityService entityService;
    @Autowired
    private EntityManageService manageService;

    @PostMapping("page")
    public AjaxResult selectEntityListByPage(@RequestBody Map<String, Object> body) {
        // 提取业务参数
        Object id = body.get(DeviceVOFieldConstant.field_id);
        String deviceName = (String) body.get(DeviceVOFieldConstant.field_device_name);
        String deviceType = (String) body.get(DeviceVOFieldConstant.field_device_type);
        String channelName = (String) body.get(DeviceVOFieldConstant.field_channel_name);
        String channelType = (String) body.get(DeviceVOFieldConstant.field_channel_type);
        Integer pageNum = (Integer) body.get(DeviceVOFieldConstant.field_page_num);
        Integer pageSize = (Integer) body.get(DeviceVOFieldConstant.field_page_size);



        // 简单校验参数
        if (MethodUtils.hasNull(pageNum, pageSize)) {
            return AjaxResult.error("参数不能为空:pageNum, pageSize");
        }

        StringBuilder sb = new StringBuilder();
        if (id != null) {
            sb.append(" (id = ").append(NumberUtils.makeLong(id)).append(") AND");
        }
        if (deviceName != null) {
            sb.append(" (device_name like '%").append(deviceName).append("%') AND");
        }
        if (deviceType != null) {
            sb.append(" (device_type = '").append(deviceType).append("') AND");
        }
        if (channelName != null) {
            sb.append(" (channel_name = '").append(channelName).append("') AND");
        }
        if (channelType != null) {
            sb.append(" (channel_type = '").append(channelType).append("') AND");
        }
        String filter = sb.toString();
        if (!filter.isEmpty()) {
            filter = filter.substring(0, filter.length() - "AND".length());
        }

        return this.selectEntityListPage(filter, "DESC", pageNum, pageSize);
    }

    private AjaxResult selectEntityListPage(String filter, String order, long pageNmu, long pageSize) {
        try {
            // 从数据库的deviceObject中查询总数
            String selectCount = PageUtils.makeSelectCountSQL("tb_device", filter);
            Integer total = this.entityService.getMapper().executeSelectCount(selectCount);

            // 分页查询数据
            String selectPage = PageUtils.makeSelectSQLPage("tb_device", filter, order, total, pageNmu, pageSize);
            List<DevicePo> poList = this.entityService.getMapper().executeSelectData(selectPage);
            List<BaseEntity> entityList = DeviceEntityMaker.makePoList2EntityList(poList);
            List<Map<String, Object>> mapList = EntityVOBuilder.buildVOList(entityList);

            this.extend(mapList);

            Map<String, Object> data = new HashMap<>();
            data.put("list", mapList);
            data.put("total", total);

            return AjaxResult.success(data);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 扩展设备状态
     * @param mapList
     * @throws JsonParseException
     */
    private void extend(List<Map<String, Object>> mapList) throws JsonParseException {
        // 构造准备查询的key
        List<String> keys = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            DeviceStatusEntity statusEntity = new DeviceStatusEntity();
            statusEntity.setId(NumberUtils.makeLong(map.get("id")));

            keys.add(statusEntity.makeServiceKey());
        }

        // 读取指定的redis数据
        RedisReader redisReader = this.manageService.getRedisReader(DeviceStatusEntity.class);
        Map<String, BaseEntity> extendMap = redisReader.readEntityMap(keys);
        if (MethodUtils.hasEmpty(extendMap)) {
            return;
        }

        for (Map<String, Object> map : mapList) {
            DeviceStatusEntity statusEntity = new DeviceStatusEntity();
            statusEntity.setId(NumberUtils.makeLong(map.get("id")));

            DeviceStatusEntity entity = (DeviceStatusEntity) extendMap.get(statusEntity.makeServiceKey());
            if (entity == null) {
                continue;
            }

            map.put(DeviceStatusVOFieldConstant.field_failed_count, entity.getCommFailedCount());
            map.put(DeviceStatusVOFieldConstant.field_failed_time, entity.getCommFailedTime());
            map.put(DeviceStatusVOFieldConstant.field_success_time, entity.getCommSuccessTime());
        }
    }


    @GetMapping("entity")
    public AjaxResult queryEntity(@QueryParam("id") Long id) {
        BaseEntity exist = this.entityService.selectEntity(id);
        if (exist == null) {
            return AjaxResult.error("实体不存在");
        }

        return AjaxResult.success(exist);
    }

    @PostMapping("entity")
    public AjaxResult insertEntity(@RequestBody Map<String, Object> params) {
        return this.insertOrUpdate(params);
    }

    @PutMapping("entity")
    public AjaxResult updateEntity(@RequestBody Map<String, Object> params) {
        return this.insertOrUpdate(params);
    }

    /**
     * 插入或者更新
     *
     * @param params 参数
     * @return 操作结果
     */
    private AjaxResult insertOrUpdate(Map<String, Object> params) {
        try {
            // 提取业务参数
            String deviceName = (String) params.get(DeviceVOFieldConstant.field_device_name);
            String deviceType = (String) params.get(DeviceVOFieldConstant.field_device_type);
            String channelType = (String) params.get(DeviceVOFieldConstant.field_channel_type);
            String channelName = (String) params.get(DeviceVOFieldConstant.field_channel_name);
            Map<String, Object> deviceParam = (Map<String, Object>) params.get(DeviceVOFieldConstant.field_device_param);

            // 简单校验参数
            if (MethodUtils.hasNull(deviceName, deviceType, channelType, channelName, deviceParam)) {
                return AjaxResult.error("参数不能为空:deviceName, deviceType, channelType, channelName, deviceParam");
            }

            // 构造作为参数的实体
            DeviceEntity entity = new DeviceEntity();
            entity.setDeviceName(deviceName);
            entity.setDeviceType(deviceType);
            entity.setChannelType(channelType);
            entity.setChannelName(channelName);
            entity.setDeviceParam(deviceParam);

            // 简单验证实体的合法性
            if (entity.hasNullServiceKey()) {
                return AjaxResult.error("具有null的service key！");
            }

            RedisReader redisReader = this.manageService.getRedisReader(DeviceEntity.class);
            RedisWriter redisWriter = this.manageService.getRedisWriter(DeviceEntity.class);

            DeviceEntity exist = (DeviceEntity) redisReader.readEntity(entity.makeServiceKey());

            // 新增/修改实体：参数不包含id为新增，包含为修改
            if (!params.containsKey("id")) {
                if (exist != null) {
                    return AjaxResult.error("实体已存在");
                }

                // 写入数据库
                this.entityService.insertEntity(entity);
                // 写入redis
                redisWriter.writeEntity(entity);

                return AjaxResult.success();
            } else {
                if (exist == null) {
                    return AjaxResult.error("实体不存在");
                }

                // 修改数据
                entity.setId(exist.getId());

                // 写入数据库
                this.entityService.updateEntity(entity);
                // 写入redis
                redisWriter.writeEntity(entity);

                return AjaxResult.success();
            }
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @DeleteMapping("entity")
    public AjaxResult deleteEntity(@QueryParam("id") Long id) {
        BaseEntity exist = this.entityService.selectEntity(id);
        if (exist == null) {
            return AjaxResult.error("实体不存在");
        }

        RedisWriter redisWriter = this.manageService.getRedisWriter(DeviceEntity.class);

        this.entityService.deleteEntity(exist);
        redisWriter.deleteEntity(exist.makeServiceKey());
        return AjaxResult.success();
    }

    @DeleteMapping("entities")
    public AjaxResult deleteEntityList(@QueryParam("ids") String ids) {
        String[] idList = ids.split(",");

        RedisWriter redisWriter = this.manageService.getRedisWriter(DeviceEntity.class);

        for (String id : idList) {
            if (id == null || id.isEmpty()) {
                continue;
            }

            BaseEntity exist = this.entityService.selectEntity(Long.parseLong(id));
            if (exist == null) {
                continue;
            }

            this.entityService.deleteEntity(exist);
            redisWriter.deleteEntity(exist.makeServiceKey());
        }

        return AjaxResult.success();
    }
}
