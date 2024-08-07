package cn.foxtech.kernel.system.repository.controller;


import cn.foxtech.common.domain.constant.RedisStatusConstant;
import cn.foxtech.common.entity.constant.DeviceDecoderVOFieldConstant;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.status.ServiceStatus;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.common.service.EdgeService;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import cn.foxtech.kernel.system.repository.service.RepoCloudCacheService;
import cn.foxtech.kernel.system.repository.service.RepoLocalAppStartService;
import cn.foxtech.kernel.system.repository.service.RepoLocalJarFileConfigService;
import cn.foxtech.kernel.system.repository.service.RepoLocalJarFileInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JAR文件列表管理：设备JAR解码器的文件列表
 */
@RestController
@RequestMapping("/repository/local/jar-file")
public class RepoLocalJarFileListController {
    @Autowired
    private EdgeService edgeService;
    @Autowired
    private RepoLocalAppStartService appStartService;

    @Autowired
    private RepoLocalJarFileInfoService fileInfoService;

    @Autowired
    private RepoCloudCacheService cacheService;

    @Autowired
    private RepoLocalJarFileConfigService configService;

    @Autowired
    private ServiceStatus serviceStatus;


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
            // 从仓库获得解码器的描述信息
            List<Map<String, Object>> repoList = this.cacheService.readList(RepoCompConstant.repository_type_decoder);

            // 取出需要装载的数据
            Set<String> loadJars = this.configService.getLoads();

            // 扫描文件，获得解码器的信息
            List<Map<String, Object>> resultList = this.fileInfoService.findJarInfo(loadJars, repoList);

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

            // 更新配置
            this.configService.updateConfig(fileName, load);

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
                this.fileInfoService.deleteFile(fileName);
            }


            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("process/restart")
    public AjaxResult restartProcess(@RequestBody Map<String, Object> params) {
        try {
            this.edgeService.testDockerEnv();

            // 查找设备服务的业务名称：用户安装的可能是device-service或者device-graalvm
            Map<String, Object> deviceService = this.serviceStatus.getActiveService(RedisStatusConstant.value_model_type_device, RedisStatusConstant.value_model_name_device, 60 * 1000);
            if (deviceService == null) {
                throw new ServiceException("Device服务尚未启动！");
            }

            String serviceType = (String) deviceService.get(RedisStatusConstant.field_service_type);
            String serviceName = (String) deviceService.get(RedisStatusConstant.field_service_name);

            // 重启服务
            this.appStartService.restartProcess(serviceName, serviceType);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("jar-info/query")
    public Map<String, Object> getJarInfo(@RequestBody Map<String, Object> body) {
        try {
            String fileName = (String) body.get(DeviceDecoderVOFieldConstant.field_file_name);

            // 简单验证
            if (MethodUtils.hasEmpty(fileName)) {
                throw new ServiceException("参数不能为空: fileName");
            }

            Map<String, Object> jarInfo = this.fileInfoService.readJarFileInfo(fileName);
            if (jarInfo == null) {
                throw new ServiceException("读取jar文件的信息失败!");
            }

            // 分页查询
            return AjaxResult.success(jarInfo);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
