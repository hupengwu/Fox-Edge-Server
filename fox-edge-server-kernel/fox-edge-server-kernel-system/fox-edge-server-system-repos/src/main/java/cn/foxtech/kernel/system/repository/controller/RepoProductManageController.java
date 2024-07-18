package cn.foxtech.kernel.system.repository.controller;

import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.common.service.EdgeService;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import cn.foxtech.kernel.system.repository.constants.RepoStatusConstant;
import cn.foxtech.kernel.system.repository.service.RepoCloudFIleInstallService;
import cn.foxtech.kernel.system.repository.service.RepoLocalAppLoadService;
import cn.foxtech.kernel.system.repository.service.RepoLocalJarFileConfigService;
import cn.foxtech.kernel.system.repository.service.RepoProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/repository/product")
public class RepoProductManageController {
    /**
     * 仓库服务
     */
    @Autowired
    private RepoCloudFIleInstallService installService;


    @Autowired
    private RepoProductService productService;

    @Autowired
    private RepoLocalJarFileConfigService configService;

    @Autowired
    private RepoLocalAppLoadService appLoadService;

    @Autowired
    private EdgeService edgeService;


    @PostMapping("/page")
    public Map<String, Object> selectPageList(@RequestBody Map<String, Object> body) {
        try {
            this.edgeService.testDockerEnv();

            // 云端查询数据
            Map<String, Object> respond = this.productService.queryProductPage(body);
            return respond;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @GetMapping("entity")
    public AjaxResult getEntity(@QueryParam("uuid") String uuid) {
        try {
            this.edgeService.testDockerEnv();

            // 简单验证
            if (MethodUtils.hasEmpty(uuid)) {
                throw new ServiceException("参数不能为空: uuid");
            }

            // 从云端查询产品实体的详情
            Map<String, Object> entity = this.productService.queryProductEntity(uuid);

            // 扫描本地文件的安装状态
            this.scanLocalStatus(entity);

            this.extendLoadConfig(entity);

            return AjaxResult.success(entity);

        } catch (FileNotFoundException e) {
            return AjaxResult.error("本地文件不存在:" + e.getMessage());
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    private void scanLocalStatus(Map<String, Object> entity) {
        // 从本地查询安装状态
        List<Map<String, Object>> comps = (List<Map<String, Object>>) entity.get("comps");
        for (Map<String, Object> comp : comps) {
            // 提取业务参数
            String modelName = (String) comp.get(RepoCompConstant.filed_model_name);
            String modelType = (String) comp.get(RepoCompConstant.filed_model_type);

            // 取出最新的文件版本信息
            Map<String, Object> lastVersion = (Map<String, Object>) comp.get(RepoCompConstant.filed_last_version);
            List<Map<String, Object>> versions = (List<Map<String, Object>>) comp.get(RepoCompConstant.filed_versions);

            // 立即扫描本地仓库中的组件状态，并将状态保存到缓存
            this.installService.scanLocalStatusAndMd5(modelType, modelName, lastVersion, versions);

            // 取出缓存中获得的扫描状态，并更新到组件之中
            this.installService.extendLocalStatus(comp);

            // 场景补充：已经安装过，但本地仓库被删除的组件，此时独立计算安装状态
            this.extendModelStatus(comp);
        }
    }

    private void extendModelStatus(Map<String, Object> comp) {
        // 提取业务参数
        String modelName = (String) comp.get(RepoCompConstant.filed_model_name);
        String modelType = (String) comp.get(RepoCompConstant.filed_model_type);

        Map<String, Object> usedVersion = (Map<String, Object>) comp.get(RepoCompConstant.filed_used_version);
        if (usedVersion != null) {
            return;
        }

        usedVersion = new HashMap<>();
        usedVersion.put(RepoCompConstant.filed_version, "unknown");

        // 扫描已经安装的模块状态
        Map<String, Object> modelStatus = this.installService.scanModelStatus(modelType, modelName);
        if (!MethodUtils.hasEmpty(modelStatus)) {
            usedVersion.put(RepoCompConstant.filed_version, modelStatus.get(RepoCompConstant.filed_version));
            usedVersion.put(RepoCompConstant.filed_status, RepoStatusConstant.status_installed);
        }

        comp.put(RepoCompConstant.filed_used_version, usedVersion);


    }

    private void extendLoadConfig(Map<String, Object> entity) {
        Set<String> loads = this.configService.getLoads();

        // 从本地查询安装状态
        List<Map<String, Object>> comps = (List<Map<String, Object>>) entity.get("comps");
        for (Map<String, Object> comp : comps) {
            // 提取业务参数
            String modelName = (String) comp.get(RepoCompConstant.filed_model_name);
            String modelType = (String) comp.get(RepoCompConstant.filed_model_type);

            // 检查参数是否为空
            if (MethodUtils.hasEmpty(modelName, modelType)) {
                continue;
            }

            // 场景1：解码器的处理
            if (RepoCompConstant.repository_type_decoder.equals(modelType)) {
                String jarFileName = modelName + ".jar";
                comp.put("load", loads.contains(jarFileName));


                continue;
            }

            if (RepoCompConstant.repository_type_service.equals(modelType)) {
                String component = (String) comp.get(RepoCompConstant.filed_component);
                if (MethodUtils.hasEmpty(component)) {
                    continue;
                }

                boolean load = this.appLoadService.queryServiceLoad(modelName, component, false);
                comp.put("load", load);
                continue;
            }
        }
    }
}
