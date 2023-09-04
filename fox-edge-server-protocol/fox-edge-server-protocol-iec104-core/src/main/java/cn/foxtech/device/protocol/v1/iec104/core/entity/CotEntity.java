package cn.foxtech.device.protocol.v1.iec104.core.entity;

import cn.foxtech.device.protocol.v1.iec104.core.enums.CotReasonEnum;
import lombok.Data;

@Data
public class CotEntity {
    /**
     * 传送原因
     */
    private int reason = CotReasonEnum.active.getValue();

    /**
     * T = 0 未试验 ； T = 1 试验 （一般 T= 0）
     */
    private boolean test = false;
    /**
     * P/N = 0 肯定 ； P/N = 1 否定 （正常为P/N = 0；P/N = 1说明该报文无效）
     */
    private boolean pn = true;

    /**
     * 源发地址：用来记录来时哪个主站的响应数据，一般写 0；
     */
    private int addr = 0;
}
