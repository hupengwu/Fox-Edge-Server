package cn.foxtech.channel.opcua.service;

import cn.foxtech.channel.common.service.TempDirManageService;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.channel.opcua.certificate.KeyStoreLoader;
import cn.foxtech.channel.opcua.entity.OpcUaChannelEntity;
import cn.foxtech.channel.opcua.entity.OpcUaConfigEntity;
import cn.foxtech.channel.opcua.entity.OpcUaNodeId;
import cn.foxtech.channel.opcua.entity.OpcUaNodeTree;
import cn.foxtech.channel.opcua.enums.BrowseModeEnum;
import cn.foxtech.channel.opcua.handler.OpcUaHandler;
import cn.foxtech.channel.opcua.utils.BrowseNodeUtils;
import cn.foxtech.common.entity.manager.RedisConsoleService;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import org.apache.log4j.Logger;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.UaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.IdentityProvider;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * 通道业务：一个channelName，一个远端设备，一个连接
 */
@Component
public class ExecuteService {
    private static final Logger logger = Logger.getLogger(ExecuteService.class);

    private final Map<String, OpcUaChannelEntity> channelEntityMap = new ConcurrentHashMap<>();

    @Autowired
    private RedisConsoleService console;

    @Autowired
    private KeyStoreLoader keyStoreLoader;
    @Autowired
    private TempDirManageService tempDirService;

    public void openChannel(String channelName, Map<String, Object> channelParam) throws Exception {
        // 创建一个实体对象
        OpcUaChannelEntity channelEntity = this.channelEntityMap.get(channelName);
        if (channelEntity == null) {
            channelEntity = new OpcUaChannelEntity();
            channelEntity.setChannelName(channelName);
            this.channelEntityMap.put(channelName, channelEntity);
        }

        // 重置旧参数
        OpcUaConfigEntity configEntity = JsonUtils.buildObject(channelParam, OpcUaConfigEntity.class);
        channelEntity.setOpcConfig(configEntity);

        // 建立连接
        this.createConnect(channelName);
    }

    private void createConnect(String channelName) throws Exception {
        // 创建一个实体对象
        OpcUaChannelEntity channelEntity = this.channelEntityMap.get(channelName);
        if (channelEntity == null) {
            throw new ServiceException("该channel实体对象不存在:" + channelName);
        }

        // 检查关键的EndpointUrl
        String endpointUrl = channelEntity.getOpcConfig().getEndpointUrl();
        if (MethodUtils.hasEmpty(endpointUrl)) {
            throw new ServiceException("EndpointUrl不能为空！");
        }

        // 创建OpcUaClient
        if (channelEntity.getOpcUaClient() == null) {
            OpcUaClient opcUaClient = this.createClient(channelEntity.getOpcConfig());
            channelEntity.setOpcUaClient(opcUaClient);
        }


        // 跟远端的设备建立连接
        if (channelEntity.getOpcLink() == null) {
            UaClient opcLink = channelEntity.getOpcUaClient().connect().get();
            if (opcLink == null) {
                throw new ServiceException("跟远端设备建立opcLink连接失败！");
            }

            channelEntity.setOpcLink(opcLink);
        }

        // 创建一个监听线程
        OpcUaHandler opcUaHandler = new OpcUaHandler(channelEntity);
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.schedule(opcUaHandler, 0, TimeUnit.MILLISECONDS);
        scheduledExecutorService.shutdown();

        channelEntity.setHandler(opcUaHandler);
    }

