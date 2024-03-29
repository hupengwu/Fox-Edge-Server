package cn.foxtech.channel.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class ChannelBaseVO {
    /**
     * 交换模式：发送并等待返回，比如对设备的问答查询
     */
    public static String MODE_EXCHANGE = "exchange";
    /**
     * 发布模式：只发送不等待接收，比如对设备的单向广播
     */
    public static String MODE_PUBLISH = "publish";
    /**
     * 接收模式：只接收不发送，比如设备的主动通知上报
     */
    public static String MODE_RECEIVE = "receive";

    /**
     * 管理模式：这不是通信模式，而是对通道进行管理
     */
    public static String MODE_MANAGE = "manage";

    /**
     * 通道类型
     */
    private String type;
    /**
     * UUID
     */
    private String uuid;
    /**
     * 通道名称
     */
    private String name;
    /**
     * 发送模式：问答模式，只送模式，接收模式
     */
    private String mode = ChannelBaseVO.MODE_EXCHANGE;
    /**
     * 发送数据
     */
    private Object send;
    /**
     * 接收到的数据
     */
    private Object recv;
    /**
     * 通信超时
     */
    private Integer timeout;
    /**
     * 重路由到某个topic
     */
    private String route;


    /**
     * 绑定信息：方便将request的信息复制给respond
     *
     * @param vo
     */
    public void bindBaseVO(ChannelBaseVO vo) {
        this.type = vo.type;
        this.uuid = vo.uuid;
        this.name = vo.name;
        this.mode = vo.mode;
        this.send = vo.send;
        this.recv = vo.recv;
        this.route = vo.route;
        this.timeout = vo.timeout;
    }
}
