package cn.foxtech.kernel.system.repository.controller;

import cn.foxtech.common.constant.HttpStatus;
import cn.foxtech.common.entity.constant.RepoCompVOFieldConstant;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.kernel.system.common.service.LocalSystemConfService;
import cn.foxtech.kernel.system.repository.service.RepoCloudScriptService;
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
    private RepoCloudScriptService cloudScriptService;

    @Autowired
    private RepoLocalCompService compService;

    @Autowired
    private LocalSystemConfService systemConfService;

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

    @PostMapping("/version/install")
    public Map<String, Object> installVersionEntity(@RequestBody Map<String, Object> body) {
        try {
            // 查询数据
            Map<String, Object> result = this.cloudScriptService.queryCloudVersionList(body);

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
            Map<String, Object> result = this.cloudScriptService.queryCloudVersionPage(body);
            Object data = result.get(AjaxResult.DATA_TAG);
            if (data != null && data instanceof Map && ((Map<String, Object>) data).get("list") != null) {
                List<Map<String, Object>> list = (List<Map<String, Object>>) ((Map<String, Object>) data).get("list");

                // 敏感词的处理
                List<Map<String, Object>> cloneList = JsonUtils.clone(list);
                this.systemConfService.sensitiveWordsString(cloneList, "replace", "author", "author");
            }


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
