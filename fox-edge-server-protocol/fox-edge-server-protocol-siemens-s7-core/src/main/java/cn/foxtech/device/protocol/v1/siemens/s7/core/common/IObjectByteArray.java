package cn.foxtech.device.protocol.v1.siemens.s7.core.common;


/**
 * 一个对象字节相关的接口
 *
 * @author xingshuang
 */
public interface IObjectByteArray {

    /**
     * 获取字节数组长度
     *
     * @return 长度结果
     */
    int byteArrayLength();

    /**
     * 转换为字节数组
     *
     * @return 字节数组
     */
    byte[] toByteArray();
}
