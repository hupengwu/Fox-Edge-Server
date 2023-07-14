package cn.foxtech.common.entity.manager;

import cn.foxtech.common.domain.constant.RedisStatusConstant;
import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RedisConsoleService extends RedisLoggerService {
    @Value("${spring.fox-service.service.type}")
    private String foxServiceType = "undefinedServiceType";

    @Value("${spring.fox-service.service.name}")
    private String foxServiceName = "undefinedServiceName";

    public RedisConsoleService() {
        this.setKey("fox.edge.service.console.public");
    }

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
        //  转换数据结构
        List<Map<String, Object>> saveList = new ArrayList<>();
        saveList.add(this.build(level, value, System.currentTimeMillis()));

        // 保存到redis
        super.out(saveList);
    }

    private Map<String, Object> build(String level, Object value, Long time) {
        Map<String, Object> map = new HashMap<>();
        map.put(RedisStatusConstant.field_service_type, this.foxServiceType);
        map.put(RedisStatusConstant.field_service_name, this.foxServiceName);
        map.put("createTime", time);
        map.put("level", level);
        map.put("value", value);

        return map;
    }
}
