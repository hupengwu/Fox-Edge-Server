package cn.foxtech.kernel.system.repository.controller;

import cn.foxtech.common.domain.constant.ServiceVOFieldConstant;
import cn.foxtech.common.entity.constant.RepoCompVOFieldConstant;
import cn.foxtech.common.entity.entity.RepoCompEntity;
import cn.foxtech.common.process.ProcessUtils;
import cn.foxtech.common.status.ServiceStatus;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.common.constants.EdgeServiceConstant;
import cn.foxtech.kernel.common.service.EdgeService;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import cn.foxtech.kernel.system.repository.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kernel/manager/repository/local/app-service")
public class RepoLocalApplicationController {
    @Autowired
    private RepoLocalAppLoadService appLoadService;

    @Autowired
    private RepoLocalAppStartService appStartService;

    @Autowired
    private RepoLocalAppConfService appConfService;

    @Autowired
    private RepoCloudInstallService installService;


    @Autowired
    private ServiceStatus serviceStatus;

    @Autowired
    private EdgeService edgeService;

    @Autowired
    private EntityManageService entityManageService;

    @Autowired
    private RepoLocalPathNameService pathNameService;

    @Autowired
    private RepoLocalAppSysService repoLocalAppSysService;


    @GetMapping("/status/entities")
    public AjaxResult selectEntityList() {
        List<Map<String, Object>> resultList = this.serviceStatus.getDataList(60 * 1000);
        return AjaxResult.success(resultList);
    }

