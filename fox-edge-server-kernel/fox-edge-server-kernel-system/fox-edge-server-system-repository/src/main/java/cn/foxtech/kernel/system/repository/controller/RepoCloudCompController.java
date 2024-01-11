package cn.foxtech.kernel.system.repository.controller;


import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.common.service.EdgeService;
import cn.foxtech.kernel.system.common.scheduler.PeriodTasksScheduler;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import cn.foxtech.kernel.system.repository.service.RepoCloudCacheService;
import cn.foxtech.kernel.system.repository.service.RepoCloudInstallService;
import cn.foxtech.kernel.system.repository.service.RepoCloudInstallStatus;
import cn.foxtech.kernel.system.repository.service.RepoLocalPathNameService;
import cn.foxtech.kernel.system.repository.task.RepoDownLoadTask;
import cn.foxtech.kernel.system.repository.task.RepoScanStatusTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kernel/manager/repository")
public class RepoCloudCompController {
    @Autowired
    private RedisConsoleService logger;
    /**
     * 仓库服务
     */
    @Autowired
    private RepoCloudInstallService installService;

    @Autowired
    private RepoCloudCacheService cacheService;

    @Autowired
    private RepoCloudInstallStatus installStatus;

    @Autowired
    private RepoLocalPathNameService pathNameService;

    /**
     * 周期任务调度器
     */
    @Autowired
    private PeriodTasksScheduler periodTasksScheduler;

    @Autowired
    private EdgeService edgeService;


