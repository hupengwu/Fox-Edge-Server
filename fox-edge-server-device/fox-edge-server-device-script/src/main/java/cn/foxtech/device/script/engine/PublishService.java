/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.script.engine;

import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.device.protocol.v1.core.channel.FoxEdgeChannelService;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.naming.CommunicationException;
import javax.script.ScriptEngine;
import java.util.HashMap;
import java.util.Map;

@Component
public class PublishService {
    @Autowired
    private ScriptEngineService engineService;

    @Autowired
    private ScriptEngineOperator engineOperator;

    @Autowired
    private ScriptEngineModel engineModel;

    public void publish(String deviceName, String manufacturer, String deviceType, OperateEntity operateEntity, Map<String, Object> params, int timeout, FoxEdgeChannelService channelService) throws ProtocolException {
        try {
            // 取出ScriptEngine
            ScriptEngine engine = this.engineService.getScriptEngine(manufacturer, deviceType);

            // 取出：设备模型信息
            String modelName = (String) operateEntity.getEngineParam().getOrDefault("modelName", "");

            // 取出编码/解码信息
            Map<String, Object> encode = (Map<String, Object>) operateEntity.getEngineParam().getOrDefault("encode", new HashMap<>());
            if (MethodUtils.hasEmpty(encode)) {
                throw new ProtocolException("找不到对应操作名称的编码函数：" + operateEntity.getOperateName());
            }

            // 编码脚本
            String encodeMain = (String) encode.getOrDefault("main", "");
            String encodeScript = (String) encode.getOrDefault("code", "");
            if (MethodUtils.hasEmpty(encodeMain, encodeScript)) {
                throw new ProtocolException("找不到对应操作名称的编码函数：" + operateEntity.getOperateName());
            }


            Object send;


            try {
                // 为ScriptEngine填入设备模型
                this.engineModel.setEnvDeviceModel(manufacturer, deviceType, modelName, params);

                // 为ScriptEngine填入全局变量
                this.engineOperator.setSendEnvValue(engine, params);

                // 数据编码
                send = this.engineOperator.encode(engine, encodeMain, encodeScript);
            } catch (Exception e) {
                throw new ProtocolException("编码错误：" + e.getMessage());
            }


            try {
                channelService.publish(deviceName, deviceType, send, timeout);
            } catch (Exception e) {
                throw new CommunicationException(e.getMessage());
            }

        } catch (Exception e) {
            throw new ProtocolException(e.getMessage());
        }
    }
}
