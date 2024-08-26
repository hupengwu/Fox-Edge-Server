/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.manager;

import cn.foxtech.common.domain.constant.RedisStatusConstant;
import cn.foxtech.common.utils.redis.list.RedisListService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RedisConsoleService extends RedisListService {
    @Getter
    private final String key = "fox.edge.service.console.public";

    @Value("${spring.fox-service.service.type}")
    private String foxServiceType = "undefinedServiceType";

    @Value("${spring.fox-service.service.name}")
    private String foxServiceName = "undefinedServiceName";

    public void error(String value) {
        this.out("ERROR", value);
    }

    public void info(String value) {
        this.out("INFO", value);
    }

    public void warn(String value) {
        this.out("WARN", value);
    }

    public void debug(String value) {
        this.out("DEBUG", value);
    }


    private void out(String level, Object value) {
        // 保存到redis
        super.push(this.build(level, value, System.currentTimeMillis()));
    }

    private Map<String, Object> build(String level, Object value, Long time) {
        // 调用者的堆栈深度
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement stackTraceElement = stackTraceElements[4];

        Map<String, Object> map = new HashMap<>();
        map.put(RedisStatusConstant.field_service_type, this.foxServiceType);
        map.put(RedisStatusConstant.field_service_name, this.foxServiceName);
        map.put("createTime", time);
        map.put("level", level);
        map.put("value", value);
        map.put("stack", stackTraceElement.getClassName() + ":" + stackTraceElement.getLineNumber() + "   " + stackTraceElement.getMethodName() + "()");

        return map;
    }
}
