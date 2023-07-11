package cn.foxtech.channel.common.api;

import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.domain.vo.PublicRequestVO;
import cn.foxtech.common.domain.vo.PublicRespondVO;
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
    public List<ChannelRespondVO> receive() throws ServiceException {
        return new ArrayList<>();
    }

    /**
     * 获得资源的信息：可以被调用方用来发现存在哪些channel资源服务
     *
     * @param requestVO 请求报文
     * @return 资源信息
     * @throws ServiceException 异常信息
     */
    public PublicRespondVO getChannelNameList(PublicRequestVO requestVO) throws ServiceException {
        PublicRespondVO respondVO = new PublicRespondVO();
        respondVO.bindResVO(requestVO);

        return respondVO;
    }

    /**
     * 打开通道
     *
     * @param channelName  通道名称
     * @param channelParam 通道参数
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


}
