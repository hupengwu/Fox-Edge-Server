package cn.foxtech.kernel.system.common.scheduler;


import cn.foxtech.common.domain.constant.RestFulManagerVOConstant;
import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import cn.foxtech.kernel.system.common.service.EntityManageService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Topic的订阅<br>
 * 背景：某些服务会通过redis topic主动发送请求
 */
@Component
public class TopicManagerScheduler extends PeriodTaskService {
    private static final Logger logger = Logger.getLogger(TopicManagerScheduler.class);

    @Autowired
    private EntityManageService entityManageService;

    public void execute(long threadId) throws Exception {
        // 检查：是否装载完毕
        if (!this.entityManageService.isInitialized()) {
            Thread.sleep(1000);
            return;
        }

        // 管理服务发过来的管理请求
        List<Object> requestVOList = SyncQueueObjectMap.inst().popup(RestFulManagerVOConstant.restful_manager, false, 16);
        for (Object request : requestVOList) {
            RestFulRequestVO requestVO = (RestFulRequestVO) request;
            // 场景1： 插入设备超时

        }
    }


}
