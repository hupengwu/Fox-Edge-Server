package cn.foxtech.huawei.iotda.service.entity.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {
    public static String getUTCTime() {
        Date current = Date.from(Instant.now());

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(current);
    }
}
