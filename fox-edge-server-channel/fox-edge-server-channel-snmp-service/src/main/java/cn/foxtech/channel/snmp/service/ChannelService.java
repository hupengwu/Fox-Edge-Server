package cn.foxtech.channel.snmp.service;

import cn.foxtech.channel.common.api.ChannelServerAPI;
import cn.foxtech.channel.common.properties.ChannelProperties;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.channel.snmp.entity.SnmpEntity;
import cn.foxtech.core.exception.ServiceException;
import org.snmp4j.mp.SnmpConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ChannelService extends ChannelServerAPI {
    /**
     * 串口名-串口映射表
     */
    private final Map<String, SnmpEntity> name2entity = new HashMap<>();

    @Autowired
    private ExecuteService executeService;


    /**
     * 常量信息
     */
    @Autowired
    private ChannelProperties channelProperties;

    public void initService() {
    }

    /**
     * 打开通道
     *
     * @param channelName  通道名称
     * @param channelParam 通道参数
     */
    @Override
    public void openChannel(String channelName, Map<String, Object> channelParam) {
        this.openSnmp(channelName, channelParam);
    }

    /**
     * 关闭通道
     *
     * @param channelName  通道名称
     * @param channelParam 通道参数
     */
    @Override
    public void closeChannel(String channelName, Map<String, Object> channelParam) {
        SnmpEntity entity = this.name2entity.get(channelName);
        if (entity == null) {
            return;
        }

        this.name2entity.remove(channelName);
    }

    private void openSnmp(String channelName, Map<String, Object> channelParam) {
        String version = (String) channelParam.get("version");
        String targetIp = (String) channelParam.get("targetIp");
        Integer targetPort = (Integer) channelParam.get("targetPort");
        String localIp = (String) channelParam.get("localIp");
        Integer localPort = (Integer) channelParam.get("localPort");
        String community = (String) channelParam.get("community");


        SnmpEntity entity = new SnmpEntity();
        entity.setLocalIp(this.channelProperties.getEnvironment().getProperty("spring.channel.localIp", String.class, ""));
        entity.setLocalPort(this.channelProperties.getEnvironment().getProperty("spring.channel.localPort", Integer.class, 161));

        if ("v1".equals(version)) {
            entity.setVersion(SnmpConstants.version1);
        } else if ("v2c".equals(version)) {
            entity.setVersion(SnmpConstants.version2c);
        } else {
            entity.setVersion(SnmpConstants.version2c);
        }

        // 目标设备的IP和端口
        if (targetIp != null && !targetIp.isEmpty()) {
            entity.setTargetIp(targetIp);
        }
        if (targetPort != null) {
            entity.setTargetPort(targetPort);
        }

        // 本地IP和端口
        if (localIp != null && !localIp.isEmpty()) {
            entity.setLocalIp(localIp);
        }
        if (localPort != null) {
            entity.setLocalPort(localPort);
        }


        if (community != null && !community.isEmpty()) {
            entity.setCommunity(community);
        }

        // 保存串口对象
        this.name2entity.put(channelName, entity);
    }


    /**
     * 查询串口数据:增加同步锁，避免并发访问带来的多线程异常。
     * @param requestVO 请求报文
     * @return 响应报文
     * @throws ServiceException 异常
     */
    @Override
    public synchronized ChannelRespondVO execute(ChannelRequestVO requestVO) throws ServiceException {
        SnmpEntity entity = this.name2entity.get(requestVO.getName());
        return this.executeService.execute(entity, requestVO);
    }

    /**
     * 执行发布操作
     *
     * @param requestVO 发布报文
     * @throws ServiceException 异常信息
     */
    @Override
    public void publish(ChannelRequestVO requestVO) throws ServiceException {
        SnmpEntity entity = this.name2entity.get(requestVO.getName());
        this.executeService.publish(entity, requestVO);
    }
}
