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
 
package cn.foxtech.demo.service.mapper.demotask;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.demo.service.entity.DemoTaskEntity;
import cn.foxtech.demo.service.entity.DemoTaskPo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DeviceConfigPo是数据库格式的对象，DeviceConfigEntity是内存格式的对象，两者需要进行转换
 */
public class DemoTaskMaker {
    /**
     * PO转Entity
     *
     * @param deviceList
     * @return
     */
    public static List<BaseEntity> makePoList2EntityList(List<BaseEntity> deviceList) {
        List<BaseEntity> deviceConfigList = new ArrayList<>();
        for (BaseEntity entity : deviceList) {
            DemoTaskPo po = (DemoTaskPo) entity;

            DemoTaskEntity config = DemoTaskMaker.makePo2Entity(po);
            deviceConfigList.add(config);
        }

        return deviceConfigList;
    }

    public static DemoTaskPo makeEntity2Po(DemoTaskEntity entity) {
        DemoTaskPo result = new DemoTaskPo();
        result.bind(entity);

        result.setTaskParam(JsonUtils.buildJsonWithoutException(entity.getTaskParam()));
        result.setDeviceIds(JsonUtils.buildJsonWithoutException(entity.getDeviceIds()));
        result.setObjectIds(JsonUtils.buildJsonWithoutException(entity.getObjectIds()));
        return result;
    }

    public static DemoTaskEntity makePo2Entity(DemoTaskPo entity) {
        DemoTaskEntity result = new DemoTaskEntity();
        result.bind(entity);

        try {
            if (entity.getDeviceIds().startsWith("[") && entity.getDeviceIds().endsWith("]")) {
                List<Object> params = JsonUtils.buildObject(entity.getDeviceIds(), List.class);
                if (params != null) {
                    result.setDeviceIds(params);
                } else {
                    System.out.println("设备配置参数转换Json对象失败：" + entity.getDeviceIds() + ":" + entity.getTaskParam());
                }
            }
            if (entity.getObjectIds().startsWith("[") && entity.getObjectIds().endsWith("]")) {
                List<String> params = JsonUtils.buildObject(entity.getObjectIds(), List.class);
                if (params != null) {
                    result.setObjectIds(params);
                } else {
                    System.out.println("设备配置参数转换Json对象失败：" + entity.getObjectIds() + ":" + entity.getTaskParam());
                }
            }
            if (entity.getTaskParam().startsWith("{") && entity.getTaskParam().endsWith("}")) {
                Map<String, Object> params = JsonUtils.buildObject(entity.getTaskParam(), Map.class);
                if (params != null) {
                    result.setTaskParam(params);
                } else {
                    System.out.println("设备配置参数转换Json对象失败：" + entity.getTaskParam() + ":" + entity.getTaskParam());
                }
            }


        } catch (Exception e) {
            System.out.println("设备配置参数转换Json对象失败：" + entity.getTaskName() + ":" + entity.getTaskParam());
            e.printStackTrace();
        }

        return result;
    }
}
