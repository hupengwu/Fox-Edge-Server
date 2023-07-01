package cn.foxtech.channel.iec104.master.service;

import cn.foxtech.channel.common.api.ChannelServerAPI;
import cn.foxtech.channel.common.constant.ChannelProperties;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.channel.iec104.master.handler.MasterLinkerHandler;
import cn.foxtech.common.domain.vo.PublicRequestVO;
import cn.foxtech.common.domain.vo.PublicRespondVO;
import cn.foxtech.common.utils.iec104.server.Iec104ClientLinkerManager;
import cn.foxtech.common.utils.iec104.server.Iec104ClientLinkerScheduler;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.core.exception.ServiceException;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelService extends ChannelServerAPI {
    @Getter
    private final Map<String, SocketAddress> name2remote = new ConcurrentHashMap();

    @Autowired
    private ExecuteService executeService;

    /**
     * 配置信息
     */
    @Autowired
    private ChannelProperties channelProperties;

    private void loadConfig() {
        try {
            // 读取JSON格式的配置文件
            File file = new File("");
            String confFileName = file.getAbsolutePath() + "/conf/fox-edge-server-channel-iec104-master.conf";

            String jsonData = new String(Files.readAllBytes(Paths.get(confFileName)));

            Map<String, Object> map = JsonUtils.buildObject(jsonData, Map.class);
            List<Map<String, Object>> params = (List<Map<String, Object>>) map.get("channels");
            for (Map<String, Object> param : params) {
                String name = (String) param.get("name");
                String host = (String) param.get("host");
                Integer port = (Integer) param.get("port");

                this.name2remote.put(name, new InetSocketAddress(host, port));
            }

        } catch (IOException e) {
            System.out.print(e);
        }
    }

    public void initService() {
        // 绑定处理器
        MasterLinkerHandler linkerHandler = new MasterLinkerHandler();
        Iec104ClientLinkerManager.bindHandler(linkerHandler);

        // 启动
        Iec104ClientLinkerScheduler.getInstance().schedule();

        if ("local".equals(this.channelProperties.getInitMode())) {
            this.loadConfig();
            for (String name : this.name2remote.keySet()) {
                Iec104ClientLinkerManager.registerRemoteAddress(this.name2remote.get(name));
            }
        }
    }

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

        this.name2remote.put(channelName, new InetSocketAddress(host, port));

        Iec104ClientLinkerManager.registerRemoteAddress(this.name2remote.get(channelName));
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

        SocketAddress socketAddress = new InetSocketAddress(host, port);
        Iec104ClientLinkerManager.unregisterRemoteAddress(socketAddress);

        this.name2remote.remove(channelName);
    }

    /**
     * 执行主从半双工操作
     *
     * @param requestVO 请求报文
     * @return 返回的json报文
     * @throws ServiceException 异常信息
     */
    @Override
    public ChannelRespondVO execute(ChannelRequestVO requestVO) throws ServiceException {
        return this.executeService.execute(this.name2remote, requestVO);
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
        respondVO.setData(this.name2remote.keySet());
        return respondVO;
    }
}
