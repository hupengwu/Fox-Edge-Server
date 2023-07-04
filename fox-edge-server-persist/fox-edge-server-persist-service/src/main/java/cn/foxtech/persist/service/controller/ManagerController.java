package cn.foxtech.persist.service.controller;

import cn.foxtech.persist.common.redistopic.RedisTopicPuberService;
import cn.foxtech.persist.common.service.EntityManageService;
import cn.foxtech.persist.common.service.EntityUpdateService;
import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.domain.vo.RestFulRespondVO;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import cn.foxtech.common.domain.constant.RestFulManagerVOConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 订阅设备的数据采集<br>
 * 背景：某些设备会主动发布数据给服务器，比如某些短信设备，MQTT设备，它们自己状态变化的时候，会发布数据给订阅者<br>
 */
@Component
public class ManagerController extends PeriodTaskService {
    @Autowired
    private EntityUpdateService entityUpdateService;

    @Autowired
    private RedisTopicPuberService puberService;

    @Autowired
    private EntityManageService entityManageService;

    public void execute(long threadId) throws Exception {
        // 检查：是否装载完毕
        if (!this.entityManageService.isInitialized()) {
            Thread.sleep(1000);
            return;
        }

        // 管理服务发过来的管理请求
        List<Object> requestVOList = SyncQueueObjectMap.inst().popup(RestFulManagerVOConstant.restful_manager, false, 1000);
        for (Object request : requestVOList) {
            RestFulRequestVO requestVO = (RestFulRequestVO) request;
            // 场景1： 删除设备数值
            if (RestFulManagerVOConstant.uri_device_value.equals(requestVO.getUri()) && "delete".equals(requestVO.getMethod())) {
                RestFulRespondVO respondVO = this.entityUpdateService.deleteValueEntity(requestVO);
                this.puberService.sendRespondVO(respondVO);
                continue;
            }
        }
    }
}
