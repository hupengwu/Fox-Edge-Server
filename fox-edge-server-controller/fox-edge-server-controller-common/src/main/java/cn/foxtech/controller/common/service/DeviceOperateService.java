package cn.foxtech.controller.common.service;

import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import cn.foxtech.controller.common.redislist.RedisListDevicePublicRequest;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.device.domain.vo.TaskRequestVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * 对设备进行操作
 */
@Component
public class DeviceOperateService {
    /**
     * 通道服务+设备服务自身额外带来的时延
     */
    private static final int timeout_device = 2000;
    /**
     * 实体管理
     */
    @Autowired
    EntityManageService entityService;

    @Autowired
    private RedisListDevicePublicRequest publisher;
    /**
     * 模块名称
     */
    @Value("${spring.redis_topic.controller_model}")
    private String controller_model = "system_controller";

    /**
     * 对设备执行操作
     *
     * @param requestVO 请求报文
     * @return 返回报文
     * @throws InterruptedException 操作异常
     * @throws ServiceException     业务异常
     * @throws TimeoutException     超时异常
     */
    public TaskRespondVO execute(TaskRequestVO requestVO) throws InterruptedException, ServiceException, TimeoutException {
        Integer timeout = requestVO.getTimeout();


        //填写UID，从众多方便返回的数据中，识别出来对应的返回报文
        String key = requestVO.getUuid();
        if (key == null || key.isEmpty()) {
            key = UUID.randomUUID().toString().replace("-", "");
            requestVO.setUuid(key);
        }

        // 重置信号
        SyncFlagObjectMap.inst().reset(key);

        // 发送数据
        this.publisher.push(requestVO);

        // 等待消息的到达：根据动态key
        TaskRespondVO respondVO = (TaskRespondVO) SyncFlagObjectMap.inst().waitDynamic(key, timeout + timeout_device);
        if (respondVO == null) {
            return TaskRespondVO.error("Device Service 响应超时！");
        }

        return respondVO;
    }
}
