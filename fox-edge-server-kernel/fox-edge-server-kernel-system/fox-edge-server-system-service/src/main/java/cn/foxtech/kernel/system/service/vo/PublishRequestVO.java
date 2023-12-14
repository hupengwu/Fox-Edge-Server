package cn.foxtech.kernel.system.service.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class PublishRequestVO {
    /**
     * 边缘节点的ID
     */
    private String edge_id = "";

    /**
     * 发布的数据库表名称
     */
    private String table_name = "";

    /**
     * 时间戳
     */
    private String time_stamp = "";


    /**
     * 数据库内容
     */
    private List<Map<String, Object>> data = new ArrayList<>();
}
