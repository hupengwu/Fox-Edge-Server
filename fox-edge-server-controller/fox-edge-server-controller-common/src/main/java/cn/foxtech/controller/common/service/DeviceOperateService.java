package cn.foxtech.controller.common.service;

import cn.foxtech.common.rpc.redis.device.client.RedisListDeviceClient;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.device.domain.vo.TaskRequestVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * 对设备进行操作
 */
@Component
public class DeviceOperateService {
    /**
     * 通道服务+设备服务自身额外带来的时延
     */
    private static final int timeout_device = 2000;


    @Autowired
    private RedisListDeviceClient deviceClient;


    /**
     * 对设备执行操作
     *
     * @param requestVO 请求报文
     * @return 返回报文
     * @throws ServiceException 业务异常
     */
    public TaskRespondVO execute(TaskRequestVO requestVO) throws ServiceException {
        Integer timeout = requestVO.getTimeout();

        //填写UID，从众多方便返回的数据中，识别出来对应的返回报文
        String key = requestVO.getUuid();
        if (key == null || key.isEmpty()) {
            key = UUID.randomUUID().toString().replace("-", "");
            requestVO.setUuid(key);
        }

        // 发出操作请求
        this.deviceClient.pushDeviceRequest(requestVO);

        // 等待消息的到达：根据动态key
        TaskRespondVO respondVO = this.deviceClient.getDeviceRespond(key, timeout + timeout_device);
        if (respondVO == null) {
            return TaskRespondVO.error("Device Service 响应超时！");
        }

        return respondVO;
    }
}
