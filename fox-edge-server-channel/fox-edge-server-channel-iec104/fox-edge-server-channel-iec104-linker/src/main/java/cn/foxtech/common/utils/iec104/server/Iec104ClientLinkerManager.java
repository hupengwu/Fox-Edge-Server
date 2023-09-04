package cn.foxtech.common.utils.iec104.server;

import cn.foxtech.device.protocol.v1.iec104.core.entity.ApduEntity;
import cn.foxtech.device.protocol.v1.iec104.core.enums.CotReasonEnum;
import cn.foxtech.device.protocol.v1.iec104.core.enums.FrameTypeEnum;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;

public class Iec104ClientLinkerManager {
    private static final Map<SocketAddress, Iec104ClientLinkerEntity> channelMap = new HashMap<>();
    private static Iec104ClientLinkerHandler handler = new Iec104ClientLinkerHandler();

    /**
     * 绑定自定义的handler
     *
     * @param newHandler  newHandler
     */
    public static synchronized void bindHandler(Iec104ClientLinkerHandler newHandler) {
        handler = newHandler;
    }

    /**
     * 注册远端设备
     *
     * @param host host
     * @param port port
     */
    public static synchronized void registerRemoteAddress(String host, int port) {
        SocketAddress remoteAddress = new InetSocketAddress(host, port);
        registerRemoteAddress(remoteAddress);
    }

    public static synchronized void registerRemoteAddress(SocketAddress remoteAddress) {
        if (channelMap.containsKey(remoteAddress)) {
            return;
        }

        channelMap.put(remoteAddress, new Iec104ClientLinkerEntity());
        channelMap.get(remoteAddress).setRemoteAddress(remoteAddress);
        channelMap.get(remoteAddress).setChannel(null);
        channelMap.get(remoteAddress).setLinked(false);
    }

    public static synchronized void unregisterRemoteAddress(SocketAddress remoteAddress) {
        if (!channelMap.containsKey(remoteAddress)) {
            return;
        }

        // 重置链路
        channelMap.get(remoteAddress).setLinked(false);
        // 重置会话
        channelMap.get(remoteAddress).getSession().setLastRequest((short) 0);
        channelMap.get(remoteAddress).resetSession();

        // 通知链路和连接断开
        handler.linkDisconnected(channelMap.get(remoteAddress));
        handler.socketDisconnected(channelMap.get(remoteAddress));

        // 注销
        channelMap.remove(remoteAddress);
    }

    /**
     * 查询等待建立TCP连接的实体
     * @return 连接的实体
     */
    public static synchronized List<SocketAddress> queryEntityList4WaitConnected() {
        List<SocketAddress> resultList = new ArrayList<>();
        for (Map.Entry<SocketAddress, Iec104ClientLinkerEntity> entry : channelMap.entrySet()) {
            Iec104ClientLinkerEntity entity = entry.getValue();

            if (entity.getChannel() == null) {
                resultList.add(entry.getKey());
            }
        }

        return resultList;
    }


    public static synchronized void updateEntity4IsConnected(Channel channel) {
        SocketAddress remoteAddress = channel.remoteAddress();
        if (!channelMap.containsKey(remoteAddress)) {
            return;
        }

        channelMap.get(remoteAddress).setChannel(channel);

        handler.socketConnected(channelMap.get(remoteAddress));
    }

    public static synchronized void updateEntity4WaitConnected(Channel channel) {
        SocketAddress remoteAddress = channel.remoteAddress();
        if (!channelMap.containsKey(remoteAddress)) {
            return;
        }

        // 重置链路
        channelMap.get(remoteAddress).setLinked(false);
        // 重置会话
        channelMap.get(remoteAddress).getSession().setLastRequest((short) 0);
        channelMap.get(remoteAddress).resetSession();

        // 通知链路和连接断开
        handler.linkDisconnected(channelMap.get(remoteAddress));
        handler.socketDisconnected(channelMap.get(remoteAddress));

        // 重置连接
        channelMap.get(remoteAddress).setChannel(null);
    }

    public static synchronized void updateEntity4IsLinked(Channel channel) {
        SocketAddress remoteAddress = channel.remoteAddress();
        if (!channelMap.containsKey(remoteAddress)) {
            return;
        }

        channelMap.get(remoteAddress).setLinked(true);
        handler.linkConnected(channelMap.get(remoteAddress));
    }

    public static synchronized void updateEntity4IsNotLinked(Channel channel) {
        SocketAddress remoteAddress = channel.remoteAddress();
        if (!channelMap.containsKey(remoteAddress)) {
            return;
        }

        // 设置链路状态为空
        channelMap.get(remoteAddress).setLinked(false);
        // 重置会话
        channelMap.get(remoteAddress).getSession().setLastRequest((short) 0);
        channelMap.get(remoteAddress).resetSession();

        // 通知链路断开
        handler.linkDisconnected(channelMap.get(remoteAddress));
    }

