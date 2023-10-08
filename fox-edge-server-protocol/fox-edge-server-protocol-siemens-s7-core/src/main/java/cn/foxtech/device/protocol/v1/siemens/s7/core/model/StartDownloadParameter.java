package cn.foxtech.device.protocol.v1.siemens.s7.core.model;


import cn.foxtech.device.protocol.v1.siemens.s7.core.common.IObjectByteArray;
import cn.foxtech.device.protocol.v1.siemens.s7.core.common.buff.ByteReadBuff;
import cn.foxtech.device.protocol.v1.siemens.s7.core.common.buff.ByteWriteBuff;
import cn.foxtech.device.protocol.v1.siemens.s7.core.enums.EDestinationFileSystem;
import cn.foxtech.device.protocol.v1.siemens.s7.core.enums.EFileBlockType;
import cn.foxtech.device.protocol.v1.siemens.s7.core.enums.EFunctionCode;
import cn.foxtech.device.protocol.v1.siemens.s7.core.utils.BooleanUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 开始下载参数
 *
 * @author xingshuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StartDownloadParameter extends DownloadParameter implements IObjectByteArray {

    /**
     * 第二部分字符串长度，1个字节
     */
    private int part2Length = 13;

    /**
     * 未知字符，1个字节
     */
    private String unknownChar = "1";

    /**
     * 装载长度，6个字节，范围000000-999999
     */
    private int loadMemoryLength = 0;

    /**
     * MC代码长度，6个字节，范围000000-999999
     */
    private int mC7CodeLength = 0;


    public StartDownloadParameter() {
        this.functionCode = EFunctionCode.START_DOWNLOAD;
    }

    @Override
    public int byteArrayLength() {
        return 32;
    }

    @Override
    public byte[] toByteArray() {
        return ByteWriteBuff.newInstance(32)
                .putByte(this.functionCode.getCode())
                .putByte((byte) (BooleanUtil.setBit(0, this.moreDataFollowing) | BooleanUtil.setBit(1, this.errorStatus)))
                .putBytes(this.errorCode)
                .putInteger(this.id)
                .putByte(this.fileNameLength)
                .putString(this.fileIdentifier)
                .putBytes(this.blockType.getByteArray())
                .putString(String.format("%05d", this.blockNumber))
                .putByte(this.destinationFileSystem.getCode())
                .putByte(this.part2Length)
                .putString(this.unknownChar)
                .putString(String.format("%06d", this.loadMemoryLength))
                .putString(String.format("%06d", this.mC7CodeLength))
                .getData();
    }

    /**
     * 字节数组数据解析
     *
     * @param data 字节数组数据
     * @return StartDownloadParameter
     */
    public static StartDownloadParameter fromBytes(final byte[] data) {
        return fromBytes(data, 0);
    }

    /**
     * 字节数组数据解析
     *
     * @param data   字节数组数据
     * @param offset 偏移量
     * @return StartDownloadParameter
     */
    public static StartDownloadParameter fromBytes(final byte[] data, final int offset) {
        if (data.length < 32) {
            throw new IndexOutOfBoundsException("解析StartDownloadParameter时，字节数组长度不够");
        }
        StartDownloadParameter res = new StartDownloadParameter();
        ByteReadBuff buff = new ByteReadBuff(data, offset);
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
        res.part2Length = buff.getByteToInt();
        res.unknownChar = buff.getString(1);
        res.loadMemoryLength = Integer.parseInt(buff.getString(6));
        res.mC7CodeLength = Integer.parseInt(buff.getString(6));
        return res;
    }

    public static StartDownloadParameter createDefault(EFileBlockType blockType,
                                                       int blockNumber,
                                                       EDestinationFileSystem destinationFileSystem,
                                                       int loadMemoryLength,
                                                       int mC7CodeLength){
        StartDownloadParameter parameter = new StartDownloadParameter();
        parameter.blockType = blockType;
        parameter.blockNumber = blockNumber;
        parameter.destinationFileSystem = destinationFileSystem;
        parameter.loadMemoryLength = loadMemoryLength;
        parameter.mC7CodeLength = mC7CodeLength;
        return parameter;
    }
}
