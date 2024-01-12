package cn.foxtech.common.utils.serialport.win32;

import cn.foxtech.common.utils.serialport.ISerialPort;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;

public class SerialPortWin32 implements ISerialPort {
    /**
     * JNA已经封装的WIN API
     */
    private static final Kernel32 KERNEL = Kernel32.INSTANCE;
    /**
     * JNA未封装的WIN API
     */
    private static final Win32API KERNELPLUS = Win32API.INSTANCE;

    /**
     * 无效指针：4294967295是32位下的-1
     */
    private static final Pointer INVALID_HANDLE_VALUE = Pointer.createConstant(4294967295L);

    //串口句柄
    private WinNT.HANDLE handle = null;

    /**
     * 串口名称
     */
    private String name = "COM1";

    @Override
    public String getName() {
        return this.name;
    }


    /**
     * 句柄是否打开
     *
     * @return
     */
    @Override
    public boolean isOpen() {
        if (this.handle == null) {
            return false;
        }

        return !this.handle.getPointer().equals(INVALID_HANDLE_VALUE);
    }

    /**
     * 关闭句柄
     */
    private void closeHandle() {
        if (this.handle == null) {
            return;
        }

        if (this.handle.getPointer().equals(INVALID_HANDLE_VALUE)) {
            return;
        }

        KERNEL.CloseHandle(this.handle);
        this.handle = null;
    }


    /**
     * 设置串口参数
     *
     * @param baudRate 速率
     * @param databits 数据位
     * @param stopbits 停止位
     * @param parity   校验位
     * @return 是否成功
     */
    @Override
    public boolean setParam(Integer baudRate, String parity, Integer databits, Integer stopbits) {
        // 检查：串口是否打开
        if (!this.isOpen()) {
            return false;
        }

        //设置dcb块
        WinBase.DCB dcb = new WinBase.DCB();
        if (!KERNEL.GetCommState(handle, dcb)) {
            this.closeHandle();
            return false;
        }

        // 格式化BCD数据：生成COM1:9600,n,8,1这样格式的数据
        String formatBcd = this.name + ":" + baudRate + "," + parity.toLowerCase() + "," + databits.toString() + "," + stopbits.toString();
        if (!KERNELPLUS.BuildCommDCB(formatBcd, dcb)) {
            return false;
        }


        //读写超时设置：ReadIntervalTimeout必须为-1，ReadTotalTimeoutMultiplier/ReadTotalTimeoutConstant为0
        WinBase.COMMTIMEOUTS CommTimeOuts = new WinBase.COMMTIMEOUTS();
        CommTimeOuts.ReadIntervalTimeout = new WinBase.DWORD(-1); //字符允许间隔ms 该参数如果为最大值，会使readfile命令立即返回
        CommTimeOuts.ReadTotalTimeoutMultiplier = new WinBase.DWORD(0); //总的超时时间(对单个字节)
        CommTimeOuts.ReadTotalTimeoutConstant = new WinBase.DWORD(0); //多余的超时时间ms
        CommTimeOuts.WriteTotalTimeoutMultiplier = new WinBase.DWORD(0); //总的超时时间(对单个字节)
        CommTimeOuts.WriteTotalTimeoutConstant = new WinBase.DWORD(2500); //多余的超时时间

        if (!KERNEL.SetCommTimeouts(handle, CommTimeOuts)) {
            return false;
        }

        // 设置通信配置
        if (!KERNEL.SetCommState(handle, dcb)) {
            return false;
        }

        /**
         * 设置串口上的发送/接收缓冲区
         */
        return KERNELPLUS.SetupComm(this.handle, new WinDef.DWORD(4096), new WinDef.DWORD(4096));
    }

    /**
     * 打开串口
     *
     * @param szPort COM1这样格式的名称
     * @return
     */
    @Override
    public boolean open(String szPort) {
        if (!szPort.startsWith("COM")) {
            return false;
        }

        // 说明：Windows下当串口号小于10的时候串口名为COM2这样的名称，当大于10的时候串口名为\\.\COM12这样的名字
        String no = szPort.substring("COM".length());
        if (Integer.parseInt(no) >= 10) {
            szPort = "\\\\.\\COM" + szPort.substring("COM".length());
        }

        //用异步方式读写串口
        this.handle = KERNEL.CreateFile(szPort, WinNT.GENERIC_READ | WinNT.GENERIC_WRITE, 0, null, WinNT.OPEN_EXISTING, WinNT.FILE_ATTRIBUTE_NORMAL, null);
        if (this.handle.getPointer().equals(INVALID_HANDLE_VALUE)) {
            return false;
        }


        this.name = szPort;

        //读写超时设置：关键配置，要注意啊，否则接收不到数据的
        WinBase.COMMTIMEOUTS CommTimeOuts = new WinBase.COMMTIMEOUTS();
        CommTimeOuts.ReadIntervalTimeout = new WinBase.DWORD(-1); //字符允许间隔ms 该参数如果为最大值，会使readfile命令立即返回
        CommTimeOuts.ReadTotalTimeoutMultiplier = new WinBase.DWORD(0); //总的超时时间(对单个字节)
        CommTimeOuts.ReadTotalTimeoutConstant = new WinBase.DWORD(0); //多余的超时时间ms
        CommTimeOuts.WriteTotalTimeoutMultiplier = new WinBase.DWORD(0); //总的超时时间(对单个字节)
        CommTimeOuts.WriteTotalTimeoutConstant = new WinBase.DWORD(2500); //多余的超时时间
        if (!KERNEL.SetCommTimeouts(handle, CommTimeOuts)) {
            this.closeHandle();
            return false;
        }

        return true;
    }

