package cn.foxtech.channel.bacnet.server.util;

import cn.foxtech.common.utils.syncobject.SyncCountObjectMap;
import cn.foxtech.common.utils.hex.HexUtils;
import com.serotonin.bacnet4j.LocalDevice;
import com.serotonin.bacnet4j.RemoteDevice;
import com.serotonin.bacnet4j.enums.DayOfWeek;
import com.serotonin.bacnet4j.enums.Month;
import com.serotonin.bacnet4j.exception.BACnetErrorException;
import com.serotonin.bacnet4j.exception.BACnetException;
import com.serotonin.bacnet4j.npdu.Network;
import com.serotonin.bacnet4j.npdu.ip.IpNetwork;
import com.serotonin.bacnet4j.npdu.ip.IpNetworkBuilder;
import com.serotonin.bacnet4j.transport.DefaultTransport;
import com.serotonin.bacnet4j.type.Encodable;
import com.serotonin.bacnet4j.type.enumerated.ErrorClass;
import com.serotonin.bacnet4j.type.enumerated.ErrorCode;
import com.serotonin.bacnet4j.type.enumerated.ObjectType;
import com.serotonin.bacnet4j.type.enumerated.PropertyIdentifier;
import com.serotonin.bacnet4j.type.primitive.Boolean;
import com.serotonin.bacnet4j.type.primitive.Date;
import com.serotonin.bacnet4j.type.primitive.Double;
import com.serotonin.bacnet4j.type.primitive.*;
import com.serotonin.bacnet4j.util.PropertyValues;
import com.serotonin.bacnet4j.util.ReadListener;
import com.serotonin.bacnet4j.util.RequestUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * BACnet的工具类
 */
public class BACnetUtils {
    /**
     * 创建网络对象
     *
     * @param ip   该IP是跟BACNet设备一个网段的IP
     *             比如你的计算机有192.168.1.5，127.0.0.1，10.34.25.45三个IP
     *             而BACNet设备的IP是192.168.1.21，192.168.1.22，那么你必须填写192.168.1.5
     * @param port 本地端口号
     * @return 本地的虚拟的BACNet网络，这个网络对象被用来进行BACNet的协议控制
     */
    public static IpNetwork buildIpNetwork(String ip, int port) {
        return new IpNetworkBuilder()
                //本机的ip
                .withLocalBindAddress(ip).withSubnet("255.255.255.0", 24).withPort(port).withReuseAddress(true).build();
    }

    /**
     * 生成一个本地的BACNet虚拟设备
     * 在BACNet中，各设备是彼此对等通信，而不是主从通信的，所以先虚拟出一个设备来，跟远端设备进行对等通信
     *
     * @param localDeviceId 本地设备ID
     * @param network       物理层网络套件
     * @return 本地设备
     * @throws Exception 操作异常
     */
    public static LocalDevice buildLocalDevice(int localDeviceId, Network network) throws Exception {
        LocalDevice localDevice = new LocalDevice(localDeviceId, new DefaultTransport(network));
        localDevice.initialize();
        return localDevice;
    }

    /**
     * 初始化本地虚拟设备
     * 这虚拟设备使用完毕后，要主动terminate掉
     *
     * @param ip            本地计算机的IP
     * @param port          本地计算机的端口号
     * @param localDeviceId 本地设备的ID
     * @return 本地设备对象
     * @throws Exception 异常信息
     */
    public static LocalDevice initLocalDevice(String ip, int port, int localDeviceId) throws Exception {
        IpNetwork ipNetwork = buildIpNetwork(ip, port);
        return buildLocalDevice(localDeviceId, ipNetwork);
    }

    /**
     * 与远端设备建立通信连接信息
     *
     * @param localDevice        本地设备对象
     * @param remoteDeviceNumber 远端设备
     * @return 远端设备
     * @throws Exception 异常信息
     */
    public static RemoteDevice getRemoteDevice(LocalDevice localDevice, int remoteDeviceNumber) throws Exception {
        return localDevice.getRemoteDeviceBlocking(remoteDeviceNumber);
    }

    /**
     * 过滤掉无法读取的类型：比如binaryValue是不能读取的
     *
     * @param oids 包含有毒数据的OID列表
     * @return 可读取的OID列表
     */
    public static List<ObjectIdentifier> filterCannotReadOids(List<ObjectIdentifier> oids) {
        List<ObjectIdentifier> readOids = new ArrayList<>();

        // 目前已经知道可以读取的数据
        for (ObjectIdentifier oid : oids) {
            if (ObjectType.device.equals(oid.getObjectType())) {
                readOids.add(oid);
            }
            if (ObjectType.analogInput.equals(oid.getObjectType())) {
                readOids.add(oid);
            }
            if (ObjectType.analogValue.equals(oid.getObjectType())) {
                readOids.add(oid);
            }
            if (ObjectType.characterstringValue.equals(oid.getObjectType())) {
                readOids.add(oid);
            }
            if (ObjectType.multiStateValue.equals(oid.getObjectType())) {
                readOids.add(oid);
            }
        }

        return readOids;
    }


