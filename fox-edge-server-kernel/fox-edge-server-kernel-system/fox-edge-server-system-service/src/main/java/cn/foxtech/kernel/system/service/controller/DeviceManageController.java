package cn.foxtech.kernel.system.service.controller;


import cn.foxtech.common.entity.constant.DeviceStatusVOFieldConstant;
import cn.foxtech.common.entity.constant.DeviceVOFieldConstant;
import cn.foxtech.common.entity.constant.OperateVOFieldConstant;
import cn.foxtech.common.entity.entity.*;
import cn.foxtech.common.entity.service.device.DeviceEntityMaker;
import cn.foxtech.common.entity.service.device.DeviceEntityService;
import cn.foxtech.common.entity.service.redis.RedisReader;
import cn.foxtech.common.entity.service.redis.RedisWriter;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.ExtendConfigUtils;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.file.TempDirManageService;
import cn.foxtech.common.utils.file.FileTextUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.number.NumberUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import com.fasterxml.jackson.core.JsonParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.QueryParam;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
    private TempDirManageService tempDirManageService;

    @Autowired
    private EntityManageService entityManageService;

    @PostMapping("page")
    public AjaxResult selectEntityPage(@RequestBody Map<String, Object> body) {
        return this.selectEntityListByPage(body);
    }

    private AjaxResult selectEntityListByPage(Map<String, Object> body) {
        // 提取业务参数
        Object id = body.get(DeviceVOFieldConstant.field_id);
        String deviceName = (String) body.get(DeviceVOFieldConstant.field_device_name);
        String deviceType = (String) body.get(DeviceVOFieldConstant.field_device_type);
        String manufacturer = (String) body.get(DeviceVOFieldConstant.field_manufacturer);
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
        if (manufacturer != null) {
            sb.append(" (manufacturer = '").append(manufacturer).append("') AND");
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

            // 扩展设备的状态信息
            this.extend(mapList);

            // 扩展设备的扩展配置信息
            List<BaseEntity> extendConfigEntityList = this.entityManageService.getEntityList(ExtendConfigEntity.class);
            ExtendConfigUtils.extendMapList(mapList, extendConfigEntityList, DeviceEntity.class);

            Map<String, Object> data = new HashMap<>();
            data.put("list", mapList);
            data.put("total", total);

            return AjaxResult.success(data);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("option")
    public AjaxResult selectOptionList(@RequestBody Map<String, Object> body) {
        try {
            String deviceType = (String) body.get(OperateVOFieldConstant.field_device_type);
            String manufacturer = (String) body.get(OperateVOFieldConstant.field_manufacturer);
            String field = (String) body.get("field");
            if (MethodUtils.hasEmpty(field)) {
                throw new ServiceException("参数缺失：field");
            }

            StringBuilder sb = new StringBuilder();
            if (field.equals("deviceName")) {
                sb.append("SELECT DISTINCT t.device_name FROM tb_device t WHERE 1=1 ");
                if (!MethodUtils.hasEmpty(manufacturer)) {
                    sb.append("AND t.manufacturer = '" + manufacturer + "'");
                }
                if (!MethodUtils.hasEmpty(deviceType)) {
                    sb.append("AND t.device_type = '" + deviceType + "'");
                }
            }
            List<DevicePo> data = this.entityService.getMapper().executeSelectData(sb.toString());

            List<Map<String, Object>> resultList = new ArrayList<>();
            for (DevicePo entity : data) {
                Map<String, Object> result = new HashMap<>();
                result.put("value", entity.getDeviceName());
                result.put("label", entity.getDeviceName());
                resultList.add(result);
            }


            return AjaxResult.success(resultList);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    /**
     * 扩展设备状态
     *
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
        RedisReader redisReader = this.entityManageService.getRedisReader(DeviceStatusEntity.class);
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
            String manufacturer = (String) params.get(DeviceVOFieldConstant.field_manufacturer);
            String channelType = (String) params.get(DeviceVOFieldConstant.field_channel_type);
            String channelName = (String) params.get(DeviceVOFieldConstant.field_channel_name);
            Map<String, Object> deviceParam = (Map<String, Object>) params.get(DeviceVOFieldConstant.field_device_param);
            Map<String, Object> extendParam = (Map<String, Object>) params.get(DeviceVOFieldConstant.field_extend_param);

            // 简单校验参数
            if (MethodUtils.hasNull(deviceName, deviceType, manufacturer, channelType, channelName, deviceParam, extendParam)) {
                return AjaxResult.error("参数不能为空:deviceName, deviceType, manufacturer, channelType, channelName, deviceParam, extendParam");
            }

            // 构造作为参数的实体
            DeviceEntity entity = new DeviceEntity();
            entity.setDeviceName(deviceName);
            entity.setDeviceType(deviceType);
            entity.setManufacturer(manufacturer);
            entity.setChannelType(channelType);
            entity.setChannelName(channelName);
            entity.setDeviceParam(deviceParam);
            entity.setExtendParam(extendParam);

            // 简单验证实体的合法性
            if (entity.hasNullServiceKey()) {
                return AjaxResult.error("具有null的service key！");
            }

            RedisReader redisReader = this.entityManageService.getRedisReader(DeviceEntity.class);
            RedisWriter redisWriter = this.entityManageService.getRedisWriter(DeviceEntity.class);

            DeviceEntity exist = (DeviceEntity) redisReader.readEntity(entity.makeServiceKey());

            // 新增/修改实体：参数不包含id为新增，包含为修改
            if (params.get("id") == null) {
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

        RedisWriter redisWriter = this.entityManageService.getRedisWriter(DeviceEntity.class);

        this.entityService.deleteEntity(exist);
        redisWriter.deleteEntity(exist.makeServiceKey());
        return AjaxResult.success();
    }

    @DeleteMapping("entities")
    public AjaxResult deleteEntityList(@QueryParam("ids") String ids) {
        String[] idList = ids.split(",");

        RedisWriter redisWriter = this.entityManageService.getRedisWriter(DeviceEntity.class);

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

    @PostMapping("/export")
    public void downloadEntityList(@RequestBody Map<String, Object> body) {
        try {
            this.tempDirManageService.createTempDir();
            this.tempDirManageService.getTempDir();

            AjaxResult result = this.selectEntityListByPage(body);

            Map<String, Object> data = (Map<String, Object>) result.get(AjaxResult.DATA_TAG);
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) data.get("list");

            // 导出文件
            String fileName = this.exportFile(dataList);

            // 下载文件
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            File download = new File(this.tempDirManageService.getTempDir() + "/" + fileName);
            if (download.exists()) {
                response.setContentType("application/x-msdownload");
                response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(), StandardCharsets.ISO_8859_1));


                InputStream inputStream = new FileInputStream(download);
                ServletOutputStream ouputStream = response.getOutputStream();
                byte[] b = new byte[1024];
                int n;
                while ((n = inputStream.read(b)) != -1) {
                    ouputStream.write(b, 0, n);
                }
                ouputStream.close();
                inputStream.close();
            }

            // 删除文件
            download.delete();

        } catch (Exception e) {
            return;
        }
    }

    private String exportFile(List<Map<String, Object>> dataList) throws IOException {
        List<String> headerLine = new ArrayList<>();
        headerLine.add("name");
        headerLine.add("type");
        headerLine.add(DeviceVOFieldConstant.field_id);
        headerLine.add(DeviceVOFieldConstant.field_device_name);
        headerLine.add(DeviceVOFieldConstant.field_device_type);
        headerLine.add(DeviceVOFieldConstant.field_manufacturer);
        headerLine.add(DeviceVOFieldConstant.field_channel_name);
        headerLine.add(DeviceVOFieldConstant.field_channel_type);
        headerLine.add(DeviceVOFieldConstant.field_create_time);
        headerLine.add(DeviceVOFieldConstant.field_update_time);

        List<String> list = new ArrayList<>();
        // 头行
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < headerLine.size(); i++) {
            if (i + 1 != headerLine.size()) {
                sb.append(headerLine.get(i) + ",");
            } else {
                sb.append(headerLine.get(i));
            }
        }
        list.add(sb.toString());

        // 数据行
        for (Map<String, Object> row : dataList) {
            sb = new StringBuilder();
            for (int i = 0; i < headerLine.size(); i++) {
                String sValue = "";

                if (i == 0) {
                    sValue = (String) row.get(DeviceVOFieldConstant.field_device_name);
                } else if (i == 1) {
                    sValue = (String) row.get(DeviceVOFieldConstant.field_device_type);
                } else {
                    Object value = row.get(headerLine.get(i));
                    if (value != null) {
                        sValue = value.toString();
                    }
                }

                if (i + 1 != headerLine.size()) {
                    sb.append(sValue + ",");
                } else {
                    sb.append(sValue);
                }
            }
            list.add(sb.toString());
        }

        String fileName = System.currentTimeMillis() + ".csv";
        FileTextUtils.writeTextFile(this.tempDirManageService.getTempDir() + "/" + fileName, list, "UTF-8", true);
        return fileName;
    }
}
