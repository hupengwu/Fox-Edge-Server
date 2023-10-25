package cn.foxtech.manager.system.redistopic;

import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.domain.vo.PublicRespondVO;
import cn.foxtech.common.domain.vo.RestFulRequestVO;
import cn.foxtech.common.domain.vo.RestFulRespondVO;
import cn.foxtech.core.exception.ServiceException;

public interface IRedisTopicService {
    /**
     * 对通道返回的数据进行处理：通道返回给manager服务的响应
     *
     * @param respondVO 返回结果
     * @throws ServiceException 异常信息
     */
    void respondChannel(ChannelRespondVO respondVO) throws ServiceException;

    /**
     * 对设备返回的数据进行处理：设备返回给manager服务的响应
     *
     * @param respondVO 返回结果
     * @throws ServiceException 异常信息
     */
    void respondDevice(PublicRespondVO respondVO) throws ServiceException;

    /**
     * 对持久化返回的数据进行处理：持久化服务Persist返回给manager服务的响应
     *
     * @param respondVO 返回结果
     * @throws ServiceException 异常信息
     */
    void respondPersist(RestFulRespondVO respondVO) throws ServiceException;

    /**
     * 对发给Manager的请求进行处理：其他服务发送给manager服务的请求
     *
     * @param requestVO 请求报文
     * @throws ServiceException 异常信息
     */
    void requestManager(RestFulRequestVO requestVO) throws ServiceException;
}
