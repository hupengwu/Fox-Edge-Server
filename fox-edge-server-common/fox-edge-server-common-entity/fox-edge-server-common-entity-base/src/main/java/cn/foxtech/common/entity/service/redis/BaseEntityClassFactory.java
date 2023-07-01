package cn.foxtech.common.entity.service.redis;

import cn.foxtech.common.entity.entity.BaseEntity;
import cn.foxtech.common.utils.reflect.JarLoaderUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 它会扫描cn.foxtech.common.entity.entity包下的所有Entity类
 * 所以，派生类也要确保放在cn.foxtech.common.entity.entity包下
 */
public class BaseEntityClassFactory {
    private static final Map<String, Class> map = new ConcurrentHashMap<>();

    /**
     * 获得对应实体的Class
     *
     * @param entityType
     * @return
     */
    public static synchronized Class getInstance(String entityType) {
        if (map.isEmpty()) {
            Map<String, Class> clazzMap = scan();
            map.putAll(clazzMap);
        }

        return map.get(entityType);
    }

    /**
     * 扫描BaseEntity的子类
     *
     * @return
     */
    private static Map<String, Class> scan() {
        String packName = BaseEntity.class.getPackage().getName();

        Map<String, Class> result = new HashMap<>();
        Set<Class<?>> classSet = JarLoaderUtils.getClasses(packName);
        for (Class<?> clazz : classSet) {
            // 检查：是否为自己
            if (BaseEntity.class.equals(clazz)) {
                continue;
            }
            //检查：是否为派生类
            if (!BaseEntity.class.isAssignableFrom(clazz)) {
                continue;
            }

            result.put(clazz.getSimpleName(), clazz);
        }

        return result;
    }
}
