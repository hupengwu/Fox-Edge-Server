package cn.foxtech.channel.tcp.client.service;

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
     * 主动半双工下的执行操作
     *
     * @param ctx       netty的socket连接后形成的ctx
     * @param requestVO 发送请求
     * @return 响应请求
     * @throws ServiceException 异常信息
     */
    public synchronized ChannelRespondVO execute(ChannelHandlerContext ctx, ChannelRequestVO requestVO) throws ServiceException {
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
            SyncFlagObjectMap.inst().reset(requestVO.getName());

            // 等待数据返回
            byte[] data = (byte[]) SyncFlagObjectMap.inst().waitDynamic(requestVO.getName(), timeout);
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
