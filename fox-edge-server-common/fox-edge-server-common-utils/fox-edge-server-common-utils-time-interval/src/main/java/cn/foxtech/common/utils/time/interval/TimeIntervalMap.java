/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.time.interval;

import java.util.HashMap;
import java.util.Map;

public class TimeIntervalMap {
    /**
     * 运行时间
     */
    private final Map<String, Long> lastTimeMap = new HashMap<>();

    public boolean testLastTime(String key, long timeInterval) {
        return this.testLastTime(key, System.currentTimeMillis(), timeInterval);
    }

    public synchronized boolean testLastTime(String key, long startTime, long timeInterval) {
        // 检查：是否到了执行周期
        long lastTime = this.lastTimeMap.getOrDefault(key, 0L);
        if (!this.testLastTime(timeInterval, lastTime)) {
            return false;
        }
        this.lastTimeMap.put(key, startTime);
        return true;
    }

    private boolean testLastTime(long timeInterval, long lastTime) {
        if (timeInterval == -1) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        return currentTime - lastTime > timeInterval;
    }
}