    public static synchronized void updateEntity4Request(Channel channel, ApduEntity apduEntity, String uuid, FrameTypeEnum waitFrameType, Set<Integer> endFlag) {
        SocketAddress remoteAddress = channel.remoteAddress();
        if (!channelMap.containsKey(remoteAddress)) {
            return;
        }

        // 预处理
        if (endFlag == null) {
            endFlag = new HashSet<>();
        }

        // 记录发送请求的APDU
        channelMap.get(remoteAddress).getSession().setRequest(apduEntity);
        // 重置计时器：当长时间没有收到结束符的时候，就判定设备响应超时
        channelMap.get(remoteAddress).getSession().setLastRespond(System.currentTimeMillis());
        // 会话的UUID：帮助主从问答能够判定对应关系
        channelMap.get(remoteAddress).getSession().setUuid(uuid);
        // 设置结束符：根据这些结束符，判定会话结束
        channelMap.get(remoteAddress).getSession().setWaitFrameType(waitFrameType);
        channelMap.get(remoteAddress).getSession().getEndFlag().clear();
        channelMap.get(remoteAddress).getSession().getEndFlag().addAll(endFlag);

    }

    public static synchronized void updateEntity4HeartBeat(Channel channel) {
        SocketAddress remoteAddress = channel.remoteAddress();
        if (!channelMap.containsKey(remoteAddress)) {
            return;
        }

        channelMap.get(remoteAddress).setHeartbeat(System.currentTimeMillis());
    }

    public static synchronized List<Iec104ClientLinkerEntity> queryEntityList4WaitLinked() {
        List<Iec104ClientLinkerEntity> resultList = new ArrayList<>();
        for (Map.Entry<SocketAddress, Iec104ClientLinkerEntity> entry : channelMap.entrySet()) {
            Iec104ClientLinkerEntity entity = entry.getValue();

            if (entity.getChannel() != null && !entity.isLinked()) {
                resultList.add(entity);
            }
        }

        return resultList;
    }

    public static synchronized List<Iec104ClientLinkerEntity> queryEntityList4IsLinked() {
        List<Iec104ClientLinkerEntity> resultList = new ArrayList<>();
        for (Map.Entry<SocketAddress, Iec104ClientLinkerEntity> entry : channelMap.entrySet()) {
            Iec104ClientLinkerEntity entity = entry.getValue();

            if (entity.getChannel() != null && entity.isLinked()) {
                resultList.add(entity);
            }
        }

        return resultList;
    }

    public static synchronized List<Iec104ClientLinkerEntity> queryEntityList4IsBlocked(long block) {
        long time = System.currentTimeMillis();

        List<Iec104ClientLinkerEntity> resultList = new ArrayList<>();
        for (Map.Entry<SocketAddress, Iec104ClientLinkerEntity> entry : channelMap.entrySet()) {
            Iec104ClientLinkerEntity entity = entry.getValue();

            // 检查：是否为闲置状态
            if (entity.getSession().isIdle()) {
                continue;
            }

            // 检查：上次接收时间到现在是否超过时间范围
            if (entity.getSession().getLastRespond() + block < time) {
                resultList.add(entity);
            }
        }

        return resultList;
    }

    public static synchronized Iec104ClientLinkerEntity queryEntity(SocketAddress remoteAddress) {
        return channelMap.get(remoteAddress);
    }

    public static synchronized void insertApdu4IFrameRespond(Channel channel, ApduEntity entity) throws Exception {
        SocketAddress remoteAddress = channel.remoteAddress();
        if (!channelMap.containsKey(remoteAddress)) {
            return;
        }

        // 记录接收到的APDU实体
        Iec104ClientSessionEntity session = channelMap.get(remoteAddress).getSession();
        session.respondSession(entity);

        // 检查：会话是否结束
        if (!CotReasonEnum.isEnd(entity.getAsdu().getCot().getReason())) {
            return;
        }

        // 场景1（一问一答）：用户侧未指明期待的结束符，则认为所有的结束符都是结束
        if (session.getEndFlag().isEmpty()) {
            handler.finishedRespond(channelMap.get(remoteAddress));

            // 重置会话，为下一次会话做准备
            session.resetSession();
            session.increaseLastSend();
        }

        // 场景2（一问多答）：用户侧已经指明期待的结束符，那么只有该结束符出现，才认为真正结束。
        if (!session.getEndFlag().isEmpty() && session.getEndFlag().contains(entity.getAsdu().getCot().getReason())) {
            handler.finishedRespond(channelMap.get(remoteAddress));

            // 重置会话，为下一次会话做准备
            session.resetSession();
            session.increaseLastSend();
        }
    }

    public static synchronized void insertApdu4SFrameRespond(Channel channel, ApduEntity entity) throws Exception{
        SocketAddress remoteAddress = channel.remoteAddress();
        if (!channelMap.containsKey(remoteAddress)) {
            return;
        }

        // 记录接收到的APDU实体
        Iec104ClientSessionEntity session = channelMap.get(remoteAddress).getSession();
        session.respondSession(entity);


        handler.finishedRespond(channelMap.get(remoteAddress));

        // 重置会话，为下一次会话做准备
        session.resetSession();
        session.increaseLastSend();
    }
}
