package cn.foxtech.common.entity.manager;

import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库部件
 */
@Data
@Component
public class EntityMySqlComponent {
    private final Map<String, BaseEntityService> dBService = new HashMap<>();

    public BaseEntityService getEntityServiceBySimpleName(String simpleName) {
        return dBService.get(simpleName);
    }

    public <T> BaseEntityService getEntityService(Class<T> clazz) {
        return dBService.get(clazz.getSimpleName());
    }
}
