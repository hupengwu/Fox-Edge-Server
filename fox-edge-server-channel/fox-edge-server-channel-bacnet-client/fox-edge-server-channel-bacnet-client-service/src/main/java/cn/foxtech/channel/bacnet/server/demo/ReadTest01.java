package cn.foxtech.channel.bacnet.server.demo;

import java.util.Arrays;
import java.util.List;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.type.primitive.UnsignedInteger;
import com.serotonin.bacnet4j.util.PropertyValues;
import com.serotonin.bacnet4j.util.ReadListener;
import com.serotonin.bacnet4j.util.RequestUtils;

/**
 * 启动Yabe的天气模拟
 * @author Jfei
 * https://blog.csdn.net/dream_broken/article/details/106646604
 * 代码只只能适用于bacnet4j-5.02.jar，如果是bacnet4j-6.00.jar
 *
 */
public class ReadTest01 {

    /**
     * Yabe在本地电脑上启动
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        LocalDevice localDevice = null;
        try {
            //创建网络对象
            IpNetwork ipNetwork = new IpNetworkBuilder()
                    .withLocalBindAddress("192.168.1.2")//本机的ip
                    .withSubnet("255.255.255.0", 24)  //掩码和长度，如果不知道本机的掩码和长度的话，可以使用后面代码的工具类获取
                    .withPort(47808) //Yabe默认的UDP端口
                    .withReuseAddress(true)
                    .build();
            //创建虚拟的本地设备，deviceNumber随意
            localDevice = new LocalDevice(123, new DefaultTransport(ipNetwork));
            localDevice.initialize();
            localDevice.startRemoteDeviceDiscovery();

            Thread.sleep(3000);

            List<RemoteDevice> remoteDeviceList = localDevice.getRemoteDevices();

            RemoteDevice rd = localDevice.getRemoteDeviceBlocking(3);//获取远程设备，instanceNumber 是设备的device id

            System.out.println("modelName=" + rd.getDeviceProperty( PropertyIdentifier.modelName));
            System.out.println("analogInput2= " +RequestUtils.readProperty(localDevice, rd, new ObjectIdentifier(ObjectType.analogInput, 2), PropertyIdentifier.presentValue, null));


            List<ObjectIdentifier> objectList =  RequestUtils.getObjectList(localDevice, rd).getValues();

            //打印所有的Object 名称
            for(ObjectIdentifier o : objectList){
                System.out.println(o);
            }


            ObjectIdentifier oid = new ObjectIdentifier(ObjectType.analogInput, 0);
            ObjectIdentifier oid1 = new ObjectIdentifier(ObjectType.analogInput, 1);
            ObjectIdentifier oid2 = new ObjectIdentifier(ObjectType.analogInput, 2);

            //获取指定的presentValue
            PropertyValues pvs = RequestUtils.readOidPresentValues(localDevice, rd,Arrays.asList(oid,oid1,oid2), new ReadListener(){
                @Override
                public boolean progress(double progress, int deviceId,
                                        ObjectIdentifier oid, PropertyIdentifier pid,
                                        UnsignedInteger pin, Encodable value) {
                    System.out.println("========");
                    System.out.println("progress=" + progress);
                    System.out.println("deviceId=" + deviceId);
                    System.out.println("oid="+oid.toString());
                    System.out.println("pid="+pid.toString());
                    System.out.println("UnsignedInteger="+pin);
                    System.out.println("value="+value.toString() + "  getClass =" +value.getClass());
                    return false;
                }

            });
            Thread.sleep(3000);
            System.out.println("analogInput:0 == " + pvs.get(oid, PropertyIdentifier.presentValue));
            //获取指定的presentValue
            PropertyValues pvs2 = RequestUtils.readOidPresentValues(localDevice, rd,Arrays.asList(oid,oid1,oid2),null);
            System.out.println("analogInput:1 == " + pvs2.get(oid1, PropertyIdentifier.presentValue));

            localDevice.terminate();
        } catch (Exception e) {
            e.printStackTrace();
            if(localDevice != null){
                localDevice.terminate();
            }
        }

    }
}
