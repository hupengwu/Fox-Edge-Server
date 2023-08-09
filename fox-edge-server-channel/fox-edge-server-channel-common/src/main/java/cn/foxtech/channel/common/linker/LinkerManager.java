package cn.foxtech.channel.common.linker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class LinkerManager {
    private static final Map<String, LinkerEntity> channelMap = new HashMap<>();
    private static LinkerHandler handler = new LinkerHandler();

    /**
     * 绑定自定义的handler
     *
     * @param newHandler 绑定的handler
     */
    public static synchronized void bindHandler(LinkerHandler newHandler) {
        handler = newHandler;
    }

    /**
     * 注册远端设备
     * @param channelName 通道名称
     */
    public static synchronized void registerChannel(String channelName) {
        if (channelMap.containsKey(channelName)) {
            return;
        }

        channelMap.put(channelName, new LinkerEntity());
        channelMap.get(channelName).setChannelName(channelName);
        channelMap.get(channelName).setLinked(false);
    }

    public static synchronized void unregisterChannel(String channelName) {
        if (!channelMap.containsKey(channelName)) {
            return;
        }

        // 重置链路
        channelMap.get(channelName).setLinked(false);

        // 通知链路断开
        handler.linkDisconnected(channelMap.get(channelName));

        // 注销
        channelMap.remove(channelName);
    }


    public static synchronized void updateEntity4LinkStatus(String channelName, boolean isLinked) {
        if (!channelMap.containsKey(channelName)) {
            return;
        }

        channelMap.get(channelName).setLinked(isLinked);

        if (isLinked) {
            // 通知链路建立
            handler.linkConnected(channelMap.get(channelName));
        } else {
            // 通知链路断开
            handler.linkDisconnected(channelMap.get(channelName));
        }
    }

    public static synchronized void updateEntity4ActiveLinker(String channelName, boolean activeSuccess) {
        if (!channelMap.containsKey(channelName)) {
            return;
        }

        if (activeSuccess) {
            // 链路心跳激活成功
            channelMap.get(channelName).setLastActive(System.currentTimeMillis());
            channelMap.get(channelName).setFailActive(0);
        } else {
            // 链路心跳激活失败
            long count = channelMap.get(channelName).getFailActive();
            channelMap.get(channelName).setFailActive(count + 1);
        }
    }

    public static synchronized List<LinkerEntity> queryEntityListByLinkStatus(boolean linkStatus) {
        List<LinkerEntity> resultList = new ArrayList<>();
        for (Map.Entry<String, LinkerEntity> entry : channelMap.entrySet()) {
            LinkerEntity entity = entry.getValue();

            if (entity.isLinked() == linkStatus) {
                resultList.add(entity);
            }
        }

        return resultList;
    }

    public static synchronized LinkerEntity queryEntity(String channelName) {
        return channelMap.get(channelName);
    }
}
