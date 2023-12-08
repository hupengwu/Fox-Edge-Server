package cn.foxtech.common.entity.entity;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class RepoCompBase extends BaseEntity {
    /**
     * 组件仓库：cloud或local
     */
    private String compRepo;
    /**
     * 组件业务类型：jar-decoder/jsp-decoder
     */
    private String compType;
    /**
     * 组件名称：这个是自动化生成
     */
    private String compName;

    /**
     * 业务Key
     *
     * @return 业务Key
     */
    public List<Object> makeServiceKeyList() {
        List<Object> list = new ArrayList<>();
        list.add(this.compRepo);
        list.add(this.compType);
        list.add(this.compName);

        return list;
    }

    /**
     * 查询过滤器
     *
     * @return 过滤器
     */
    public Object makeWrapperKey() {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("comp_repo", this.compRepo);
        queryWrapper.eq("comp_type", this.compType);
        queryWrapper.eq("comp_name", this.compName);

        return queryWrapper;
    }

    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = new ArrayList<>();
        return list;
    }

    public void bind(RepoCompBase other) {
        this.setId(other.getId());
        this.setCreateTime(other.getCreateTime());
        this.setUpdateTime(other.getUpdateTime());

        this.compRepo = other.compRepo;
        this.compType = other.compType;
        this.compName = other.compName;
    }
}
