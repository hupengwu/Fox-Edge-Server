package cn.foxtech.channel.tcp.client.service;

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
    private final Map<String, List<byte[]>> channelMap = new ConcurrentHashMap<>();

    @Autowired
    private ChannelProperties channelProperties;

    @Autowired
    private EntityManageService entityManageService;

    public synchronized void push(String channelName, byte[] pdu) {
        List<byte[]> list = this.channelMap.computeIfAbsent(channelName, k -> new CopyOnWriteArrayList<>());
        if (list.size() > 128) {
            return;
        }

        list.add(pdu);
    }


    public List<ChannelRespondVO> popAll() {
        List<ChannelRespondVO> respondVOList = new ArrayList<>();
        for (String channelName : this.channelMap.keySet()) {
            List<byte[]> list = this.channelMap.get(channelName);

            // 获得该serviceKey相对应的通道
            ChannelEntity channelEntity = this.getChannelEntity(channelName);
            if (channelEntity != null) {
                // 把收到的数据提交给该通道
                for (byte[] pdu : list) {
                    ChannelRespondVO respondVO = new ChannelRespondVO();
                    respondVO.setUuid(null);
                    respondVO.setType(this.channelProperties.getChannelType());
                    respondVO.setName(channelEntity.getChannelName());
                    respondVO.setRecv(HexUtils.byteArrayToHexString(pdu));

                    respondVOList.add(respondVO);
                }
            }

            this.channelMap.remove(channelName);
        }

        return respondVOList;

    }

    private ChannelEntity getChannelEntity(String channelName) {
        if (channelName == null) {
            return null;
        }

        ChannelEntity find = new ChannelEntity();
        find.setChannelName(channelName);
        find.setChannelType(this.channelProperties.getChannelType());


        ChannelEntity channelEntity = this.entityManageService.getEntity(find.makeServiceKey(), ChannelEntity.class);

        return channelEntity;
    }
}
