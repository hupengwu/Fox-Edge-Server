package cn.foxtech.device.protocol.v1.s7plc.core.model;


import cn.foxtech.device.protocol.v1.s7plc.core.enums.EFunctionCode;
import cn.foxtech.device.protocol.v1.s7plc.core.utils.BooleanUtil;
import cn.foxtech.device.protocol.v1.s7plc.core.common.IObjectByteArray;
import cn.foxtech.device.protocol.v1.s7plc.core.common.buff.ByteReadBuff;
import cn.foxtech.device.protocol.v1.s7plc.core.common.buff.ByteWriteBuff;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 上传响应参数
 *
 * @author xingshuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UploadAckParameter extends Parameter implements IObjectByteArray {

    /**
     * 后续是否还有更多数据
     */
    protected boolean moreDataFollowing = false;

    /**
     * 错误状态
     */
    protected boolean errorStatus = false;


    public UploadAckParameter() {
        this.functionCode = EFunctionCode.UPLOAD;
    }

    /**
     * 字节数组数据解析
     *
     * @param data 字节数组数据
     * @return DownloadAckParameter
     */
    public static UploadAckParameter fromBytes(final byte[] data) {
        return fromBytes(data, 0);
    }

    /**
     * 字节数组数据解析
     *
     * @param data   字节数组数据
     * @param offset 偏移量
     * @return DownloadAckParameter
     */
    public static UploadAckParameter fromBytes(final byte[] data, final int offset) {
        if (data.length < 2) {
            throw new IndexOutOfBoundsException("解析DownloadAckParameter时，字节数组长度不够");
        }
        ByteReadBuff buff = new ByteReadBuff(data, offset);
        UploadAckParameter res = new UploadAckParameter();
        res.functionCode = EFunctionCode.from(buff.getByte(0));
        res.moreDataFollowing = buff.getBoolean(1, 0);
        res.errorStatus = buff.getBoolean(1, 1);
        return res;
    }

    @Override
    public int byteArrayLength() {
        return 2;
    }

    @Override
    public byte[] toByteArray() {
        return ByteWriteBuff.newInstance(2)
                .putByte(this.functionCode.getCode())
                .putByte((byte) (BooleanUtil.setBit(0, this.moreDataFollowing) | BooleanUtil.setBit(1, this.errorStatus)))
                .getData();
    }
}
