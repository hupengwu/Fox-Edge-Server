package cn.foxtech.channel.common.utils;

import cn.foxtech.channel.domain.ChannelVOConstant;

import java.util.HashMap;
import java.util.Map;

public class ChannelStatusUtils {
    public static Map<String, Object> buildStatus(boolean isOpen, long activeTime) {
        Map<String, Object> result = new HashMap<>();
        result.put(ChannelVOConstant.value_channel_status_is_open, isOpen);
        result.put(ChannelVOConstant.value_channel_status_is_active, activeTime);
        return result;
    }
}
