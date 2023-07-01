package cn.foxtech.common.entity.constant;

public class DeviceMapperVOFieldConstant extends BaseVOFieldConstant {
    public static final String field_device_type = "deviceType";
    public static final String field_object_name = "objectName";
    public static final String field_mapper_name = "mapperName";
    public static final String field_mapper_mode = "mapperMode";


    /**
     * 映射模式：
     *
     * original   0  原始值：例如将"对象1"依然保留为“对象1"
     * replace    1  替换，例如将"对象1"重命名成“object 1”，只保留
     * duplicate  2  副本，例如为“对象1”新增一个"object 1"的副本，同时保留"对象1"
     * filter     3  过滤，例如将“对象1”剔除掉
     */
    public static final int mapper_mode_original = 0;
    public static final int mapper_mode_replace = 1;
    public static final int mapper_mode_duplicate = 2;
    public static final int mapper_mode_filter = 3;
}
