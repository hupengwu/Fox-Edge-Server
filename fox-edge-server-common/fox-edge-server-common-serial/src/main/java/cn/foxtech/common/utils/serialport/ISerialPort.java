package cn.foxtech.common.utils.serialport;

import cn.foxtech.common.utils.serialport.win32.SerialPortWin32;
import cn.foxtech.common.utils.serialport.linux.SerialPortLinux;
import cn.foxtech.common.utils.serialport.linux.entity.OutValue;
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

    /**
     * 串口是否打开
     *
     * @return 是否打开
     */
    public boolean isOpen();

    /**
     * 打开串口
     *
     * @param name LINUX下串口名为ttyS0这样格式的数据，WINDOWS下串口名为COM1这样的格式
     * @return 操作是否成功
     */
    public boolean open(String name);


    /**
     * 设置串口参数
     *
     * @param baudRate 速率
     * @param databits 数据位
     * @param stopbits 停止位
     * @param parity   校验位
     * @return 是否成功
     */
    public boolean setParam(Integer baudRate, String parity, Integer databits, Integer stopbits);

    /**
     * 发送数据
     *
     * @param data    缓冲区
     * @param sendLen
     * @return 操作是否成功
     */
    public boolean sendData(byte[] data, OutValue sendLen);

    /**
     * 接收数据
     *
     * @param data     准备发送的数据库
     * @param mTimeout 最大超时等待时间，单位毫秒
     * @param recvLen  接收到数据
     * @return 操作是否成功
     */
    public boolean recvData(byte[] data, long mTimeout, OutValue recvLen);

    /**
     * 清空缓冲区
     *
     * @return 操作是否成功
     */
    public boolean clearRecvFlush();

    /**
     * 清空缓冲区
     *
     * @return 操作是否成功
     */
    public boolean clearSendFlush();

    /**
     * 关闭串口
     */
    public boolean close();
}
