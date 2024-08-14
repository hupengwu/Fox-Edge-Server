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
 
package cn.foxtech.common.entity.service.redis;


import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RedisReaderService {
    private static final Map<String, RedisReader> map = new ConcurrentHashMap<>();


    public static synchronized <T> RedisReader newInstance(Class<T> clazz, RedisTemplate redisTemplate) {
        RedisReader instance = new RedisReader();
        instance.setEntityType(clazz.getSimpleName());
        instance.setRedisTemplate(redisTemplate);
        return instance;
    }

    public static synchronized <T> RedisReader getInstanceBySimpleName(String clazzSimpleName, RedisTemplate redisTemplate) {
        // 如果已经存在，那么取出该实例
        if (map.containsKey(clazzSimpleName)) {
            return map.get(clazzSimpleName);
        }

        // 分配并保存实例
        RedisReader instance = new RedisReader();
        instance.setEntityType(clazzSimpleName);
        instance.setRedisTemplate(redisTemplate);

        map.put(instance.getEntityType(), instance);
        return map.get(clazzSimpleName);
    }

    public static <T> RedisReader getInstance(Class<T> clazz, RedisTemplate redisTemplate) {
        return getInstanceBySimpleName(clazz.getSimpleName(), redisTemplate);
    }
}
