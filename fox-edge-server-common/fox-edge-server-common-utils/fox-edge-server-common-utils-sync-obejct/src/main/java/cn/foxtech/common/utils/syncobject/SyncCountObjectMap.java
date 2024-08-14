/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.syncobject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 计数器类型的同步对象
 */
public class SyncCountObjectMap {
    /**
     * 实例化对象
     */
    private static final SyncCountObjectMap inst = new SyncCountObjectMap();
    /**
     * map容器
     */
    private final Map<String, SyncCountObject> map = new ConcurrentHashMap<>();

    /**
     * 进程空间内的唯一实例对象
     *
     * @return 进程空间类的唯一实例
     */
    public static SyncCountObjectMap inst() {
        return inst;
    }

    /**
     * 重置信号
     * @param channelName 通道名称
     */
    public void reset(String channelName,int max) {
        // 检查：该channel是否实例了一个同步对象
        if (!this.map.containsKey(channelName)) {
            this.map.put(channelName, new SyncCountObject());
        }

        // 取出同步对象
        SyncCountObject syncObject = this.map.get(channelName);

        // 同步锁
        synchronized (syncObject.lock) {
            syncObject.count = 0;
            syncObject.max = max;
            syncObject.dataList.clear();
        }
    }

    /**
     * 等待信号
     *
     * @param channelName 通道名
     * @param timeOut
     */
    public List<Object> wait(String channelName, long timeOut) {
        // 检查：该channel是否实例了一个同步对象
        if (!this.map.containsKey(channelName)) {
            this.map.put(channelName, new SyncCountObjectMap.SyncCountObject());
        }

        // 取出同步对象
        SyncCountObjectMap.SyncCountObject syncObject = this.map.get(channelName);

        // 同步锁
        synchronized (syncObject.lock) {
            // 等待标记变化：直到最大超时
            long start = System.currentTimeMillis();
            while (syncObject.count < syncObject.max) {
                try {
                    syncObject.lock.wait(5);
                    if (System.currentTimeMillis() > start + timeOut) {
                        break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (syncObject.count >= syncObject.max) {
                return syncObject.dataList;
            }
        }

        return syncObject.dataList;
    }

    /**
     * 通知事件到达
     *
     * @param channelName 通道名称
     * @param data        传递的数据
     */
    public void notify(String channelName, Object data) {
        if (!this.map.containsKey(channelName)) {
            this.map.put(channelName, new SyncCountObject());
        }
        SyncCountObject syncObject = this.map.get(channelName);

        synchronized (syncObject.lock) {
            syncObject.count++;
            syncObject.dataList.add(data);
        }
    }

    /**
     * 标记类型的同步对象
     */
    private static class SyncCountObject {
        private final Object lock = "";
        private int count = 0;
        private int max = 0;
        private List<Object> dataList = new ArrayList<>();
    }
}
