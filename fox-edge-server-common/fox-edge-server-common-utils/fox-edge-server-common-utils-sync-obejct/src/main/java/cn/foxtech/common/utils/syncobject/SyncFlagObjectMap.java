/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 *
 *     This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * --------------------------------------------------------------------------- */
 
package cn.foxtech.common.utils.syncobject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 标记类型的同步对象
 */
public class SyncFlagObjectMap {
    /**
     * 实例化对象
     */
    private static final SyncFlagObjectMap inst = new SyncFlagObjectMap();
    /**
     * map容器
     */
    private final Map<String, SyncFlagObject> map = new ConcurrentHashMap<>();

    /**
     * 进程空间内的唯一实例对象
     *
     * @return 进程空间类的唯一实例
     */
    public static SyncFlagObjectMap inst() {
        return inst;
    }

    /**
     * 重置信号
     *
     * @param key 通道名称
     */
    public void reset(String key) {
        // 检查：该channel是否实例了一个同步对象
        if (!this.map.containsKey(key)) {
            SyncFlagObject syncObject = new SyncFlagObject();
            this.map.put(key, syncObject);
        }

        // 取出同步对象
        SyncFlagObject syncObject = this.map.get(key);

        // 同步锁
        synchronized (syncObject.lock) {
            syncObject.flag = false;
        }
    }


    /**
     * 等待信号
     *
     * @param key     通道名
     * @param timeOut
     */
    private Object wait(String key, long timeOut, boolean dynamic) throws InterruptedException {
        // 检查：该channel是否实例了一个同步对象
        if (!this.map.containsKey(key)) {
            this.map.put(key, new SyncFlagObject());
        }

        // 取出同步对象
        SyncFlagObject syncObject = this.map.get(key);

        // 获得锁
        synchronized (syncObject.lock) {
            long start = System.currentTimeMillis();
            long end = start;

            // java线程之间的notify并不可靠，不能单纯的通过同一个对象的wait来判断
            while (end - start < timeOut) {
                // 等待信号
                syncObject.lock.wait(50);

                // 检查：是否为到达标记导致的停止等待
                if (syncObject.flag) {
                    this.map.remove(key);
                    return syncObject.data;
                } else {
                    end = System.currentTimeMillis();
                }
            }
        }

        this.map.remove(key);
        return null;
    }

    public Object waitConstant(String key, long timeOut) throws InterruptedException {
        return this.wait(key, timeOut, false);
    }

    public Object waitDynamic(String key, long timeOut) throws InterruptedException {
        return this.wait(key, timeOut, true);
    }

    /**
     * 通知事件到达
     *
     * @param key  通道名称
     * @param data 传递的数据
     */
    private void notify(String key, Object data, boolean dynamic) {
        SyncFlagObject syncObject = this.map.get(key);
        if (dynamic) {
            if (syncObject == null) {
                return;
            }
        } else {
            if (syncObject == null) {
                syncObject = new SyncFlagObject();
                this.map.put(key, syncObject);
            }
        }

        // 获得锁
        synchronized (syncObject.lock) {
            syncObject.flag = true;
            syncObject.data = data;

            // 发出信号通知
            syncObject.lock.notify();
        }
    }

    public void notifyConstant(String key, Object data) {
        this.notify(key, data, false);
    }

    public void notifyDynamic(String key, Object data) {
        this.notify(key, data, true);
    }

    /**
     * 是否有这个key：可以通过这来判断，是否为问答方式返回的数据，还是设备主动上报的数据
     * @param key
     * @return
     */
    public boolean containsKey(String key) {
        return this.map.containsKey(key);
    }

    /**
     * 标记类型的同步对象
     */
    private static class SyncFlagObject {
        private final Object lock = "";
        private boolean flag = false;
        private Object data = null;
    }
}
