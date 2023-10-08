package cn.foxtech.device.protocol.v1.siemens.s7.core.hex;

import cn.foxtech.device.protocol.v1.siemens.s7.core.exceptions.HexParseException;
import cn.foxtech.device.protocol.v1.siemens.s7.core.utils.FloatUtil;
import cn.foxtech.device.protocol.v1.siemens.s7.core.utils.IntegerUtil;
import cn.foxtech.device.protocol.v1.siemens.s7.core.utils.ShortUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 16进制数据解析
 *
 * @author ShuangPC
 */

public class HexParse {

    /**
     * 数据源
     */
    private final byte[] rdSrc;

    public HexParse() {
        this(new byte[0]);
    }

    public HexParse(byte[] rdSrc) {
        this.rdSrc = rdSrc;
    }

    private <T> List<T> toHandle(int byteOffset, int count, double typeByteLength, Function<Integer, T> fun) {
        if (byteOffset < 0 || byteOffset >= this.rdSrc.length) {
            throw new HexParseException(String.format("字节偏移量[%d] 超过 总数据长度[%d]", byteOffset, this.rdSrc.length));
        }
        if (count < 1) {
            throw new HexParseException(String.format("获取的数据个数[%d] < 1", count));
        }
        if (byteOffset + count * typeByteLength > this.rdSrc.length) {
            throw new HexParseException(String.format("字节偏移量[%d] + 数据个数[%d] * 类型字节长度[%f] > 总数据字节长度[%d]", byteOffset, count, typeByteLength, this.rdSrc.length));
        }
        List<T> res = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            res.add(fun.apply(i));
        }
        return res;
    }

    /**
     * 获取Boolean的数据
     *
     * @param byteOffset 字节偏移量
     * @param bitOffset  位偏移量
     * @return Boolean数据
     */
    public Boolean toBoolean(int byteOffset, int bitOffset) {
        return this.toBoolean(byteOffset, bitOffset, 1).get(0);
    }

    /**
     * 获取Boolean的数据列表
     *
     * @param byteOffset 字节偏移量
     * @param bitOffset  位偏移量
     * @param count      个数
     * @return Boolean的数据列表
     */
    public List<Boolean> toBoolean(int byteOffset, int bitOffset, int count) {
        if (bitOffset < 0 || bitOffset > 7) {
            throw new HexParseException("bitOffset位偏移量范围[0,7]");
        }

        return this.toHandle(byteOffset, count, 0.125, i -> {
            int bitAdd = (bitOffset + i) % 8;
            int byteAdd = byteOffset + (bitOffset + i) / 8;
            return (((this.rdSrc[byteAdd] & 0xFF) >> bitAdd) & 0x01) == 0x01;
        });
    }

    /**
     * 获取Int8数据
     *
     * @param byteOffset 字节偏移量
     * @return Int8数据
     */
    public Byte toInt8(int byteOffset) {
        return this.toInt8(byteOffset, 1).get(0);
    }

    /**
     * 获取Int8数据列表
     *
     * @param byteOffset 字节偏移量
     * @param count      个数
     * @return Int8数据列表
     */
    public List<Byte> toInt8(int byteOffset, int count) {
        return this.toHandle(byteOffset, count, 1, i -> this.rdSrc[byteOffset + i]);
    }

    /**
     * 获取UInt8数据
     *
     * @param byteOffset 字节偏移量
     * @return UInt8数据
     */
    public Integer toUInt8(int byteOffset) {
        return this.toUInt8(byteOffset, 1).get(0);
    }

    /**
     * 获取UInt8数据列表
     *
     * @param byteOffset 字节偏移量
     * @param count      个数
     * @return UInt8数据列表
     */
    public List<Integer> toUInt8(int byteOffset, int count) {
        return this.toHandle(byteOffset, count, 1, i -> this.rdSrc[byteOffset + i] & 0xFF);
    }

    /**
     * 获取Int16的数据
     *
     * @param byteOffset 字节偏移量
     * @return Int16数据
     */
    public Short toInt16(int byteOffset) {
        return this.toInt16(byteOffset, 1, false).get(0);
    }

    /**
     * 获取Int16的数据列表
     *
     * @param byteOffset   字节偏移量
     * @param count        个数
     * @param littleEndian 小端模式
     * @return Int16的数据列表
     */
    public List<Short> toInt16(int byteOffset, int count, boolean littleEndian) {
        int typeByteLength = 2;
        return this.toHandle(byteOffset, count, typeByteLength, i -> ShortUtil.toInt16(this.rdSrc, byteOffset + i * typeByteLength, littleEndian));
    }

    /**
     * 获取单个UInt16的数据
     *
     * @param byteOffset 字节偏移量
     * @return UInt16数据
     */
    public Integer toUInt16(int byteOffset) {
        return this.toUInt16(byteOffset, 1, false).get(0);
    }

    /**
     * 获取UInt16的数据列表
     *
     * @param byteOffset   字节偏移量
     * @param count        个数
     * @param littleEndian 小端模式
     * @return UInt16的数据列表
     */
    public List<Integer> toUInt16(int byteOffset, int count, boolean littleEndian) {
        int typeByteLength = 2;
        return this.toHandle(byteOffset, count, typeByteLength, i -> ShortUtil.toUInt16(this.rdSrc, byteOffset + i * typeByteLength, littleEndian));
    }

    /**
     * 获取Int32数据
     *
     * @param byteOffset 字节偏移量
     * @return Int32数据
     */
    public Integer toInt32(int byteOffset) {
        return this.toInt32(byteOffset, 1, false).get(0);
    }

    /**
     * 获取Int32的数据列表
     *
     * @param byteOffset   字节偏移量
     * @param count        个数
     * @param littleEndian 小端模式
     * @return Int32的数据列表
     */
    public List<Integer> toInt32(int byteOffset, int count, boolean littleEndian) {
        int typeByteLength = 4;
        return this.toHandle(byteOffset, count, typeByteLength, i -> IntegerUtil.toInt32(this.rdSrc, byteOffset + i * typeByteLength, littleEndian));
    }

    /**
     * 获取UInt32数据
     *
     * @param byteOffset 字节偏移量
     * @return UInt32数据
     */
    public Long toUInt32(int byteOffset) {
        return this.toUInt32(byteOffset, 1, false).get(0);
    }

    /**
     * 获取UInt32的数据列表
     *
     * @param byteOffset   字节偏移量
     * @param count        个数
     * @param littleEndian 小端模式
     * @return UInt32的数据列表
     */
    public List<Long> toUInt32(int byteOffset, int count, boolean littleEndian) {
        int typeByteLength = 4;
        return this.toHandle(byteOffset, count, typeByteLength, i -> IntegerUtil.toUInt32(this.rdSrc, byteOffset + i * typeByteLength, littleEndian));
    }

    /**
     * 获取Float32的数据
     *
     * @param byteOffset 字节偏移量
     * @return Float32数据
     */
    public Float toFloat32(int byteOffset) {
        return this.toFloat32(byteOffset, 1, false).get(0);
    }

    /**
     * 获取Float32的数据列表
     *
     * @param byteOffset   字节偏移量
     * @param count        个数
     * @param littleEndian 小端模式
     * @return Float32的数据列表
     */
    public List<Float> toFloat32(int byteOffset, int count, boolean littleEndian) {
        int typeByteLength = 4;
        return this.toHandle(byteOffset, count, typeByteLength, i -> FloatUtil.toFloat32(this.rdSrc, byteOffset + i * typeByteLength, littleEndian));
    }

    /**
     * 获取Float64的数据
     *
     * @param byteOffset 字节偏移量
     * @return Float64数据
     */
    public Double toFloat64(int byteOffset) {
        return this.toFloat64(byteOffset, 1, false).get(0);
    }

    /**
     * 获取Float64的数据列表
     *
     * @param byteOffset   字节偏移量
     * @param count        个数
     * @param littleEndian 小端模式
     * @return Float64的数据列表
     */
    public List<Double> toFloat64(int byteOffset, int count, boolean littleEndian) {
        int typeByteLength = 8;
        return this.toHandle(byteOffset, count, typeByteLength, i -> FloatUtil.toFloat64(this.rdSrc, byteOffset + i * typeByteLength, littleEndian));
    }

    /**
     * 获取UTF-8格式的字符串
     *
     * @param byteOffset 字节偏移量
     * @param count      数量
     * @return UTF-8格式的字符串
     */
    public String toStringUtf8(int byteOffset, int count) {
        int typeByteLength = 1;
        if (byteOffset < 0 || byteOffset >= this.rdSrc.length) {
            throw new HexParseException(String.format("字节偏移量[%d] 超过 总数据长度[%d]", byteOffset, this.rdSrc.length));
        }
        if (count < 1) {
            throw new HexParseException(String.format("获取的数据个数[%d] < 1", count));
        }
        if (byteOffset + count * typeByteLength > this.rdSrc.length) {
            throw new HexParseException(String.format("字节偏移量[%d] + 数据个数[%d] * 类型字节长度[%f] > 总数据字节长度[%d]", byteOffset, count, typeByteLength, this.rdSrc.length));
        }
        byte[] bs = new byte[count];
        System.arraycopy(this.rdSrc, byteOffset, bs, 0, count);
        return new String(bs, StandardCharsets.UTF_8);
    }

    /**
     * 根据DataUnit类型解析数据
     *
     * @param unit 数据单元
     */
    @SuppressWarnings("unchecked")
    public void parseData(DataUnit unit) {
        switch (unit.getDataType()) {
            case BOOL:
                if (unit.getCount() == 1) {
                    unit.setValue(this.toBoolean(unit.getByteOffset(), unit.getBitOffset()));
                } else {
                    unit.setValue(this.toBoolean(unit.getByteOffset(), unit.getBitOffset(), unit.getCount()));
                }
                break;
            case INT8:
                if (unit.getCount() == 1) {
                    unit.setValue(this.toInt8(unit.getByteOffset()));
                } else {
                    unit.setValue(this.toInt8(unit.getByteOffset(), unit.getCount()));
                }
                break;
            case UINT8:
                if (unit.getCount() == 1) {
                    unit.setValue(this.toUInt8(unit.getByteOffset()));
                } else {
                    unit.setValue(this.toUInt8(unit.getByteOffset(), unit.getCount()));
                }
                break;
            case INT16:
                if (unit.getCount() == 1) {
                    unit.setValue(this.toInt16(unit.getByteOffset()));
                } else {
                    unit.setValue(this.toInt16(unit.getByteOffset(), unit.getCount(), unit.getLittleEndian()));
                }
                break;
            case UINT16:
                if (unit.getCount() == 1) {
                    unit.setValue(this.toUInt16(unit.getByteOffset()));
                } else {
                    unit.setValue(this.toUInt16(unit.getByteOffset(), unit.getCount(), unit.getLittleEndian()));
                }
                break;
            case INT32:
                if (unit.getCount() == 1) {
                    unit.setValue(this.toInt32(unit.getByteOffset()));
                } else {
                    unit.setValue(this.toInt32(unit.getByteOffset(), unit.getCount(), unit.getLittleEndian()));
                }
                break;
            case UINT32:
                if (unit.getCount() == 1) {
                    unit.setValue(this.toUInt32(unit.getByteOffset()));
                } else {
                    unit.setValue(this.toUInt32(unit.getByteOffset(), unit.getCount(), unit.getLittleEndian()));
                }
                break;
            case FLOAT32:
                if (unit.getCount() == 1) {
                    unit.setValue(this.toFloat32(unit.getByteOffset()));
                } else {
                    unit.setValue(this.toFloat32(unit.getByteOffset(), unit.getCount(), unit.getLittleEndian()));
                }
                break;
            case FLOAT64:
                if (unit.getCount() == 1) {
                    unit.setValue(this.toFloat64(unit.getByteOffset()));
                } else {
                    unit.setValue(this.toFloat64(unit.getByteOffset(), unit.getCount(), unit.getLittleEndian()));
                }
                break;
            case STRING:
                unit.setValue(this.toStringUtf8(unit.getByteOffset(), unit.getCount()));
                break;
            default:
                throw new HexParseException("无法解析数据，数据类型不存在");
        }
    }

    /**
     * 解析数据列表
     *
     * @param list 数据列表
     */
    public void parseDataList(List<DataUnit> list) {
        list.forEach(this::parseData);
    }
}
