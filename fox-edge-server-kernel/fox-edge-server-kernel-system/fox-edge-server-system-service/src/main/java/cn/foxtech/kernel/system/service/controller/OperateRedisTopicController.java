package cn.foxtech.kernel.system.service.controller;


import cn.foxtech.core.domain.AjaxResult;
import cn.foxtech.kernel.system.service.service.RedisTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 将restful请求转换为coap请求
 *
 * @author hupengwu
 */
@RestController
@RequestMapping("/kernel/manager/proxy-redis-topic/proxy/redis/topic")
public class OperateRedisTopicController {
    @Autowired
    RedisTopicService redisTopicService;

    @PostMapping("channel")
    public Object executeChannel(@RequestBody Map<String, Object> body) {
        try {
            return this.redisTopicService.executeChannel(body);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("device")
    public Object executeDevice(@RequestBody Map<String, Object> body) {
        try {
            return this.redisTopicService.executeDevice(body);
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }
}
