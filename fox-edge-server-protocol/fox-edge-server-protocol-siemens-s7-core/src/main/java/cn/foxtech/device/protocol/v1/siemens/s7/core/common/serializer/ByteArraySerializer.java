package cn.foxtech.device.protocol.v1.siemens.s7.core.common.serializer;


import cn.foxtech.device.protocol.v1.siemens.s7.core.common.buff.ByteReadBuff;
import cn.foxtech.device.protocol.v1.siemens.s7.core.common.buff.ByteWriteBuff;
import cn.foxtech.device.protocol.v1.siemens.s7.core.common.buff.EByteBuffFormat;
import cn.foxtech.device.protocol.v1.siemens.s7.core.common.enums.EDataType;
import cn.foxtech.device.protocol.v1.siemens.s7.core.exceptions.ByteArrayParseException;
import cn.foxtech.device.protocol.v1.siemens.s7.core.utils.BooleanUtil;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 字节数组序列化工具
 *
 * @author xingshuang
 */
public class ByteArraySerializer implements IByteArraySerializable {

    public static ByteArraySerializer newInstance() {
        return new ByteArraySerializer();
    }

    @Override
    public <T> T toObject(final Class<T> targetClass, final byte[] src) {
        try {
            final T bean = targetClass.newInstance();
            for (final Field field : targetClass.getDeclaredFields()) {
                final ByteArrayVariable variable = field.getAnnotation(ByteArrayVariable.class);
                if (variable == null) {
                    continue;
                }
                ByteArrayParameter parameter = new ByteArrayParameter(variable.byteOffset(), variable.bitOffset(),
                        variable.count(), variable.type(), variable.littleEndian());
                this.checkByteArrayVariable(parameter);
                this.extractData(src, bean, field, parameter);
            }
            return bean;
        } catch (Exception e) {
            throw new ByteArrayParseException("解析成对象错误，原因：" + e.getMessage(), e);
        }
    }

    /**
     * 转换为对象，提取参数数据
     *
     * @param parameter 参数
     * @param src       字节数组数据
     * @return 含值的参数
     */
    public ByteArrayParameter extractParameter(final ByteArrayParameter parameter, final byte[] src) {
        return this.extractParameter(Collections.singletonList(parameter), src).get(0);
    }

    /**
     * 转换为list对象，提取参数数据
     *
     * @param parameters 参数列表
     * @param src        字节数组数据
     * @return 含值的list参数
     */
    public List<ByteArrayParameter> extractParameter(final List<ByteArrayParameter> parameters, final byte[] src) {
        try {
            for (final ByteArrayParameter parameter : parameters) {
                if (parameter == null) {
                    throw new ByteArrayParseException("ByteArrayParameter列表中存在null");
                }

                this.checkByteArrayVariable(parameter);
                Field field = parameter.getClass().getDeclaredField("value");
                this.extractData(src, parameter, field, parameter);
            }
            return parameters;
        } catch (Exception e) {
            throw new ByteArrayParseException("解析成对象错误，原因：" + e.getMessage(), e);
        }
    }

