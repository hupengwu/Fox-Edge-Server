package cn.foxtech.common.entity.utils;

import cn.foxtech.common.utils.bean.BeanMapUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 将Entity生成VO的数据结构：要剔除掉一些Entity内部数据
 */
public class EntityVOBuilder {
    public static <T> List<String> getFilterKeys() {
        List<String> filterKeys = new ArrayList<>();

        filterKeys.add("serviceKey");
        filterKeys.add("serviceKeyList");
        filterKeys.add("serviceValue");
        filterKeys.add("wrapperKey");
        return filterKeys;
    }

    public static <T> List<Map<String, Object>> buildVOList(Collection<T> entityList) {
        return BeanMapUtils.objectToMap(entityList, getFilterKeys());
    }

    public static <T> Map<String, Object> buildVO(T entity) {
        return BeanMapUtils.objectToMap(entity, getFilterKeys());
    }
}
