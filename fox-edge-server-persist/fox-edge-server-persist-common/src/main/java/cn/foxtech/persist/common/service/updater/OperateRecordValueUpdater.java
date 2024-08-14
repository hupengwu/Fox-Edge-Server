/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 *
 *     This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * --------------------------------------------------------------------------- */
 
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
