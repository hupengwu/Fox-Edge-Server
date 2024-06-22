package cn.foxtech.common.entity.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
@TableName("tb_user_menu")
public class UserMenuPo extends UserMenuBase {
    /**
     * 菜单列表
     */
    private String menu = "[]";


    /**
     * 获取业务值
     *
     * @return 对象列表
     */
    public List<Object> makeServiceValueList() {
        List<Object> list = super.makeServiceValueList();
        list.add(this.menu);

        return list;
    }
}
