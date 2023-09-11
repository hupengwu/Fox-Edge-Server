package cn.foxtech.channel.common.api;

import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.core.exception.ServiceException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Channel服务的API接口
 * 每一种具体的Channel模块会去实现这些方法，那么Channel框架在控制这些Channel模块的时候，会展现出相应的特性
 */
public class ChannelServerAPI {
    /**
     * 执行主从半双工操作：上位机向设备问询，并等待设备的回答，直到设备响应或者超时
     *
     * @param requestVO 请求报文
     * @return 返回的json报文
     * @throws ServiceException 异常信息
     */
    public ChannelRespondVO execute(ChannelRequestVO requestVO) throws ServiceException {
        throw new ServiceException("该channel不支持主从问答方式");
    }

    /**
     * 执行发布操作：上位机向设备单向发送报文，不需要等待设备的返回
     *
     * @param requestVO 发布报文
     * @throws ServiceException 异常信息
     */
    public void publish(ChannelRequestVO requestVO) throws ServiceException {
        throw new ServiceException("该channel不支持Publish方式");
    }

    /**
     * 设备的主动上报消息：设备向上位机
     *
     * @return 上报消息
     * @throws ServiceException 异常信息
     */
    public List<ChannelRespondVO> report() throws ServiceException {
        return new ArrayList<>();
    }


    /**
     * 打开通道
     *
     * @param channelName  通道名称
     * @param channelParam 通道参数
     * @throws Exception 异常信息
     */
    public void openChannel(String channelName, Map<String, Object> channelParam) throws Exception {
    }

    /**
     * 关闭通道
     *
     * @param channelName  通道名称
     * @param channelParam 通道参数
     */
    public void closeChannel(String channelName, Map<String, Object> channelParam) {

    }

    /**
     * 管理通道操作：上层服务，对通道的管理操作
     *
     * @param requestVO 发布报文
     * @return 响应VO
     * @throws ServiceException 异常信息
     */
    public ChannelRespondVO manageChannel(ChannelRequestVO requestVO) throws ServiceException {
        throw new ServiceException("该channel不支持管理操作");
    }
}
