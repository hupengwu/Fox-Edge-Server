package cn.foxtech.device.protocol.v1.iec104.slaver;

import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeDeviceType;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperate;
import cn.foxtech.device.protocol.v1.core.annotation.FoxEdgeOperateParam;
import cn.foxtech.device.protocol.v1.core.exception.ProtocolException;
import cn.foxtech.device.protocol.v1.utils.BcdUtils;
import cn.foxtech.device.protocol.v1.core.utils.JsonUtils;
import cn.foxtech.device.protocol.v1.iec104.core.builder.ApduVOBuilder;
import cn.foxtech.device.protocol.v1.iec104.core.entity.*;
import cn.foxtech.device.protocol.v1.iec104.core.enums.AsduTypeIdEnum;
import cn.foxtech.device.protocol.v1.iec104.core.enums.CotReasonEnum;
import cn.foxtech.device.protocol.v1.iec104.core.vo.ApduVO;
import cn.foxtech.device.protocol.v1.iec104.slaver.template.Iec104Template;
import cn.foxtech.device.protocol.v1.iec104.slaver.template.JReadDataTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 总召唤的编码解码器
 */
@FoxEdgeDeviceType(value = "IEC104 Device", manufacturer = "Fox Edge")
public class IEC104SlaverGeneralCall {
    /**
     * 总召唤的编码器
     *
     * @param param 参数
     * @return 待发送的对象
     */
    @FoxEdgeOperate(name = "总召唤", polling = true, type = FoxEdgeOperate.encoder, timeout = 10000)
    @FoxEdgeOperateParam(names = {"设备地址"}, values = {"1"})
    public static Object encodeSessionRequest(Map<String, Object> param) {
        try {
            // I帧控制头
            IControlEntity controlEntity = new IControlEntity();
            controlEntity.setSend((short) 0);
            controlEntity.setAccept((short) 0);

            ApduEntity apduEntity = new ApduEntity();
            apduEntity.setControl(controlEntity);

            AsduEntity asduEntity = new AsduEntity();
            apduEntity.setAsdu(asduEntity);

            // 总召唤的类型ID
            asduEntity.setTypeId(AsduTypeIdEnum.generalCall.getValue());

            // 非连续的数据：总召唤会要求带回各种数据，就不可能连续成一个数组
            VsqEntity vsq = new VsqEntity();
            vsq.setSq(false);
            vsq.setNum(0);
            asduEntity.setVsq(vsq);

            // 激活
            CotEntity cot = new CotEntity();
            cot.setAddr(0);
            cot.setTest(false);
            cot.setPn(true);
            cot.setReason(CotReasonEnum.active.getValue());
            asduEntity.setCot(cot);

            // 通用地址：1
            asduEntity.setCommonAddress(1);

            byte[] data = {0x00, 0x00, 0x00, 0x14};
            asduEntity.setData(data);

            // 实体转换为VO
            ApduVO apduVO = ApduVOBuilder.buildVO(apduEntity);

            // 一问多答：要额外指明等待指定的结束符activeEnded才算结束
            apduVO.getWaitEndFlag().add(CotReasonEnum.activeEnded.getValue());

            return apduVO;
        } catch (Exception e) {
            throw new ProtocolException("编码错误：" + e.getMessage());
        }
    }

    /**
     * 总召唤的解码器
     *
     * @param respond channel返回的响应对象
     * @param param 设备的参数信息
     * @return 解码后的设备数据
     */
    @FoxEdgeOperate(name = "总召唤", polling = true, type = FoxEdgeOperate.decoder, timeout = 10000)
    @FoxEdgeOperateParam(names = {"设备地址"}, values = {"1"})
    public static Map<String, Object> decodeSessionRequest(Object respond, Map<String, Object> param) {
        Map<String, Object> values = new HashMap<>();

        // 从输入参数中，提取解码需要的模板参数
        String templateName = (String) param.get("template_name");
        String operateName = (String) param.get("operate_name");
        Map<String, Object> operate = (Map<String, Object>) param.get("operate");
        List<Map<String, Object>> decoderParams = (List<Map<String, Object>>) operate.get("decoder_param");
        Map<Integer, String> typeId2csv = new HashMap<>();
        for (Map<String, Object> decoderParam : decoderParams) {
            typeId2csv.put(Integer.parseInt(decoderParam.get("typeId").toString()), decoderParam.get("table").toString());
        }

        // 对收到的一批分组报文，使用模板进行解码
        List<Map<String, Object>> apduMapList = (List<Map<String, Object>>) respond;
        for (Map<String, Object> apduMap : apduMapList) {
            // 数据结构的转换：MAP->VO->Entity
            ApduVO apduVO = JsonUtils.buildObjectWithoutException(apduMap, ApduVO.class);
            ApduEntity apduEntity = ApduVOBuilder.buildEntity(apduVO);

            AsduEntity asduEntity = apduEntity.getAsdu();
            if (asduEntity == null) {
                continue;
            }


            // 单点信息
            values.putAll(decodeSinglePointSignal(asduEntity, operateName, templateName, typeId2csv));
            // 测量值，标度化值
            values.putAll(decodeScaledTelemetry(asduEntity, operateName, templateName, typeId2csv));
        }

        return values;
    }

