/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 这是设备服务使用的OperateMethodEntity
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class OperateMethodEntity extends OperateMethodBase {
    /**
     * JSP引擎的配置
     */
    private Map<String, Object> engineParam = new HashMap<>();

    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = super.makeServiceValueList();
        list.add(this.engineParam);
        return list;
    }

    public void bind(OperateMethodEntity other) {
        super.bind(other);
    }

    @Override
    public void bind(Map<String, Object> map) {
        super.bind(map);

        this.engineParam = (Map<String, Object>) map.getOrDefault("engineParam", new HashMap<>());
    }

    @Override
    public BaseEntity build(Map<String, Object> map) {
        try {
            if (map == null || map.isEmpty()) {
                return null;
            }

            OperateMethodEntity entity = new OperateMethodEntity();
            entity.bind(map);

            return entity;
        } catch (Exception e) {
            return null;
        }
    }
}
