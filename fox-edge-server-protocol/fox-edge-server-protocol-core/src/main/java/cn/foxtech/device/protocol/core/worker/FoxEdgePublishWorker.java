package cn.foxtech.device.protocol.core.worker;

import cn.foxtech.device.protocol.core.method.FoxEdgeMethodTemplate;
import cn.foxtech.device.protocol.core.method.FoxEdgePublishMethod;
import cn.foxtech.device.protocol.core.channel.FoxEdgeChannelService;
import cn.foxtech.device.protocol.core.exception.ProtocolException;

import javax.naming.CommunicationException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

public class FoxEdgePublishWorker {
    /**
     * 对设备进行操作
     *
     * @param deviceName     设备名称
     * @param deviceType     设备类型
     * @param operateName    操作名称
     * @param params         参数表
     * @param timeout        通信超时
     * @param channelService 通道服务
     * @throws ProtocolException      业务异常，比如找不到解码器
     * @throws CommunicationException 通信异常
     */
    public static void publish(String deviceName, String deviceType, String operateName, Map<String, Object> params, int timeout, FoxEdgeChannelService channelService) throws ProtocolException, CommunicationException {
        try {
            // 根据设备类型查找编码器集合
            Map<String, FoxEdgePublishMethod> methodPairs = FoxEdgeMethodTemplate.inst().getPublishMethod().get(deviceType);
            if (methodPairs == null) {
                throw new ProtocolException("找不到对应设备类型的编码器：" + deviceType);
            }

            // 根据操作名称，获得对应的编码/解码函数
            FoxEdgePublishMethod methodPair = methodPairs.get(operateName);
            if (methodPair == null) {
                throw new ProtocolException("找不到对应操作名称的编码函数：" + operateName);
            }

            // 主从半双工，必须同时有编码和解码函数
            if (methodPair.getEncoderMethod() == null) {
                throw new ProtocolException("找不到对应操作名称的编码函数：" + operateName);
            }

            if (timeout <= 0) {
                timeout = methodPair.getTimeout();
            }

            // 编码
            Object send = methodPair.getEncoderMethod().invoke(null, params);

            try {
                // 向设备发布数据
                channelService.publish(deviceName, deviceType, send, timeout);
            } catch (Exception e) {
                throw new CommunicationException(e.getMessage());
            }
        } catch (InvocationTargetException ie) {
            throw new ProtocolException(ie.getTargetException().getMessage());
        } catch (Exception e) {
            throw new ProtocolException(e.getMessage());
        }
    }
}