    @Override
    public <T> byte[] toByteArray(final T targetBean) {
        try {
            // 组装数据，同时计算最大的字节长度
            int buffSize = 0;
            List<ByteArrayParseData> parseDataList = new ArrayList<>();
            for (final Field field : targetBean.getClass().getDeclaredFields()) {
                final ByteArrayVariable variable = field.getAnnotation(ByteArrayVariable.class);
                if (variable == null) {
                    continue;
                }
                ByteArrayParameter parameter = new ByteArrayParameter(variable.byteOffset(), variable.bitOffset(),
                        variable.count(), variable.type(), variable.littleEndian());
                this.checkByteArrayVariable(parameter);
                parseDataList.add(new ByteArrayParseData(variable, field));
                int maxPos = variable.byteOffset() + variable.count() * variable.type().getByteLength();
                if (maxPos > buffSize) {
                    buffSize = maxPos;
                }
            }
            if (buffSize == 0 || parseDataList.isEmpty()) {
                return new byte[0];
            }
            // 填充字节数组的内容
            ByteWriteBuff buff = ByteWriteBuff.newInstance(buffSize);
            for (ByteArrayParseData item : parseDataList) {
                item.getField().setAccessible(true);
                Object data = item.getField().get(targetBean);
                if (data == null) {
                    continue;
                }
                if (item.getVariable().count() == 1) {
                    this.fillOneData(item.getVariable(), data, buff, 0);
                } else {
                    this.fillListData(item.getVariable(), data, buff);
                }
            }
            return buff.getData();
        } catch (Exception e) {
            throw new ByteArrayParseException("解析成对象错误，原因：" + e.getMessage(), e);
        }
    }

    /**
     * 提取数据
     *
     * @param src      数据内容，字节数组
     * @param bean     对象
     * @param field    字段
     * @param variable 字节数组注解
     * @param <T>      类型
     * @throws IllegalAccessException 访问异常
     */
    private <T> void extractData(byte[] src, T bean, Field field, ByteArrayParameter variable) throws IllegalAccessException {
        ByteReadBuff buff = new ByteReadBuff(src, 0, variable.isLittleEndian(), EByteBuffFormat.DC_BA);
        field.setAccessible(true);
        switch (variable.getType()) {
            case BOOL:
                List<Boolean> booleans = IntStream.range(0, variable.getCount())
                        .mapToObj(x -> {
                            int byteAdd = variable.getByteOffset() + (variable.getBitOffset() + x) / 8;
                            int bitAdd = (variable.getBitOffset() + x) % 8;
                            return buff.getBoolean(byteAdd, bitAdd);
                        }).collect(Collectors.toList());
                field.set(bean, variable.getCount() == 1 ? booleans.get(0) : booleans);
                break;
            case BYTE:
                List<Byte> bytes = IntStream.range(0, variable.getCount())
                        .mapToObj(x -> buff.getByte(variable.getByteOffset() + x * variable.getType().getByteLength()))
                        .collect(Collectors.toList());
                field.set(bean, variable.getCount() == 1 ? bytes.get(0) : bytes);
                break;
            case UINT16:
                List<Integer> uint16s = IntStream.range(0, variable.getCount())
                        .mapToObj(x -> buff.getUInt16(variable.getByteOffset() + x * variable.getType().getByteLength()))
                        .collect(Collectors.toList());
                field.set(bean, variable.getCount() == 1 ? uint16s.get(0) : uint16s);
                break;
            case INT16:
                List<Short> int16s = IntStream.range(0, variable.getCount())
                        .mapToObj(x -> buff.getInt16(variable.getByteOffset() + x * variable.getType().getByteLength()))
                        .collect(Collectors.toList());
                field.set(bean, variable.getCount() == 1 ? int16s.get(0) : int16s);
                break;
            case UINT32:
                List<Long> uint32s = IntStream.range(0, variable.getCount())
                        .mapToObj(x -> buff.getUInt32(variable.getByteOffset() + x * variable.getType().getByteLength()))
                        .collect(Collectors.toList());
                field.set(bean, variable.getCount() == 1 ? uint32s.get(0) : uint32s);
                break;
            case INT32:
                List<Integer> int32s = IntStream.range(0, variable.getCount())
                        .mapToObj(x -> buff.getInt32(variable.getByteOffset() + x * variable.getType().getByteLength()))
                        .collect(Collectors.toList());
                field.set(bean, variable.getCount() == 1 ? int32s.get(0) : int32s);
                break;
            case FLOAT32:
                List<Float> float32s = IntStream.range(0, variable.getCount())
                        .mapToObj(x -> buff.getFloat32(variable.getByteOffset() + x * variable.getType().getByteLength()))
                        .collect(Collectors.toList());
                field.set(bean, variable.getCount() == 1 ? float32s.get(0) : float32s);
                break;
            case FLOAT64:
                List<Double> float64s = IntStream.range(0, variable.getCount())
                        .mapToObj(x -> buff.getFloat64(variable.getByteOffset() + x * variable.getType().getByteLength()))
                        .collect(Collectors.toList());
                field.set(bean, variable.getCount() == 1 ? float64s.get(0) : float64s);
                break;
            case STRING:
                field.set(bean, buff.getString(variable.getByteOffset(), variable.getCount()));
                break;
            default:
                throw new ByteArrayParseException("提取数据的时候无法识别数据类型");
        }
    }

