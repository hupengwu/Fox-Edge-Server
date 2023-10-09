package cn.foxtech.device.protocol.v1.s7plc.core.model;


import cn.foxtech.device.protocol.v1.s7plc.core.enums.EDestinationFileSystem;
import cn.foxtech.device.protocol.v1.s7plc.core.enums.EFunctionCode;
import cn.foxtech.device.protocol.v1.s7plc.core.utils.BooleanUtil;
import cn.foxtech.device.protocol.v1.s7plc.core.common.IObjectByteArray;
import cn.foxtech.device.protocol.v1.s7plc.core.common.buff.ByteReadBuff;
import cn.foxtech.device.protocol.v1.s7plc.core.common.buff.ByteWriteBuff;
import cn.foxtech.device.protocol.v1.s7plc.core.enums.EFileBlockType;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 下载参数
 *
 * @author xingshuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DownloadParameter extends UploadAckParameter implements IObjectByteArray {

    /**
     * 未知字节，2个字节
     */
    protected byte[] errorCode = new byte[]{0x01, 0x00};

    /**
     * 下载的Id，4个字节（没用）
     */
    protected long id = 0x00000000;

    /**
     * 文件名长度，1个字节
     */
    protected int fileNameLength = 9;

    /**
     * 文件id，1个字节
     */
    protected String fileIdentifier = "_";

    /**
     * 数据块类型，2个字节
     */
    protected EFileBlockType blockType = EFileBlockType.OB;

    /**
     * 数据块编号，5个字节，范围00000-99999
     */
    protected int blockNumber = 0;

    /**
     * 目标文件系统，1个字节
     */
    protected EDestinationFileSystem destinationFileSystem = EDestinationFileSystem.P;


    public DownloadParameter() {
        this.functionCode = EFunctionCode.DOWNLOAD;
    }

    /**
     * 字节数组数据解析
     *
     * @param data 字节数组数据
     * @return DownloadParameter
     */
    public static DownloadParameter fromBytes(final byte[] data) {
        return fromBytes(data, 0);
    }

    /**
     * 字节数组数据解析
     *
     * @param data   字节数组数据
     * @param offset 偏移量
     * @return DownloadParameter
     */
    public static DownloadParameter fromBytes(final byte[] data, final int offset) {
        if (data.length < 18) {
            throw new IndexOutOfBoundsException("解析DownloadParameter时，字节数组长度不够");
        }
        ByteReadBuff buff = new ByteReadBuff(data, offset);
        DownloadParameter res = new DownloadParameter();
        res.functionCode = EFunctionCode.from(buff.getByte());
        byte b = buff.getByte();
        res.moreDataFollowing = BooleanUtil.getValue(b, 0);
        res.errorStatus = BooleanUtil.getValue(b, 1);
        res.errorCode = buff.getBytes(2);
        res.id = buff.getUInt32();
        res.fileNameLength = buff.getByteToInt();
        res.fileIdentifier = buff.getString(1);
        res.blockType = EFileBlockType.from(buff.getString(2));
        res.blockNumber = Integer.parseInt(buff.getString(5));
        res.destinationFileSystem = EDestinationFileSystem.from(buff.getByte());
        return res;
    }

    /**
     * 创建默认的下载中参数
     *
     * @param blockType             数据块类型
     * @param blockNumber           数据块编号
     * @param destinationFileSystem 目标文件系统
     * @param moreDataFollowing     是否有更多数据
     * @return DownloadParameter
     */
    public static DownloadParameter createDefault(EFileBlockType blockType,
                                                  int blockNumber,
                                                  EDestinationFileSystem destinationFileSystem,
                                                  boolean moreDataFollowing) {
        DownloadParameter parameter = new DownloadParameter();
        parameter.blockType = blockType;
        parameter.blockNumber = blockNumber;
        parameter.destinationFileSystem = destinationFileSystem;
        parameter.setMoreDataFollowing(moreDataFollowing);
        return parameter;
    }

    @Override
    public int byteArrayLength() {
        return 18;
    }

    @Override
    public byte[] toByteArray() {
        return ByteWriteBuff.newInstance(18)
                .putByte(this.functionCode.getCode())
                .putByte((byte) (BooleanUtil.setBit(0, this.moreDataFollowing) | BooleanUtil.setBit(1, this.errorStatus)))
                .putBytes(this.errorCode)
                .putInteger(this.id)
                .putByte(this.fileNameLength)
                .putString(this.fileIdentifier)
                .putBytes(this.blockType.getByteArray())
                .putString(String.format("%05d", this.blockNumber))
                .putByte(this.destinationFileSystem.getCode())
                .getData();
    }
}
