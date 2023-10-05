package cn.foxtech.manager.system.controller;

import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.manager.system.constants.RepoComponentConstant;
import cn.foxtech.manager.system.constants.RepoProductConstant;
import cn.foxtech.manager.system.service.RepoComponentService;
import cn.foxtech.manager.system.service.RepoProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.ws.rs.QueryParam;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


    @PostMapping("/page")
    public AjaxResult selectPageList(@RequestBody Map<String, Object> body) {
        try {
            // 提取业务参数
            String source = (String) body.get("source");
            String modelType = (String) body.get(RepoProductConstant.filed_model_type);

            // 简单验证
            if (MethodUtils.hasEmpty(modelType)) {
                throw new ServiceException("参数不能为空:modelType");
            }

            // 查询本地/远程文件列表
            List<Map<String, Object>> list;
            if ("local".equals(source)) {
                list = this.productService.queryLocalListFile();
            } else {
                list = this.productService.queryUriListFile();
            }

            List<Map<String, Object>> resultList = new ArrayList<>();
            list.forEach((Map<String, Object> value) -> {
                boolean result = true;
                String fieldName = "";

                // 模糊搜索
                fieldName = RepoProductConstant.filed_keyword;
                if (body.containsKey(fieldName)) {
                    boolean fuzzy = false;
                    fuzzy |= ((String) value.getOrDefault(RepoProductConstant.filed_model, "")).toLowerCase().contains(((String) body.get(fieldName)).toLowerCase());
                    fuzzy |= ((String) value.getOrDefault(RepoProductConstant.filed_manufacturer, "")).toLowerCase().contains(((String) body.get(fieldName)).toLowerCase());
                    fuzzy |= ((String) value.getOrDefault(RepoProductConstant.filed_description, "")).toLowerCase().contains(((String) body.get(fieldName)).toLowerCase());
                    fuzzy |= ((String) value.getOrDefault(RepoProductConstant.filed_tags, "")).toLowerCase().contains(((String) body.get(fieldName)).toLowerCase());

                    result &= fuzzy;
                }

                if (result) {
                    resultList.add(value);
                }
            });

            // 分页查询
            return PageUtils.getPageMapList(resultList, body);
        } catch (FileNotFoundException e) {
            return AjaxResult.error("本地文件不存在:" + e.getMessage());
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

            this.scanLocalStatus(entity);


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

            // 立即扫描本地组件的状态，并将状态保存到缓存
            this.componentService.scanLocalStatusAndMd5(modelType, modelName, modelVersion, lastVersion, versions);

            // 取出缓存中获得的扫描状态，并更新到组件之中
            this.componentService.extendLocalStatus(comp);
        }
    }
}
