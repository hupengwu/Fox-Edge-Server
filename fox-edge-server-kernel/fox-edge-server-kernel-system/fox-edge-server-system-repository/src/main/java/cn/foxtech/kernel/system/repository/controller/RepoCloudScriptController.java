package cn.foxtech.kernel.system.repository.controller;

import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.kernel.system.repository.service.RepoCloudScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/kernel/manager/repository/script")
public class RepoCloudScriptController {
    /**
     * 仓库服务
     */
    @Autowired
    private RepoCloudScriptService cloudScriptService;

    @PostMapping("/page")
    public Map<String, Object> selectCompPageList(@RequestBody Map<String, Object> body) {
        try {
            Map<String, Object> result = this.cloudScriptService.queryCloudCompList(body);
            return result;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/version/operate/entities")
    public Map<String, Object> selectOperateList(@RequestBody Map<String, Object> body) {
        try {
            Map<String, Object> result = this.cloudScriptService.queryCloudOperateList(body);
            return result;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/version/page")
    public Map<String, Object> selectVersionPageList(@RequestBody Map<String, Object> body) {
        try {
            Map<String, Object> result = this.cloudScriptService.queryCloudVersionList(body);
            return result;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/version/operate/entity")
    public Map<String, Object> getVersionPageList(@RequestBody Map<String, Object> body) {
        try {
            Map<String, Object> result = this.cloudScriptService.queryCloudOperateEntity(body);
            return result;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