    /**
     * 读取数据：不能一次读取太多的数据，会返回不了的
     *
     * @param localDevice  本地设备
     * @param remoteDevice 远端设备
     * @param oids         oid
     * @param timeOut      超时
     * @return 远端设备的数值
     * @throws BACnetException 异常信息
     * @throws InterruptedException 异常信息
     */
    public static List<Map<String, Object>> readOidPresentValues(LocalDevice localDevice, RemoteDevice remoteDevice, List<ObjectIdentifier> oids, int timeOut) throws BACnetException, InterruptedException {
        // 设置等待标识：比如设备ID,等待3个ID共3次返回
        SyncCountObjectMap.inst().reset(remoteDevice.getName(), oids.size());

        //获取指定的presentValue
        PropertyValues pvs = RequestUtils.readOidPresentValues(localDevice, remoteDevice, oids, new ReadListener() {
            @Override
            public boolean progress(double progress, int deviceId, ObjectIdentifier oid, PropertyIdentifier pid, UnsignedInteger pin, Encodable value) {
                Map<String, Object> map = new HashMap<>();
                map.put("progress", progress);
                map.put("deviceId", deviceId);
                map.put("oidType", oid.getObjectType().getTypeId());
                map.put("oid", oid.getInstanceNumber());
                map.put("pid", pid.toString());
                map.put("pin", pin);
                map.put("value", value.toString());
                map.put("valueType", value.getClass().getSimpleName());

                SyncCountObjectMap.inst().notify(remoteDevice.getName(), map);

                // return false意思是还有后续数据没有处理完成，继续处理下一个数据
                return false;
            }

        });

        // 等待处理完的信号
        List<Object> objList = SyncCountObjectMap.inst().wait(remoteDevice.getName(), timeOut);
        if (objList == null) {
            return null;
        }

        // 转换容器类型
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Object object : objList) {
            mapList.add((Map<String, Object>) object);
        }

        return mapList;
    }

    public static void writePresentValue(LocalDevice localDevice, int remoteDeviceId, ObjectIdentifier oid, String valueType, Object value, int timeOut) throws BACnetException, InterruptedException, ParseException {
        Encodable encodable = buildEncodable(valueType, value.toString());
        writePresentValue(localDevice, remoteDeviceId, oid, encodable, timeOut);
    }

    public static void writePresentValue(LocalDevice localDevice, int remoteDeviceId, ObjectIdentifier oid, Encodable encodable, int timeOut) throws BACnetException, InterruptedException, ParseException {
        //获取远程设备
        RemoteDevice remoteDevice = localDevice.getRemoteDevice(remoteDeviceId).get();

        //有些设备的OID值是引用另一个OID值，这时候必须先修改out of service为true，断开它们的关联关系
//        RequestUtils.writeProperty(localDevice, remoteDevice, oid, PropertyIdentifier.outOfService, Boolean.TRUE);

        //修改属性值
        RequestUtils.writePresentValue(localDevice, remoteDevice, oid, encodable);
    }

    public static Primitive buildEncodable(String valueType, String value) throws BACnetErrorException, ParseException {
        if (valueType.equals("Null")) {
            return new Null();
        } else if (valueType.equals("Boolean")) {
            return Boolean.valueOf(java.lang.Boolean.parseBoolean(value));
        } else if (valueType.equals("UnsignedInteger")) {
            return new UnsignedInteger(Integer.parseInt(value));
        } else if (valueType.equals("SignedInteger")) {
            return new SignedInteger(Integer.parseInt(value));
        } else if (valueType.equals("Real")) {
            return new Real(Float.parseFloat(value));
        } else if (valueType.equals("Double")) {
            return new Double(java.lang.Double.valueOf(value));
        } else if (valueType.equals("OctetString")) {
            return new OctetString(HexUtils.hexStringToByteArray(value));
        } else if (valueType.equals("CharacterString")) {
            return new CharacterString(value);
        }
//        else if (valueType.equals("BitString")) {
//            return new BitString(value);
//        }
        else if (valueType.equals("Enumerated")) {
            return new Enumerated(Integer.parseInt(value));
        } else if (valueType.equals("Date")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date date = sdf.parse(value);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return new Date(calendar.get(Calendar.YEAR), Month.valueOf(calendar.get(Calendar.MONTH)), calendar.get(Calendar.DATE), DayOfWeek.valueOf(calendar.get(Calendar.DAY_OF_WEEK)));
        } else if (valueType.equals("Time")) {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
            java.util.Date date = sdf.parse(value);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return new Time(calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), calendar.get(Calendar.MILLISECOND) * 10);
        }
//     else if (valueType.equals("ObjectIdentifier")) {
//
//            return new ObjectIdentifier(Integer.parseInt(value));
//        }
        throw new BACnetErrorException(ErrorClass.property, ErrorCode.invalidDataType);
    }
}
