package cn.foxtech.device.protocol.v1.s7plc.core.algorithm;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 合并组
 *
 * @author xingshuang
 */
@Data
public class S7ComGroup {

    /**
     * 数据项列表
     */
    private List<S7ComItem> items = new ArrayList<>();

    /**
     * 添加数据
     *
     * @param item 数据项
     */
    public void add(S7ComItem item) {
        this.items.add(item);
    }
}
