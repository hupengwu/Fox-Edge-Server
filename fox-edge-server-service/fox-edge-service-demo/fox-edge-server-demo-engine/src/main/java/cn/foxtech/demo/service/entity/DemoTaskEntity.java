/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.demo.service.entity;

import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 周期快照任务模板
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DemoTaskEntity extends DemoTaskBase {
    /**
     * 配置集合
     */
    private Map<String, Object> taskParam = new HashMap<>();
    /**
     * 对象列表
     */
    private List<Object> deviceIds = new ArrayList<>();
    /**
     * 对象列表
     */
    private List<String> objectIds = new ArrayList<>();


    /**
     * 获取业务值
     *
     * @return
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = super.makeServiceValueList();
        list.add(this.taskParam);
        list.add(this.deviceIds);
        list.add(this.objectIds);

        return list;
    }
}
