package cn.foxtech.channel.snmp.service;

import cn.foxtech.core.exception.ServiceException;
import org.apache.log4j.Logger;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
public class SnmpService {
    private static final Logger logger = Logger.getLogger(SnmpService.class);

    private final String DEFAULT_PROTOCOL = "udp";
    private final long DEFAULT_TIMEOUT = 3 * 1000L;
    private final int DEFAULT_RETRY = 3;

    public static void main(String[] args) {

        SnmpService test = new SnmpService();

        test.testGetAsyList();
    }

    /**
     * 创建对象communityTarget，用于返回target
     *
     * @return CommunityTarget
     */
    public CommunityTarget createSnmpTarget(String targetIp, int targetPort, int version, String community) {
        Address address = GenericAddress.parse(DEFAULT_PROTOCOL + ":" + targetIp + "/" + targetPort);
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(address);
        target.setVersion(version);
        target.setTimeout(DEFAULT_TIMEOUT);
        target.setRetries(DEFAULT_RETRY);
        return target;
    }

    private Snmp createSnmpLocal(UdpAddress localAddress) throws IOException {
        // 1、初始化多线程消息转发类
        MessageDispatcher messageDispatcher = new MessageDispatcherImpl();

        // 其中要增加三种处理模型。如果snmp初始化使用的是Snmp(TransportMapping<? extends Address>
        // transportMapping) ,就不需要增加
        messageDispatcher.addMessageProcessingModel(new MPv1());
        messageDispatcher.addMessageProcessingModel(new MPv2c());
        // 当要支持snmpV3版本时，需要配置user
        OctetString localEngineID = new OctetString(MPv3.createLocalEngineID());
        USM usm = new USM(SecurityProtocols.getInstance().addDefaultProtocols(), localEngineID, 0);
        UsmUser user = new UsmUser(new OctetString("SNMPV3"), AuthSHA.ID, new OctetString("authPassword"), PrivAES128.ID, new OctetString("privPassword"));
        usm.addUser(user.getSecurityName(), user);
        messageDispatcher.addMessageProcessingModel(new MPv3(usm));

        // 2、创建transportMapping
        TransportMapping<?> transport;
        if (localAddress == null) {
            transport = new DefaultUdpTransportMapping();
        } else {
            transport = new DefaultUdpTransportMapping(localAddress);
        }

        // 创建SNMP监听器
        Snmp snmp = new Snmp(transport);
        snmp.listen();

        return snmp;
    }

    /**
     * 根据OID列表，采用异步方式一次获取多条OID数据，并且以List形式返回
     *
     * @param localIp   本地IP
     * @param targetIp  目标IP
     * @param version   snmp版本号 SnmpConstants.version2c
     * @param community 团体属性
     * @param oidList   oid列表
     * @param timeout   通信超时
     * @return
     */
    public PDU snmpASynGetList(String localIp, int localPort, String targetIp, int targetPort, int version, String community, List<String> oidList, int timeout) {
        final PDU[] respond = {null};
        Snmp snmp = null;

        try {
            // 创建目标对象：对端的设备
            CommunityTarget target = createSnmpTarget(targetIp, targetPort, version, community);

            // 创建本地对象
            UdpAddress localAddress = (UdpAddress) GenericAddress.parse(DEFAULT_PROTOCOL + ":" + localIp + "/" + localPort);
            snmp = this.createSnmpLocal(localAddress);

            /* 异步获取 */
            final CountDownLatch latch = new CountDownLatch(1);
            ResponseListener listener = new ResponseListener() {
                public void onResponse(ResponseEvent event) {
                    // 撤销请求
                    ((Snmp) event.getSource()).cancel(event.getRequest(), this);

                    // 获取返回的数据
                    respond[0] = event.getResponse();

                    // 关闭计数器
                    latch.countDown();
                }
            };

            // 准备发送的数据
            PDU pdu = new PDU();
            pdu.setType(PDU.GET);
            for (String oid : oidList) {
                pdu.add(new VariableBinding(new OID(oid)));
            }

            // 异步发送数据
            snmp.send(pdu, target, null, listener);

            // 异步等待数据
            latch.await(timeout, TimeUnit.MILLISECONDS);

            // 关闭snmp
            snmp.close();
        } catch (Exception e) {
            throw new ServiceException("SNMP Get Exception:" + e.getMessage());
        } finally {
            if (snmp != null) {
                try {
                    snmp.close();
                } catch (IOException ex1) {
                    snmp = null;
                }
            }

        }

        return respond[0];
    }

    public void testGetAsyList() {
        String ip = "192.168.3.133";
        String community = "public";
        List<String> oidList = new ArrayList<String>();
        oidList.add("1.3.6.1.4.1.2021.10.1.6.1");
        for (int i = 0; i < 100000; i++) {
            PDU respond = this.snmpASynGetList("192.168.1.3", 161, ip, 161, SnmpConstants.version2c, community, oidList, 10 * 1000);

            if (respond == null) {
                logger.error("[ERROR]: response is null");
            } else if (respond.getErrorStatus() != 0) {
                logger.error("[ERROR]: response status" + respond.getErrorStatus() + " Text:" + respond.getErrorStatusText());
            } else {
                Map<String, Object> result = new HashMap<>();
                for (int j = 0; j < respond.size(); j++) {
                    VariableBinding vb = respond.get(j);

                    result.put(vb.getOid().format(), vb.getVariable().toString());
                }
            }
        }
        System.out.println("i am first!");
    }


}
