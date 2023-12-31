package cn.foxtech.device.protocol.v1.dahua.fire.core.entity;

import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.dahua.fire.core.utils.IntegerUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Coap模式（电信IoT平台）
 */
@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class LwM2MPduEntity extends PduEntity {
    /**
     * 业务流水号(2 字节)
     * 发生改变，流水号增加
     */
    private int sn = 0;
    /**
     * 控制单元
     */
    private LwM2MCtrlEntity ctrlEntity = new LwM2MCtrlEntity();
    /**
     * 应用数据单元：可选项目
     */
    private AduEntity aduEntity;

    public static int getSum(byte[] data, int offset, int length) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += data[offset + i] & 0xff;
        }

        return sum;
    }

    public static byte[] encodeEntity(LwM2MPduEntity pduEntity) {
        // 对应用单元，进行数据编码
        byte[] aduData = pduEntity.aduEntity == null ? null : AduEntity.encodeEntity(pduEntity.aduEntity);

        // 确定：应用单元和它的长度
        int aduDataLength = (aduData != null ? aduData.length : 0);

        // 将应用单元的长度，记录到控制单元上
        pduEntity.ctrlEntity.setAduLength(aduDataLength);

        // 对控制单元进行数据编码
        byte[] ctrlData = LwM2MCtrlEntity.encodeEntity(pduEntity.ctrlEntity);

        // 分配PDU的数据块
        byte[] data = new byte[3 + ctrlData.length + aduDataLength];

        int index = 0;

        // 业务流水号（2 字节）
        IntegerUtil.encodeInteger2byte(pduEntity.sn, data, index);
        index += 2;

        // 控制单元
        System.arraycopy(ctrlData, 0, data, index, ctrlData.length);
        index += ctrlData.length;

        // 应用单元
        if (aduData != null) {
            System.arraycopy(aduData, 0, data, index, aduData.length);
            index += aduData.length;
        }

        // 校验和（1 字节）
        int sum = getSum(data, 2, ctrlData.length + aduDataLength);
        data[index++] = (byte) sum;

        return data;
    }

    public static LwM2MPduEntity decodeEntity(byte[] data) {
        LwM2MPduEntity pduEntity = new LwM2MPduEntity();

        // 获得PDU的大小：PUD外层格式，是否合法
        int pduSize = getPduSize(data);

        int index = 2;

        // 解码：控制单元
        LwM2MCtrlEntity ctrlEntity = pduEntity.getCtrlEntity();
        LwM2MCtrlEntity.decodeEntity(data, index, ctrlEntity);
        index += LwM2MCtrlEntity.size();


        // 检测：是否有应用数据
        if (ctrlEntity.getAduLength() == 0) {
            pduEntity.aduEntity = null;
            return pduEntity;
        }

        // 解码：应用数据单元
        AduEntity aduEntity = new AduEntity();
        AduEntity.decodeEntity(data, index, ctrlEntity.getAduLength(), ctrlEntity.getCmd(), aduEntity);
        pduEntity.aduEntity = aduEntity;

        return pduEntity;
    }

    public static int getPduSize(byte[] data) {
        if (data.length < 3) {
            throw new ProtocolException("PDU的长度，至少3个字节");
        }

        // 数据帧长度（2 字节）
        int length = IntegerUtil.decodeInteger2byte(data, 2);


        // 报文长度不正确
        if (data.length != 6 + length) {
            throw new ProtocolException("PDU的长度，不匹配");
        }

        // 校验和（1 字节）
        int sum = getSum(data, 2, length + 3);
        if (data[5 + length] != (byte) sum) {
            throw new ProtocolException("校验和不正确");
        }

        return length + 6;
    }
}
