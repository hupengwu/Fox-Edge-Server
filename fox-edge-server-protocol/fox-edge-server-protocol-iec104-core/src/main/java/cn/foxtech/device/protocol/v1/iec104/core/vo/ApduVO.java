/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.iec104.core.vo;

import cn.foxtech.device.protocol.v1.iec104.core.enums.FrameTypeEnum;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class ApduVO {
    /**
     * 控制信息:1+1+4字节
     */
    private ControlVO control = new ControlVO();

    /**
     * 数据单元：6+N字节
     * 只有I帧才包含这段数据，S帧和U帧不包含这段数据
     */
    private AsduVO asdu;

    /**
     * 期待的结束符
     */
    private String waitFrameType = FrameTypeEnum.I_FORMAT.name();

    /**
     * 期待的I帧结束符
     */
    private Set<Integer> waitEndFlag = new HashSet<>();

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    public static class ControlVO {
        /**
         * 帧类型：I/U/S
         */
        private String type;

        /**
         * U帧命令字
         */
        private String cmd;

        /**
         * S帧和I帧：发送序列号N(S)
         */
        private Short send;

        /**
         * S帧和I帧：接收序列号N(R)
         */
        private Short accept;
    }

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    public static class AsduVO {
        /**
         * 类型标识TI:1字节
         */
        private int typeId;
        /**
         * 可变结构限定词:1字节
         */
        private VsqVO vsq = new VsqVO();
        /**
         * 传输原因:2字节
         */
        private CotVO cot = new CotVO();


        /**
         * 应用服务数据单元公共地址:2字节
         */
        private int commonAddress = 1;
        /**
         * 信息体
         */
        private String data;
    }

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    public static class VsqVO {
        /**
         * 地址是否连续
         */
        private boolean sq = false;
        /**
         * 当地址为连续时，信息体元素地址数量   0-7位
         */
        private int num = 0;
    }

    @Getter(AccessLevel.PUBLIC)
    @Setter(AccessLevel.PUBLIC)
    public static class CotVO {
        /**
         * 传送原因
         */
        private int reason = 0;

        /**
         * 传送原因描述
         */
        private String reasonMsg;

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
}
