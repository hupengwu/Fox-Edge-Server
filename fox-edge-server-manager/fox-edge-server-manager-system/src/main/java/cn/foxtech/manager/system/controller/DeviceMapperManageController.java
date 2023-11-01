package cn.foxtech.manager.system.controller;


import cn.foxtech.common.entity.constant.DeviceMapperVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceMapperEntity;
import cn.foxtech.common.entity.entity.ExtendConfigEntity;
import cn.foxtech.common.entity.utils.EntityVOBuilder;
import cn.foxtech.common.entity.utils.ExtendConfigUtils;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.file.TempDirManageService;
import cn.foxtech.common.utils.file.FileTextUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.common.utils.number.NumberUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.manager.system.service.EntityManageService;
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
import java.util.*;

@RestController
@RequestMapping("/kernel/manager/device/mapper")
public class DeviceMapperManageController {
    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private TempDirManageService tempDirManageService;


    @GetMapping("entities")
    public AjaxResult selectEntityList() {
        List<BaseEntity> entityList = this.entityManageService.getEntityList(DeviceMapperEntity.class);
        return AjaxResult.success(EntityVOBuilder.buildVOList(entityList));
    }

    @PostMapping("entities")
    public AjaxResult selectEntityList(@RequestBody Map<String, Object> body) {
        return this.selectEntityList(body, false);
    }

    @PostMapping("page")
    public AjaxResult selectPageList(@RequestBody Map<String, Object> body) {
        return this.selectEntityList(body, true);
    }

