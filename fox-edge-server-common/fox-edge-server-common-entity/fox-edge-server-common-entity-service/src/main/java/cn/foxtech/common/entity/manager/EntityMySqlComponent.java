/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.manager;

import cn.foxtech.common.entity.service.mybatis.BaseEntityService;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据库部件
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class EntityMySqlComponent {
    private final Map<String, BaseEntityService> dBService = new HashMap<>();

    public BaseEntityService getEntityServiceBySimpleName(String simpleName) {
        return dBService.get(simpleName);
    }

    public <T> BaseEntityService getEntityService(Class<T> clazz) {
        return dBService.get(clazz.getSimpleName());
    }
}
