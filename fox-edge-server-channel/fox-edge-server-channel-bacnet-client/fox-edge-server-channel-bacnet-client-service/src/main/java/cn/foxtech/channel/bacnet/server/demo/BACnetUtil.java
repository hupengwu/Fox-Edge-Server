package cn.foxtech.channel.bacnet.server.demo;

import java.util.List;

import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.ObjectIdentifier;
import com.serotonin.bacnet4j.util.RequestUtils;


/**
 * https://blog.csdn.net/qq_39432715/article/details/112986340
 */
public class BACnetUtil {

    /**
     * 创建网络对象
     */
    public static IpNetwork initIpNetwork(String ip, int port) {
        IpNetwork ipNetwork = new IpNetworkBuilder()
                //本机的ip
                .withLocalBindAddress(ip)
                .withSubnet("255.255.255.0", 24)
                //Yabe默认的UDP端口
                .withPort(port)
                .withReuseAddress(true)
                .build();
        return ipNetwork;
    }

    /**
     * 创建虚拟的本地设备，deviceNumber随意
     */
    public static LocalDevice initLocalDevice(int deviceNumber, IpNetwork ipNetwork) {
        LocalDevice localDevice = new LocalDevice(deviceNumber, new DefaultTransport(ipNetwork));
        try {
            //初始化
            localDevice.initialize();
            return localDevice;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        BACnetUtil test = new BACnetUtil();
        //初始化网络对象
        IpNetwork ipNetwork = initIpNetwork("192.168.1.2", 47808);
        //初始化虚拟的本地设备
        LocalDevice localDevice = initLocalDevice(123, ipNetwork);
        try {
            //获取远程设备，instanceNumber 是设备的device id
            //3202777风机
            //新风3202658
            //组合式100102
            //分体式100101
            RemoteDevice remoteDevice = localDevice.getRemoteDeviceBlocking(2883764,5000);
            localDevice.startRemoteDeviceDiscovery();
            List<RemoteDevice> remoteDeviceList = localDevice.getRemoteDevices();
            //获取全部键值对
            //System.err.println(PropertyIdentifier.getPrettyMap());

            //改设备id的设备节点，和数据节点，设备节点固定为第一位
            List<ObjectIdentifier> objectList = RequestUtils.getObjectList(localDevice, remoteDevice).getValues();


            for(int i=1;i<objectList.size();i++){
                //根据他的属性名获取值
                //System.err.println(RequestUtils.getProperty(localDevice, remoteDevice ,objectList.get(i), PropertyIdentifier.objectName));
                String name=((Encodable)RequestUtils.getProperty(localDevice, remoteDevice ,objectList.get(i), PropertyIdentifier.objectName)).toString();
                System.out.println(name);
                //解析风机
                //getFan(name,localDevice,remoteDevice,objectList.get(i));
                //解析新风
                //getFreshairconditioningunit(name,localDevice,remoteDevice,objectList.get(i));
                //解析分体式
                getSplitheatrecoveryfreshairconditioningunit(name,localDevice,remoteDevice,objectList.get(i));
                //解析组合式
                //getCombinedheatrecoveryairconditioningunit(name,localDevice,remoteDevice,objectList.get(i));
            }


            Thread.sleep(3000);
            localDevice.terminate();
        } catch (Exception e) {
            e.printStackTrace();
            if (localDevice != null) {
                localDevice.terminate();
            }
        }
    }

    /**
     * 解析风机
     * @param name
     * @param localDevice
     * @param remoteDevice
     * @param objectIdentifier
     */
    public static void getFan(String name,LocalDevice localDevice,RemoteDevice remoteDevice,ObjectIdentifier objectIdentifier){
        if("EFMAS".equals(name)){
            try {
                //排风手自动
                String EFMAS = ((Encodable) RequestUtils.getProperty(localDevice, remoteDevice ,objectIdentifier, PropertyIdentifier.presentValue)).toString();
                if("inactive".equals(EFMAS)){
                    System.err.println("手动");
                }else {
                    System.err.println("自动");
                }
            } catch (BACnetException e) {
                e.printStackTrace();
            }
        }else if("SFRS".equals(name)){
            try {
                //排风运行状态
                String SFRS= ((Encodable) RequestUtils.getProperty(localDevice, remoteDevice ,objectIdentifier, PropertyIdentifier.presentValue)).toString();
                if("inactive".equals(SFRS)){
                    System.err.println("手动");
                }else {
                    System.err.println("自动");
                }
            } catch (BACnetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解析新风机组
     * @param name
     * @param localDevice
     * @param remoteDevice
     * @param objectIdentifier
     */
    public static void getFreshairconditioningunit(String name,LocalDevice localDevice,RemoteDevice remoteDevice,ObjectIdentifier objectIdentifier){
        if("SFtemp".equals(name)){
            try {
                //新风空调送风温度
                String SFtemp = ((Encodable) RequestUtils.getProperty(localDevice, remoteDevice ,objectIdentifier, PropertyIdentifier.presentValue)).toString();
                System.err.println(SFtemp);
            } catch (BACnetException e) {
                e.printStackTrace();
            }
        }else if("SFRS".equals(name)){
            try {
                //新风运行状态
                String SFRS= ((Encodable) RequestUtils.getProperty(localDevice, remoteDevice ,objectIdentifier, PropertyIdentifier.presentValue)).toString();
                if("inactive".equals(SFRS)){
                    System.err.println("停止");
                }else {
                    System.err.println("启动");
                }
            } catch (BACnetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解析组合式
     * @param name
     * @param localDevice
     * @param remoteDevice
     * @param objectIdentifier
     */
    public static void getCombinedheatrecoveryairconditioningunit(String name,LocalDevice localDevice,RemoteDevice remoteDevice,ObjectIdentifier objectIdentifier){
        if("AHUHFTEMP_AV".equals(name)){
            try {
                //组合式回风温度
                String AHUHFTEMP_AV = ((Encodable) RequestUtils.getProperty(localDevice, remoteDevice ,objectIdentifier, PropertyIdentifier.presentValue)).toString();
                System.err.println(AHUHFTEMP_AV);
            } catch (BACnetException e) {
                e.printStackTrace();
            }
        }else if("AHUSFTEMP_AV".equals(name)){
            try {
                //组合式送风温度
                String AHUSFTEMP_AV = ((Encodable) RequestUtils.getProperty(localDevice, remoteDevice ,objectIdentifier, PropertyIdentifier.presentValue)).toString();
                System.err.println(AHUSFTEMP_AV);
            } catch (BACnetException e) {
                e.printStackTrace();
            }
        }else if("FanSFRS".equals(name)){
            try {
                //组合式运行状态
                String FanSFRS= ((Encodable) RequestUtils.getProperty(localDevice, remoteDevice ,objectIdentifier, PropertyIdentifier.presentValue)).toString();
                if("inactive".equals(FanSFRS)){
                    System.err.println("手动");
                }else {
                    System.err.println("自动");
                }
            } catch (BACnetException e) {
                e.printStackTrace();
            }
            //FanSFCXPDS_BI
        }else if("AHUHFSHIDU_AV".equals(name)){
            try {
                //组合式回风湿度
                String AHUHFSHIDU_AV = ((Encodable) RequestUtils.getProperty(localDevice, remoteDevice ,objectIdentifier, PropertyIdentifier.presentValue)).toString();
                System.err.println(AHUHFSHIDU_AV);
            } catch (BACnetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解析分体式
     * @param name
     * @param localDevice
     * @param remoteDevice
     * @param objectIdentifier
     */
    public static void getSplitheatrecoveryfreshairconditioningunit(String name,LocalDevice localDevice,RemoteDevice remoteDevice,ObjectIdentifier objectIdentifier){
        if("EFtemp".equals(name)){
            try {
                //分体式空调送风温度
                String EFtemp = ((Encodable) RequestUtils.getProperty(localDevice, remoteDevice ,objectIdentifier, PropertyIdentifier.presentValue)).toString();
                System.err.println(EFtemp);
            } catch (BACnetException e) {
                e.printStackTrace();
            }
        }else if("SFtemp".equals(name)){
            try {
                //分体式空调送风温度
                String SFtemp = ((Encodable) RequestUtils.getProperty(localDevice, remoteDevice ,objectIdentifier, PropertyIdentifier.presentValue)).toString();
                System.err.println(SFtemp);
            } catch (BACnetException e) {
                e.printStackTrace();
            }
        }else if("RSfan".equals(name)){
            try {
                //分体式运行状态
                String RSFAN = ((Encodable) RequestUtils.getProperty(localDevice, remoteDevice ,objectIdentifier, PropertyIdentifier.presentValue)).toString();
                if("inactive".equals(RSFAN)){
                    System.err.println("手动");
                }else {
                    System.err.println("自动");
                }
            } catch (BACnetException e) {
                e.printStackTrace();
            }
        }

    }
}

