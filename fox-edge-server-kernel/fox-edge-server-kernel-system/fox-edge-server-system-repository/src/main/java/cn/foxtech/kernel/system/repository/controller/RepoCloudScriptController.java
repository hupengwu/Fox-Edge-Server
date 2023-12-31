package cn.foxtech.kernel.system.repository.controller;

import cn.foxtech.common.constant.HttpStatus;
import cn.foxtech.common.entity.constant.RepoCompVOFieldConstant;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.kernel.system.repository.service.RepoCloudRemoteService;
import cn.foxtech.kernel.system.repository.service.RepoLocalCompService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/kernel/manager/repository/script")
public class RepoCloudScriptController {
    /**
     * 仓库服务
     */
    @Autowired
    private RepoCloudRemoteService cloudScriptService;

    @Autowired
    private RepoLocalCompService compService;

    @PostMapping("/page")
    public Map<String, Object> selectCompPageList(@RequestBody Map<String, Object> body) {
        try {
            Map<String, Object> result = this.cloudScriptService.queryCloudCompScriptPage(body);
            return result;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/version/operate/entities")
    public Map<String, Object> selectOperateList(@RequestBody Map<String, Object> body) {
        try {
            Map<String, Object> result = this.cloudScriptService.queryCloudScriptOperateList(body);
            return result;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/version/install")
    public Map<String, Object> installVersionEntity(@RequestBody Map<String, Object> body) {
        try {
            // 查询数据
            Map<String, Object> result = this.cloudScriptService.queryCloudScriptVersionList(body);

            // 检查状态
            if (!HttpStatus.SUCCESS.equals(result.get(AjaxResult.CODE_TAG))) {
                return result;
            }

            // 提取数据
            List<Map<String, Object>> list = (List<Map<String, Object>>) result.get(AjaxResult.DATA_TAG);
            if (list == null || list.isEmpty()) {
                return result;
            }

            Map<String, Object> data = list.get(0);

            // 安装版本
            this.compService.installVersion(RepoCompVOFieldConstant.value_comp_type_jsp_decoder, data);
            return result;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/version/page")
    public Map<String, Object> selectVersionPageList(@RequestBody Map<String, Object> body) {
        try {
            Map<String, Object> result = this.cloudScriptService.queryCloudScriptVersionPage(body);
            return result;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/version/operate/entity")
    public Map<String, Object> getVersionPageList(@RequestBody Map<String, Object> body) {
        try {
            Map<String, Object> result = this.cloudScriptService.queryCloudScriptOperateEntity(body);
            return result;
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
