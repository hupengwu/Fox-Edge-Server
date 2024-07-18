package cn.foxtech.kernel.system.repository.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class RepoCompScriptOperateEntity {
    /**
     * 操作命令
     */
    private String operateName;
    /**
     * 业务类型：device、channel
     */
    private String serviceType;
    /**
     * 操作模式: exchange/publish/report
     */
    private String operateMode;
    /**
     * 返回的数据类型：状态/记录
     */
    private String dataType;
    /**
     * 引擎参数：真正的操作内容
     */
    private Map<String, Object> engineParam;
}
