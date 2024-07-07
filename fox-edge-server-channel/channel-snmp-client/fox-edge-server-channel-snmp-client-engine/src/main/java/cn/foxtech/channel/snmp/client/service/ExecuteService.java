package cn.foxtech.channel.snmp.client.service;

import cn.foxtech.channel.domain.ChannelRequestVO;
import cn.foxtech.channel.domain.ChannelRespondVO;
import cn.foxtech.channel.snmp.client.entity.SnmpEntity;
import cn.foxtech.core.exception.ServiceException;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 执行者
 */
@Component
public class ExecuteService {
    @Autowired
    private SnmpService snmpService;

    /**
     * 查询数据
     *
     * @param snmpEntity snmp参数
     * @param requestVO  报文参数
     * @return 返回值
     * @throws ServiceException 异常信息
     */
    public synchronized ChannelRespondVO execute(SnmpEntity snmpEntity, ChannelRequestVO requestVO) throws ServiceException {
        Map<String, Object> send = (Map<String, Object>) requestVO.getSend();
        List<String> oidList = (List<String>) send.get("oids");
        String operate = (String) send.get("operate");
        Integer timeout = requestVO.getTimeout();

        if (timeout == null) {
            throw new ServiceException("超时参数不能为空！");
        }
        if (timeout > 10 * 1000) {
            throw new ServiceException("超时参数不能大于10秒！");
        }

        // 检查串口
        if (snmpEntity == null) {
            throw new ServiceException("通道实体不存在！");
        }

        if ("GET".equals(operate)) {
            if (oidList == null || oidList.isEmpty()) {
                throw new ServiceException("发送数据不能为空！");
            }

            // 获得返回的PDU
            PDU respond = this.snmpService.snmpASynGetList(snmpEntity.getLocalIp(), snmpEntity.getLocalPort(), snmpEntity.getTargetIp(), snmpEntity.getTargetPort(), snmpEntity.getVersion(), snmpEntity.getCommunity(), oidList, timeout);

            // 解析数据
            Map<String, Object> result = new HashMap<>();
            if (respond == null) {
                throw new ServiceException("[ERROR]: snmp response is null！");
            } else if (respond.getErrorStatus() != 0) {
                throw new ServiceException("[ERROR]: response status " + respond.getErrorStatus() + " Text:" + respond.getErrorStatusText());
            } else {

                for (int j = 0; j < respond.size(); j++) {
                    VariableBinding vb = respond.get(j);
                    result.put(vb.getOid().format(), vb.getVariable().toString());
                }
            }

            ChannelRespondVO respondVO = new ChannelRespondVO();
            respondVO.bindBaseVO(requestVO);
            respondVO.setRecv(result);
            return respondVO;
        }

        throw new ServiceException("不支持的操作！");

    }

    /**
     * 执行发布操作
     *
     * @param snmpEntity snmp参数
     * @param requestVO  报文参数
     * @throws ServiceException 异常
     */
    public void publish(SnmpEntity snmpEntity, ChannelRequestVO requestVO) throws ServiceException {
        throw new ServiceException("不支持publish操作！");
    }
}
