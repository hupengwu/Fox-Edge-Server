package cn.foxtech.device.protocol.v1.utils.netty;

/**
 * 报文头格式：TCP报文通常是固定的报头+报长来解决粘包的问题，只是开发者对报头和报长度的位置各自定义。
 * 所以单独抽取一个基类型，作为切包的的方法。
 */
public class SplitMessageHandler {
    protected int[] header = new int[1];

    /**
     * 最小包长度检查
     *
     * @return 最小包长度
     */
    public int getHeaderLength() {
        return this.header.length;
    }

    public void setHeaderValue(int index, int value){
        header[index] = value;
    }

    /**
     * 是否为非法报文：通过检查报文头部，这些协议中约定的起始标记，判定该报文是否为合法的报文
     * @return 非法报文
     */
    public boolean isInvalidPack(){
        return false;
    }

    /**
     * 包长度信息：从minPack数组中，取出报文长度信息
     * @return 完整帧的报文长度
     */
    public int getPackLength() {
        return 1;
    }
}
