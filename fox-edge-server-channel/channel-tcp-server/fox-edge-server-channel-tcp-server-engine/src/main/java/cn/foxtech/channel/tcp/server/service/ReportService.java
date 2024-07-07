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
