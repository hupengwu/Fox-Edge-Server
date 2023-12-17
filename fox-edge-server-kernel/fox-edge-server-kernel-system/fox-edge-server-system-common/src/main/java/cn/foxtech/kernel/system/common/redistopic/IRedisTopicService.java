package cn.foxtech.kernel.system.common.redistopic;

import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.domain.vo.PublicRespondVO;
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
}
