package cn.foxtech.link.common.utils;

import cn.foxtech.link.domain.LinkVOConstant;

import java.util.HashMap;
import java.util.Map;

public class LinkStatusUtils {
    public static Map<String, Object> buildStatus(boolean isOpen, long activeTime) {
        Map<String, Object> result = new HashMap<>();
        result.put(LinkVOConstant.value_link_status_is_open, isOpen);
        result.put(LinkVOConstant.value_link_status_is_active, activeTime);
        return result;
    }
}
