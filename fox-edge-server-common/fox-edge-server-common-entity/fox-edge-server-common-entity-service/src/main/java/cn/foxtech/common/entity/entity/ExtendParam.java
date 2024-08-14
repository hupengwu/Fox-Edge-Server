/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ExtendParam {
    /**
     * 绑定的对象列表
     */
    private Set<Object> binds = new HashSet<>();
    /**
     * 扩展的字段列表
     */
    private List<ExtendField> fields = new ArrayList<>();

    public void bind(Map<String, Object> map) {
        this.binds.clear();
        this.binds.addAll((Collection) map.getOrDefault("binds", new HashSet<>()));

        this.fields.clear();
        Collection fields = (Collection) map.getOrDefault("fields", new HashSet<>());
        for (Object field : fields) {
            if (field instanceof ExtendField) {
                this.fields.add((ExtendField) field);
                continue;
            }
            if (field instanceof Map) {
                ExtendField value = new ExtendField();
                value.bind((Map) field);
                this.fields.add(value);
                continue;
            }

        }
    }
}