    @GetMapping("/process/entities")
    public AjaxResult getProcess() {
        try {
            // 从磁盘中查找所有的shell文件信息
            List<Map<String, Object>> confFileInfoList = this.appConfService.getConfFileInfoList();

            // 按docker模式处理一遍
            confFileInfoList = this.buildDockerMode(confFileInfoList);

            // 扩展数据库中的启动配置信息
            this.appLoadService.extendStartConfig(confFileInfoList);

            // 扩展相关的进程状态信息
            ProcessUtils.extendAppStatus(confFileInfoList);

            List<Map<String, Object>> sysProcessList = this.repoLocalAppSysService.getSysProcessList();
            sysProcessList.addAll(confFileInfoList);


            return AjaxResult.success(sysProcessList);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    private List<Map<String, Object>> buildDockerMode(List<Map<String, Object>> confFileInfoList) throws IOException, InterruptedException {
        if (!EdgeServiceConstant.value_env_type_docker.equals(this.edgeService.getEnvType())) {
            return confFileInfoList;
        }

        Map<String, Object> confFileInfoMaps = new HashMap<>();
        for (Map<String, Object> confFileInfo : confFileInfoList) {
            String appType = (String) confFileInfo.getOrDefault(ServiceVOFieldConstant.field_app_type, "");
            String appName = (String) confFileInfo.getOrDefault(ServiceVOFieldConstant.field_app_name, "");

            confFileInfoMaps.put(appType + ":" + appName, confFileInfo);
        }


        List<Map<String, Object>> processList = ProcessUtils.getProcess();
        for (Map<String, Object> process : processList) {
            String appType = (String) process.getOrDefault(ServiceVOFieldConstant.field_app_type, "");
            String appName = (String) process.getOrDefault(ServiceVOFieldConstant.field_app_name, "");

            Map<String, Object> confFileInfo = (Map<String, Object>) confFileInfoMaps.get(appType + ":" + appName);
            if (confFileInfo == null) {
                continue;
            }

            process.putAll(confFileInfo);
        }

        return processList;
    }

    @PostMapping("config/load")
    public AjaxResult setServiceLoad(@RequestBody Map<String, Object> body) {
        try {
            this.edgeService.disable4Docker();

            // 提取业务参数
            String appName = (String) body.get(ServiceVOFieldConstant.field_app_name);
            String appType = (String) body.get(ServiceVOFieldConstant.field_app_type);
            Boolean appLoad = (Boolean) body.get(ServiceVOFieldConstant.field_app_load);

            if (MethodUtils.hasEmpty(appName, appType, appLoad)) {
                throw new ServiceException("参数不能为空: appName, appType, appLoad");
            }

            // 保存配置
            this.appLoadService.saveServiceLoad(appName, appType, appLoad);

            // 如果修改为非启动状态，那么同时也停止进程
            if (!appLoad) {
                this.appStartService.stopProcess(appName, appType);
            } else {
                this.appStartService.restartProcess(appName, appType);
            }

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }


    @PostMapping("process/restart")
    public AjaxResult restartProcess(@RequestBody Map<String, Object> body) {
        try {
            this.edgeService.disable4Docker();

            // 提取业务参数
            String appName = (String) body.get(ServiceVOFieldConstant.field_app_name);
            String appType = (String) body.get(ServiceVOFieldConstant.field_app_type);

            // 简单校验参数
            if (MethodUtils.hasEmpty(appName, appType)) {
                return AjaxResult.error("参数不能为空:appName, appType");
            }

            // 查询启动配置项目：如果是配置未启动，那么才可以重启该进程
            Boolean appLoad = this.appLoadService.queryServiceLoad(appName, appType, true);
            if (appLoad) {
                this.appStartService.restartProcess(appName, appType);
            }

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("process/stop")
    public AjaxResult stopProcess(@RequestBody Map<String, Object> body) {
        try {
            this.edgeService.disable4Docker();

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
            this.appStartService.stopProcess(appName, appType);

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("process/uninstall")
    public AjaxResult uninstallProcess(@RequestBody Map<String, Object> body) {
        try {
            this.edgeService.disable4Docker();
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
            this.installService.uninstallServiceFile(appName, appType);

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

    @PutMapping("conf")
    public AjaxResult updateConf(@RequestBody Map<String, Object> params) {
        try {
            Integer id = (Integer) params.get(RepoCompVOFieldConstant.field_id);
            String compType = (String) params.get(RepoCompVOFieldConstant.field_comp_type);
            String compRepo = (String) params.get(RepoCompVOFieldConstant.field_comp_repo);
            Map<String, Object> compParam = (Map<String, Object>) params.get(RepoCompVOFieldConstant.field_comp_param);

            // 简单校验参数
            if (MethodUtils.hasNull(id, compType, compRepo, compParam)) {
                throw new ServiceException("参数不能为空: id, compType, compRepo, compParam");
            }

            if (!compRepo.equals(RepoCompVOFieldConstant.value_comp_repo_local) || !compType.equals(RepoCompVOFieldConstant.value_comp_type_app_service)) {
                throw new ServiceException("组件类型不正确!");
            }

            RepoCompEntity entity = this.entityManageService.getEntity(Long.parseLong(id.toString()), RepoCompEntity.class);
            if (entity == null) {
                throw new ServiceException("实体不存在！");
            }

            // 复制一个副本，避免修改到原本
            entity = JsonUtils.clone(entity);

            String appType = (String) entity.getCompParam().get(ServiceVOFieldConstant.field_app_type);
            String appName = (String) entity.getCompParam().get(ServiceVOFieldConstant.field_app_name);
            if (MethodUtils.hasEmpty(appName, appType)) {
                throw new ServiceException("缺失参数：appName, appType");
            }

            if (ServiceVOFieldConstant.field_type_kernel.equals(appType)) {
                throw new ServiceException("kernel服务不允许修改!");
            }

            String userParam = (String) compParam.getOrDefault(ServiceVOFieldConstant.field_user_param, "");

            // 修改内容
            entity.getCompParam().put(ServiceVOFieldConstant.field_user_param, userParam);

            // 保存文件
            String pathName = this.pathNameService.getPathName4LocalShell2confFile(appType, appName);
            this.appConfService.saveConf(pathName, entity.getCompParam());

            // 更新配置
            this.entityManageService.updateEntity(entity);

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
