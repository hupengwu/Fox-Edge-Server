package cn.foxtech.channel.simulator.service;

import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.utils.hex.HexUtils;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 执行者
 */
@Component
public class ExecuteService {
    /**
     * 执行主从半双工操作
     *
     * @param requestVO 请求报文
     * @return 返回的json报文
     * @throws ServiceException 异常信息
     */
    public ChannelRespondVO execute(Map<String, String> recv2rspd, Map<String, String> recv2rsrd, ChannelRequestVO requestVO) throws ServiceException {
        String recv = HexUtils.byteArrayToHexString(HexUtils.hexStringToByteArray((String) requestVO.getSend())).toUpperCase();
        if (!recv2rspd.containsKey(recv)) {
            throw new ServiceException("没有对应的响应数据");
        }

        String hexString = "";
        if (recv2rsrd.containsKey(recv) && (System.currentTimeMillis() % 2 == 0)) {
            hexString = recv2rsrd.get(recv).toUpperCase();
        } else {
            hexString = recv2rspd.get(recv).toUpperCase();
        }

        ChannelRespondVO respondVO = new ChannelRespondVO();
        respondVO.bindBaseVO(requestVO);
        respondVO.setRecv(hexString);
        return respondVO;
    }
}
