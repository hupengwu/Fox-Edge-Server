package cn.foxtech.device.protocol.v1.bass260zj;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeReport;
import cn.foxtech.device.protocol.v1.core.constants.FoxEdgeConstant;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.telecom.core.TelecomEntity;
import cn.foxtech.device.protocol.v1.telecom.core.TelecomProtocol;
import cn.foxtech.device.protocol.v1.utils.BcdUtils;
import cn.foxtech.device.protocol.v1.utils.HexUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FoxEdgeDeviceType(value = "BASS260ZJ", manufacturer = "广东高新兴")
public class BASS260ZJGetCardRecord extends TelecomProtocol {
    /**
     * 读取刷卡记录
     *
     * @param param
     * @return
     */
    @FoxEdgeOperate(name = "读取刷卡记录", polling = true, type = FoxEdgeOperate.encoder, mode = FoxEdgeOperate.record, timeout = 2000)
    public static String packCmdGetCardRecord(Map<String, Object> param) {
        TelecomEntity entity = new TelecomEntity();
        entity.setVer((byte) 0x10);
        entity.setAddr((byte) 0x01);
        entity.setCID1((byte) 0x80);// 设备分类码 环境控制类=8 门禁暂时固定分组码=0
        entity.setCID2((byte) 0x4A);// 读取信息

        byte[] data = new byte[3];
        data[0] = (byte) 0xF2;// COMMAND GROUP 读取命令
        data[1] = (byte) 0xE2;// COMMAND TYPE 顺序读取一条历史记录
        data[2] = (byte) 0x00;// DATAF 门禁从LOADP位置读取一条记录返给SU， 门禁自动将LOADP指向下一条记录
        entity.setData(data);
        byte[] arrCmd = BASS260ZJGetCardRecord.packCmd4Entity(entity);

        return HexUtils.byteArrayToHexString(arrCmd);
    }

    /**
     * 读取刷卡记录
     * FoxEdgeOperate：上位机主动问询，包括编码和解码函数
     * FoxEdgeEvent：设备主动上报，只包括解码函数，上位机需要根据通道识别设备，然后根据数据
     * 内容的格式识别是否为刷卡记录。
     * 说明：对于多种命令类型上报的事件，每个类型可以定义一个解码器，框架会用这些解码器去逐个匹配
     * 只要不抛出异常，一路正常执行的，就认为格式匹配正确了。
     * 所以设备的报文格式一定要有足够的区别特征，否则会混淆彼此的事件类型。
     *
     * @param hexString
     * @param param
     * @return
     */
    @FoxEdgeReport(type = FoxEdgeReport.alarm)
    @FoxEdgeOperate(name = "读取刷卡记录", polling = true, type = FoxEdgeOperate.decoder, mode = FoxEdgeOperate.record, timeout = 2000)
    public static List<Map<String, Object>> unPackCmdGetCardRecord(String hexString, Map<String, Object> param) {
        byte[] arrCmd = HexUtils.hexStringToByteArray(hexString);

        TelecomEntity entity = BASS260ZJGetCardRecord.unPackCmd2Entity(arrCmd);
        byte[] dat = entity.getData();

        // 检查:数据域长度
        if (dat.length != 14) {
            throw new ProtocolException("返回的data长度不正确！");
        }

        // 卡号
        byte[] card = new byte[4];
        for (int i = 0; i < 4; i++) {
            card[i] = dat[i + 1];
        }
        String cardID = HexUtils.byteArrayToHexString(card).replace(" ", "");

        //日期
        byte[] time = new byte[7];
        for (int i = 0; i < 7; i++) {
            time[i] = dat[i + 5];
        }
        String datetime = String.format("%02d%02d-%02d-%02d %02d:%02d:%02d", BcdUtils.bcd2int(time[0]), BcdUtils.bcd2int(time[1]), BcdUtils.bcd2int(time[2]), BcdUtils.bcd2int(time[3]), BcdUtils.bcd2int(time[4]), BcdUtils.bcd2int(time[5]), BcdUtils.bcd2int(time[6]));


        byte byStatus = dat[12];
        byte byRemark = dat[13];

        // 失败的话:为其他不解析的事件类型
        String event = unPackRemark(byStatus, byRemark);

        Map<String, Object> record = new HashMap<>();
        record.put("cardId", cardID);
        record.put("datetime", datetime);
        record.put("event", event);

        // 生成数据结构
        List<Map<String, Object>> dataList = new ArrayList<>();
        dataList.add(record);

        // 打上刷卡记录的类型标识
        return makeRecordTypeTag("刷卡记录", dataList);
    }

    /**
     * 为每条记录打上记录类型标记
     *
     * @param recordType 记录的类型
     * @param recordList 记录的数据
     * @return 记录的列表
     */
    public static List<Map<String, Object>> makeRecordTypeTag(String recordType, List<Map<String, Object>> recordList) {
        for (Map<String, Object> record : recordList) {
            record.put(FoxEdgeConstant.RECORD_TYPE_TAG, recordType);
        }

        return recordList;
    }

    private static String unPackRemark(byte dwStatus, byte dwRemark) {

        // 备注(1字节)(D7'D0 ,其中D0 最低位)的D7=0 表示该条历史记录从未被SU
        // 读取过;D7=1表示该条历史记录已被SU读取过。

        // 刷卡开门
        if ((dwRemark & 0x7F) == 0) {
            if ((dwStatus & 0x04) == 0) {
                return "刷卡进门";
            } else {
                return "刷卡出门";
            }
        } else if ((dwRemark & 0x7F) == 2)//远程开门
        {
            return "远程开门";
        } else if ((dwRemark & 0x7F) == 3)//按钮开门
        {
            return "无效数据";
        } else if ((dwRemark & 0x7F) == 8)//无效的用户卡刷卡记录
        {
            return "非法门禁卡刷卡";
        } else if ((dwRemark & 0x7F) == 9)//用户卡的有效期已过
        {
            return "过期门禁卡刷卡";
        } else if ((dwRemark & 0x7F) == 10)//当前时间该用户卡无进入权限
        {
            return "限期门禁卡刷卡";
        } else if ((dwRemark & 0x7F) == 11)//非法开门
        {
            return "非法开门";
        } else if ((dwRemark & 0x7F) == 12)//非法离开
        {
            return "非法离开";
        } else if ((dwRemark & 0x7F) == 13)//离开未关门
        {
            return "离开未关门";
        }

        return "";
    }
}
