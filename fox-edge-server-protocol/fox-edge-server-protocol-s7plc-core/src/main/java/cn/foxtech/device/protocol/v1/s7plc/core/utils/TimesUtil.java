/*
 * MIT License
 *
 * Copyright (c) 2021-2099 Oscura (xingshuang) <xingshuang_cool@163.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package cn.foxtech.device.protocol.v1.s7plc.core.utils;


import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 时间工具
 *
 * @author xingshuang
 */
public class TimesUtil {

    private TimesUtil() {
        // NOOP
    }

    //region NTP

    /**
     * 获取NTP所有的秒时间
     *
     * @param dateTime 日期时间
     * @return 秒
     */
    public static long getNTPTotalSecond(LocalDateTime dateTime) {
        Duration between = Duration.between(getNTPOriginDateTime(), dateTime);
        return between.getSeconds();
    }

    /**
     * 获取NTP日期
     *
     * @param day 天
     * @return Date
     */
    public static LocalDate getNTPDate(long day) {
        return getUTCOriginDate().plusDays(day);
    }

    /**
     * 获取NTP时间
     *
     * @param second 秒
     * @return DateTime
     */
    public static LocalDateTime getNTPDateTime(long second) {
        return getNTPOriginDateTime().plusSeconds(second);
    }

    /**
     * 获取NTP起始时间
     *
     * @return 起始时间
     */
    public static LocalDate getNTPOriginDate() {
        return getNTPOriginDateTime().toLocalDate();
    }

    /**
     * 获取NTP起始时间
     *
     * @return 起始时间
     */
    public static LocalDateTime getNTPOriginDateTime() {
        return LocalDateTime.of(1900, 1, 1, 0, 0, 0);
    }

    //endregion


    //region UTC

    /**
     * 获取UTC所有的秒时间
     *
     * @param dateTime 日期时间
     * @return 秒
     */
    public static long getUTCTotalSecond(LocalDateTime dateTime) {
        Duration between = Duration.between(getUTCOriginDateTime(), dateTime);
        return between.getSeconds();
    }

    /**
     * 获取UTC日期
     *
     * @param day 天
     * @return Date
     */
    public static LocalDate getUTCDate(long day) {
        return getUTCOriginDate().plusDays(day);
    }

    /**
     * 获取UTC时间
     *
     * @param second 秒
     * @return DateTime
     */
    public static LocalDateTime getUTCDateTime(long second) {
        return getUTCOriginDateTime().plusSeconds(second);
    }

    /**
     * 获取UTC起始时间
     *
     * @return 起始时间
     */
    public static LocalDate getUTCOriginDate() {
        return getUTCOriginDateTime().toLocalDate();
    }

    /**
     * 获取UTC起始时间
     *
     * @return 起始时间
     */
    public static LocalDateTime getUTCOriginDateTime() {
        return LocalDateTime.of(1970, 1, 1, 0, 0, 0);
    }

    //endregion
}
