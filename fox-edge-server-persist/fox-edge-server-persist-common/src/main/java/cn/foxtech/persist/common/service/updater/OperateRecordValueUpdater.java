package cn.foxtech.persist.common.service.updater;

import cn.foxtech.device.domain.vo.OperateRespondVO;
import cn.foxtech.common.entity.entity.OperateRecordEntity;
import cn.foxtech.common.entity.service.operaterecord.OperateRecordEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OperateRecordValueUpdater {
    /**
     * 设备记录服务
     */
    @Autowired
    private OperateRecordEntityService operateRecordEntityService;

    public void updateOperateRecordValue(String clientName, OperateRespondVO operateRespondVO) {
        if (!Boolean.TRUE.equals(operateRespondVO.getRecord())) {
            return;
        }

        OperateRecordEntity recordEntity = new OperateRecordEntity();
        recordEntity.setDeviceName(operateRespondVO.getDeviceName());
        recordEntity.setDeviceType(operateRespondVO.getDeviceType());
        recordEntity.setManufacturer(operateRespondVO.getManufacturer());
        recordEntity.setRecordName(operateRespondVO.getOperateName());
        recordEntity.setClientModel(clientName);
        recordEntity.setOperateUuid(operateRespondVO.getUuid());
        recordEntity.setRecordParam(operateRespondVO.getParam());
        recordEntity.setRecordData(operateRespondVO.getData());

        // 保存到数据库
        this.operateRecordEntityService.insertEntity(recordEntity);
    }
}
