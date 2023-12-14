package cn.foxtech.kernel.system.repository.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

/**
 * 云端的mongodb风格的基础类
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class BaseModel {
    /**
     * ID
     */
    String id;
    /**
     * 创建时间
     */
    Long createTime;
    /**
     * 更新时间
     */
    Long updateTime;
}
