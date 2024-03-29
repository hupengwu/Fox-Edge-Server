package cn.foxtech.channel.udp.server.service;

import cn.foxtech.channel.common.api.ChannelServerAPI;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import io.netty.channel.ChannelHandlerContext;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelService extends ChannelServerAPI {
    private final Map<String, String> channelName2ServiceKey = new ConcurrentHashMap<>();

    @Autowired
    private ChannelManager channelManager;

    @Autowired
    private PublishService publishService;

    @Autowired
    private ReportService reportService;

    /**
     * 上次检查时间
     */
    @Setter
    private long lifeCycle = 3600 * 1000L;

    @Override
    public Object getReportLock() {
        return this.reportService;
    }

    /**
     * 打开通道：tcp-server是服务端，连接是自动触发的，不存在真正的打开和关闭操作
     *
     * @param channelName  通道名称
     * @param channelParam 通道参数
     */
    @Override
    public void openChannel(String channelName, Map<String, Object> channelParam) {
        String serviceKey = (String) channelParam.get("serviceKey");
        if (MethodUtils.hasEmpty(serviceKey)) {
            throw new ServiceException("参数不能为空: serviceKey");
        }

        this.channelName2ServiceKey.put(channelName, serviceKey);
    }

    /**
     * 关闭通道：tcp-server是服务端，连接是自动触发的，不存在真正的打开和关闭操作
     *
     * @param channelName  通道名称
     * @param channelParam 通道参数
     */
    @Override
    public void closeChannel(String channelName, Map<String, Object> channelParam) {
        String serviceKey = (String) channelParam.get("serviceKey");
        if (MethodUtils.hasEmpty(serviceKey)) {
            throw new ServiceException("参数不能为空: serviceKey");
        }

        this.channelName2ServiceKey.remove(channelName);
    }

    /**
     * 执行发布操作：单向下行操作
     *
     * @param requestVO 发布报文
     * @throws ServiceException 异常信息
     */
    @Override
    public synchronized void publish(ChannelRequestVO requestVO) throws ServiceException {
        String serviceKey = this.channelName2ServiceKey.get(requestVO.getName());
        if (MethodUtils.hasEmpty(serviceKey)) {
            throw new ServiceException("参数不能为空: serviceKey");
        }

        // 取出对端设备的在线信息
        ChannelHandlerContext ctx = this.channelManager.getChannel(serviceKey);
        InetSocketAddress skt = this.channelManager.getAddress(serviceKey);

        if (ctx == null || skt == null) {
            throw new ServiceException("找不到对应的socket:" + requestVO.getName());
        }

        this.publishService.publish(ctx, skt, requestVO);
    }

    /**
     * 主动上报操作：单向上行
     *
     * @return 上行报文
     * @throws ServiceException 异常信息
     */
    @Override
    public synchronized List<ChannelRespondVO> report() throws ServiceException {
        // 搭顺风车：清理长期没有接收到数据报文的通道
        this.channelManager.removeTimeout(this.lifeCycle);

        return this.reportService.popAll();
    }
}
