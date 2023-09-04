package cn.foxtech.channel.iec104.master.service;

import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.utils.iec104.server.Iec104ClientLinkerEntity;
import cn.foxtech.common.utils.iec104.server.Iec104ClientLinkerManager;
import cn.foxtech.common.utils.json.JsonUtils;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.device.protocol.v1.iec104.core.builder.ApduVOBuilder;
import cn.foxtech.device.protocol.v1.iec104.core.encoder.ApduEncoder;
import cn.foxtech.device.protocol.v1.iec104.core.entity.ApduEntity;
import cn.foxtech.device.protocol.v1.iec104.core.entity.IControlEntity;
import cn.foxtech.device.protocol.v1.iec104.core.enums.FrameTypeEnum;
import cn.foxtech.device.protocol.v1.iec104.core.vo.ApduVO;
import org.springframework.stereotype.Component;

import java.net.SocketAddress;
import java.util.*;

/**
 * 执行者
 */
@Component
public class ExecuteService {
    /**
     * 执行主从半双工操作
     *
     * @param requestVO 请求报文
     * @return 返回的json报文
     * @throws ServiceException 异常信息
     */
    public ChannelRespondVO execute(Map<String, SocketAddress> name2remote, ChannelRequestVO requestVO) throws ServiceException {
        if (!name2remote.containsKey(requestVO.getName())) {
            throw new ServiceException("指定的channel不存在：" + requestVO.getName());
        }

        SocketAddress remoteAddress = name2remote.get(requestVO.getName());
        if (remoteAddress == null) {
            throw new ServiceException("指定的remoteAddress不存在：" + requestVO.getName());
        }

        Iec104ClientLinkerEntity linkerEntity = Iec104ClientLinkerManager.queryEntity(remoteAddress);
        if (remoteAddress == null) {
            throw new ServiceException("指定的linkerEntity不存在：" + requestVO.getName());
        }

        if (linkerEntity.getChannel() == null) {
            throw new ServiceException("指定的从站尚未建立TCP连接：" + requestVO.getName());
        }
        if (!linkerEntity.isLinked()) {
            throw new ServiceException("指定的从站尚未建立链路：" + requestVO.getName());
        }

        if (!linkerEntity.isIdleSession()) {
            throw new ServiceException("指定的从站正在被使用：" + requestVO.getName());
        }

        try {
            // 将MAP转换为VO对象
            ApduVO apduVO = JsonUtils.buildObject((Map) requestVO.getSend(), ApduVO.class);

            // 将VO转换为实体
            ApduEntity apduEntity = ApduVOBuilder.buildEntity(apduVO);

            // 等待结束条件
            FrameTypeEnum waitFrameType = FrameTypeEnum.valueOf(apduVO.getWaitFrameType());
            Set<Integer> endFlag = apduVO.getWaitEndFlag();

            if (!(apduEntity.getControl() instanceof IControlEntity)) {
                throw new ServiceException("只能接受I帧格式的发送请求：" + requestVO.getName());
            }

            // 打上流水号
            ((IControlEntity) apduEntity.getControl()).setSend(linkerEntity.getSession().getLastRequest());

            // 重置信号
            String key = UUID.randomUUID().toString();
            SyncFlagObjectMap.inst().reset(key);

            // 记录发送的实体
            Iec104ClientLinkerManager.updateEntity4Request(linkerEntity.getChannel(), apduEntity, key, waitFrameType, endFlag);

            // 向从站发送数据
            linkerEntity.getChannel().writeAndFlush(ApduEncoder.encodeApdu(apduEntity));


            // 等待消息的到达：根据动态key
            List<ApduEntity> responds = (List<ApduEntity>) SyncFlagObjectMap.inst().waitDynamic(key, requestVO.getTimeout());
            if (responds == null) {
                throw new ServiceException("设备响应超时！");
            }

            List<ApduVO> apduVOList = new ArrayList<>();
            for (ApduEntity respondEntity : responds) {
                ApduVO respondVO = ApduVOBuilder.buildVO(respondEntity);
                apduVOList.add(respondVO);
            }

            // 返回数据
            ChannelRespondVO respondVO = new ChannelRespondVO();
            respondVO.bindBaseVO(requestVO);
            respondVO.setRecv(apduVOList);
            return respondVO;

        } catch (Exception e) {
            ChannelRespondVO respondVO = ChannelRespondVO.error(e.getMessage());
            respondVO.bindBaseVO(requestVO);
            respondVO.setRecv(null);
            return respondVO;
        }
    }
}
