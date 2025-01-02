/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.time.interval;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TimeIntervalMap {
    /**
     * 运行时间
     */
    private final Map<String, Long> lastTimeMap = new HashMap<>();

    public static long calculate(long timeInterval, String timeUnit) {
        if (timeUnit.equals("second")) {
            return timeInterval * 1000;
        }
        if (timeUnit.equals("minute")) {
            return timeInterval * 1000 * 60;
        }
        if (timeUnit.equals("hour")) {
            return timeInterval * 1000 * 3600;
        }
        if (timeUnit.equals("day")) {
            return timeInterval * 1000 * 3600 * 24;
        }

        return -1;
    }

    public static long getFixTime(long currentTime, int timeInterval, String timeUnit) {
        Date date = new Date(currentTime);
        Calendar calendar = Calendar.getInstance();
        if (timeUnit.equals("second")) {
            calendar.setTime(date);
            calendar.set(Calendar.MILLISECOND, 0);


            int value = (calendar.get(Calendar.SECOND) / timeInterval) * timeInterval;
            calendar.set(Calendar.SECOND, value);
        }
        if (timeUnit.equals("minute")) {
            calendar.setTime(date);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            int value = (calendar.get(Calendar.MINUTE) / timeInterval) * timeInterval;
            calendar.set(Calendar.MINUTE, value);
        }
        if (timeUnit.equals("hour")) {
            calendar.setTime(date);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            int value = (calendar.get(Calendar.HOUR) / timeInterval) * timeInterval;
            calendar.set(Calendar.HOUR, value);
        }
        if (timeUnit.equals("day")) {
            calendar.setTime(date);
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            int value = (calendar.get(Calendar.DAY_OF_MONTH) / timeInterval) * timeInterval;
            calendar.set(Calendar.DAY_OF_MONTH, value);
        }

        return calendar.getTime().getTime();
    }

    public boolean testLastTime(String key, long timeInterval) {
        return this.testLastTime(key, System.currentTimeMillis(), timeInterval);
    }

    public synchronized boolean testLastTime(String key, long currentTime, long timeInterval) {
        // 检查：是否到了执行周期
        long lastTime = this.lastTimeMap.getOrDefault(key, 0L);
        if (!this.testLastTime(timeInterval, lastTime, currentTime)) {
            return false;
        }
        this.lastTimeMap.put(key, currentTime);
        return true;
    }

    private boolean testLastTime(long timeInterval, long lastTime, long currentTime) {
        if (timeInterval == -1) {
            return false;
        }
        if (lastTime == currentTime) {
            return false;
        }

        return currentTime - lastTime >= timeInterval;
    }
}
