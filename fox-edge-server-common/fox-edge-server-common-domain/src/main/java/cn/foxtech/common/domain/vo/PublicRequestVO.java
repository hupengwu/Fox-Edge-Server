package cn.foxtech.common.domain.vo;

/**
 * 传统的rpc风格的接口：跟restful风格比起来，主要是面向多种模块/自定义命令风格
 * restful风格：比较适用于对等通信模型
 * rpc风格：可以面向消息订阅/发布模型
 * 正因为FoxEdge比较多采用消息订阅模型，所以有rpc风格的接口
 */
public class PublicRequestVO extends PublicVO {
}
