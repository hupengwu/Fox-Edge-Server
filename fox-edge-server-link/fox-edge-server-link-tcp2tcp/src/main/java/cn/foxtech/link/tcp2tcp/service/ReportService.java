package cn.foxtech.link.tcp2tcp.service;

import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.entity.entity.ChannelEntity;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import cn.foxtech.link.common.properties.LinkProperties;
import cn.foxtech.link.common.service.EntityManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ReportService {
    private final Map<String, List<byte[]>> channelMap = new ConcurrentHashMap<>();

    @Autowired
    private LinkProperties channelProperties;

    @Autowired
    private EntityManageService entityManageService;

    public synchronized void push(String serviceKey, byte[] pdu) {
        List<byte[]> list = this.channelMap.computeIfAbsent(serviceKey, k -> new CopyOnWriteArrayList<>());
        if (list.size() > 128) {
            return;
        }

        list.add(pdu);
    }


    public List<ChannelRespondVO> popAll() {
        List<ChannelRespondVO> respondVOList = new ArrayList<>();
        for (String serviceKey : this.channelMap.keySet()) {
            List<byte[]> list = this.channelMap.get(serviceKey);

            ChannelEntity channelEntity = this.entityManageService.getEntity(ChannelEntity.class, (Object value) -> {
                ChannelEntity entity = (ChannelEntity) value;

                // 检查：是否为本通道类型
                if (!entity.getChannelType().equals(this.channelProperties.getLinkType())){
                    return false;
                }

                // 检查：是否为相同的serviceKey
                return serviceKey.equals(entity.getChannelParam().get("serviceKey"));
            });

            if (channelEntity == null) {
                this.channelMap.remove(serviceKey);
                continue;
            }


            for (byte[] pdu : list) {
                ChannelRespondVO respondVO = new ChannelRespondVO();
                respondVO.setUuid(null);
                respondVO.setType(this.channelProperties.getLinkType());
                respondVO.setName(channelEntity.getChannelName());
                respondVO.setRecv(HexUtils.byteArrayToHexString(pdu));

                respondVOList.add(respondVO);
            }

            this.channelMap.remove(serviceKey);
        }

        return respondVOList;

    }
}
