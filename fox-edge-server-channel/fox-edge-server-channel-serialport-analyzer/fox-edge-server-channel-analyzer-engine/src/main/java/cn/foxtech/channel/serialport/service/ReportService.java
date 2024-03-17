package cn.foxtech.channel.serialport.service;

import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.utils.hex.HexUtils;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 执行者
 */
@Component
public class ReportService {
    private final Map<String, List<Object>> channelNameMap = new ConcurrentHashMap<>();
    @Autowired
    private ChannelProperties channelProperties;


    public void push(String channelName, Object data) {
        List<Object> list = this.channelNameMap.computeIfAbsent(channelName, k -> new CopyOnWriteArrayList<>());
        list.add(data);

        synchronized (this) {
            this.notify();
        }
    }

    public List<ChannelRespondVO> report() throws ServiceException {
        List<ChannelRespondVO> respondVOList = new ArrayList<>();

        try {
            for (String channelName : this.channelNameMap.keySet()) {
                List<Object> list = this.channelNameMap.get(channelName);

                for (Object data : list) {
                    ChannelRespondVO respondVO = new ChannelRespondVO();
                    respondVO.setUuid(null);
                    respondVO.setType(this.channelProperties.getChannelType());
                    respondVO.setName(channelName);

                    if (data instanceof String) {
                        respondVO.setRecv(data);
                    } else {
                        respondVO.setRecv(HexUtils.byteArrayToHexString((byte[]) data));
                    }

                    respondVOList.add(respondVO);
                }
            }

            this.channelNameMap.clear();
        } catch (Exception e) {
            e.getMessage();
        }


        return respondVOList;
    }
}
