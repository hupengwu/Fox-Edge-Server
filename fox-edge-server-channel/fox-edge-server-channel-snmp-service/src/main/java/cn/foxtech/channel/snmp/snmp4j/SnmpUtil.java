package cn.foxtech.channel.snmp.snmp4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * https://blog.csdn.net/wjs040/article/details/106938421
 * https://www.cnblogs.com/yuanfy008/p/8046189.html
 * author: Administrator
 * createtime: 2020/6/23 17:56
 */
public class SnmpUtil {
    private static final Logger log = LoggerFactory.getLogger(SnmpUtil.class);
    private static final String community = "public";
    //   private static final String ipAddress = "udp:192.168.3.133/";
    public static Snmp snmp = null;

    /**
     * 初始化snmp
     * @param localAddress 本地IP
     * @throws IOException 异常
     */
    public static void initSnmp(String localAddress) throws IOException {
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
        TransportMapping<?> transportMapping;
        if (localAddress == null || localAddress.isEmpty()) {
            transportMapping = new DefaultUdpTransportMapping();
        } else {
            UdpAddress updAddr = (UdpAddress) GenericAddress.parse(localAddress);
            transportMapping = new DefaultUdpTransportMapping(updAddr);
        }

        // 3、正式创建snmp
        snmp = new Snmp(messageDispatcher, transportMapping);
        // 开启监听
        snmp.listen();
    }

    private static Target createTarget(int version, String ipAddress, int port) {
        Target target = null;
        if (!(version == SnmpConstants.version3 || version == SnmpConstants.version2c || version == SnmpConstants.version1)) {
            log.error("参数version异常");
            return target;
        }
        if (version == SnmpConstants.version3) {
            target = new UserTarget();
            // snmpV3需要设置安全级别和安全名称，其中安全名称是创建snmp指定user设置的new
            // OctetString("SNMPV3")
            target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
            target.setSecurityName(new OctetString("SNMPV3"));
        } else {
            // snmpV1和snmpV2需要指定团体名名称
            target = new CommunityTarget();
            ((CommunityTarget) target).setCommunity(new OctetString(community));
            if (version == SnmpConstants.version2c) {
                target.setSecurityModel(SecurityModel.SECURITY_MODEL_SNMPv2c);
            }
        }
        target.setVersion(version);
        // 必须指定，没有设置就会报错。
        target.setAddress(GenericAddress.parse(ipAddress + port));
        target.setRetries(5);
        target.setTimeout(3000);
        return target;
    }

    private static PDU createPDU(int version, int type, String oid) {
        PDU pdu = null;
        if (version == SnmpConstants.version3) {
            pdu = new ScopedPDU();
        } else {
            pdu = new PDUv1();
        }
        pdu.setType(type);
        // 可以添加多个变量oid
        pdu.add(new VariableBinding(new OID(oid)));
        return pdu;
    }

    private static PDU createPDU(int version, int type, List<String> oids) {
        PDU pdu = null;
        if (version == SnmpConstants.version3) {
            pdu = new ScopedPDU();
        } else {
            pdu = new PDUv1();
        }
        pdu.setType(type);
        // 可以添加多个变量oid
        for (String oid : oids) {
            pdu.add(new VariableBinding(new OID(oid)));
        }
        return pdu;
    }

    public static void snmpGet(String ipAddress, List<String> oids) {
        try {
            // 2、创建目标对象
            Target target = createTarget(SnmpConstants.version2c, ipAddress, SnmpConstants.DEFAULT_COMMAND_RESPONDER_PORT);
            // 3、创建报文
            PDU pdu = createPDU(SnmpConstants.version2c, PDU.GET, oids);
            System.out.println("-------> 发送PDU <-------");
            // 4、发送报文，并获取返回结果
            ResponseEvent responseEvent = snmp.send(pdu, target);
            PDU response = responseEvent.getResponse();
            if (response == null) {
                return;
            }

            for (VariableBinding var : response.getVariableBindings()) {
                System.out.println("返回结果：" + var.getOid() + ":" + var.getVariable());
            }
            System.out.println("-------> 接收PDU <-------");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void snmpGet1(String ipAddress, String oid) {
        try {
            // 1、初始化snmp,并开启监听
            initSnmp("udp:192.168.1.3/161");
            // 2、创建目标对象
            Target target = createTarget(SnmpConstants.version2c, ipAddress, SnmpConstants.DEFAULT_COMMAND_RESPONDER_PORT);
            // 3、创建报文
            PDU pdu = createPDU(SnmpConstants.version2c, PDU.GET, oid);
            System.out.println("-------> 发送PDU <-------");
            // 4、发送报文，并获取返回结果
            ResponseEvent responseEvent = snmp.send(pdu, target);
            PDU response = responseEvent.getResponse();
            System.out.println("返回结果：" + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void snmpWalk(String ipAddress, String oid) {
        try {
            // 1、初始化snmp,并开启监听
            initSnmp("udp:192.168.1.3/161");
            // 2、创建目标对象
            Target target = createTarget(SnmpConstants.version2c, ipAddress, SnmpConstants.DEFAULT_COMMAND_RESPONDER_PORT);
            // 3、创建报文
            PDU pdu = createPDU(SnmpConstants.version2c, PDU.GETNEXT, oid);
            System.out.println("-------> 发送PDU <-------");
            // 4、发送报文，并获取返回结果
            boolean matched = true;
            while (matched) {
                ResponseEvent responseEvent = snmp.send(pdu, target);
                if (responseEvent == null || responseEvent.getResponse() == null) {
                    break;
                }
                PDU response = responseEvent.getResponse();
                String nextOid = null;
                Vector<? extends VariableBinding> variableBindings = response.getVariableBindings();
                for (int i = 0; i < variableBindings.size(); i++) {
                    VariableBinding variableBinding = variableBindings.elementAt(i);
                    Variable variable = variableBinding.getVariable();
                    nextOid = variableBinding.getOid().toDottedString();
                    // 如果不是这个节点下的oid则终止遍历，否则会输出很多，直到整个遍历完。
                    if (!nextOid.startsWith(oid)) {
                        matched = false;
                        break;
                    }
                    // System.out.println(variable);
                }
                if (!matched) {
                    break;
                }
                pdu.clear();
                pdu.add(new VariableBinding(new OID(nextOid)));
                System.out.println("返回结果：" + response);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        // 1、初始化snmp,并开启监听
        initSnmp("udp:192.168.1.3/161");
        List<String> oids = new ArrayList<>();
        oids.add("1.3.6.1.4.1.8072.1.9.1.1.2.8.103.114.112.99.111.109.109.49.0.0.1.4.114.101.97.100");
        oids.add("1.3.6.1.2.1.6.3.0");
        oids.add("1.3.6.1.2.1.1.1.0");
        for (int i = 0; i < 10000; i++) {
            //           snmpGet("1.3.6.1.2.1.1.1.0");
            snmpGet("udp:192.168.3.133/", oids);
            snmpGet("udp:192.168.1.22/", oids);
        }
        //   snmpWalk("1.3.6.1.2.1.25.3.3.1.2");// CPU的当前负载，N个核就有N个负载4
    }
}
