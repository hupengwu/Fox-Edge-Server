package cn.foxtech.manager.system.controller;

import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.manager.system.constants.RepoComponentConstant;
import cn.foxtech.manager.system.constants.RepositoryStatusConstant;
import cn.foxtech.manager.system.service.DecoderConfigService;
import cn.foxtech.manager.system.service.ProcessLoadService;
import cn.foxtech.manager.system.service.RepoComponentService;
import cn.foxtech.manager.system.service.RepoProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.io.FileNotFoundException;
import java.util.*;

@RestController
@RequestMapping("/kernel/manager/repository/product")
public class RepoProductManageController {
    /**
     * 仓库服务
     */
    @Autowired
    private RepoComponentService componentService;


    @Autowired
    private RepoProductService productService;

    @Autowired
    private DecoderConfigService decoderConfigService;

    @Autowired
    private ProcessLoadService processLoadService;


    @PostMapping("/page")
    public Map<String, Object> selectPageList(@RequestBody Map<String, Object> body) {
        try {
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
            String modelName = (String) comp.get(RepoComponentConstant.filed_model_name);
            String modelType = (String) comp.get(RepoComponentConstant.filed_model_type);
            String modelVersion = (String) comp.get(RepoComponentConstant.filed_model_version);

            // 取出最新的文件版本信息
            Map<String, Object> lastVersion = (Map<String, Object>) comp.get(RepoComponentConstant.filed_last_version);
            List<Map<String, Object>> versions = (List<Map<String, Object>>) comp.get(RepoComponentConstant.filed_versions);

            // 立即扫描本地仓库中的组件状态，并将状态保存到缓存
            this.componentService.scanLocalStatusAndMd5(modelType, modelName, modelVersion, lastVersion, versions);

            // 取出缓存中获得的扫描状态，并更新到组件之中
            this.componentService.extendLocalStatus(comp);

            // 场景补充：已经安装过，但本地仓库被删除的组件，此时独立计算安装状态
            this.extendModelStatus(comp);
        }
    }

    private void extendModelStatus(Map<String, Object> comp) {
        // 提取业务参数
        String modelName = (String) comp.get(RepoComponentConstant.filed_model_name);
        String modelType = (String) comp.get(RepoComponentConstant.filed_model_type);
        String modelVersion = (String) comp.get(RepoComponentConstant.filed_model_version);

        Map<String, Object> usedVersion = (Map<String, Object>) comp.get(RepoComponentConstant.filed_used_version);
        if (usedVersion != null) {
            return;
        }

        usedVersion = new HashMap<>();
        usedVersion.put(RepoComponentConstant.filed_version, "unknown");

        // 扫描已经安装的模块状态
        Map<String, Object> modelStatus = this.componentService.scanModelStatus(modelType, modelName, modelVersion);
        if (!MethodUtils.hasEmpty(modelStatus)) {
            usedVersion.put(RepoComponentConstant.filed_version, modelStatus.get(RepoComponentConstant.filed_version));
            usedVersion.put(RepoComponentConstant.filed_status, RepositoryStatusConstant.status_installed);
        }

        comp.put(RepoComponentConstant.filed_used_version, usedVersion);


    }

    private void extendLoadConfig(Map<String, Object> entity) {
        Set<String> loads = this.decoderConfigService.getLoads();

        // 从本地查询安装状态
        List<Map<String, Object>> comps = (List<Map<String, Object>>) entity.get("comps");
        for (Map<String, Object> comp : comps) {
            // 提取业务参数
            String modelName = (String) comp.get(RepoComponentConstant.filed_model_name);
            String modelType = (String) comp.get(RepoComponentConstant.filed_model_type);
            String modelVersion = (String) comp.get(RepoComponentConstant.filed_model_version);


            // 检查参数是否为空
            if (MethodUtils.hasEmpty(modelName, modelType, modelVersion)) {
                continue;
            }

            // 场景1：解码器的处理
            if (RepoComponentConstant.repository_type_decoder.equals(modelType)) {
                String jarFileName = modelName + "." + modelVersion + ".jar";
                comp.put("load", loads.contains(jarFileName));


                continue;
            }

            if (RepoComponentConstant.repository_type_service.equals(modelType)) {
                String component = (String) comp.get(RepoComponentConstant.filed_component);
                if (MethodUtils.hasEmpty(component)) {
                    continue;
                }

                boolean load = this.processLoadService.queryServiceLoad(modelName, component, false);
                comp.put("load", load);
                continue;
            }
        }
    }
}
