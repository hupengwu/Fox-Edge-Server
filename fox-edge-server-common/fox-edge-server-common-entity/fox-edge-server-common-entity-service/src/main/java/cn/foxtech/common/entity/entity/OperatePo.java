package cn.foxtech.common.entity.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@TableName("tb_operate")
public class OperatePo extends OperateMethodBase {
    /**
     * 脚本引擎
     */
    private String engineParam;

    /**
     * 扩展参数
     */
    private String extendParam;

    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueListList() {
        List<Object> list = this.makeServiceValueList();
        list.add(this.engineParam);
        list.add(this.extendParam);
        return list;
    }
}