    /**
     * 单字节的单点遥测信息的解码
     *
     * @param asduEntity
     * @return
     */
    private static Map<String, Object> decodeSinglePointSignal(AsduEntity asduEntity, String operateName, String templateName, Map<Integer, String> typeId2csv) {
        Map<String, Object> values = new HashMap<>();
        if (AsduTypeIdEnum.singlePointSignal.getValue() != asduEntity.getTypeId()) {
            return values;
        }

        String csvFile = typeId2csv.get(AsduTypeIdEnum.singlePointSignal.getValue());
        JReadDataTemplate template = Iec104Template.newInstance().getTemplate(operateName, templateName, AsduTypeIdEnum.singlePointSignal.getValue(), csvFile, JReadDataTemplate.class);

        VsqEntity vsq = asduEntity.getVsq();
        CotEntity cot = asduEntity.getCot();
        byte[] data = asduEntity.getData();

        // 3字节起始地址+N个单字节的布尔量
        if (vsq.isSq() && CotReasonEnum.responseStationCall.getValue() == cot.getReason()) {
            if (vsq.getNum() + 3 != data.length) {
                throw new ProtocolException("返回的单点信息数量不正确");
            }

            int address = data[0] & 0xff;
            address += (data[1] & 0xff) * 0x100;
            address += (data[2] & 0xff) * 0x10000;

            int[] status = new int[vsq.getNum()];

            Map<Integer, Integer> statusList = new HashMap<>();
            for (int i = 0; i < vsq.getNum(); i++) {
                statusList.put(address + i, data[i + 3] & 0xff);
            }

            values = template.decode(address, statusList);
        }


        return values;
    }

    /**
     * 3字节的标度化值解码
     *
     * @param asduEntity
     * @param operateName
     * @param templateName
     * @param typeId2csv
     * @return
     */
    private static Map<String, Object> decodeScaledTelemetry(AsduEntity asduEntity, String operateName, String templateName, Map<Integer, String> typeId2csv) {
        Map<String, Object> values = new HashMap<>();
        if (AsduTypeIdEnum.scaledTelemetry.getValue() != asduEntity.getTypeId()) {
            return values;
        }

        String csvFile = typeId2csv.get(AsduTypeIdEnum.scaledTelemetry.getValue());
        JReadDataTemplate template = Iec104Template.newInstance().getTemplate(operateName, templateName, AsduTypeIdEnum.scaledTelemetry.getValue(), csvFile, JReadDataTemplate.class);


        VsqEntity vsq = asduEntity.getVsq();
        CotEntity cot = asduEntity.getCot();
        byte[] data = asduEntity.getData();

        // 3字节起始地址+N个3字节的整数
        if (vsq.isSq() && CotReasonEnum.responseStationCall.getValue() == cot.getReason()) {
            if (vsq.getNum() * 3 + 3 != data.length) {
                throw new ProtocolException("返回的测量值标度化值信息数量不正确");
            }

            int address = data[0] & 0xff;
            address += (data[1] & 0xff) * 0x100;
            address += (data[2] & 0xff) * 0x10000;

            Map<Integer, Integer> statusList = new HashMap<>();
            for (int i = 0; i < vsq.getNum(); i++) {
                int v0 = BcdUtils.bcd2int(data[3 * i + 3]);
                int v1 = BcdUtils.bcd2int(data[3 * i + 4]);
                int v2 = BcdUtils.bcd2int(data[3 * i + 5]);

                statusList.put(address + i, v0 + v1 * 10 + v2 * 100);
            }

            values = template.decode(address, statusList);
        }


        return values;
    }
}
