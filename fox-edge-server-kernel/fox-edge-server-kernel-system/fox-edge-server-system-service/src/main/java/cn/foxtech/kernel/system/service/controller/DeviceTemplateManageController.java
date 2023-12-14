package cn.foxtech.kernel.system.service.controller;

import cn.foxtech.common.entity.utils.PageUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.repository.constants.RepoCompConstant;
import cn.foxtech.kernel.system.repository.service.RepoCloudInstallStatus;
import cn.foxtech.kernel.system.repository.service.RepoLocalCsvFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kernel/manager/device/template")
public class DeviceTemplateManageController {
    @Autowired
    private RepoCloudInstallStatus installStatus;

    @Autowired
    private RepoLocalCsvFileService templateService;


    @PostMapping("/page")
    public AjaxResult selectEntityList(@RequestBody Map<String, Object> body) {
        try {
            // 从磁盘中查找所有的模板文件信息
            List<Map<String, Object>> resultList = this.templateService.queryTemplateList();

            return PageUtils.getPageMapList(resultList, body);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/delete")
    public AjaxResult deleteEntityList(@RequestBody Map<String, Object> body) {
        try {
            // 提取业务参数
            List<Map<String, Object>> list = (List<Map<String, Object>>) body.get("list");

            // 简单验证
            if (MethodUtils.hasEmpty(list)) {
                throw new ServiceException("参数不能为空:list");
            }

            for (Map<String, Object> map : list) {
                // 提取业务参数
                String modelName = (String) map.get(RepoCompConstant.filed_model_name);
                String modelVersion = (String) map.get(RepoCompConstant.filed_model_version);
                String version = (String) map.get(RepoCompConstant.filed_version);
                String component = (String) map.get(RepoCompConstant.filed_component);
                String fileName = (String) map.get("fileName");

                // 简单验证
                if (MethodUtils.hasEmpty(modelName, modelVersion, version, component, fileName)) {
                    throw new ServiceException("参数不能为空:modelName, modelVersion,version, component, fileName");
                }

                File file = new File("");
                File delete = new File(file.getAbsolutePath() + "/template/" + modelName + "/" + modelVersion + "/" + version + "/" + fileName);
                delete.delete();
            }

            // 变化后，重新比对跟仓库的状态
            this.installStatus.scanRepositoryStatus(RepoCompConstant.repository_type_template);

            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/download")
    public void downloadEntityList(@RequestBody Map<String, Object> body) {
        try {
            // 提取业务参数
            String modelName = (String) body.get(RepoCompConstant.filed_model_name);
            String modelVersion = (String) body.get(RepoCompConstant.filed_model_version);
            String version = (String) body.get(RepoCompConstant.filed_version);
            String component = (String) body.get(RepoCompConstant.filed_component);
            String fileName = (String) body.get("fileName");

            // 简单验证
            if (MethodUtils.hasEmpty(modelName, modelVersion, version, component, fileName)) {
                throw new ServiceException("参数不能为空:modelName, modelVersion, version, component, fileName");
            }

            HttpServletResponse resp = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();

            File file = new File("");
            File download = new File(file.getAbsolutePath() + "/template/" + modelName + "/" + modelVersion + "/" + version + "/" + fileName);
            if (download.exists()) {
                resp.setContentType("application/x-msdownload");
                resp.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(), StandardCharsets.ISO_8859_1));
                InputStream inputStream = new FileInputStream(download);
                ServletOutputStream ouputStream = resp.getOutputStream();
                byte[] b = new byte[1024];
                int n;
                while ((n = inputStream.read(b)) != -1) {
                    ouputStream.write(b, 0, n);
                }
                ouputStream.close();
                inputStream.close();
            }
        } catch (Exception e) {
            return;
        }
    }
}
