package cn.foxtech.channel.s7plc.client.service;

import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.common.utils.method.MethodUtils;
import cn.foxtech.core.exception.ServiceException;
import cn.foxtech.device.protocol.v1.s7plc.core.enums.EDataType;
import cn.foxtech.device.protocol.v1.s7plc.core.serializer.S7Parameter;
import cn.foxtech.device.protocol.v1.s7plc.core.serializer.S7Serializer;
import cn.foxtech.device.protocol.v1.s7plc.core.service.S7PLC;
import cn.foxtech.device.protocol.v1.utils.HexUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 执行者
 */
@Component
public class ExecuteService {
    /**
     * 查询数据
     *
     * @param entity    snmp参数
     * @param requestVO 报文参数
     * @return 返回值
     * @throws ServiceException 异常信息
     */
    public synchronized ChannelRespondVO execute(S7PLC entity, ChannelRequestVO requestVO) throws ServiceException {
        Map<String, Object> send = (Map<String, Object>) requestVO.getSend();

        String method = (String) send.get("method");
        if (MethodUtils.hasEmpty(method)) {
            throw new ServiceException("参数不能为空： method");
        }

        if ("readData".equals(method)) {
            List<Map<String, Object>> params = (List<Map<String, Object>>) send.get("params");
            if (MethodUtils.hasEmpty(params)) {
                throw new ServiceException("列表参数不能为空： params");
            }


            List<S7Parameter> list = this.readData(entity, params);

            this.convertArrays(list);

            Map<String, Object> result = new HashMap<>();
            result.put("method", method);
            result.put("list", list);


            ChannelRespondVO respondVO = new ChannelRespondVO();
            respondVO.bindBaseVO(requestVO);
            respondVO.setRecv(result);
            return respondVO;
        }
        if ("writeData".equals(method)) {
            List<Map<String, Object>> params = (List<Map<String, Object>>) send.get("params");
            if (MethodUtils.hasEmpty(params)) {
                throw new ServiceException("列表参数不能为空： params");
            }


            this.writeData(entity, params);

            ChannelRespondVO respondVO = new ChannelRespondVO();
            respondVO.bindBaseVO(requestVO);
            respondVO.setRecv(null);
            return respondVO;
        }

        throw new ServiceException("method允许值：readData");

    }

    private List<S7Parameter> readData(S7PLC entity, List<Map<String, Object>> params) {
        List<S7Parameter> list = new ArrayList<>();

        for (Map<String, Object> param : params) {
            String address = (String) param.get("address");
            String dataType = (String) param.get("dataType");
            Integer count = (Integer) param.get("count");

            if (MethodUtils.hasEmpty(address, dataType)) {
                throw new ServiceException("参数不能为空：address, dataType");
            }

            S7Parameter parameter = null;
            if (count == null) {
                parameter = new S7Parameter(address, EDataType.valueOf(dataType));
            } else {
                parameter = new S7Parameter(address, EDataType.valueOf(dataType), count);
            }

            list.add(parameter);
        }

        S7Serializer s7Serializer = S7Serializer.newInstance(entity);
        List<S7Parameter> actual = s7Serializer.read(list);
        return actual;
    }

    private void writeData(S7PLC entity, List<Map<String, Object>> params) {
        List<S7Parameter> list = new ArrayList<>();

        for (Map<String, Object> param : params) {
            String address = (String) param.get("address");
            String dataType = (String) param.get("dataType");
            Object value = param.get("value");

            if (MethodUtils.hasEmpty(address, dataType, value)) {
                throw new ServiceException("参数不能为空：address, dataType, value");
            }


            EDataType type = EDataType.valueOf(dataType);

            Integer count = 1;
            S7Parameter parameter = null;
            if (EDataType.BYTE.equals(type)) {
                byte[] bytes = HexUtils.hexStringToByteArray((String) value);
                if (bytes == null || bytes.length == 0) {
                    throw new ServiceException("BYTE数组不能为空!");
                }
                count = bytes.length;

                parameter = new S7Parameter(address, type, count, bytes);
            } else if (EDataType.STRING.equals(type)) {
                String str = (String) value;
                if (MethodUtils.hasEmpty(str)) {
                    throw new ServiceException("STRING字符串不能为空!");
                }

                count = str.length();

                parameter = new S7Parameter(address, type, count, str);
            } else if (EDataType.UINT16.equals(type)) {
                parameter = new S7Parameter(address, type, count, Integer.valueOf(value.toString()));
            } else if (EDataType.INT16.equals(type)) {
                parameter = new S7Parameter(address, type, count, Short.valueOf(value.toString()));
            } else if (EDataType.TIME.equals(type) || EDataType.UINT32.equals(type)) {
                parameter = new S7Parameter(address, type, count, Long.valueOf(value.toString()));
            } else if (EDataType.FLOAT32.equals(type)) {
                parameter = new S7Parameter(address, type, count, Float.valueOf(value.toString()));
            } else if (EDataType.FLOAT64.equals(type)) {
                parameter = new S7Parameter(address, type, count, Double.valueOf(value.toString()));
            } else {
                parameter = new S7Parameter(address, type, 1, value);
            }


            list.add(parameter);
        }

        S7Serializer s7Serializer = S7Serializer.newInstance(entity);
        s7Serializer.write(list);
    }

    private void convertArrays(List<S7Parameter> parameters) {
        for (S7Parameter parameter : parameters) {
            Object value = parameter.getValue();
            if (value instanceof byte[]) {
                parameter.setValue(HexUtils.byteArrayToHexString((byte[]) value));
            }
        }

    }
}
