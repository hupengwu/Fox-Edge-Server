/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.entity.utils;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.utils.pair.Pair;

import java.util.*;

public class ExtendUtils {
    /**
     * 扩展信息
     *
     * @param mapList          基本数据
     * @param extendEntityList 扩展数据
     * @param key              基础数据和扩展数据，彼此关联的字段，相当于SQL语句中JOIN ON A.KEY=B.KEY
     * @param property         需要从extendEntityList中添加的字段列表
     */
    public static void extend(List<Map<String, Object>> mapList, Collection<BaseEntity> extendEntityList, String key, Set<String> property) {
        List<Pair<String, String>> pairs = new ArrayList<>();
        pairs.add(new Pair<>(key, key));
        extend(mapList, extendEntityList, pairs, property);
    }

    public static void extend(List<Map<String, Object>> mapList, Collection<BaseEntity> extendEntityList, List<Pair<String, String>> pairs, Set<String> property) {
        List<Map<String, Object>> extendList = EntityVOBuilder.buildVOList(extendEntityList);
        extend(mapList, extendList, pairs, property);
    }

    public static void extend(List<Map<String, Object>> mapList, List<Map<String, Object>> extendList, List<Pair<String, String>> pairs, Set<String> property) {
        if (mapList == null) {
            return;
        }

        // 重新组织扩展信息，作为下一步的字典
        Map<Object, Map<String, Object>> indexMap = new HashMap<>();
        for (Map<String, Object> map : extendList) {
            List<Object> serviceKeys = new ArrayList<>();
            for (Pair<String, String> key : pairs) {
                Object value = map.get(key.getValue());
                if (value == null) {
                    continue;
                }

                serviceKeys.add(value);
            }
            if (serviceKeys.size() != pairs.size()) {
                continue;
            }

            indexMap.put(serviceKeys, map);
        }

        for (Map<String, Object> map : mapList) {
            List<Object> serviceKeys = new ArrayList<>();
            for (Pair<String, String> pair : pairs) {
                Object value = map.get(pair.getKey());
                if (value == null) {
                    continue;
                }

                serviceKeys.add(value);
            }
            if (serviceKeys.size() != pairs.size()) {
                continue;
            }

            Map<String, Object> extend = indexMap.get(serviceKeys);
            if (extend == null) {
                continue;
            }

            for (String p : property) {
                Object extendValue = extend.get(p);
                if (extendValue == null) {
                    continue;
                }

                map.put(p, extendValue);
            }
        }

    }


    /**
     * 扩展一列信息
     *
     * @param mapList   基本数据
     * @param extendMap 扩展信息
     * @param key       基础数据和扩展数据，彼此关联的字段，相当于SQL语句中LEFT JOIN ON A.KEY=B.KEY
     * @param property  扩展字段的名称
     */
    public static void extend(List<Map<String, Object>> mapList, Map extendMap, String key, String property) {
        if (mapList == null) {
            return;
        }

        for (Map<String, Object> map : mapList) {
            Object value = map.get(key);
            if (value == null) {
                continue;
            }

            Object extendValue = extendMap.get(value);
            if (extendValue == null) {
                continue;
            }

            map.put(property, extendValue);
        }
    }
}
