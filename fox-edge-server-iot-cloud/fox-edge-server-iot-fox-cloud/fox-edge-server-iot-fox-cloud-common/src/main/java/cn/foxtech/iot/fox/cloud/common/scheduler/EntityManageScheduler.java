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
 
package cn.foxtech.iot.fox.cloud.common.scheduler;


import cn.foxtech.common.utils.scheduler.singletask.PeriodTaskService;
import cn.foxtech.iot.fox.cloud.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 实体管理器的定时同步数据
 */
@Component
public class EntityManageScheduler extends PeriodTaskService {

    @Autowired
    private EntityManageService entityManageService;


    @Override
    public void execute(long threadId) throws Exception {
        Thread.sleep(1000);

        // 从redis和cache之间互相同步
        this.entityManageService.syncEntity();
    }

}
