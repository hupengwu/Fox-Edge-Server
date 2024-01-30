package cn.foxtech.device.service.controller;

import cn.foxtech.common.domain.constant.RedisStatusConstant;
import cn.foxtech.common.domain.constant.RedisTopicConstant;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.common.status.ServiceStatus;
import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.device.domain.constant.DeviceMethodVOFieldConstant;
import cn.foxtech.device.domain.vo.OperateRequestVO;
import cn.foxtech.device.domain.vo.OperateRespondVO;
import cn.foxtech.device.domain.vo.TaskRequestVO;
import cn.foxtech.device.domain.vo.TaskRespondVO;
import cn.foxtech.device.service.redistopic.RedisTopicPuberService;
import cn.foxtech.device.service.service.EntityManageService;
import cn.foxtech.device.service.service.OperateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.naming.CommunicationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备执行操作控制器：处理上层应用对设备的操作请求，包括批量操作/单步操作/发布操作
 */
@Component
public class DeviceExecuteController extends PeriodTaskService {
    /**
     * 实体管理
     */
    @Autowired
    EntityManageService entityManageService;

    /**
     * redis topic发布者
     */
    @Autowired
    private RedisTopicPuberService puberService;

    /**
     * 操作服务
     */
    @Autowired
    private OperateService operateService;

    @Autowired
    private ServiceStatus serviceStatus;

    /**
     * 执行任务
     *
     * @param threadId 线程ID
     * @throws Exception 异常信息
     */
    public void execute(long threadId) throws Exception {
        // 检查：是否装载完毕
        if (!this.entityManageService.isInitialized()) {
            Thread.sleep(1000);
            return;
        }

        // 从消息队列中，一个个弹出消息，逐个处理，这样才能多线程并行处理。
        List<Object> list = SyncQueueObjectMap.inst().popup(RedisTopicConstant.model_device, true);
        for (Object object : list) {
            if (!(object instanceof TaskRequestVO)) {
                continue;
            }

            TaskRequestVO taskRequestVO = (TaskRequestVO) object;
            TaskRespondVO taskRespondVO = new TaskRespondVO();
            taskRespondVO.bindBaseVO(taskRequestVO);

            // 逐个步骤的操作设备
            List<OperateRequestVO> deviceRequestVOS = taskRequestVO.getRequestVOS();
            for (OperateRequestVO operateRequestVO : deviceRequestVOS) {
                OperateRespondVO operateRespondVO = this.execute(operateRequestVO);
                taskRespondVO.getRespondVOS().add(operateRespondVO);
            }

            // 返回数据给发送者
            this.puberService.sendRespondVO(taskRespondVO);

            // 再主动上报操作记录到持久化服务
            this.puberService.sendOperateRespondVO(taskRespondVO);
        }
    }


    /**
     * 单步操作
     *
     * @param requestVO 结构化的请求
     * @return 结构化的响应
     */
    private OperateRespondVO execute(OperateRequestVO requestVO) {
        // 单步操作的参数
        String deviceName = requestVO.getDeviceName();
        String operateName = requestVO.getOperateName();
        Integer timeout = requestVO.getTimeout();


        DeviceEntity deviceEntity = null;
        try {
            // 获得设备详细信息
            deviceEntity = this.entityManageService.getDeviceEntity(deviceName);
            if (deviceEntity == null) {
                throw new ServiceException("指定名称的设备不存在：" + deviceName);
            }

            // 获得操作实体
            OperateEntity find = new OperateEntity();
            find.setManufacturer(deviceEntity.getManufacturer());
            find.setDeviceType(deviceEntity.getDeviceType());
            find.setOperateName(operateName);
            OperateEntity operateEntity = this.entityManageService.getEntity(find.makeServiceKey(), OperateEntity.class);
            if (operateEntity == null) {
                throw new ServiceException("指定的操作，不存在!：" + deviceName);
            }

            // 通过时间戳，判断通道服务是否正在运行
            if (!this.serviceStatus.isActive(RedisStatusConstant.value_model_type_channel, deviceEntity.getChannelType(), 60 * 1000)) {
                throw new ServiceException("指定的Channel服务尚未运行：" + deviceEntity.getChannelType());
            }

            // 组合参数：先组合设备上的参数，再组合操作上的参数，也就是操作参数的优先级高于设备上的参数
            Map<String, Object> param = new HashMap<>();
            param.putAll(deviceEntity.getDeviceParam());
            if (requestVO.getParam() != null) {
                param.putAll(requestVO.getParam());
            }

            // 交换类操作
            if (DeviceMethodVOFieldConstant.value_operate_exchange.equals(requestVO.getOperateMode())) {
                // 执行请求操作
                Map<String, Object> value = this.operateService.smplExchange(deviceName, operateEntity, param, timeout);
                Map<String, Object> status = OperateRespondVO.buildCommonStatus(System.currentTimeMillis(), 0, 0);

                OperateRespondVO respondVO = new OperateRespondVO();
                respondVO.bindBaseVO(requestVO);
                respondVO.setDeviceType(deviceEntity.getDeviceType());
                respondVO.setManufacturer(deviceEntity.getManufacturer());
                respondVO.setData(value, status);
                return respondVO;
            }

            // 发布类操作
            if (DeviceMethodVOFieldConstant.value_operate_publish.equals(requestVO.getOperateMode())) {
                // 执行请求操作
                this.operateService.smplPublish(deviceName, operateEntity, param, timeout);

                OperateRespondVO respondVO = new OperateRespondVO();
                respondVO.bindBaseVO(requestVO);

                return respondVO;
            }

            throw new ServiceException("不支持的操作类型");
        } catch (CommunicationException e) {
            // 通信失败的时候，发送一个通信失败的通知给管理服务
            this.notifyDeviceTimeOut(deviceEntity, requestVO);

            // 返回失败信息给请求的来源
            return this.makeExceptionRespondVO(requestVO, e.getMessage());
        } catch (Exception e) {
            // 返回失败信息给请求的来源
            return this.makeExceptionRespondVO(requestVO, e.getMessage());
        }
    }

    private void notifyDeviceTimeOut(DeviceEntity deviceEntity, OperateRequestVO requestVO) {
        if (deviceEntity == null) {
            return;
        }
    }

    /**
     * 生成返回的数据结构
     *
     * @param requestVO    请求消息
     * @param errorMessage 响应消息
     * @return
     */
    private OperateRespondVO makeExceptionRespondVO(OperateRequestVO requestVO, String errorMessage) {
        // 通信失败：通过通信状态字段告知外部
        Map<String, Object> status = OperateRespondVO.buildCommonStatus(0, System.currentTimeMillis(), 1);

        OperateRespondVO respondVO = OperateRespondVO.error(errorMessage);
        respondVO.bindBaseVO(requestVO);
        respondVO.setData(new HashMap<>(), status);

        return respondVO;
    }
}
