package cn.foxtech.device.service.service;

import cn.foxtech.common.entity.entity.DeviceEntity;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.device.domain.vo.OperateRespondVO;
import cn.foxtech.device.protocol.core.constants.FoxEdgeConstant;
import cn.foxtech.device.protocol.core.worker.FoxEdgeExchangeWorker;
import cn.foxtech.device.protocol.core.worker.FoxEdgePublishWorker;
import cn.foxtech.device.protocol.core.worker.FoxEdgeReportWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
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


    /**
     * 对设备进行某个操作
     *
     * @param deviceName  设备名称
     * @param operateName 操作名称
     * @param param       参数信息
     * @throws Exception 操作异常
     */
    public void smplPublish(String deviceName, String operateName, Map<String, Object> param, int timeout) throws Exception {
        DeviceEntity deviceEntity = this.entityService.getDeviceEntity(deviceName);
        if (deviceEntity == null) {
            throw new ServiceException("在数据库中找不到设备：" + deviceName);
        }

        // 对设备进行操作
        FoxEdgePublishWorker.publish(deviceName, deviceEntity.getDeviceType(), operateName, param, timeout, channelService);
    }

    /**
     * 对设备进行某个操作
     *
     * @param deviceName  设备名称
     * @param operateName 操作名称
     * @param param       参数信息
     * @return 设备的数值信息
     * @throws Exception 操作异常
     */
    public Map<String, Object> smplExchange(String deviceName, String operateName, Map<String, Object> param, int timeout) throws Exception {
        DeviceEntity deviceEntity = this.entityService.getDeviceEntity(deviceName);
        if (deviceEntity == null) {
            throw new ServiceException("在数据库中找不到设备：" + deviceName);
        }

        // 对设备进行操作
        return FoxEdgeExchangeWorker.exchange(deviceName, deviceEntity.getDeviceType(), operateName, param, timeout, channelService);
    }

    public OperateRespondVO decodeReport(DeviceEntity deviceEntity, Object recv, Map<String, Object> param) {
        // 对数据进行解码
        Map<String, Object> data = FoxEdgeReportWorker.decode(deviceEntity.getDeviceType(), recv, param);

        OperateRespondVO respondVO = new OperateRespondVO();
        respondVO.setDeviceName(deviceEntity.getDeviceName());
        respondVO.setDeviceType(deviceEntity.getDeviceType());
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
