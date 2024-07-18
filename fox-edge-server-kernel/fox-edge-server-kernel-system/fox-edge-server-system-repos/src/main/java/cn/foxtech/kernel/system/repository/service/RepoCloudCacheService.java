package cn.foxtech.kernel.system.repository.service;

import cn.foxtech.common.utils.redis.value.RedisValueService;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class RepoCloudCacheService extends RedisValueService {
    @Getter
    private final String key = "fox.edge.list:manage:repo:cache";

    /**
     * 初始化为永不过期
     */
    public RepoCloudCacheService() {
        this.setTimeout(-1L);
    }

    public void saveList(String modelType, List<Map<String, Object>> list) {
        super.set(modelType, list);
    }

    public List<Map<String, Object>> readList(String modelType) {
        try {
            Object list = super.get(modelType, 5);
            if (list == null) {
                return new ArrayList<>();
            }

            return (List<Map<String, Object>>) list;
        } catch (Exception e) {
            return new ArrayList<>();
        }

    }
}
