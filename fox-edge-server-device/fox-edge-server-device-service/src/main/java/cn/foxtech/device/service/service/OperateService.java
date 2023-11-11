package cn.foxtech.device.service.service;

import cn.foxtech.common.entity.constant.OperateVOFieldConstant;
import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.common.entity.entity.OperateEntity;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.device.domain.constant.DeviceMethodVOFieldConstant;
import cn.foxtech.device.domain.vo.OperateRespondVO;
import cn.foxtech.device.protocol.v1.core.constants.FoxEdgeConstant;
import cn.foxtech.device.protocol.v1.core.worker.FoxEdgeExchangeWorker;
import cn.foxtech.device.protocol.v1.core.worker.FoxEdgePublishWorker;
import cn.foxtech.device.protocol.v1.core.worker.FoxEdgeReportWorker;
import cn.foxtech.device.script.engine.ScriptEngineExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作服务：包括单项操作和批量操作两种方式
 */
@Component
public class OperateService {
    @Autowired
    private EntityManageService entityService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ScriptEngineExecutor scriptEngineExecutor;


    /**
     * 对设备进行某个操作
     *
     * @param deviceName    设备名称
     * @param operateEntity 操作名称
     * @param param         参数信息
     * @throws Exception 操作异常
     */
    public void smplPublish(String deviceName, OperateEntity operateEntity, Map<String, Object> param, int timeout) throws Exception {
        DeviceEntity deviceEntity = this.entityService.getDeviceEntity(deviceName);
        if (deviceEntity == null) {
            throw new ServiceException("在数据库中找不到设备：" + deviceName);
        }

        // 对设备进行操作
        if (OperateVOFieldConstant.value_engine_java.equals(operateEntity.getEngineType())) {
            FoxEdgePublishWorker.publish(deviceName, deviceEntity.getManufacturer(), deviceEntity.getDeviceType(), operateEntity.getOperateName(), param, timeout, channelService);
        }
        if (OperateVOFieldConstant.value_engine_javascript.equals(operateEntity.getEngineType())) {
            this.scriptEngineExecutor.publish(deviceName, deviceEntity.getManufacturer(), deviceEntity.getDeviceType(), operateEntity, param, timeout, channelService);
        }

        throw new ServiceException("不支持该编码引擎类型：" + operateEntity.getEngineType());
    }

    /**
     * 对设备进行某个操作
     *
     * @param deviceName    设备名称
     * @param operateEntity 操作实体
     * @param param         参数信息
     * @return 设备的数值信息
     * @throws Exception 操作异常
     */
    public Map<String, Object> smplExchange(String deviceName, OperateEntity operateEntity, Map<String, Object> param, int timeout) throws Exception {
        DeviceEntity deviceEntity = this.entityService.getDeviceEntity(deviceName);
        if (deviceEntity == null) {
            throw new ServiceException("在数据库中找不到设备：" + deviceName);
        }

        if (OperateVOFieldConstant.value_engine_java.equals(operateEntity.getEngineType())) {
            return FoxEdgeExchangeWorker.exchange(deviceName, deviceEntity.getManufacturer(), deviceEntity.getDeviceType(), operateEntity.getOperateName(), param, timeout, channelService);
        }
        if (OperateVOFieldConstant.value_engine_javascript.equals(operateEntity.getEngineType())) {
            return this.scriptEngineExecutor.exchange(deviceName, deviceEntity.getManufacturer(), deviceEntity.getDeviceType(), operateEntity, param, timeout, channelService);
        }

        throw new ServiceException("不支持该编码/解码引擎类型：" + operateEntity.getEngineType());
    }

    private List<BaseEntity> getJspOperateReport(DeviceEntity deviceEntity) {
        List<BaseEntity> entityList = this.entityService.getEntityList(OperateEntity.class, (Object value) -> {
            OperateEntity operateEntity = (OperateEntity) value;

            if (!operateEntity.getManufacturer().equals(deviceEntity.getManufacturer())) {
                return false;
            }
            if (!operateEntity.getDeviceType().equals(deviceEntity.getDeviceType())) {
                return false;
            }
            if (!operateEntity.getEngineType().equals(OperateVOFieldConstant.value_engine_javascript)) {
                return false;
            }
            return operateEntity.getOperateMode().equals(DeviceMethodVOFieldConstant.value_operate_report);
        });

        return entityList;
    }

    public OperateRespondVO decodeReport(DeviceEntity deviceEntity, Object recv, Map<String, Object> param) {
        // 对数据进行解码
        Map<String, Object> data;
        try {
            // 先尝试用JAVA的JAR解码器进行解码，如果找不到能处理的解码器的时候，会抛出异常
            data = FoxEdgeReportWorker.decode(deviceEntity.getManufacturer(), deviceEntity.getDeviceType(), recv, param);
        } catch (Exception e) {
            // 当JAVA解码器处理不了的时候，再尝试使用JavaScript解码器进行解码
            List<BaseEntity> jspReportList = this.getJspOperateReport(deviceEntity);
            data = this.scriptEngineExecutor.decode(deviceEntity.getManufacturer(), deviceEntity.getDeviceType(), jspReportList, recv, param);
        }

        OperateRespondVO respondVO = new OperateRespondVO();
        respondVO.setDeviceName(deviceEntity.getDeviceName());
        respondVO.setDeviceType(deviceEntity.getDeviceType());
        respondVO.setManufacturer(deviceEntity.getManufacturer());
        respondVO.setParam(param);
        respondVO.setOperateName((String) data.get(FoxEdgeConstant.OPERATE_NAME_TAG));


        // 通信状态
        Map<String, Object> commStatus = OperateRespondVO.buildCommonStatus(System.currentTimeMillis(), 0, 0);

        Map<String, Object> dat = new HashMap<>();
        dat.put(OperateRespondVO.data_value, data.get(FoxEdgeConstant.DATA_TAG));
        dat.put(OperateRespondVO.data_comm_status, commStatus);

        respondVO.setData(dat);

        return respondVO;
    }
}
