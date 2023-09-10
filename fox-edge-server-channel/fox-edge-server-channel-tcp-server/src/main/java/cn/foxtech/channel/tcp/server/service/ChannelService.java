package cn.foxtech.channel.tcp.server.service;

import cn.foxtech.channel.common.api.ChannelServerAPI;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelService extends ChannelServerAPI {
    private final Map<String, String> channelName2ServiceKey = new ConcurrentHashMap<>();

    @Autowired
    private ChannelManager channelManager;

    @Autowired
    private ExecuteService executeService;

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
     * 执行主从半双工操作
     *
     * @param requestVO 请求报文
     * @return 返回的json报文
     * @throws ServiceException 异常信息
     */
    @Override
    public synchronized ChannelRespondVO execute(ChannelRequestVO requestVO) throws ServiceException {
        String serviceKey = this.channelName2ServiceKey.get(requestVO.getName());
        if (MethodUtils.hasEmpty(serviceKey)) {
            throw new ServiceException("参数不能为空: serviceKey");
        }

        ChannelHandlerContext ctx = channelManager.getContext(serviceKey);
        if (ctx == null) {
            throw new ServiceException("找不到对应的socket:" + requestVO.getName());
        }

        return this.executeService.execute(ctx, requestVO);
    }
}
