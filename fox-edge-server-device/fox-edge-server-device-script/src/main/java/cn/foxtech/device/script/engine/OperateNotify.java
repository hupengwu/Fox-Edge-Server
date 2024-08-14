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
 
package cn.foxtech.device.script.engine;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.entity.service.redis.BaseConsumerTypeNotify;
import cn.foxtech.common.utils.method.MethodUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import java.util.Map;
import java.util.Set;


/**
 * OperateEntity变更通知
 * 说明：OperateEntity发生修改的时候，为ScriptEngine重新装载对应的JavaScript脚本
 */
@Component
public class OperateNotify implements BaseConsumerTypeNotify {
    private static final Logger logger = Logger.getLogger(OperateNotify.class);

    @Autowired
    private RedisConsoleService console;

    @Autowired
    private ScriptEngineService engineService;

    /**
     * 通知变更
     *
     * @param addMap 增加
     * @param delSet 删除
     * @param mdyMap 修改
     */
    @Override
    public void notify(String entityType, long updateTime, Map<String, BaseEntity> addMap, Set<String> delSet, Map<String, BaseEntity> mdyMap) {
        for (String key : addMap.keySet()) {
            this.rebindScriptEngine(addMap.get(key));
        }
        for (String key : mdyMap.keySet()) {
            this.rebindScriptEngine(mdyMap.get(key));
        }
        for (String key : delSet) {

        }
    }

    /**
     * 绑定JavaScript引擎：使用OperateEntity中的JavaScript脚本初始化ScriptEngine
     *
     * @param entity 实体
     */
    public void rebindScriptEngine(BaseEntity entity) {
        try {
            OperateEntity operateEntity = (OperateEntity) entity;

            // 检测：是否为JavaScript引擎
            if (!operateEntity.getEngineType().equals("JavaScript")) {
                return;
            }
            // 检测：是否为设备类型
            if (!operateEntity.getServiceType().equals("device")) {
                return;
            }

            // 取出该设备的引擎
            ScriptEngine engine = this.engineService.getScriptEngine(operateEntity.getManufacturer(), operateEntity.getDeviceType());


            // 为引擎装载JSP脚本
            if (operateEntity.getOperateMode().equals("include")) {
                Map<String, Object> include = (Map<String, Object>) operateEntity.getEngineParam().get("include");
                if (!MethodUtils.hasEmpty(include)) {
                    String jsp = (String) include.get("code");
                    engine.eval(jsp);
                }
            } else {
                Map<String, Object> decode = (Map<String, Object>) operateEntity.getEngineParam().get("decode");
                if (!MethodUtils.hasEmpty(decode)) {
                    String jsp = (String) decode.get("code");
                    engine.eval(jsp);
                }

                Map<String, Object> encode = (Map<String, Object>) operateEntity.getEngineParam().get("encode");
                if (!MethodUtils.hasEmpty(encode)) {
                    String jsp = (String) encode.get("code");
                    engine.eval(jsp);
                }
            }

        } catch (Exception e) {
            String message = "初始化脚本引擎异常：" + entity.makeServiceKey() + "; " + e.getMessage();
            logger.error(message);
            this.console.error(message);
        }


    }
}
