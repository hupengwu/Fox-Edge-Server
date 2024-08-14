/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.core.worker;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.constants.FoxEdgeConstant;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.core.method.FoxEdgeMethodTemplate;
import cn.foxtech.device.protocol.v1.core.method.FoxEdgeReportMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoxEdgeReportWorker {
    /**
     * 对数据进行分析解码
     *
     * @param deviceType 设备类型
     * @param recv       待解码的上报数据
     * @param params     设备的配置参数
     * @return 数据
     * @throws ProtocolException 解码失败
     */
    public static Map<String, Object> decode(String manufacturer, String deviceType, Object recv, Map<String, Object> params) throws ProtocolException {
        // 根据设备类型查找解码器集合
        FoxEdgeMethodTemplate template = FoxEdgeMethodTemplate.inst();
        Map<String, Object> methodPairs = template.getReportMethod(manufacturer, deviceType);
        if (methodPairs == null) {
            throw new ProtocolException("找不到对应设备类型的解码器：" + manufacturer + ":" + deviceType);
        }

        for (Map.Entry<String, Object> entry : methodPairs.entrySet()) {
            Map<String, Object> methodMap = (Map<String, Object>) entry.getValue();

            // 根据操作名称，获得对应的编码/解码函数
            FoxEdgeReportMethod methodPair = (FoxEdgeReportMethod) methodMap.get("method");


            try {
                // 将解码结果，根据模式，用各自的字段带回
                if (FoxEdgeOperate.record.equals(methodPair.getMode())) {
                    // 记录格式
                    List<Map<String, Object>> values = (List<Map<String, Object>>) methodPair.getDecoderMethod().invoke(null, recv, params);

                    Map<String, Object> recordValue = new HashMap<>();
                    recordValue.put(FoxEdgeOperate.record, values);

                    Map<String, Object> result = new HashMap<>();
                    result.put(FoxEdgeConstant.OPERATE_NAME_TAG, methodPair.getName());
                    result.put(FoxEdgeConstant.DATA_TAG, recordValue);

                    return result;
                } else {
                    // 状态格式
                    Map<String, Object> values = (Map<String, Object>) methodPair.getDecoderMethod().invoke(null, recv, params);

                    Map<String, Object> statusValue = new HashMap<>();
                    statusValue.put(FoxEdgeOperate.status, values);

                    Map<String, Object> result = new HashMap<>();
                    result.put(FoxEdgeConstant.OPERATE_NAME_TAG, methodPair.getName());
                    result.put(FoxEdgeConstant.DATA_TAG, statusValue);
                    return result;
                }
            } catch (Exception e) {
                continue;
            }
        }

        throw new ProtocolException("找不到对应设备类型的解码器：" + manufacturer + ":" + deviceType);
    }
}
