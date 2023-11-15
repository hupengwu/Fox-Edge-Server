package cn.foxtech.channel.mqtt.service;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.common.service.EntityManageService;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.entity.entity.ChannelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ReportService {
    private final Map<String, List<String>> channelMap = new ConcurrentHashMap<>();

    @Autowired
    private ChannelProperties channelProperties;

    @Autowired
    private EntityManageService entityManageService;

    public synchronized void push(String serviceKey, String message) {
        List<String> list = this.channelMap.computeIfAbsent(serviceKey, k -> new CopyOnWriteArrayList<>());
        if (list.size() > 128) {
            return;
        }

        list.add(message);
    }


    public List<ChannelRespondVO> popAll() {
        List<ChannelRespondVO> respondVOList = new ArrayList<>();
        for (String serviceKey : this.channelMap.keySet()) {
            List<String> list = this.channelMap.get(serviceKey);

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


            for (String pdu : list) {
                ChannelRespondVO respondVO = new ChannelRespondVO();
                respondVO.setUuid(null);
                respondVO.setType(this.channelProperties.getChannelType());
                respondVO.setName(channelEntity.getChannelName());
                respondVO.setRecv(pdu);

                respondVOList.add(respondVO);
            }

            this.channelMap.remove(serviceKey);
        }

        return respondVOList;

    }
}