    /**
     * 查询实体数据
     *
     * @param body   查询参数
     * @param isPage 是否是分页模式。分页模式，要求有pageNum/pageSize参数，并按分页格式返回
     * @return 实体数据
     */
    private AjaxResult selectEntityList(Map<String, Object> body, boolean isPage) {
        try {
            List<BaseEntity> entityList = this.entityManageService.getEntityList(DeviceMapperEntity.class, (Object value) -> {
                DeviceMapperEntity entity = (DeviceMapperEntity) value;

                boolean result = true;

                if (body.containsKey(DeviceMapperVOFieldConstant.field_device_type)) {
                    result &= entity.getDeviceType().equals(body.get(DeviceMapperVOFieldConstant.field_device_type));
                }
                if (body.containsKey(DeviceMapperVOFieldConstant.field_object_name)) {
                    result &= entity.getObjectName().equals(body.get(DeviceMapperVOFieldConstant.field_object_name));
                }
                if (body.containsKey(DeviceMapperVOFieldConstant.field_mapper_name)) {
                    result &= entity.getMapperName().equals(body.get(DeviceMapperVOFieldConstant.field_mapper_name));
                }
                if (body.containsKey(DeviceMapperVOFieldConstant.field_mapper_mode)) {
                    result &= entity.getMapperMode().equals(body.get(DeviceMapperVOFieldConstant.field_mapper_mode));
                }

                return result;
            });


            List<BaseEntity> extendConfigEntityList = this.entityManageService.getEntityList(ExtendConfigEntity.class);

            // 获得分页数据
            List<Map<String, Object>> mapList = new ArrayList<>();
            if (isPage) {
                AjaxResult result = PageUtils.getPageList(entityList, body);
                Map<String, Object> data = (Map<String, Object>) result.getOrDefault(AjaxResult.DATA_TAG, new HashMap<>());
                mapList = (List<Map<String, Object>>) (data.getOrDefault("list", new ArrayList<>()));

                // 补充扩展字段
                ExtendConfigUtils.extendMapList(mapList, extendConfigEntityList, DeviceMapperEntity.class);

                return result;
            } else {
                mapList = EntityVOBuilder.buildVOList(entityList);

                // 补充扩展字段
                ExtendConfigUtils.extendMapList(mapList, extendConfigEntityList, DeviceMapperEntity.class);

                return AjaxResult.success(mapList);
            }

        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("entity")
    public AjaxResult queryEntity(@QueryParam("id") Long id) {
        DeviceMapperEntity exist = this.entityManageService.getEntity(id, DeviceMapperEntity.class);
        if (exist == null) {
            return AjaxResult.error("实体不存在");
        }

        return AjaxResult.success(exist);
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
            List<Map<String, Object>> list = (List<Map<String, Object>>) params.get("list");

            // 简单验证
            if (MethodUtils.hasEmpty(list)) {
                throw new ServiceException("参数不能为空:list");
            }

            for (Map<String, Object> item : list) {
                Integer id = (Integer) item.get(DeviceMapperVOFieldConstant.field_id);
                String mapperName = (String) item.get(DeviceMapperVOFieldConstant.field_mapper_name);
                Integer mapperMode = (Integer) item.get(DeviceMapperVOFieldConstant.field_mapper_mode);
                Map<String, Object> extendParam = (Map<String, Object>) item.get(DeviceMapperVOFieldConstant.field_extend_param);


                if (MethodUtils.hasEmpty(id, mapperName, mapperMode)) {
                    throw new ServiceException("参数不能为空:id, mapperName, mapperMode");
                }
                if (MethodUtils.hasNull(extendParam)) {
                    throw new ServiceException("参数不能为空: extendParam");
                }

                DeviceMapperEntity entity = this.entityManageService.getEntity(NumberUtils.makeLong(id), DeviceMapperEntity.class);
                if (entity == null) {
                    continue;
                }

                if (!mapperMode.equals(entity.getMapperMode()) || !mapperName.equals(entity.getMapperName()) || !extendParam.equals(entity.getExtendParam())) {
                    entity.setMapperName(mapperName);
                    entity.setMapperMode(mapperMode);
                    entity.setExtendParam(extendParam);

                    this.entityManageService.updateEntity(entity);
                }
            }

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/delete")
    public AjaxResult deleteDecoderPackageFile(@RequestBody Map<String, Object> body) {
        try {
            // 提取业务参数
            List<Object> list = (List<Object>) body.get("list");

            // 简单验证
            if (MethodUtils.hasEmpty(list)) {
                throw new ServiceException("参数不能为空:list");
            }

            for (Object id : list) {
                this.entityManageService.deleteEntity(NumberUtils.makeLong(id), DeviceMapperEntity.class);
            }

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/export")
    public void downloadEntityList(@RequestBody Map<String, Object> body) {
        try {
            this.tempDirManageService.createTempDir();
            this.tempDirManageService.getTempDir();

            AjaxResult result = this.selectEntityList(body, true);

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

    private Set<String> getFields(List<Map<String, Object>> dataList) {
        Set<String> keys = new HashSet<>();
        for (Map<String, Object> data : dataList) {
            Map<String, Object> extendParam = (Map<String, Object>) data.get(DeviceMapperVOFieldConstant.field_extend_param);
            for (String key : extendParam.keySet()) {
                keys.add(DeviceMapperVOFieldConstant.field_extend_param + "." + key);
            }
        }

        return keys;
    }

    private String exportFile(List<Map<String, Object>> dataList) throws IOException {
        Set<String> extendParamKeys = this.getFields(dataList);

        List<String> headerLine = new ArrayList<>();
        headerLine.add(DeviceMapperVOFieldConstant.field_id);
        headerLine.add(DeviceMapperVOFieldConstant.field_device_type);
        headerLine.add(DeviceMapperVOFieldConstant.field_object_name);
        headerLine.add(DeviceMapperVOFieldConstant.field_mapper_name);
        headerLine.add(DeviceMapperVOFieldConstant.field_mapper_mode);
        headerLine.add(DeviceMapperVOFieldConstant.field_value_Type);
        headerLine.addAll(extendParamKeys);


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

                Object value = null;
                String header = headerLine.get(i);
                if (header.startsWith(DeviceMapperVOFieldConstant.field_extend_param + ".")) {
                    header = header.substring((DeviceMapperVOFieldConstant.field_extend_param + ".").length());
                    Map<String, Object> extendParam = (Map<String, Object>) row.get(DeviceMapperVOFieldConstant.field_extend_param);

                    value = extendParam.get(header);
                } else {
                    value = row.get(header);
                }

                if (value != null) {
                    sValue = value.toString();
                } else {
                    sValue = "";
                }
                // 将逗号替换为中文，避免跟CSV的,冲突
                sValue = sValue.replace(",","；");

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
