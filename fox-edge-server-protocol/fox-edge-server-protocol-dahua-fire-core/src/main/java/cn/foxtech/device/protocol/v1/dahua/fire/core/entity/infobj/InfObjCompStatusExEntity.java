package cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.IntegerUtil;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.TimeUtil;
import cn.foxtech.device.protocol.v1.utils.ByteUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 信息对象: 注册包
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class InfObjCompStatusExEntity extends InfObjEntity {
    /**
     * 系统类型（1 字节）
     */
    private int sysType = 0;
    /**
     * 系统地址（1 字节）
     */
    private int sysAddress = 0;
    /**
     * 部件类型（1 字节）
     */
    private int compType = 0;
    /**
     * 部件回路（2 字节）
     */
    private int compCirc = 0;
    /**
     * 部件节点（2 字节）
     */
    private int compNode = 0;
    /**
     * 部件状态（8 字节）
     */
    private long compStatus = 0;

    /**
     * 部件说明（31 字节）
     */
    private String compDescription = "";

    /**
     * 时间标签(6 字节)：控制单元中时间标签传输，秒在前，年在后，取自系统当前时间，如 15:14:17 11/9/19；
     */
    private String time = "2000-01-01 00:00:00";

    @Override
    public List<Integer> getAduSizes(byte[] data, int offset, int aduLength) {
        // 信息体的数量
        int count = data[offset + 1];

        // 类型标志[1 字节]+信息体数量[1 字节]+多个信息体对象[N 字节]
        int length = count * this.getSize();

        if (aduLength != 2 + length) {
            throw new ProtocolException("验证ADU的长度与具体的格式，不匹配");
        }

        // 返回列表
        List<Integer> aduList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            aduList.add(this.getSize());
        }
        return aduList;
    }

    public static void decodeEntity(byte[] data, InfObjCompStatusExEntity entity) {
        if (data.length != entity.getSize()) {
            throw new ProtocolException("注册包类型的信息对象，固定长度为110");
        }


        int index = 0;

        // 系统类型(1 字节)
        entity.sysType = data[index++] & 0xff;

        // 系统地址(1 字节)
        entity.sysAddress = data[index++] & 0xff;

        // 部件类型(1 字节)
        entity.compType = data[index++] & 0xff;

        // 部件回路(2 字节)
        entity.compCirc = IntegerUtil.decodeInteger2byte(data, index);
        index += 2;
        // 部件节点(2 字节)
        entity.compNode = IntegerUtil.decodeInteger2byte(data, index);
        index += 2;

        /**
         * 部件状态(8 字节)
         */
        entity.compStatus = IntegerUtil.decodeLong8byte(data, index);
        index += 8;
        /**
         * 部件说明(31 字节)
         */
        entity.compDescription = ByteUtils.decodeAscii(data, index, 31, true);
        index += 31;

        // 时间标签(6 字节)
        entity.time = TimeUtil.decodeTime6byte(data, index);
        index += 6;
    }

    public static byte[] encodeEntity(InfObjCompStatusExEntity entity) {
        byte[] data = new byte[entity.getEncodeSize()];


        int index = 0;

        // 系统类型(1 字节)
        data[index++] = (byte) entity.sysType;

        // 系统地址(1 字节)
        data[index++] = (byte) entity.sysAddress;

        // 部件类型(1 字节)
        data[index++] = (byte) entity.compType;

        // 部件回路(2 字节)
        IntegerUtil.encodeInteger2byte(entity.compCirc, data, index);
        index += 2;

        // 部件节点(2 字节)
        IntegerUtil.encodeInteger2byte(entity.compNode, data, index);
        index += 2;

        /**
         * 部件状态(8 字节)
         */
        IntegerUtil.encodeLong8byte(entity.compStatus, data, index);
        index += 8;

        /**
         * 部件说明(31 字节)
         */
        ByteUtils.encodeAscii(entity.compDescription, data, index, 31, true);
        index += 31;

        // 时间标签(6 字节)
        TimeUtil.encodeTime6byte(entity.time, data, index);
        index += 6;

        return data;
    }

    public int getSize() {
        return 52;
    }

    /**
     * 包长度
     *
     * @return 包长度
     */
    @Override
    public int getEncodeSize() {
        return getSize();
    }

    @Override
    public int getDecodeSize(byte[] data, int offset, int aduLength){
        return getSize();
    }

    @Override
    public void decode(byte[] data) {
        decodeEntity(data, this);
    }

    @Override
    public byte[] encode() {
        return encodeEntity(this);
    }


}
