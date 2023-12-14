package cn.foxtech.kernel.system.service.controller;

import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.kernel.common.service.EdgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kernel/manager/environment")
public class EnvironmentController {
    @Autowired
    private EdgeService edgeService;

    @GetMapping("type")
    public AjaxResult queryEnvType() {
        try {
            // 不正确的参数
            return AjaxResult.success("", this.edgeService.getEnvType());
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
