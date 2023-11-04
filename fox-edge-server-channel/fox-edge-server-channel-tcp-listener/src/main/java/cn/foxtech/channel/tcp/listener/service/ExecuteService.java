package cn.foxtech.channel.tcp.listener.service;

import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.utils.hex.HexUtils;
import cn.foxtech.common.utils.syncobject.SyncFlagObjectMap;
import cn.foxtech.core.exception.ServiceException;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

/**
 * 执行者
 */
@Component
public class ExecuteService {
    /**
     * 查询串口数据:增加同步锁，避免并发访问带来的多线程异常。
     *
     * @return
     */
    public synchronized ChannelRespondVO execute(ChannelHandlerContext ctx, ChannelRequestVO requestVO) throws ServiceException {
        String name = requestVO.getName();
        String sendData = (String) requestVO.getSend();
        int timeout = requestVO.getTimeout();

        // 检查：数据是否为空
        if (sendData == null || sendData.isEmpty()) {
            throw new ServiceException("发送数据不能为空");
        }

        // 格式转换
        byte[] send = HexUtils.hexStringToByteArray(sendData);

        try {
            // 发送数据
            ctx.channel().writeAndFlush(send);

            // 以serviceKey为半双工的问答识别标记
            String serviceKey = ctx.channel().remoteAddress().toString();
            SyncFlagObjectMap.inst().reset(serviceKey);

            // 等待数据返回
            byte[] data = (byte[]) SyncFlagObjectMap.inst().waitDynamic(serviceKey, timeout);
            if (data == null) {
                throw new ServiceException("接收socket数据返回失败:设备响应超时!");
            }

            // 格式转换
            String hexString = HexUtils.byteArrayToHexString(data, true);

            ChannelRespondVO respondVO = new ChannelRespondVO();
            respondVO.bindBaseVO(requestVO);
            respondVO.setRecv(hexString);
            return respondVO;
        } catch (Exception e) {
            throw new ServiceException("接收socket数据返回失败:" + e.getMessage());
        }

    }
}
