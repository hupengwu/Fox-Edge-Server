package cn.foxtech.common.entity.constant;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

public class BaseVOFieldConstant {
    public static final String field_id = "id";
    public static final String field_page_id = "pageId";

    public static final String field_page_num = "pageNum";
    public static final String field_page_size = "pageSize";
    public static final String field_limit = "limit";

    public static final String field_create_time = "createTime";
    public static final String field_update_time = "updateTime";

    public static final String field_order_by_desc = "orderByDesc";
    public static final String field_order_by_asc = "orderByAsc";


    /**
     * 分页查询
     * @param queryWrapper 过滤器
     * @param key 过滤条件
     * @param value 过滤值
     */
    public static void makePageQueryWrapper(Object queryWrapper, String key, Object value) {
        // 大于等于页面的起始ID
        if (key.equals(BaseVOFieldConstant.field_page_id)) {
            ((QueryWrapper) queryWrapper).ge("id", value);
        }
        // 页面的大小
        if (key.equals(BaseVOFieldConstant.field_page_size)) {
            ((QueryWrapper) queryWrapper).last("limit " + value);
        }
        // 排序
        if (key.equals(BaseVOFieldConstant.field_order_by_desc)) {
            ((QueryWrapper) queryWrapper).orderByDesc(value);
        }
        if (key.equals(BaseVOFieldConstant.field_order_by_asc)) {
            ((QueryWrapper) queryWrapper).orderByAsc(value);
        }
    }

    public static void makeQueryWrapper(Object queryWrapper, String key, Object value) {
        // 大于等于页面的起始ID
        if (key.equals(BaseVOFieldConstant.field_page_id)) {
            ((QueryWrapper) queryWrapper).ge("id", value);
        }
        // 页面的大小
        if (key.equals(BaseVOFieldConstant.field_page_size)) {
            ((QueryWrapper) queryWrapper).last("limit " + value);
        }
        if (key.equals(BaseVOFieldConstant.field_create_time)) {
            ((QueryWrapper) queryWrapper).ge("create_time", value);
        }
        if (key.equals(BaseVOFieldConstant.field_update_time)) {
            ((QueryWrapper) queryWrapper).ge("update_time", value);
        }
        // 页面的大小
        if (key.equals(BaseVOFieldConstant.field_limit)) {
            ((QueryWrapper) queryWrapper).last("limit " + value);
        }
        if (key.equals(BaseVOFieldConstant.field_order_by_desc)) {
            ((QueryWrapper) queryWrapper).orderByDesc(value);
        }
        if (key.equals(BaseVOFieldConstant.field_order_by_asc)) {
            ((QueryWrapper) queryWrapper).orderByAsc(value);
        }
    }
}
