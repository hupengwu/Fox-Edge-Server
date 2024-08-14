/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@TableName("tb_operate_monitor_task")
public class OperateMonitorTaskPo extends OperateMonitorTaskBase {
    /**
     * JSON参数
     */
    private String templateParam;

    /**
     * JSON参数
     */
    private String deviceIds;

    /**
     * JSON参数
     */
    private String taskParam;


    /**
     * 业务Key：这个可能不是唯一的，不要用它查找唯一性数据，可以用它来筛选数据
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        return super.makeServiceKeyList();
    }

    /**
     * 查询过滤器
     *
     * @return 过滤器
     */
    public Object makeWrapperKey() {
        return super.makeWrapperKey();
    }

    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueListList() {
        List<Object> list = this.makeServiceValueList();
        list.add(this.templateParam);
        list.add(this.deviceIds);
        list.add(this.taskParam);


        return list;
    }
}
