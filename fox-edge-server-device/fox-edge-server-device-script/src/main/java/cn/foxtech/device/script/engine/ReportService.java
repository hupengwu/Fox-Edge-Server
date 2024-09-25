/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.script.engine;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.entity.manager.InitialConfigService;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.script.ScriptEngine;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ReportService {
    @Autowired
    private InitialConfigService configService;

    @Autowired
    private ScriptEngineService engineService;

    @Autowired
    private ScriptEngineOperator engineOperator;


    public Map<String, Object> decode(String manufacturer, String deviceType, List<BaseEntity> jspReportList, Object recv, Map<String, Object> params) throws ProtocolException {
        // 打印日志
        this.printLogger("接收到通道上报数据：\n" + recv);

        // 取出ScriptEngine
        ScriptEngine engine = this.engineService.getScriptEngine(manufacturer, deviceType);

        // 为ScriptEngine填入全局变量
        this.engineOperator.setRecvEnvValue(engine, recv, params);

        // 逐个解码器进行测试
        for (BaseEntity entity : jspReportList) {
            OperateEntity operateEntity = (OperateEntity) entity;

            try {
                // 取出编码/解码信息
                Map<String, Object> decode = (Map<String, Object>) operateEntity.getEngineParam().getOrDefault("decode", new HashMap<>());
                if (MethodUtils.hasEmpty(decode)) {
                    throw new ProtocolException("找不到对应操作名称的解码函数：" + operateEntity.getOperateName());
                }

                // 解码脚本
                String decodeMain = (String) decode.getOrDefault("main", "");
                String decodeScript = (String) decode.getOrDefault("code", "");
                if (MethodUtils.hasEmpty(decodeMain, decodeScript)) {
                    throw new ProtocolException("找不到对应操作名称的解码函数：" + operateEntity.getOperateName());
                }


                // 将解码结果，根据模式，用各自的字段带回
                if (FoxEdgeOperate.record.equals(operateEntity.getDataType())) {
                    return this.engineOperator.decodeRecord(engine, operateEntity.getOperateName(), decodeMain, decodeScript);
                } else if (FoxEdgeOperate.result.equals(operateEntity.getDataType())) {
                    return this.engineOperator.decodeResult(engine, operateEntity.getOperateName(), decodeMain, decodeScript);
                } else {
                    return this.engineOperator.decodeStatus(engine, operateEntity.getOperateName(), decodeMain, decodeScript);
                }
            } catch (Exception e) {
                continue;
            }
        }

        throw new ProtocolException("找不到对应设备类型的解码器：" + manufacturer + ":" + deviceType);
    }

    private void printLogger(Object recv) {
        if (recv == null) {
            return;
        }

        if (!Boolean.TRUE.equals(this.configService.getConfigParam("serverConfig").get("logger"))) {
            return;
        }


        this.configService.getLogger().info(recv.toString());
    }

}
