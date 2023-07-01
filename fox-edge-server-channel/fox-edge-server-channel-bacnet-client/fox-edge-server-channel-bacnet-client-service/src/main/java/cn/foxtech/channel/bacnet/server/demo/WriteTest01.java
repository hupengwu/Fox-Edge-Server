package cn.foxtech.channel.bacnet.server.demo;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.Real;
import com.serotonin.bacnet4j.util.RequestUtils;

/**
 * https://blog.csdn.net/dream_broken/article/details/106646604
 */
public class WriteTest01 {

    public static void main(String[] args) throws Exception {
        LocalDevice d = null;
        try {
            IpNetwork ipNetwork = new IpNetworkBuilder()
                    .withLocalBindAddress("192.168.1.3")
                    .withSubnet("255.255.255.0", 24)
                    .withPort(47808)
                    .withReuseAddress(true)
                    .build();

            d = new LocalDevice(123, new DefaultTransport(ipNetwork));
            d.initialize();
            d.startRemoteDeviceDiscovery();

            RemoteDevice rd = d.getRemoteDevice(3).get();//获取远程设备

            //必须先修改out of service为true
        //    RequestUtils.writeProperty(d, rd, new ObjectIdentifier(ObjectType.analogValue, 1),PropertyIdentifier.outOfService, Boolean.FALSE);
       //     Thread.sleep(1000);
            //修改属性值
            RequestUtils.writePresentValue(d, rd, new ObjectIdentifier(ObjectType.analogValue, 0), new Real(7111));
            RequestUtils.writePresentValue(d, rd, new ObjectIdentifier(ObjectType.analogValue, 1), new Real(7222));
            RequestUtils.writePresentValue(d, rd, new ObjectIdentifier(ObjectType.analogValue, 2), new Real(7333));
            RequestUtils.writePresentValue(d, rd, new ObjectIdentifier(ObjectType.analogValue, 3), new Real(7444));

      //      Thread.sleep(2000);
            System.out.println("analogValue0= " +RequestUtils.readProperty(d, rd, new ObjectIdentifier(ObjectType.analogValue, 0), PropertyIdentifier.presentValue, null));
            System.out.println("analogValue1= " +RequestUtils.readProperty(d, rd, new ObjectIdentifier(ObjectType.analogValue, 1), PropertyIdentifier.presentValue, null));
            System.out.println("analogValue2= " +RequestUtils.readProperty(d, rd, new ObjectIdentifier(ObjectType.analogValue, 2), PropertyIdentifier.presentValue, null));
            System.out.println("analogValue3= " +RequestUtils.readProperty(d, rd, new ObjectIdentifier(ObjectType.analogValue, 3), PropertyIdentifier.presentValue, null));

            Thread.sleep(1000);
            d.terminate();
        } catch (Exception e) {
            e.printStackTrace();
            if(d != null){
                d.terminate();
            }
        }

    }
}