    /**
     * 校验字节数组注解的参数
     *
     * @param variable 参数
     */
    private void checkByteArrayVariable(ByteArrayParameter variable) {
        if (variable.getByteOffset() < 0) {
            throw new ByteArrayParseException("字节偏移量不能为负数");
        }
        if (variable.getCount() < 0) {
            throw new ByteArrayParseException("数据个数不能为负数");
        }
        if (variable.getType() == EDataType.BOOL && (variable.getBitOffset() > 7 || variable.getBitOffset() < 0)) {
            throw new ByteArrayParseException("当数据类型为bool时，位偏移量只能是[0,7]");
        }
    }

    /**
     * 填充一个数据
     *
     * @param variable 字节数组注解对象
     * @param data     数据对象
     * @param buff     字节缓存
     * @param index    索引，第几个
     */
    private void fillOneData(ByteArrayVariable variable, Object data, ByteWriteBuff buff, int index) {
        switch (variable.type()) {
            case BOOL:
                int byteAdd = variable.byteOffset() + (variable.bitOffset() + index) / 8;
                int bitAdd = (variable.bitOffset() + index) % 8;
                byte newByte = BooleanUtil.setBit(buff.getByte(byteAdd), bitAdd, (Boolean) data);
                buff.putByte(newByte, byteAdd);
                break;
            case BYTE:
                buff.putByte((Byte) data, variable.byteOffset() + index * variable.type().getByteLength());
                break;
            case UINT16:
                buff.putShort((Integer) data, variable.byteOffset() + index * variable.type().getByteLength(), variable.littleEndian());
                break;
            case INT16:
                buff.putShort((Short) data, variable.byteOffset() + index * variable.type().getByteLength(), variable.littleEndian());
                break;
            case UINT32:
                buff.putInteger((Long) data, variable.byteOffset() + index * variable.type().getByteLength(), variable.littleEndian());
                break;
            case INT32:
                buff.putInteger((Integer) data, variable.byteOffset() + index * variable.type().getByteLength(), variable.littleEndian());
                break;
            case FLOAT32:
                buff.putFloat((Float) data, variable.byteOffset() + index * variable.type().getByteLength(), variable.littleEndian());
                break;
            case FLOAT64:
                buff.putDouble((Double) data, variable.byteOffset() + index * variable.type().getByteLength(), variable.littleEndian());
                break;
            case STRING:
                buff.putString((String) data, StandardCharsets.US_ASCII, variable.byteOffset());
                break;
            default:
                throw new ByteArrayParseException("填充数据的时候无法识别数据类型");
        }
    }

    /**
     * 填充多个数据，必须是list的数据
     *
     * @param variable 字节数组注解对象
     * @param data     数据对象
     * @param buff     字节缓存
     */
    private void fillListData(ByteArrayVariable variable, Object data, ByteWriteBuff buff) {
        if (variable.type() == EDataType.STRING) {
            buff.putString((String) data, StandardCharsets.US_ASCII, variable.byteOffset());
        } else {
            List<Object> list = (List<Object>) data;
            for (int i = 0; i < list.size(); i++) {
                this.fillOneData(variable, list.get(i), buff, i);
            }
        }
    }
}
