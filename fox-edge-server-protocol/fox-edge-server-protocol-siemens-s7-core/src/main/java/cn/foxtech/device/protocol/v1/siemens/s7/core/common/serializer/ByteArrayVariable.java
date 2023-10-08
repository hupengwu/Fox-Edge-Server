package cn.foxtech.device.protocol.v1.siemens.s7.core.common.serializer;


import cn.foxtech.device.protocol.v1.siemens.s7.core.common.enums.EDataType;

import java.lang.annotation.*;

/**
 * 字节数组变量参数
 */
@Target(value = {ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ByteArrayVariable {

    /**
     * 字节偏移量
     *
     * @return 字节偏移量
     */
    int byteOffset() default 0;

    /**
     * 位偏移量
     *
     * @return 位偏移量
     */
    int bitOffset() default 0;

    /**
     * 数量，数量大于1的时候对应的数据必须使用list
     *
     * @return 数量
     */
    int count() default 1;

    /**
     * 类型
     *
     * @return 类型
     */
    EDataType type() default EDataType.BYTE;

    /**
     * 是否小端模式
     *
     * @return 是否小端模式
     */
    boolean littleEndian() default false;

//    /**
//     * 数据格式
//     *
//     * @return 数据格式
//     */
//    EByteBuffFormat format() default EByteBuffFormat.DC_BA;
}
