package cn.foxtech.channel.simulator.service;

import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class ReportService {
    public List<ChannelRespondVO> popAll(String channelType, Map<String, String> channel2event) throws ServiceException {
        List<ChannelRespondVO> respondVOList = new ArrayList<>();
        for (Map.Entry<String, String> entry : channel2event.entrySet()) {
            String channelName = entry.getKey();
            String event = entry.getValue();

            ChannelRespondVO respondVO = new ChannelRespondVO();
            respondVO.setUuid(null);
            respondVO.setType(channelType);
            respondVO.setName(channelName);
            respondVO.setRecv(event);

            respondVOList.add(respondVO);
        }

        return respondVOList;
    }
}
