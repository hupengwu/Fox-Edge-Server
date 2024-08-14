/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 *
 *     This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * --------------------------------------------------------------------------- */
 
package cn.foxtech.common.entity.entity;


import cn.foxtech.common.utils.MapUtils;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@TableName("tb_operate_monitor_task")
public class OperateMonitorTaskEntity extends OperateMonitorTaskBase {
    /**
     * JSON参数
     */
    private List<Map<String, Object>> templateParam = new ArrayList<>();
    /**
     * JSON参数
     */
    private List<Object> deviceIds = new ArrayList<>();

    /**
     * JSON参数
     */
    private Map<String, Object> taskParam = new HashMap<>();


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
    public List<Object> makeServiceValueList() {
        List<Object> list = super.makeServiceValueList();
        list.add(this.templateParam);
        list.add(this.deviceIds);
        list.add(this.taskParam);

        return list;
    }

    @Override
    public void bind(Map<String, Object> map) {
        super.bind(map);

        this.templateParam = (List<Map<String, Object>>) map.getOrDefault("templateParam", new ArrayList<>());
        this.deviceIds = (List<Object>) map.getOrDefault("deviceIds", new ArrayList<>());
        this.taskParam = (Map<String, Object>) map.getOrDefault("taskParam", new HashMap<>());
    }

    @Override
    public BaseEntity build(Map<String, Object> map) {
        try {
            if (map == null || map.isEmpty()) {
                return null;
            }

            OperateMonitorTaskEntity entity = new OperateMonitorTaskEntity();
            entity.bind(map);

            return entity;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 填充缺省值
     */
    public void setDefaultValue() {
        MapUtils.setDefaultValue(this.taskParam, "timeMode", String.class, "interval");
        MapUtils.setDefaultValue(this.taskParam, "timeUnit", String.class, "second");
        MapUtils.setDefaultValue(this.taskParam, "timeInterval", Integer.class, 1);
    }

}
