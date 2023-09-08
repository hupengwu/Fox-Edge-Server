package cn.foxtech.manager.system.controller;

import cn.foxtech.common.domain.constant.ServiceVOFieldConstant;
import cn.foxtech.common.process.ProcessUtils;
import cn.foxtech.common.status.ServiceStatus;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.manager.system.service.ProcessLoadService;
import cn.foxtech.manager.system.service.ProcessStartService;
import cn.foxtech.manager.system.utils.ServiceIniFilesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kernel/manager/service")
public class ServiceManageController {
    @Autowired
    private ProcessLoadService processLoadService;

    @Autowired
    private ProcessStartService processStartService;


    @Autowired
    private ServiceStatus serviceStatus;


    @GetMapping("/status/entities")
    public AjaxResult selectEntityList() {
        List<Map<String, Object>> resultList = this.serviceStatus.getDataList(60 * 1000);
        return AjaxResult.success(resultList);
    }

    @GetMapping("/process/entities")
    public AjaxResult getProcess() {
        try {
            // 从磁盘中查找所有的shell文件信息
            List<Map<String, Object>> confFileInfoList = ServiceIniFilesUtils.getConfFileInfoList();

            // 扩展数据库中的启动配置信息
            this.processLoadService.extendStartConfig(confFileInfoList);

            // 扩展相关的进程状态信息
            ProcessUtils.extendAppStatus(confFileInfoList);

            return AjaxResult.success(confFileInfoList);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("config/load")
    public AjaxResult setServiceLoad(@RequestBody Map<String, Object> body) {
        try {
            // 提取业务参数
            String appName = (String) body.get(ServiceVOFieldConstant.field_app_name);
            String appType = (String) body.get(ServiceVOFieldConstant.field_app_type);
            Boolean appLoad = (Boolean) body.get(ServiceVOFieldConstant.field_app_load);

            if (MethodUtils.hasEmpty(appName, appType, appLoad)) {
                throw new ServiceException("参数不能为空: appName, appType, appLoad");
            }

            // 保存配置
            this.processLoadService.saveServiceLoad(appName, appType, appLoad);

            // 如果修改为非启动状态，那么同时也停止进程
            if (!appLoad) {
                this.processStartService.stopProcess(appName, appType);
            } else {
                this.processStartService.restartProcess(appName, appType);
            }

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


    @PostMapping("process/restart")
    public AjaxResult restartProcess(@RequestBody Map<String, Object> body) {
        try {
            // 提取业务参数
            String appName = (String) body.get(ServiceVOFieldConstant.field_app_name);
            String appType = (String) body.get(ServiceVOFieldConstant.field_app_type);

            // 简单校验参数
            if (MethodUtils.hasEmpty(appName, appType)) {
                return AjaxResult.error("参数不能为空:appName, appType");
            }

            // 查询启动配置项目：如果是配置未启动，那么才可以重启该进程
            Boolean appLoad = this.processLoadService.queryServiceLoad(appName, appType, true);
            if (appLoad) {
                this.processStartService.restartProcess(appName, appType);
            }

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("process/stop")
    public AjaxResult stopProcess(@RequestBody Map<String, Object> body) {
        try {
            // 提取业务参数
            String appName = (String) body.get(ServiceVOFieldConstant.field_app_name);
            String appType = (String) body.get(ServiceVOFieldConstant.field_app_type);

            // 简单校验参数
            if (MethodUtils.hasEmpty(appName, appType)) {
                return AjaxResult.error("参数不能为空:appName, appType");
            }

            if (ServiceVOFieldConstant.field_type_kernel.equals(appType)) {
                return AjaxResult.error("该级别的服务不允许停止:" + appType);
            }

            // 停止进程
            this.processStartService.stopProcess(appName, appType);

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("process/uninstall")
    public AjaxResult uninstallProcess(@RequestBody Map<String, Object> body) {
        try {
            // 提取业务参数
            String appName = (String) body.get(ServiceVOFieldConstant.field_app_name);
            String appType = (String) body.get(ServiceVOFieldConstant.field_app_type);

            // 简单校验参数
            if (MethodUtils.hasEmpty(appName, appType)) {
                return AjaxResult.error("参数不能为空:appName, appType");
            }

            if (ServiceVOFieldConstant.field_type_kernel.equals(appType)) {
                return AjaxResult.error("该级别的服务不允许删除:" + appType);
            }

            // 停止进程
            this.processStartService.uninstallProcess(appName, appType );

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("process/gc")
    public AjaxResult gcProcess(@QueryParam("pid") Long pid) {
        try {
            // 简单校验参数
            if (MethodUtils.hasNull(pid)) {
                return AjaxResult.error("参数不能为空:pid");
            }

            Long rpid = 0L;
            List<Map<String, Object>> processList = ProcessUtils.getProcess();
            for (Map<String, Object> map : processList) {
                if (pid.equals(map.get(ServiceVOFieldConstant.field_pid))) {
                    rpid = (Long) map.get(ServiceVOFieldConstant.field_pid);
                    break;
                }
            }
            if (rpid == null || rpid == 0L) {
                return AjaxResult.error("找不到对应的进程!");
            }

            // 重启设备服务进程
            List<String> resultList = ProcessUtils.gcProcess(pid);
            if (resultList.isEmpty()) {
                return AjaxResult.error("返回空！");
            }

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
