package cn.foxtech.channel.tcp.listener.service;

import cn.foxtech.channel.common.api.ChannelServerAPI;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.channel.tcp.listener.entity.TcpListenerEntity;
import cn.foxtech.channel.tcp.listener.handler.ChannelHandler;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.device.protocol.v1.utils.netty.ServiceKeyHandler;
import cn.foxtech.device.protocol.v1.utils.netty.SplitMessageHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelService extends ChannelServerAPI {
    @Getter
    private final Map<String, TcpListenerEntity> channelName2Entity = new ConcurrentHashMap<>();

    @Autowired
    private ChannelManager channelManager;

    @Autowired
    private PublishService publishService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ClassManager classManager;


    /**
     * 打开通道：tcp-server是服务端，连接是自动触发的，不存在真正的打开和关闭操作
     *
     * @param channelName  通道名称
     * @param channelParam 通道参数
     */
    @Override
    public void openChannel(String channelName, Map<String, Object> channelParam) {
        String host = (String) channelParam.get("host");
        Integer port = (Integer) channelParam.get("port");
        Map<String, Object> handler = (Map<String, Object>) channelParam.get("handler");

        // 检查：参数是否为空
        if (MethodUtils.hasEmpty(host, port, handler)) {
            throw new ServiceException("参数不能为空: host, port, handler ");
        }

        String splitMessageHandler = (String) handler.get("splitMessageHandler");
        String serviceKeyHandler = (String) handler.get("serviceKeyHandler");
        String serviceKey = (String) handler.get("serviceKey");
        if (MethodUtils.hasEmpty(splitMessageHandler, serviceKeyHandler, serviceKey)) {
            throw new ServiceException("参数不能为空: splitMessageHandler, serviceKeyHandler, serviceKey ");
        }

        TcpListenerEntity listenerEntity = this.channelName2Entity.get(channelName);
        if (listenerEntity != null) {
            return;
        }

        // 构造拆包的对象实例
        SplitMessageHandler splitMessageHandlerInstance = null;
        try {
            Class splitHandler = this.classManager.getSplitMessageHandler(splitMessageHandler);
            if (splitHandler == null) {
                throw new ServiceException("找不到拆包的handler类，请检查是否安装了包含该类的JAR文件：" + splitMessageHandler);
            }
            splitMessageHandlerInstance = (SplitMessageHandler) splitHandler.newInstance();
        } catch (Exception e) {
            throw new ServiceException("实例化对象失败:" + splitMessageHandler);
        }

        // 构造身份识别的对象实例
        ServiceKeyHandler serviceKeyHandlerInstance = null;
        try {
            Class serviceHandler = this.classManager.getServiceKeyHandler(serviceKeyHandler);
            if (serviceHandler == null) {
                throw new ServiceException("找不到身份识别用的handler类，请检查是否安装了包含该类的JAR文件：" + serviceKeyHandler);
            }

            serviceKeyHandlerInstance = (ServiceKeyHandler) serviceHandler.newInstance();
        } catch (Exception e) {
            throw new ServiceException("实例化对象失败:" + serviceKeyHandler);
        }

        // 建立实体对象
        listenerEntity = new TcpListenerEntity();
        listenerEntity.setRemoteHost(host);
        listenerEntity.setRemotePort(port);
        listenerEntity.setSocketAddress(new InetSocketAddress(host, port));
        listenerEntity.setSplitMessageHandler(splitMessageHandlerInstance);
        listenerEntity.setServiceKeyHandler(serviceKeyHandlerInstance);
        listenerEntity.setServiceKey(serviceKey);
        listenerEntity.setChannelHandler(new ChannelHandler());
        listenerEntity.getChannelHandler().setReportService(this.reportService);
        listenerEntity.getChannelHandler().setChannelManager(this.channelManager);
        listenerEntity.getChannelHandler().setSplitMessageHandler(splitMessageHandlerInstance);
        listenerEntity.getChannelHandler().setServiceKeyHandler(serviceKeyHandlerInstance);


        this.channelName2Entity.put(channelName, listenerEntity);
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

        this.channelName2Entity.remove(channelName);
    }

    /**
     * 执行发布操作：单向下行操作
     *
     * @param requestVO 发布报文
     * @throws ServiceException 异常信息
     */
    @Override
    public synchronized void publish(ChannelRequestVO requestVO) throws ServiceException {
        TcpListenerEntity listenerEntity = this.channelName2Entity.get(requestVO.getName());
        if (listenerEntity == null) {
            throw new ServiceException("该通道尚未打开: " + requestVO.getName());
        }

        ChannelHandlerContext ctx = this.channelManager.getContext(listenerEntity.getServiceKey());
        if (ctx == null) {
            throw new ServiceException("该通道尚未与对端设备建立连接，或者对端设备尚未通过身份认证:" + requestVO.getName());
        }

        this.publishService.publish(ctx, requestVO);
    }

    /**
     * 主动上报操作：单向上行
     *
     * @return 上行报文
     * @throws ServiceException 异常信息
     */
    @Override
    public synchronized List<ChannelRespondVO> report() throws ServiceException {
        return this.reportService.popAll();
    }
}
