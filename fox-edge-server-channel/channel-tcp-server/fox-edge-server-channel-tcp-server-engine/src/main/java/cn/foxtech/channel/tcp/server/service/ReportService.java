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
 
package cn.foxtech.channel.tcp.server.service;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.common.service.EntityManageService;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.entity.entity.ChannelEntity;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ReportService {
    private final Map<String, List<Object>> channelMap = new ConcurrentHashMap<>();

    @Autowired
    private ChannelProperties channelProperties;

    @Autowired
    private EntityManageService entityManageService;

    public synchronized void push(String serviceKey, Object pdu) {
        List<Object> list = this.channelMap.computeIfAbsent(serviceKey, k -> new CopyOnWriteArrayList<>());
        list.add(pdu);

        synchronized (this) {
            this.notify();
        }
    }


    public List<ChannelRespondVO> popAll() {
        List<ChannelRespondVO> respondVOList = new ArrayList<>();
        for (String serviceKey : this.channelMap.keySet()) {
            List<Object> list = this.channelMap.get(serviceKey);

            ChannelEntity channelEntity = this.entityManageService.getEntity(ChannelEntity.class, (Object value) -> {
                ChannelEntity entity = (ChannelEntity) value;

                // 检查：是否为本通道类型
                if (!entity.getChannelType().equals(this.channelProperties.getChannelType())) {
                    return false;
                }

                // 检查：是否为相同的serviceKey
                return serviceKey.equals(entity.getChannelParam().get("serviceKey"));
            });

            if (channelEntity == null) {
                this.channelMap.remove(serviceKey);
                continue;
            }


            for (Object pdu : list) {
                ChannelRespondVO respondVO = new ChannelRespondVO();
                respondVO.setUuid(null);
                respondVO.setType(this.channelProperties.getChannelType());
                respondVO.setName(channelEntity.getChannelName());
                if (pdu instanceof String) {
                    respondVO.setRecv(pdu);
                } else {
                    respondVO.setRecv(HexUtils.byteArrayToHexString((byte[]) pdu));
                }


                respondVOList.add(respondVO);
            }

            this.channelMap.remove(serviceKey);
        }

        return respondVOList;

    }
}
