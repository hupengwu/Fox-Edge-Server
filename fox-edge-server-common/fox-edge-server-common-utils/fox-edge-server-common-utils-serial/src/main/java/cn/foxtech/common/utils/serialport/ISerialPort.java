package cn.foxtech.common.utils.serialport;

import cn.foxtech.common.utils.serialport.linux.SerialPortLinux;
import cn.foxtech.common.utils.serialport.win32.SerialPortWin32;
import com.sun.jna.Platform;

/**
 * 串口接口：方便跨平台的各自串口类实现
 */
public interface ISerialPort {
    static ISerialPort newInstance() {
        if (Platform.isWindows()) {
            return new SerialPortWin32();
        } else if (Platform.isLinux()) {
            return new SerialPortLinux();
        } else {
            return null;
        }
    }

    public String getName();

    /**
     * 串口是否打开
     *
     * @return 是否打开
     */
    boolean isOpen();

    /**
     * 打开串口
     *
     * @param name LINUX下串口名为ttyS0这样格式的数据，WINDOWS下串口名为COM1这样的格式
     * @return 操作是否成功
     */
    boolean open(String name);


    /**
     * 设置串口参数
     *
     * @param baudRate 速率
     * @param databits 数据位
     * @param stopbits 停止位
     * @param parity   校验位
     * @param commTimeOuts   commTimeOuts的字节时间间隔
     * @return 是否成功
     */
    boolean setParam(Integer baudRate, String parity, Integer databits, Integer stopbits, Integer commTimeOuts);

    /**
     * 发送数据
     *
     * @param data    缓冲区
     * @return 实际发送的数据长度
     */
    int sendData(byte[] data);

    /**
     * 接收数据
     *
     * @param data     准备发送的数据库
     * @param mTimeout 最大超时等待时间，单位毫秒
     * @return 接收到数据
     */
    int recvData(byte[] data, long mTimeout);

    /**
     * 读取串口数据
     *
     * @param recvBuffer      缓存
     * @param minPackInterval 两组数据报文之间，最小的时间间隔
     * @param maxPackInterval 两组数据报文之间，最大的时间间隔
     * @return 报文长度
     */
    public int recvData(byte[] recvBuffer, long minPackInterval, long maxPackInterval);

    /**
     * 清空缓冲区
     *
     * @return 操作是否成功
     */
    boolean clearRecvFlush();

    /**
     * 清空缓冲区
     *
     * @return 操作是否成功
     */
    boolean clearSendFlush();

    /**
     * 关闭串口
     */
    boolean close();
}
