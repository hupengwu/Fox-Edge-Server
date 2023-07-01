package cn.foxtech.channel.coap.client.service;


import cn.foxtech.channel.common.api.ChannelServerAPI;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.utils.http.HttpClientUtil;
import cn.foxtech.channel.http.domain.vo.DataVO;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HttpClientService extends ChannelServerAPI {

    /**
     * 执行主从半双工操作
     *
     * @param requestVO 请求报文
     * @return 返回的json报文
     * @throws ServiceException 异常信息
     */
    @Override
    public ChannelRespondVO execute(ChannelRequestVO requestVO) throws ServiceException {
        try {
            // 转换对象
            DataVO dataVO = DataVO.buildVO(requestVO.getSend());

            // 提取数据
            int timeout = requestVO.getTimeout();

            if (dataVO.getMethod().equalsIgnoreCase("post")) {
                Map map = HttpClientUtil.executePost(dataVO.getUrl(), dataVO.getBody(), Map.class);

                ChannelRespondVO respondVO = new ChannelRespondVO();
                respondVO.bindBaseVO(requestVO);
                respondVO.setRecv(map);
                return respondVO;
            }
            if (dataVO.getMethod().equalsIgnoreCase("get")) {
                Map map =  HttpClientUtil.executeGet(dataVO.getUrl(), Map.class);

                ChannelRespondVO respondVO = new ChannelRespondVO();
                respondVO.bindBaseVO(requestVO);
                respondVO.setRecv(map);
                return respondVO;
            }

            throw new ServiceException("不支持的操作！");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }

    }
}
