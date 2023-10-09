package cn.foxtech.device.protocol.v1.s7plc.core.service;


import cn.foxtech.device.protocol.v1.s7plc.core.model.RequestItem;
import cn.foxtech.device.protocol.v1.s7plc.core.utils.AddressUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 地址的包装类
 *
 * @author xingshuang
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MultiAddressRead {

    /**
     * 请求项列表
     */
    List<RequestItem> requestItems = new ArrayList<>();

    public MultiAddressRead addData(String address, int count) {
        this.requestItems.add(AddressUtil.parseByte(address, count));
        return this;
    }
}
