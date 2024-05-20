package cn.foxtech.value.ex.task.service.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据加工任务
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class DataTask {
    /**
     * 名称
     */
    private String taskName;
    /**
     * 数据源：输入数据的范围
     */
    private DataSource dataSource = new DataSource();
    /**
     * 缓存大小
     */
    private Integer cacheSize = 1;
    /**
     * 计算脚本
     */
    private String methodScript = "";

    public void bind(Map<String, Object> taskParam) {
        this.methodScript = (String) taskParam.getOrDefault("methodScript", "");
        this.cacheSize = (Integer) taskParam.getOrDefault("cacheSize", 1);

        Map<String, Object> dataSource = (Map<String, Object>) taskParam.getOrDefault("dataSource", new HashMap<>());
        this.dataSource.bind(dataSource);
    }
}
