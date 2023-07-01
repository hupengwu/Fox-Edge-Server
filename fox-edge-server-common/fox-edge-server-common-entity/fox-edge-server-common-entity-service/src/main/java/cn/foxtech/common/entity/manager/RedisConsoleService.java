package cn.foxtech.common.entity.manager;

import cn.foxtech.common.domain.constant.RedisStatusConstant;
import cn.foxtech.common.utils.redis.logger.RedisLoggerService;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RedisConsoleService extends RedisLoggerService {
    private static final Logger logger = Logger.getLogger(RedisConsoleService.class);

    @Value("${spring.fox-service.service.type}")
    private String foxServiceType = "undefinedServiceType";

    @Value("${spring.fox-service.service.name}")
    private String foxServiceName = "undefinedServiceName";

    public RedisConsoleService() {
        this.setKey("fox.edge.service.console.public");
    }

    public void error(Object value) {
        this.out("ERROR", value);
    }
    public void info(Object value) {
        this.out("INFO", value);
    }
    public void warn(Object value) {
        this.out("WARN", value);
    }
    public void debug(Object value) {
        this.out("DEBUG", value);
    }


    private void out(String level, Object value) {
        this.logger(level, value);

        //  转换数据结构
        List<Map<String, Object>> saveList = new ArrayList<>();
        saveList.add(this.build(level, value, System.currentTimeMillis()));

        // 保存到redis
        super.out(saveList);
    }

    private void logger(String level, Object value) {
        if (!(value instanceof String)) {
            return;
        }

        String out = "";

        // 获得父函数的名称
        StackTraceElement[] stackTraceElement = Thread.currentThread().getStackTrace();
        int deep = 4;
        if (stackTraceElement.length > deep) {
            String className = stackTraceElement[deep].getClassName();
            String methodName = stackTraceElement[deep].getMethodName();
            int lineNumber = stackTraceElement[deep].getLineNumber();
            out = "(" + className + ":" + lineNumber + "):" + methodName + "():" + value;
        } else {
            out = (String) value;
        }

        if (Level.toLevel(level).equals(Level.INFO)) {
            logger.info(out);
        } else if (Level.toLevel(level).equals(Level.ERROR)) {
            logger.error(out);
        } else if (Level.toLevel(level).equals(Level.DEBUG)) {
            logger.debug(out);
        } else if (Level.toLevel(level).equals(Level.WARN)) {
            logger.warn(out);
        }
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
