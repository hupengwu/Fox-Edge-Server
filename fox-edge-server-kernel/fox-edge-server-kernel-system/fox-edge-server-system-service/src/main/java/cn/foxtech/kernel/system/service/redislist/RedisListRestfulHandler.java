package cn.foxtech.kernel.system.service.redislist;

import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.kernel.system.common.scheduler.RedisListRestfulScheduler;
import cn.foxtech.kernel.system.service.controller.ChannelManageController;
import cn.foxtech.kernel.system.service.controller.DeviceManageController;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 为各服务提供的Restful操作接口：该接口不进行返回，只进行静默操作
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@Component
public class RedisListRestfulHandler extends cn.foxtech.kernel.system.common.redislist.RedisListRestfulHandler {
    private static final Logger logger = Logger.getLogger(RedisListRestfulScheduler.class);

    @Autowired
    private RedisConsoleService console;

    @Autowired
    private ChannelManageController channelManageController;

    @Autowired
    private DeviceManageController deviceManageController;

    @Override
    public void onMessage(Object message) {
        try {
            RestFulRequestVO respondVO = JsonUtils.buildObject(message, RestFulRequestVO.class);
            if (MethodUtils.hasEmpty(respondVO.getUri(), respondVO.getMethod())) {
                throw new ServiceException("参数缺失：uri, method");
            }

            // 场景1：创建通道的消息
            if ("/kernel/manager/channel/entity".equals(respondVO.getUri()) && "post".equalsIgnoreCase(respondVO.getMethod())) {
                if (MethodUtils.hasNull(respondVO.getData())) {
                    throw new ServiceException("参数缺失：data");
                }

                Map<String, Object> body = JsonUtils.buildObject(respondVO.getData(), Map.class);
                this.channelManageController.insertEntity(body);
                return;
            }

            // 场景1：创建通道的消息
            if ("/kernel/manager/device/entity".equals(respondVO.getUri()) && "post".equalsIgnoreCase(respondVO.getMethod())) {
                if (MethodUtils.hasNull(respondVO.getData())) {
                    throw new ServiceException("参数缺失：data");
                }

                Map<String, Object> body = JsonUtils.buildObject(respondVO.getData(), Map.class);
                this.deviceManageController.insertEntity(body);
                return;
            }

        } catch (Exception e) {
            String txt = "更新设备数据，发生异常：" + e.getMessage();
            logger.error(txt);
            this.console.error(txt);
        }
    }
}