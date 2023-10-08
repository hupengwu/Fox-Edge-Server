package cn.foxtech.device.protocol.v1.siemens.s7.core.model;


import cn.foxtech.device.protocol.v1.siemens.s7.core.common.IObjectByteArray;
import cn.foxtech.device.protocol.v1.siemens.s7.core.enums.EDestinationFileSystem;
import cn.foxtech.device.protocol.v1.siemens.s7.core.enums.EFileBlockType;
import cn.foxtech.device.protocol.v1.siemens.s7.core.enums.EFunctionCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 结束下载参数
 *
 * @author xingshuang
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EndDownloadParameter extends DownloadParameter implements IObjectByteArray {

    public EndDownloadParameter() {
        this.functionCode = EFunctionCode.END_DOWNLOAD;
    }

    /**
     * 创建默认的下载中参数
     *
     * @param blockType             数据块类型
     * @param blockNumber           数据块编号
     * @param destinationFileSystem 目标文件系统
     * @return EndDownloadParameter
     */
    public static EndDownloadParameter createDefault(EFileBlockType blockType,
                                                     int blockNumber,
                                                     EDestinationFileSystem destinationFileSystem) {
        EndDownloadParameter parameter = new EndDownloadParameter();
        parameter.blockType = blockType;
        parameter.blockNumber = blockNumber;
        parameter.destinationFileSystem = destinationFileSystem;
        return parameter;
    }
}
