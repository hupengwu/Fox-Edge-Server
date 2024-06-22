package cn.foxtech.common.entity.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将离散的设备参数聚合成复合的设备参数
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ExtendConfigEntity extends ExtendConfigBase {
    /**
     * 配置集合
     */
    private ExtendParam extendParam = new ExtendParam();

    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = super.makeServiceValueList();
        list.add(this.extendParam);

        return list;
    }

    @Override
    public void bind(Map<String, Object> map) {
        super.bind(map);

        Map<String, Object> extendParam = (Map<String, Object>) map.getOrDefault("extendParam", new HashMap<>());
        this.extendParam.bind(extendParam);
    }

    @Override
    public BaseEntity build(Map<String, Object> map) {
        try {
            if (map == null || map.isEmpty()) {
                return null;
            }

            ExtendConfigEntity entity = new ExtendConfigEntity();
            entity.bind(map);

            return entity;
        } catch (Exception e) {
            return null;
        }
    }
}
