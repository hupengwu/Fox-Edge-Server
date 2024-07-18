package cn.foxtech.kernel.system.repository.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 脚本软件版本信息
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class RepoCompScriptVersionEntity extends BaseModel {
    /**
     * 作者：主要是开发者
     */
    private String author;

    /**
     * 父节点RepoCompScriptEntity的ID
     */
    private String scriptId;
    /**
     * 版本描述
     */
    private String description;
    /**
     * 操作信息
     */
    private List<RepoCompScriptOperateEntity> operates = new ArrayList<>();
}