    @PostMapping("/page")
    public AjaxResult selectPageList(@RequestBody Map<String, Object> body) {
        try {
            // 提取业务参数
            String source = (String) body.get("source");
            String modelType = (String) body.get(RepoCompConstant.filed_model_type);

            // 简单验证
            if (MethodUtils.hasEmpty(modelType)) {
                throw new ServiceException("参数不能为空:modelType");
            }

            // 查询本地/远程文件列表
            List<Map<String, Object>> list;
            if ("local".equals(source)) {
                list = this.cacheService.queryLocalListFile(modelType);
            } else {
                list = this.installService.queryUriListFile(modelType);
            }

            // 分析本地的安装状态
            for (Map<String, Object> map : list) {
                this.installService.extendLocalStatus(map);
            }


            List<Map<String, Object>> resultList = new ArrayList<>();
            list.forEach((Map<String, Object> value) -> {
                boolean result = true;
                String fieldName = "";

                // 模糊搜索
                fieldName = RepoCompConstant.filed_keyword;
                if (body.containsKey(fieldName)) {
                    boolean fuzzy = false;
                    fuzzy |= ((String) value.getOrDefault(RepoCompConstant.filed_model_name, "")).toLowerCase().contains(((String) body.get(fieldName)).toLowerCase());
                    fuzzy |= ((String) value.getOrDefault(RepoCompConstant.filed_model_type, "")).toLowerCase().contains(((String) body.get(fieldName)).toLowerCase());
                    fuzzy |= ((String) value.getOrDefault(RepoCompConstant.filed_description, "")).toLowerCase().contains(((String) body.get(fieldName)).toLowerCase());
                    fuzzy |= ((String) value.getOrDefault(RepoCompConstant.filed_manufacturer, "")).toLowerCase().contains(((String) body.get(fieldName)).toLowerCase());
                    fuzzy |= ((String) value.getOrDefault(RepoCompConstant.filed_device_type, "")).toLowerCase().contains(((String) body.get(fieldName)).toLowerCase());

                    result &= fuzzy;
                }

                // 后面是精确匹配
                fieldName = RepoCompConstant.filed_version;
                if (body.containsKey(fieldName)) {
                    result &= ((String) value.getOrDefault(fieldName, "")).contains((String) body.get(fieldName));
                }
                fieldName = RepoCompConstant.filed_component;
                if (body.containsKey(fieldName)) {
                    result &= value.getOrDefault(fieldName, "").equals(body.get(fieldName));
                }
                fieldName = RepoCompConstant.filed_status;
                if (body.containsKey(fieldName)) {
                    Map<String, Object> lastVersion = (Map<String, Object>) value.getOrDefault(RepoCompConstant.filed_last_version, new HashMap<>());
                    result &= lastVersion.getOrDefault(fieldName, 0).equals(body.get(fieldName));
                }


                if (result) {
                    resultList.add(value);
                }
            });


            //   分页查询
            return PageUtils.getPageMapList(resultList, body);
        } catch (FileNotFoundException e) {
            return AjaxResult.error("本地文件不存在:" + e.getMessage());
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/download")
    public AjaxResult downloadPackFile(@RequestBody Map<String, Object> body) {
        try {
            this.edgeService.disable4Docker();

            // 提取业务参数
            List<Map<String, Object>> list = (List<Map<String, Object>>) body.get("list");

            // 简单验证
            if (MethodUtils.hasEmpty(list)) {
                throw new ServiceException("参数不能为空:list");
            }

            for (Map<String, Object> map : list) {
                // 提取业务参数
                String modelType = (String) map.get(RepoCompConstant.filed_model_type);
                String modelName = (String) map.get(RepoCompConstant.filed_model_name);
                String modelVersion = (String) map.get(RepoCompConstant.filed_model_version);
                String version = (String) map.get(RepoCompConstant.filed_version);
                String stage = (String) map.get(RepoCompConstant.filed_stage);
                String pathName = (String) map.get(RepoCompConstant.filed_path_name);
                String component = (String) map.get(RepoCompConstant.filed_component);

                // 简单验证
                if (MethodUtils.hasEmpty(modelType, modelName, modelVersion, version, stage, pathName, component)) {
                    throw new ServiceException("参数不能为空: modelType, modelName, modelVersion, version, stage, pathName, component");
                }

                if (!this.installService.testUrlFileCanBeOpen(modelType, modelName, modelVersion, version, pathName)) {
                    throw new ServiceException("Fox-Cloud上文件无法下载，请联系该模块的发布者!");
                }

                this.periodTasksScheduler.insertPeriodTask(new RepoDownLoadTask(this.installService, this.installStatus, modelType, modelName, modelVersion, version, stage, pathName, component));
            }

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/scan")
    public AjaxResult deleteDecoderFile(@RequestBody Map<String, Object> body) {
        try {
            this.edgeService.disable4Docker();

            // 提取业务参数
            String modelType = (String) body.get(RepoCompConstant.filed_model_type);

            // 简单验证
            if (MethodUtils.hasEmpty(modelType)) {
                throw new ServiceException("参数不能为空: modelType");
            }

            this.periodTasksScheduler.insertPeriodTask(new RepoScanStatusTask(this.installService, this.installStatus, this.cacheService, this.pathNameService, this.logger, modelType));
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/install")
    public AjaxResult installPackFile(@RequestBody Map<String, Object> body) {
        try {
            this.edgeService.disable4Docker();

            // 提取业务参数
            String modelType = (String) body.get(RepoCompConstant.filed_model_type);
            String modelName = (String) body.get(RepoCompConstant.filed_model_name);
            String modelVersion = (String) body.get(RepoCompConstant.filed_model_version);
            String version = (String) body.get(RepoCompConstant.filed_version);
            String stage = (String) body.get(RepoCompConstant.filed_stage);
            String component = (String) body.get(RepoCompConstant.filed_component);

            // 简单验证
            if (MethodUtils.hasEmpty(modelType, modelName, modelVersion, version, stage, component)) {
                throw new ServiceException("参数不能为空: modelType, modelName, modelVersion, version, stage, component");
            }

            // 创建所属的组件实体
            this.installService.insertRepoCompEntity(modelType, modelName, modelVersion);

            // 安装文件组件
            this.installService.installFile(modelType, modelName, modelVersion, version, stage, component);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/delete")
    public AjaxResult deletePackageFile(@RequestBody Map<String, Object> body) {
        try {
            this.edgeService.disable4Docker();

            // 提取业务参数
            List<Map<String, Object>> list = (List<Map<String, Object>>) body.get("list");

            // 简单验证
            if (MethodUtils.hasEmpty(list)) {
                throw new ServiceException("参数不能为空: list");
            }

            for (Map<String, Object> map : list) {
                // 提取业务参数
                String modelType = (String) map.get(RepoCompConstant.filed_model_type);
                String modelName = (String) map.get(RepoCompConstant.filed_model_name);
                String modelVersion = (String) map.get(RepoCompConstant.filed_model_version);
                String version = (String) map.get(RepoCompConstant.filed_version);
                String stage = (String) map.get(RepoCompConstant.filed_stage);
                String component = (String) map.get(RepoCompConstant.filed_component);

                // 简单验证
                if (MethodUtils.hasEmpty(modelType, modelName, modelVersion, version, stage, component)) {
                    throw new ServiceException("参数不能为空: modelType, modelName, modelVersion, version, stage, component");
                }

                this.installService.deletePackageFile(modelType, modelName, modelVersion, version, stage, component);
            }

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
