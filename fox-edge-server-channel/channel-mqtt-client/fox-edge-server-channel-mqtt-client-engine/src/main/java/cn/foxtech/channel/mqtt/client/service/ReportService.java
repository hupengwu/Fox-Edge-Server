package cn.foxtech.channel.mqtt.client.service;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.common.service.EntityManageService;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.entity.entity.ChannelEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
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

    public synchronized void push(String topic, String message) {
        List<String> list = this.channelMap.computeIfAbsent(topic, k -> new CopyOnWriteArrayList<>());
        list.add(message);

        synchronized (this) {
            this.notify();
        }
    }


    public List<ChannelRespondVO> popAll() {
        List<ChannelRespondVO> respondVOList = new ArrayList<>();
        for (String topic : this.channelMap.keySet()) {
            List<String> list = this.channelMap.get(topic);

            ChannelEntity channelEntity = this.entityManageService.getEntity(ChannelEntity.class, (Object value) -> {
                ChannelEntity entity = (ChannelEntity) value;

                // 检查：是否为本通道类型
                if (!entity.getChannelType().equals(this.channelProperties.getChannelType())) {
                    return false;
                }

                List<String> topics = (List<String>)entity.getChannelParam().get("topics");

                // 检查：是否为相同的serviceKey
                return topics.contains(topic);
            });

            if (channelEntity == null) {
                this.channelMap.remove(topic);
                continue;
            }


            for (String body : list) {
                ChannelRespondVO respondVO = new ChannelRespondVO();
                respondVO.setUuid(null);
                respondVO.setType(this.channelProperties.getChannelType());
                respondVO.setName(channelEntity.getChannelName());

                Map<String, Object> map = new HashMap<>();
                map.put("topic", topic);
                map.put("body", body);

                respondVO.setRecv(body);

                respondVOList.add(respondVO);
            }

            this.channelMap.remove(topic);
        }

        return respondVOList;

    }
}
