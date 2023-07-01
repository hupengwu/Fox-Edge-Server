package cn.foxtech.channel.tcpsocket.service;

import cn.foxtech.channel.common.api.ChannelServerAPI;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.channel.tcpsocket.entity.TcpClientSocket;
import cn.foxtech.common.domain.vo.PublicRequestVO;
import cn.foxtech.common.domain.vo.PublicRespondVO;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ChannelService extends ChannelServerAPI {
    private final Map<String, TcpClientSocket> socketMap = new HashMap<>();

    @Autowired
    private ExecuteService executeService;

    /**
     * 打开通道
     *
     * @param channelName  通道名称
     * @param channelParam 通道参数
     */
    @Override
    public void openChannel(String channelName, Map<String, Object> channelParam) {
        String host = (String) channelParam.get("host");
        Integer port = (Integer) channelParam.get("port");

        if (MethodUtils.hasEmpty(host, port)) {
            throw new ServiceException("参数不能为空:host,port");
        }


        TcpClientSocket tcpClientSocket = this.socketMap.get(channelName);
        if (tcpClientSocket != null) {
            return;
        }

        tcpClientSocket = new TcpClientSocket();
        tcpClientSocket.setHost(host);
        tcpClientSocket.setPort(port);

        this.socketMap.put(channelName, tcpClientSocket);
    }

    /**
     * 关闭通道
     *
     * @param channelName  通道名称
     * @param channelParam 通道参数
     */
    @Override
    public void closeChannel(String channelName, Map<String, Object> channelParam) {
        String host = (String) channelParam.get("host");
        Integer port = (Integer) channelParam.get("port");

        // 生成客户端socket
        TcpClientSocket tcpClientSocket = this.socketMap.get(channelName);
        if (tcpClientSocket == null) {
            return;
        }

        tcpClientSocket.close();

        this.socketMap.remove(channelName);
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
        TcpClientSocket tcpClientSocket = this.socketMap.get(requestVO.getName());
        if (tcpClientSocket == null) {
            throw new ServiceException("找不到对应的socket:" + requestVO.getName());
        }

        return this.executeService.execute(tcpClientSocket, requestVO);
    }

    /**
     * 获得资源的信息
     *
     * @return 资源信息
     * @throws ServiceException 异常信息
     */
    @Override
    public PublicRespondVO getChannelNameList(PublicRequestVO requestVO) throws ServiceException {
        PublicRespondVO respondVO = new PublicRespondVO();
        respondVO.bindResVO(requestVO);

        List<String> channelNameList = new ArrayList<>();
        channelNameList.addAll(this.socketMap.keySet());
        respondVO.setData(channelNameList);

        return respondVO;
    }
}
