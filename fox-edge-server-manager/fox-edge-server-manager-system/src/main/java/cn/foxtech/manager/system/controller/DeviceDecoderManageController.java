package cn.foxtech.manager.system.controller;


import cn.foxtech.common.entity.constant.DeviceDecoderVOFieldConstant;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.manager.system.constants.RepositoryConstant;
import cn.foxtech.manager.system.service.JarFileInfoService;
import cn.foxtech.manager.system.service.ManageConfigService;
import cn.foxtech.manager.system.service.ProcessStartService;
import cn.foxtech.manager.system.service.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kernel/manager/device/decoder")
public class DeviceDecoderManageController {
    @Autowired
    private ManageConfigService manageConfigService;


    @Autowired
    private ProcessStartService processStartService;

    @Autowired
    private JarFileInfoService jarFileInfoService;


    @Autowired
    private RepositoryService repositoryService;


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

            // 获得数据库中的配置
            Map<String, Object> configValue = this.manageConfigService.getConfigValue("device-service", "system", "decoderConfig");

            // 从仓库获得解码器的描述信息
            List<Map<String, Object>> repoList = this.repositoryService.queryLocalListFile(RepositoryConstant.repository_type_decoder);

            // 扫描文件，获得解码器的信息
            List<Map<String, Object>> resultList = this.jarFileInfoService.findJarInfo(configValue, repoList);

            // 分页返回
            return PageUtils.getPageMapList(resultList, body);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
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
            String fileName = (String) params.get(DeviceDecoderVOFieldConstant.field_file_name);
            Boolean load = (Boolean) params.get(DeviceDecoderVOFieldConstant.field_load);

            // 简单校验参数
            if (MethodUtils.hasNull(fileName, load)) {
                return AjaxResult.error("参数不能为空:fileName, load");
            }

            // 获得数据库中的配置
            Map<String, Object> configValue = this.manageConfigService.getConfigValue("device-service", "system", "decoderConfig");

            // 取出列表数据
            List<Map<String, Object>> dataList = (List<Map<String, Object>>) configValue.get(DeviceDecoderVOFieldConstant.field_list);

            // 修改数值
            for (Map<String, Object> map : dataList) {
                String fileNameValue = (String) map.get(DeviceDecoderVOFieldConstant.field_file_name);

                if (fileName.startsWith(this.jarFileInfoService.getPackName(fileNameValue))) {
                    if (fileNameValue.equals(fileName)) {
                        map.put(DeviceDecoderVOFieldConstant.field_load, load);
                    } else {
                        if (Boolean.TRUE.equals(load)) {
                            map.put(DeviceDecoderVOFieldConstant.field_load, false);
                        }
                    }
                }
            }

            this.manageConfigService.saveConfigValue("device-service", "system", "decoderConfig", configValue);

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("entity/delete")
    public AjaxResult deleteEntityList(@RequestBody Map<String, Object> params) {
        try {
            List<String> fileNames = (List<String>) params.get(DeviceDecoderVOFieldConstant.field_file_name);
            if (MethodUtils.hasEmpty(fileNames)) {
                return AjaxResult.error("参数不能为空:fileName");
            }

            for (String fileName : fileNames) {
                // 删除本地文件
                this.jarFileInfoService.deleteFile(fileName);
            }


            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("process/restart")
    public AjaxResult restartProcess(@RequestBody Map<String, Object> params) {
        try {
            this.processStartService.restartProcess("device-service", "system");
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
