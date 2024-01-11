package cn.foxtech.channel.common.api;


import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.core.exception.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Channel服务的API接口
 * 每一种具体的Channel模块会去实现这些方法，那么Channel框架在控制这些Channel模块的时候，会展现出相应的特性
 */
@Component
public class ChannelClientAPI {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelClientAPI.class);

    @Autowired
    private ChannelServerAPI channelServerAPI;

    /**
     * 执行主从半双工操作：上位机向设备问询，并等待设备的回答，直到设备响应或者超时
     *
     * @param requestVO 请求报文
     * @return 返回的json报文
     * @throws ServiceException 异常信息
     */
    public ChannelRespondVO execute(ChannelRequestVO requestVO) throws ServiceException {
        ChannelRespondVO respondVO = this.channelServerAPI.execute(requestVO);

        return respondVO;
    }

    /**
     * 执行发布操作：上位机向设备单向发送报文，不需要等待设备的返回
     *
     * @param requestVO 发布报文
     * @throws ServiceException 异常信息
     */
    public void publish(ChannelRequestVO requestVO) throws ServiceException {
        this.channelServerAPI.publish(requestVO);
    }


    /**
     * 设备的主动上报消息：设备向上位机
     *
     * @param timeout 等待超时
     * @return 上报消息
     * @throws ServiceException 异常信息
     * @throws InterruptedException 异常信息
     */
    public List<ChannelRespondVO> reportTo(long timeout) throws ServiceException, InterruptedException {
        // 获得作为同步锁的对象
        Object lock = this.channelServerAPI.getReportLock();

        // 等待其他线程的上报通知
        synchronized (lock) {
            lock.wait(timeout);
        }

        // 取出需要上报的数据
        List<ChannelRespondVO> list = this.channelServerAPI.report();
        return list;
    }

    /**
     * 打开通道
     *
     * @param channelName  通道名称
     * @param channelParam 通道参数
     * @throws Exception 异常
     */
    public void openChannel(String channelName, Map<String, Object> channelParam) throws Exception {
        this.channelServerAPI.openChannel(channelName, channelParam);
    }

    /**
     * 关闭通道
     *
     * @param channelName  通道名称
     * @param channelParam 通道参数
     */
    public void closeChannel(String channelName, Map<String, Object> channelParam) {
        this.channelServerAPI.closeChannel(channelName, channelParam);
    }

    /**
     * 对通道进行管理操作
     *
     * @param requestVO 操作请求
     * @return 响应
     * @throws ServiceException 异常状况
     */
    public ChannelRespondVO manageChannel(ChannelRequestVO requestVO) throws ServiceException {
        return this.channelServerAPI.manageChannel(requestVO);
    }
}
