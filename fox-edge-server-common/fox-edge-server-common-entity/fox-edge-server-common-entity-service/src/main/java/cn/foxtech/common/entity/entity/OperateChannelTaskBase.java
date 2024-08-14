/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.entity;

import cn.foxtech.common.utils.number.NumberUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OperateChannelTaskBase extends BaseEntity {
    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 设备名称
     */
    private String channelName;
    /**
     * 设备类型名
     */
    private String channelType;

    /**
     * 发送模式
     */
    private String sendMode;

    /**
     * 通信超时
     */
    private Integer timeout;

    /**
     * 业务Key：这个可能不是唯一的，不要用它查找唯一性数据，可以用它来筛选数据
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        List<Object> list = new ArrayList<>();
        list.add(this.taskName);


        return list;
    }

    /**
     * 查询过滤器
     *
     * @return 过滤器
     */
    public Object makeWrapperKey() {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_name", this.taskName);

        return queryWrapper;
    }

    /**
     * 获取业务值
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = new ArrayList<>();
        list.add(this.channelName);
        list.add(this.channelType);
        list.add(this.sendMode);
        list.add(this.timeout);
        return list;
    }

    public void bind(OperateChannelTaskBase other) {
        this.taskName = other.taskName;
        this.channelName = other.channelName;
        this.channelType = other.channelType;
        this.sendMode = other.sendMode;
        this.timeout = other.timeout;


        super.bind(other);
    }

    @Override
    public void bind(Map<String, Object> map) {
        super.bind(map);

        this.taskName = (String) map.get("taskName");
        this.channelName = (String) map.get("channelName");
        this.channelType = (String) map.get("channelType");
        this.sendMode = (String) map.get("sendMode");
        this.timeout = NumberUtils.makeInteger(map.get("timeout"));
    }
}
