package cn.foxtech.device.protocol.v1.dahua.fire.core.entity.infobj;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.IntegerUtil;
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
public class InfObjSetFuncReqEntity extends InfObjEntity {
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
     * 操作类型（2 字节）
     */
    private int operateType = 0;
    /**
     * 操作参数（2 字节）
     */
    private int operateValue = 0;
    /**
     * 预留（2 字节）
     */
    private int reserve = 0;

    public static void decodeEntity(byte[] data, InfObjSetFuncReqEntity entity) {
        if (data.length != entity.getSize()) {
            throw new ProtocolException("信息对象" + entity.getClass().getSimpleName() + "，必须长度为" + entity.getSize());
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

        // 操作类型(2 字节)
        entity.operateType = IntegerUtil.decodeInteger2byte(data, index);
        index += 2;

        // 操作参数(2 字节)
        entity.operateValue = IntegerUtil.decodeInteger2byte(data, index);
        index += 2;

        // 操作参数(2 字节)
        entity.reserve = IntegerUtil.decodeInteger2byte(data, index);
        index += 2;
    }

    public static byte[] encodeEntity(InfObjSetFuncReqEntity entity) {
        byte[] data = new byte[entity.getSize()];


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
         * 操作类型(2 字节)
         */
        IntegerUtil.encodeInteger2byte(entity.operateType, data, index);
        index += 2;

        /**
         * 操作参数(2 字节)
         */
        IntegerUtil.encodeInteger2byte(entity.operateValue, data, index);
        index += 2;

        /**
         * 预留(2 字节)
         */
        IntegerUtil.encodeInteger2byte(entity.reserve, data, index);
        index += 2;

        return data;
    }

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

    public int getSize() {
        return 13;
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