    private OpcUaClient createClient(OpcUaConfigEntity channelEntity) throws Exception {
        this.tempDirService.createTempDir();
        Path securityTempDir = Paths.get(this.tempDirService.getTempDir());


        KeyStoreLoader loader = keyStoreLoader.load(securityTempDir, channelEntity.getCertificate().getFile());

        // 搜索OPC节点
        List<EndpointDescription> endpoints;
        try {
            endpoints = DiscoveryClient.getEndpoints(channelEntity.getEndpointUrl()).get();
        } catch (Throwable e) {
            // try the explicit discovery endpoint as well
            String discoveryUrl = channelEntity.getEndpointUrl();

            if (!discoveryUrl.endsWith("/")) {
                discoveryUrl += "/";
            }
            discoveryUrl += "discovery";

            //      log.info("Trying explicit discovery URL: {}", discoveryUrl);
            endpoints = DiscoveryClient.getEndpoints(discoveryUrl).get();
        }

        EndpointDescription endpoint = endpoints.stream().filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri())).filter(endpointFilter()).findFirst().orElseThrow(() -> new ServiceException("no desired endpoints returned"));

        // 验证方式：根据idp用户名/密码，确定是何种验证方式
        IdentityProvider identityProvider;
        if (MethodUtils.hasEmpty(channelEntity.getIdpUsername(), channelEntity.getIdpPassword())) {
            identityProvider = new AnonymousProvider();
        } else {
            identityProvider = new UsernameProvider(channelEntity.getIdpUsername(), channelEntity.getIdpPassword());
        }

        OpcUaClientConfig config = OpcUaClientConfig.builder()// 生成builder
                .setApplicationName(LocalizedText.english("my"))//
                .setApplicationUri(channelEntity.getAppUri())//
                .setCertificate(loader.getClientCertificate())//
                .setKeyPair(loader.getClientKeyPair()).setEndpoint(endpoint)//
                .setIdentityProvider(identityProvider) // 匿名验证
                .setRequestTimeout(Unsigned.uint(5000)) //
                .build();

        return OpcUaClient.create(config);
    }

    public void closeChannel(String channelName) {
        // 创建一个实体对象
        OpcUaChannelEntity channelEntity = this.channelEntityMap.get(channelName);
        if (channelEntity == null) {
            return;
        }

        // 释放监听器线程
        if (channelEntity.getHandler() != null) {
            try {
                channelEntity.getHandler().removeListener();
                channelEntity.setHandler(null);
            } catch (Exception e) {
                this.console.error(e.getMessage());
                this.logger.error(e.getMessage());
            }
        }

        // 释放跟远端的设备建立连接
        if (channelEntity.getOpcLink() != null) {
            try {
                channelEntity.getOpcLink().disconnect();
                channelEntity.setOpcLink(null);
            } catch (Exception e) {
                this.console.error(e.getMessage());
                this.logger.error(e.getMessage());
            }
        }

        // 释放OpcUaClient对象
        if (channelEntity.getOpcUaClient() != null) {
            try {
                channelEntity.getOpcUaClient().disconnect();
                channelEntity.setOpcUaClient(null);
            } catch (Exception e) {
                this.console.error(e.getMessage());
                this.logger.error(e.getMessage());
            }
        }

        this.channelEntityMap.remove(channelName);
    }

    private Predicate<EndpointDescription> endpointFilter() {
        return e -> true;
    }

    /**
     * 执行主从半双工操作
     *
     * @param requestVO 请求报文
     * @return 返回的json报文
     * @throws ServiceException 异常信息
     */
    public ChannelRespondVO execute(ChannelRequestVO requestVO) {
        try {
            OpcUaChannelEntity channelEntity = this.channelEntityMap.get(requestVO.getName());
            if (channelEntity == null) {
                throw new ServiceException("指定的channel不存在：" + requestVO.getName());
            }

            // 检测：连接是否建立，如果尚未建立，就尝试建立
            if (channelEntity.getOpcLink() == null || channelEntity.getHandler() == null) {
                this.createConnect(requestVO.getName());
            }
            // 检测：尝试建立后，是否依然无法建立连接，那么此时才是真的无法连接
            if (channelEntity.getOpcLink() == null || channelEntity.getHandler() == null) {
                throw new ServiceException("跟远端的设备链路未建立：" + requestVO.getName());
            }

            if (requestVO.getSend() == null) {
                throw new ServiceException("Send必须为非空：" + requestVO.getName());
            }

            if (!(requestVO.getSend() instanceof Map)) {
                throw new ServiceException("Send必须为Map结构：" + requestVO.getName());
            }

            Map<String, Object> send = (Map<String, Object>) requestVO.getSend();
            if (send.isEmpty()) {
                throw new ServiceException("Send必须为非空：" + requestVO.getName());
            }

            String operate = (String) send.get("operate");
            if (MethodUtils.hasEmpty(operate)) {
                throw new ServiceException("send中的operate参数不能未空：" + requestVO.getName());
            }


            if (BrowseModeEnum.valueOf(operate).equals(BrowseModeEnum.browseTree) || BrowseModeEnum.valueOf(operate).equals(BrowseModeEnum.browseChild) || BrowseModeEnum.valueOf(operate).equals(BrowseModeEnum.browseChildValue)) {
                Map<String, Object> nodeIdMap = (Map<String, Object>) send.get("nodeId");
                OpcUaNodeId opcUaNodeId = OpcUaNodeId.buildEntity(nodeIdMap);

                NodeId nodeId = null;
                if (opcUaNodeId != null) {
                    nodeId = opcUaNodeId.buildNodeId();
                }

                OpcUaNodeTree result = BrowseNodeUtils.browseNode(channelEntity, nodeId, "", BrowseModeEnum.valueOf(operate));

                // 返回数据
                ChannelRespondVO respondVO = new ChannelRespondVO();
                respondVO.bindBaseVO(requestVO);
                respondVO.setRecv(result);
                return respondVO;
            }


            throw new ServiceException("未定义的operate参数：" + requestVO.getName());
        } catch (Exception e) {
            ChannelRespondVO respondVO = ChannelRespondVO.error(e.getMessage());
            respondVO.bindBaseVO(requestVO);
            respondVO.setRecv(null);
            return respondVO;
        }
    }

}
