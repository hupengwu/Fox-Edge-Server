package cn.foxtech.device.protocol.v1.core.worker;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.channel.FoxEdgeChannelService;
import cn.foxtech.device.protocol.v1.core.enums.WorkerLoggerType;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.core.method.FoxEdgeExchangeMethod;
import cn.foxtech.device.protocol.v1.core.method.FoxEdgeMethodTemplate;

import javax.naming.CommunicationException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FoxEdgeExchangeWorker {
    /**
     * 对设备进行操作
     *
     * @param deviceName     设备名称
     * @param manufacturer   生产厂商
     * @param deviceType     设备类型
     * @param operateName    操作名称
     * @param params         参数表
     * @param timeout        通信超时
     * @param channelService 通道服务
     * @return 结果集合
     * @throws ProtocolException      业务异常，比如找不到解码器
     * @throws CommunicationException 通信失败产生的异常
     */
    public static Map<String, Object> exchange(String deviceName, String manufacturer, String deviceType, String operateName, Map<String, Object> params, int timeout, FoxEdgeChannelService channelService) throws ProtocolException, CommunicationException {
        try {
            // 根据设备类型查找解码器集合
            Map<String, Object> methodPairs = FoxEdgeMethodTemplate.inst().getExchangeMethod(manufacturer, deviceType);
            if (methodPairs == null) {
                throw new ProtocolException("找不到对应设备类型的解码器：" + manufacturer + ":" + deviceType);
            }

            Map<String, FoxEdgeExchangeMethod> methodMap = (Map<String, FoxEdgeExchangeMethod>) methodPairs.get(operateName);
            if (methodMap == null) {
                throw new ProtocolException("找不到对应操作名称的编码/解码函数：" + operateName);
            }

            // 根据操作名称，获得对应的编码/解码函数
            FoxEdgeExchangeMethod methodPair = methodMap.get("method");
            if (methodPair == null) {
                throw new ProtocolException("数据结构异常!");
            }

            // 主从半双工，必须同时有编码和解码函数
            if (methodPair.getEncoderMethod() == null || methodPair.getDecoderMethod() == null) {
                throw new ProtocolException("找不到对应操作名称的编码/解码函数：" + operateName);
            }

            if (timeout <= 0) {
                timeout = methodPair.getTimeout();
            }

            Object send;
            Object recv;

            // 编码
            send = methodPair.getEncoderMethod().invoke(null, params);

            // 打印日志
            channelService.printLogger(deviceName, manufacturer, deviceType, WorkerLoggerType.send, send);

            try {
                recv = channelService.exchange(deviceName, deviceType, send, timeout);
            } catch (Exception e) {
                throw new CommunicationException(e.getMessage());
            }

            // 打印日志
            channelService.printLogger(deviceName, manufacturer, deviceType, WorkerLoggerType.recv, recv);

            // 将解码结果，根据模式，用各自的字段带回
            if (FoxEdgeOperate.record.equals(methodPair.getMode())) {
                // 记录格式
                List<Map<String, Object>> values = (List<Map<String, Object>>) methodPair.getDecoderMethod().invoke(null, recv, params);

                Map<String, Object> result = new HashMap<>();
                result.put(FoxEdgeOperate.record, values);
                return result;
            } else if (FoxEdgeOperate.result.equals(methodPair.getMode())) {
                // 结果格式
                Map<String, Object> values = (Map<String, Object>) methodPair.getDecoderMethod().invoke(null, recv, params);

                Map<String, Object> result = new HashMap<>();
                result.put(FoxEdgeOperate.result, values);
                return result;

            } else {
                // 状态格式
                Map<String, Object> values = (Map<String, Object>) methodPair.getDecoderMethod().invoke(null, recv, params);

                Map<String, Object> result = new HashMap<>();
                result.put(FoxEdgeOperate.status, values);
                return result;
            }

        } catch (InvocationTargetException ie) {
            throw new ProtocolException(ie.getTargetException().getMessage());
        } catch (CommunicationException ce) {
            throw new CommunicationException(ce.getMessage());
        } catch (Exception e) {
            throw new ProtocolException(e.getMessage());
        }
    }
}
