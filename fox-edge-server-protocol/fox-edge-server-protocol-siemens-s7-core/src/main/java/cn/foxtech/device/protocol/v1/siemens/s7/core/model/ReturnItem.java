package cn.foxtech.device.protocol.v1.siemens.s7.core.model;


import cn.foxtech.device.protocol.v1.siemens.s7.core.common.IObjectByteArray;
import cn.foxtech.device.protocol.v1.siemens.s7.core.common.buff.ByteWriteBuff;
import cn.foxtech.device.protocol.v1.siemens.s7.core.enums.EReturnCode;
import lombok.Data;

/**
 * 返回项
 *
 * @author xingshuang
 */
@Data
public class ReturnItem implements IObjectByteArray {

    /**
     * 返回码 <br>
     * 字节大小：1 <br>
     * 字节序数：0
     */
    protected EReturnCode returnCode = EReturnCode.SUCCESS;

    /**
     * 字节数组数据解析
     *
     * @param data 字节数组数据
     * @return ReturnItem
     */
    public static ReturnItem fromBytes(final byte[] data) {
        ReturnItem returnItem = new ReturnItem();
        returnItem.returnCode = EReturnCode.from(data[0]);
        return returnItem;
    }

    /**
     * 获取默认数据返回
     *
     * @param returnCode 返回码
     * @return 返回数据
     */
    public static ReturnItem createDefault(EReturnCode returnCode) {
        ReturnItem item = new ReturnItem();
        item.returnCode = returnCode;
        return item;
    }

    @Override
    public int byteArrayLength() {
        return 1;
    }

    @Override
    public byte[] toByteArray() {
        return ByteWriteBuff.newInstance(this.byteArrayLength())
                .putByte(returnCode.getCode())
                .getData();
    }
}
