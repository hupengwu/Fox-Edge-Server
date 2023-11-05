package cn.foxtech.channel.tcp.client.service;

import cn.foxtech.channel.common.api.ChannelServerAPI;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.channel.tcp.client.entity.TcpClientEntity;
import cn.foxtech.channel.tcp.client.handler.ChannelHandler;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
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
    private final Map<String, TcpClientEntity> channelName2Entity = new ConcurrentHashMap<>();

    private final Map<String, String> serviceKey2ChanelName = new ConcurrentHashMap<>();

    @Autowired
    private ChannelManager channelManager;

    @Autowired
    private PublishService publishService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ExecuteService executeService;

    @Autowired
    private ClassManager classManager;

    @Autowired
    private RedisConsoleService consoleService;


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
        String mode = (String) channelParam.get("mode");
        Map<String, Object> handler = (Map<String, Object>) channelParam.get("handler");

        // 检查：参数是否为空
        if (MethodUtils.hasEmpty(host, port)) {
            throw new ServiceException("参数不能为空: host, port ");
        }

        TcpClientEntity entity;
        if ("full-duplex".equals(mode)) {
            entity = this.buildFullDuplexEntity(host, port, handler);
        } else {
            entity = this.buildHalfDuplexEntity(host, port);
        }

        // 记录信息
        this.channelName2Entity.put(channelName, entity);
        this.serviceKey2ChanelName.put(entity.getServiceKey(), channelName);
    }

    /**
     * 全双工模式下的实体
     *
     * @param host    host
     * @param port    port
     * @param handler handler
     * @return 实体
     */
    private TcpClientEntity buildFullDuplexEntity(String host, Integer port, Map<String, Object> handler) {
        if (handler == null) {
            throw new ServiceException("全双工模式下，参数不能为空: handler ");
        }
        String splitMessageHandler = (String) handler.get("splitMessageHandler");
        if (MethodUtils.hasEmpty(splitMessageHandler)) {
            throw new ServiceException("参数不能为空: splitMessageHandler ");
        }


        // 构造拆包的对象实例
        SplitMessageHandler splitMessageHandlerInstance;
        try {
            Class<?> splitHandler = this.classManager.getSplitMessageHandler(splitMessageHandler);
            if (splitHandler == null) {
                throw new ServiceException("找不到拆包的handler类，请检查是否安装了包含该类的JAR文件：" + splitMessageHandler);
            }
            splitMessageHandlerInstance = (SplitMessageHandler) splitHandler.newInstance();
        } catch (Exception e) {
            throw new ServiceException("实例化对象失败:" + splitMessageHandler);
        }

        // 建立实体对象
        TcpClientEntity entity = new TcpClientEntity();
        entity.setRemoteHost(host);
        entity.setRemotePort(port);
        entity.setFullDuplex(true);
        entity.setSocketAddress(new InetSocketAddress(host, port));
        entity.setSplitMessageHandler(splitMessageHandlerInstance);
        entity.setServiceKey(new InetSocketAddress(host, port).toString());
        entity.setChannelHandler(new ChannelHandler());
        entity.getChannelHandler().setReportService(this.reportService);
        entity.getChannelHandler().setChannelManager(this.channelManager);
        entity.getChannelHandler().setConsoleService(this.consoleService);
        entity.getChannelHandler().setFullDuplex(true);
        entity.getChannelHandler().setSplitMessageHandler(splitMessageHandlerInstance);
        return entity;
    }

    /**
     * 半双工模式下的实体
     *
     * @param host host
     * @param port port
     * @return 实体
     */
    private TcpClientEntity buildHalfDuplexEntity(String host, Integer port) {
        // 建立实体对象
        TcpClientEntity entity = new TcpClientEntity();
        entity.setRemoteHost(host);
        entity.setRemotePort(port);
        entity.setFullDuplex(false);
        entity.setSocketAddress(new InetSocketAddress(host, port));
        entity.setSplitMessageHandler(null);
        entity.setServiceKey(new InetSocketAddress(host, port).toString());
        entity.setChannelHandler(new ChannelHandler());
        entity.getChannelHandler().setReportService(this.reportService);
        entity.getChannelHandler().setChannelManager(this.channelManager);
        entity.getChannelHandler().setConsoleService(this.consoleService);
        entity.getChannelHandler().setFullDuplex(false);
        entity.getChannelHandler().setSplitMessageHandler(null);
        return entity;
    }

    /**
     * 关闭通道：tcp-server是服务端，连接是自动触发的，不存在真正的打开和关闭操作
     *
     * @param channelName  通道名称
     * @param channelParam 通道参数
     */
    @Override
    public void closeChannel(String channelName, Map<String, Object> channelParam) {
        TcpClientEntity entity = this.channelName2Entity.get(channelName);

        // 关闭socket
        ChannelHandlerContext ctx = this.channelManager.getContext(entity.getServiceKey());
        if (ctx != null) {
            ctx.channel().disconnect();
            ctx.channel().closeFuture();
        }

        // 删除记录
        this.channelName2Entity.remove(channelName);
        this.serviceKey2ChanelName.remove(entity.getServiceKey());
    }

    /**
     * 执行发布操作：单向下行操作
     *
     * @param requestVO 发布报文
     * @throws ServiceException 异常信息
     */
    @Override
    public synchronized void publish(ChannelRequestVO requestVO) throws ServiceException {
        TcpClientEntity entity = this.channelName2Entity.get(requestVO.getName());
        if (entity == null) {
            throw new ServiceException("该通道尚未打开: " + requestVO.getName());
        }

        ChannelHandlerContext ctx = this.channelManager.getContext(entity.getServiceKey());
        if (ctx == null) {
            throw new ServiceException("该通道尚未与对端设备建立连接，或者对端设备尚未主动发起身份认证:" + requestVO.getName());
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
        return this.reportService.popAll(this.serviceKey2ChanelName);
    }

    @Override
    public synchronized ChannelRespondVO execute(ChannelRequestVO requestVO) throws ServiceException {
        TcpClientEntity entity = this.channelName2Entity.get(requestVO.getName());
        if (entity == null) {
            throw new ServiceException("通道的配置参数不正确，未能注册通道成功！:" + requestVO.getName());
        }


        if (entity.isFullDuplex()) {
            throw new ServiceException("当前是全双工方式，只支持publish和report两种操作:" + requestVO.getName());
        }

        ChannelHandlerContext ctx = this.channelManager.getContext(entity.getServiceKey());
        if (ctx == null) {
            throw new ServiceException("与远端尚未建立连接:" + requestVO.getName());
        }

        return this.executeService.execute(ctx, requestVO);
    }
}
