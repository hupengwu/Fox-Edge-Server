package cn.foxtech.manager.system.scheduler;


import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.manager.system.service.EntityManageService;
import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.entity.entity.DeviceTimeOutEntity;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import cn.foxtech.common.domain.constant.RestFulManagerVOConstant;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
            if (RestFulManagerVOConstant.uri_device_timeout.equals(requestVO.getUri()) && "post".equals(requestVO.getMethod())) {
                this.updateDeviceTimeOut(requestVO);
                continue;
            }
        }
    }

    /**
     * 更新通信超时的设备信息
     * @param requestVO
     */
    private void updateDeviceTimeOut(RestFulRequestVO requestVO) {
        try {
            Map<String, Object> data = (Map<String, Object>) requestVO.getData();

            Long time = System.currentTimeMillis();

            // 转换数据结构
            DeviceTimeOutEntity entity = JsonUtils.buildObject(data,DeviceTimeOutEntity.class);

            // 初始化数据
            entity.setCommFailedTime(time);
            entity.setCommFailedCount(1);
            entity.setCommSuccessTime(0);
            entity.setCreateTime(time);
            entity.setUpdateTime(time);

            // 保存数据
            DeviceTimeOutEntity existEntity = this.entityManageService.getEntity(entity.makeServiceKey(), DeviceTimeOutEntity.class);
            if (existEntity == null) {
                this.entityManageService.insertRDEntity(entity);
            } else {
                entity.setCreateTime(existEntity.getCreateTime());
                entity.setCommFailedTime(existEntity.getCommFailedTime());
                entity.setCommFailedCount(existEntity.getCommFailedCount() + 1);
                this.entityManageService.updateRDEntity(entity);
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
