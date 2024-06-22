package cn.foxtech.common.entity.entity;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class RepoCompEntity extends RepoCompBase {
    /**
     * JSON参数
     */
    private Map<String, Object> compParam = new HashMap<>();

    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = super.makeServiceValueList();
        list.add(this.compParam);

        return list;
    }

    @Override
    public void bind(Map<String, Object> map) {
        super.bind(map);

        this.compParam = (Map<String, Object>) map.getOrDefault("compParam", new HashMap<>());
    }

    @Override
    public BaseEntity build(Map<String, Object> map) {
        try {
            if (map == null || map.isEmpty()) {
                return null;
            }

            RepoCompEntity entity = new RepoCompEntity();
            entity.bind(map);

            return entity;
        } catch (Exception e) {
            return null;
        }
    }
}
