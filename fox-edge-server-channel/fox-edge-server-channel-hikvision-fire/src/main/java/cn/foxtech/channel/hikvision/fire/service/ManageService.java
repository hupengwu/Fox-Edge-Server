package cn.foxtech.channel.hikvision.fire.service;

import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import org.springframework.stereotype.Component;

@Component
public class ManageService {
    public ChannelRespondVO manageChannel(ChannelRequestVO requestVO) {
        ChannelRespondVO respondVO = new ChannelRespondVO();
        respondVO.bindBaseVO(requestVO);

        return respondVO;
    }
}