    /**
     * 关闭串口
     *
     * @return
     */
    @Override
    public boolean close() {
        // 检查串口是否打开
        if (!this.isOpen()) {
            return false;
        }

        // 复位串口设置
        if (!KERNELPLUS.SetCommMask(handle, null)) {
            return false;
        }

        // 关闭串口句柄
        this.closeHandle();

        return true;
    }

    /**
     * 发送数据
     *
     * @return
     */
    @Override
    public int sendData(byte[] data) {
        if (!this.isOpen()) {
            throw new RuntimeException("串口尚未打开：" + this.name);
        }

        // 发送数据
        IntByReference dwBytesWritten = new IntByReference();
        boolean bWriteStat = KERNEL.WriteFile(this.handle, data, data.length, dwBytesWritten, null);
        return dwBytesWritten.getValue();
    }

    public void ClearCommError() {
        if (!this.isOpen()) {
            throw new RuntimeException("串口尚未打开：" + this.name);
        }

        //   清空串口的出错标识
        Win32Macro.COMSTAT ComStat = new Win32Macro.COMSTAT();
        WinDef.DWORDByReference dwErrorFlags = new WinDef.DWORDByReference();
        if (!KERNELPLUS.ClearCommError(this.handle, dwErrorFlags, ComStat)) {
            throw new RuntimeException("ClearCommError异常：" + this.name);
        }
    }

    /**
     * 线程的sleep
     *
     * @param ms
     */
    private void sleep(long ms) {
        try {
            Thread.sleep(5);
        } catch (Exception e) {
        }
    }

    /**
     * 异步模式需要的单纯读数据
     *
     * @param readBuffer 缓存
     * @return 返回的数据
     */
    @Override
    public int readData(byte[] readBuffer) {
        if (!this.isOpen()) {
            throw new RuntimeException("串口尚未打开：" + this.name);
        }

        IntByReference lngBytesRead = new IntByReference();
        KERNEL.ReadFile(handle, readBuffer, readBuffer.length, lngBytesRead, null);
        return lngBytesRead.getValue();
    }

    /**
     * 读取串口数据
     *
     * @param recvBuffer      缓存
     * @param minPackInterval 两组数据报文之间，最小的时间间隔
     * @param maxPackInterval 两组数据报文之间，最大的时间间隔
     * @return 报文长度
     */
    @Override
    public int recvData(byte[] recvBuffer, long minPackInterval, long maxPackInterval) {
        if (!this.isOpen()) {
            throw new RuntimeException("串口尚未打开：" + this.name);
        }

        // 限制为有效范围
        if (minPackInterval < 10) {
            minPackInterval = 10;
        }
        if (minPackInterval > 1000) {
            minPackInterval = 1000;
        }


        long start = System.currentTimeMillis();
        int recvCount = 0;
        boolean hasRead = false;
        byte[] tempBuff = new byte[256];

        while (true) {
            // 单次读数据到临时数组
            IntByReference lngBytesRead = new IntByReference();
            KERNEL.ReadFile(handle, tempBuff, tempBuff.length, lngBytesRead, null);
            if (lngBytesRead.getValue() > 0) {
                // 检测：如果数据粘合的长度已经超过了可容纳的缓存，那么宁愿丢弃后续报文，也要确保安全而退出
                if (lngBytesRead.getValue() + recvCount > recvBuffer.length) {
                    return recvCount;
                }

                // 将数据复制到外部数组
                System.arraycopy(tempBuff, 0, recvBuffer, recvCount, Math.min(recvBuffer.length - recvCount, lngBytesRead.getValue()));

                // 记录：这次读取到了部分数据
                recvCount += lngBytesRead.getValue();
                // 记录：已经有部分数据到达
                hasRead = true;
            }

            if (hasRead) {// 此前有接收到数据

                if (lngBytesRead.getValue() > 0) {
                    // <1> 上次最近一次有数据，说明后续可能还有，那就继续读取
                    sleep(minPackInterval);
                    continue;
                } else {
                    // <2> 上次最近一次没数据，说明后续已经没有数据了，那就退出流程了
                    break;
                }
            } else {// 此前没有接收到数据

                // // <3> 在整个超时期间，都没有数据
                long spand = System.currentTimeMillis() - start;
                if (spand > maxPackInterval) {
                    break;
                }

                sleep(minPackInterval);
            }
        }

        return recvCount;
    }

    /**
     * @param recvBuffer 接收缓冲区
     * @param uTimeout   最大超时等待时间，单位微秒
     * @return 接收到数据
     */
    @Override
    public int recvData(byte[] recvBuffer, long uTimeout) {
        return recvData(recvBuffer, 10, uTimeout);
    }

    @Override
    public boolean clearRecvFlush() {
        if (!this.isOpen()) {
            return false;
        }
        return KERNELPLUS.PurgeComm(handle, new WinDef.DWORD(Win32Macro.PURGE_RXABORT | Win32Macro.PURGE_RXCLEAR)); //
    }

    @Override
    public boolean clearSendFlush() {
        if (!this.isOpen()) {
            return false;
        }

        return KERNELPLUS.PurgeComm(handle, new WinDef.DWORD(Win32Macro.PURGE_TXABORT | Win32Macro.PURGE_TXCLEAR)); //
    }
}
