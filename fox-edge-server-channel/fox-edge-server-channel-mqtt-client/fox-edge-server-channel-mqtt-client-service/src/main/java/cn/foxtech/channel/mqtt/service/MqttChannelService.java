package cn.foxtech.channel.mqtt.service;

import cn.foxtech.channel.common.api.ChannelServerAPI;
import cn.foxtech.channel.common.constant.ChannelProperties;
import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.utils.ContainerUtils;
import cn.foxtech.common.utils.hex.HexUtils;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import cn.foxtech.common.utils.syncobject.SyncQueueObjectMap;
import cn.foxtech.core.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class MqttChannelService extends ChannelServerAPI {
    @Autowired
    private ChannelProperties constants;

    /**
     * mqtt客户端服务
     */
    @Autowired
    private MqttClientService clientService;

    /**
     * 执行请求
     *
     * @param requestVO
     * @return
     * @throws ServiceException
     */
    @Override
    public ChannelRespondVO execute(ChannelRequestVO requestVO) throws ServiceException {
        try {
            // 发送到服务器的topic下的数据
            String topic = requestVO.getName();

            byte[] payload = HexUtils.hexStringToByteArray((String) requestVO.getSend());

            // 重置信号
            SyncFlagObjectMap.inst().reset(topic);

            // 发送数据
            this.clientService.getClientService().publish(topic, payload);

            // 等待消息的到达
            Map<Integer, byte[]> pair = (Map<Integer, byte[]>) SyncFlagObjectMap.inst().waitConstant(topic, requestVO.getTimeout());
            if (pair == null) {
                throw new ServiceException("设备响应超时！");
            }
            for (Map.Entry<Integer, byte[]> entry : pair.entrySet()) {
                String hexString = HexUtils.byteArrayToHexString(entry.getValue());

                ChannelRespondVO respondVO = new ChannelRespondVO();
                respondVO.bindBaseVO(requestVO);
                respondVO.setRecv(hexString);
                return respondVO;
            }

            throw new ServiceException("数据处理异常！");
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    /**
     * 执行发布操作
     *
     * @param requestVO 发布报文
     * @throws ServiceException 异常信息
     */
    @Override
    public void publish(ChannelRequestVO requestVO) throws ServiceException {
        // 发送到服务器的topic下的数据
        String topic = requestVO.getName();
        byte[] payload = HexUtils.hexStringToByteArray((String) requestVO.getSend());

        // 发送数据
        this.clientService.getClientService().publish(topic, payload);
    }


    /**
     * 设备的主动上报消息
     *
     * @return 上报消息
     * @throws ServiceException 异常信息
     */
    @Override
    public List<ChannelRespondVO> receive() throws ServiceException {
        try {
            List<Object> result = SyncQueueObjectMap.inst().popup(constants.getChannelType(), false);
            return ContainerUtils.buildClassList(result, ChannelRespondVO.class);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
