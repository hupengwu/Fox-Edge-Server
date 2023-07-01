package cn.foxtech.channel.snmp.snmp4j.demo;

import cn.foxtech.channel.snmp.snmp4j.SnmpData;

import java.util.ArrayList;
import java.util.List;

public class SnmpTest {

    /**
     * @param args
     */
    public static void main(String[] args) {

        // TODO Auto-generated method stub
        SnmpTest test = new SnmpTest();

        test.testGet();

    }

    public void testGet() {
        String ip = "192.168.3.133";
        String community = "public";
        String oidval = "1.3.6.1.4.1.2021.10.1.3.1";
  //      String oidval = ".1.3.6.1.4.1.2021.10.1.6.1";
        SnmpData.snmpGet(ip, community, oidval);
    }

    public void testGetList() {
        String ip = "192.168.3.133";
        String community = "public";
        List<String> oidList = new ArrayList<String>();
        oidList.add("1.3.6.1.2.1.1.5.0");
        oidList.add("1.3.6.1.2.1.1.7.0");
        oidList.add("1.3.6.1.4.1.2021.10.1.6.1");
        SnmpData.snmpGetList(ip, community, oidList);
    }

    public void testGetAsyList() {
        String ip = "192.168.3.133";
        String community = "public";
        List<String> oidList = new ArrayList<String>();
        oidList.add("1.3.6.1.4.1.2021.10.1.6.1");
        oidList.add("1.3.6.1.4.1.2021.10.1.6.1");
        for (int i = 0; i < 100000; i++) {
            SnmpData.snmpASynGetList(ip, community, oidList);
        }
        System.out.println("i am first!");
    }

    public void testWalk() {
        String ip = "127.0.0.1";
        String community = "public";
        String targetOid = "1.3.6.1.2.1.1.5.0";
        SnmpData.snmpWalk(ip, community, targetOid);
    }

    public void testAsyWalk() {
        String ip = "127.0.0.1";
        String community = "public";
        // 异步采集数据
        SnmpData.snmpASynWalk(ip, community, "1.3.6.1.2.1.25.4.2.1.2");
    }

    public void testSetPDU() throws Exception {
        String ip = "127.0.0.1";
        String community = "public";
        SnmpData.setPDU(ip, community, "1.3.6.1.2.1.1.6.0", "jianghuiwen");
    }

    public void testVersion() {
        System.out.println(org.snmp4j.version.VersionInfo.getVersion());
    }
}
