package cn.foxtech.common.utils.serialport.linux;

import cn.foxtech.common.utils.serialport.ISerialPort;
import cn.foxtech.common.utils.serialport.linux.entity.FD_SET;
import cn.foxtech.common.utils.serialport.linux.entity.TERMIOS;
import cn.foxtech.common.utils.serialport.linux.entity.TIMEVAL;

/**
 * LINUX串口对象的封装类
 */
public class SerialPortLinux implements ISerialPort {
    private static final LinuxAPI API = LinuxAPI.INSTANCE;
    /**
     * 新的串口设备选项
     */
    private final TERMIOS ntm = new TERMIOS();
    /**
     * 旧的串口设备选项
     */
    private final TERMIOS otm = new TERMIOS();
    /**
     * 串口设备名称，例："/dev/ttyS0"
     */
    private String name = "/dev/ttyS0";
    /**
     * 串口设备ID
     */
    private int fd = -1;

    @Override
    public String getName(){
        return this.name;
    }
    /**
     * 串口是否已经打开
     *
     * @return
     */
    @Override
    public boolean isOpen() {
        return this.fd > 0;
    }

    /**
     * 打开串口：linux的串口是ttyS0、ttyS1格式的参数
     *
     * @param name 串口1：ttyS0，串口2：ttyS1
     * @return 是否打开成功
     */
    @Override
    public boolean open(String name) {
        // 生成串口设备的名称
        this.name = "/dev/" + name;

        // 打开参数
        int oflag = 0;
        int O_RDWR = 02;
        int O_NOCTTY = 0400;
        int O_NONBLOCK = 04000;

        // 打开并且设置串口
        oflag |= O_RDWR;// 读、写打开
        oflag |= O_NOCTTY;// 不将此设备分配作为此进程的控制终端
        oflag |= O_NONBLOCK;// 设置非阻塞方式
        this.fd = API.open(this.name, oflag);
        if (this.fd < 0) {
            return false;
        }

        // 获取串口的参数
        int rtn = API.tcgetattr(this.fd, this.otm);
        if (rtn != 0) {
            API.close(this.fd);
            this.fd = -1;
            return false;
        }

        return true;
    }


    /**
     * 设置串口参数
     *
     * @param baudRate     速率
     * @param databits     数据位
     * @param stopbits     停止位
     * @param parity       校验位
     * @param commTimeOuts commTimeOuts的字节时间间隔
     * @return 是否成功
     */
    @Override
    public boolean setParam(Integer baudRate, String parity, Integer databits, Integer stopbits, Integer commTimeOuts) {
        // 参数转换为大写
        if (parity == null) {
            return false;
        }
        parity = parity.toUpperCase();

        // 清空tm数据
        API.bzero(this.ntm, this.ntm.size());

        // 就是将终端设置为原始模式
        API.cfmakeraw(this.ntm);


        // <1> 设置波特率
        switch (baudRate) {
            case 300:
                this.ntm.c_cflag |= LinuxMacro.B300;
                break;
            case 600:
                this.ntm.c_cflag |= LinuxMacro.B600;
                break;
            case 1200:
                this.ntm.c_cflag |= LinuxMacro.B1200;
                break;
            case 1800:
                this.ntm.c_cflag |= LinuxMacro.B1800;
                break;
            case 2400:
                this.ntm.c_cflag |= LinuxMacro.B2400;
                break;
            case 4800:
                this.ntm.c_cflag |= LinuxMacro.B4800;
                break;
            case 9600:
                this.ntm.c_cflag |= LinuxMacro.B9600;
                break;
            case 19200:
                this.ntm.c_cflag |= LinuxMacro.B19200;
                break;
            case 38400:
                this.ntm.c_cflag |= LinuxMacro.B38400;
                break;
            default:
                this.ntm.c_cflag |= LinuxMacro.B9600;
                break;
        }
        this.ntm.c_cflag = baudRate;
        this.ntm.c_cflag |= LinuxMacro.CLOCAL | LinuxMacro.CREAD;

        // <2> 设置数据位
        this.ntm.c_cflag &= ~LinuxMacro.CSIZE;
        switch (databits) {
            case 5:
                this.ntm.c_cflag |= LinuxMacro.CS5;
                break;
            case 6:
                this.ntm.c_cflag |= LinuxMacro.CS6;
                break;
            case 7:
                this.ntm.c_cflag |= LinuxMacro.CS7;
                break;
            case 8:
                this.ntm.c_cflag |= LinuxMacro.CS8;
                break;
            default:
                this.ntm.c_cflag |= LinuxMacro.CS8;
                break;
        }

        // <3> 设置奇偶校验位数
        if ("N".equals(parity)) {
            this.ntm.c_cflag &= ~LinuxMacro.PARENB; /* Clear parity enable */
            this.ntm.c_iflag &= ~LinuxMacro.INPCK; /* Enable parity checking */
        } else if ("O".equals(parity)) {
            this.ntm.c_cflag |= (LinuxMacro.PARODD | LinuxMacro.PARENB); /* 设置为奇效验*/
            this.ntm.c_iflag |= LinuxMacro.INPCK; /* Disnable parity checking */
        } else if ("E".equals(parity)) {
            this.ntm.c_cflag |= LinuxMacro.PARENB; /* Enable parity */
            this.ntm.c_cflag &= ~LinuxMacro.PARODD; /* 转换为偶效验*/
            this.ntm.c_iflag |= LinuxMacro.INPCK; /* Disnable parity checking */
        } else if ("S".equals(parity)) {
            this.ntm.c_cflag &= ~LinuxMacro.PARENB;
            this.ntm.c_cflag &= ~LinuxMacro.CSTOPB;
        } else {
            this.ntm.c_cflag &= ~LinuxMacro.PARENB; /* Clear parity enable */
            this.ntm.c_iflag &= ~LinuxMacro.INPCK; /* Enable parity checking */
        }

        // <4> 设置停止位
        switch (stopbits) {
            case 1:
                this.ntm.c_cflag &= ~LinuxMacro.CSTOPB;// 1位停止
                break;
            case 2:
                this.ntm.c_cflag |= LinuxMacro.CSTOPB;// 2位停止位
                break;
            default:
                this.ntm.c_cflag &= ~LinuxMacro.CSTOPB;
                break;
        }

        // 设置控制字符
        this.ntm.c_cc[LinuxMacro.VTIME] = 1;  // 读取字符的最小数量
        this.ntm.c_cc[LinuxMacro.VMIN] = 1;   // 读取第一个字符的等待时间

        // 清除输入缓存
        int rtn = 0;
        rtn = API.tcflush(this.fd, LinuxMacro.TCIFLUSH);
        if (rtn != 0) {
            return false;
        }
        // 清除输出缓存
        rtn = API.tcflush(this.fd, LinuxMacro.TCOFLUSH);
        if (rtn != 0) {
            return false;
        }

        // 将属性设置入串口
        rtn = API.tcsetattr(this.fd, LinuxMacro.TCSANOW, this.ntm);
        return rtn == 0;
    }

    /**
     * 发送数据
     *
     * @param data 缓冲区
     * @return
     */
    @Override
    public int sendData(byte[] data) {
        if (this.fd < 0) {
            throw new RuntimeException("串口尚未打开：" + this.name);
        }

        // 不停的发送数据，直到发送完为止
        int dataLen = data.length;
        byte[] sendData = data;
        int postion = 0;
        while (postion < dataLen) {
            int len = API.write(this.fd, sendData, dataLen - postion);
            if (len > 0) {
                postion += len;

                // 复制尚未发完的后半截数据，到新的数组，准备重新发送
                int newLength = dataLen - postion;
                byte[] copy = new byte[newLength];
                System.arraycopy(sendData, postion, copy, 0, Math.min(sendData.length, newLength));
                sendData = copy;

                dataLen = newLength;
            } else if (len <= 0) {
                break;
            }
        }

        // 输出发送的数据量
        return postion;
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
     * 读取串口数据
     *
     * @param recvBuffer      缓存
     * @param minPackInterval 两组数据报文之间，最小的时间间隔
     * @param maxPackInterval 两组数据报文之间，最大的时间间隔
     * @return 报文长度
     */
    @Override
    public int recvData(byte[] recvBuffer, long minPackInterval, long maxPackInterval) {
        if (this.fd < 0) {
            throw new RuntimeException("串口尚未打开：" + this.name);
        }


        // 设置select串口需要的fd_set
        FD_SET readset = new FD_SET();
        LinuxMacro.FD_ZERO(readset);
        LinuxMacro.FD_SET(this.fd, readset);


        // 指明select的最大等待时间1000微秒
        TIMEVAL tv = new TIMEVAL();
        tv.tv_sec = maxPackInterval / 1000;
        tv.tv_usec = maxPackInterval % 1000;

        // select：readset中是否有描述符被改变
        int maxfd = this.fd + 1;
        if (API.select(maxfd, readset, null, null, tv) < 0) {
            throw new RuntimeException("串口select异常：" + this.name);
        }

        // 等待一些时间，确保数据完全抵达
        this.sleep(minPackInterval);

        // 检查返回结果
        if (LinuxMacro.FD_ISSET(this.fd, readset)) {
            int recv = API.read(this.fd, recvBuffer, recvBuffer.length);
            return recv;
        }

        return 0;
    }

    /**
     * 接收数据
     *
     * @param data     准备发送的数据库
     * @param mTimeout 最大超时等待时间，单位毫秒
     * @return 接收到数据
     */
    @Override
    public int recvData(byte[] data, long mTimeout) {
        return recvData(data, 10, mTimeout);
    }

    /**
     * 清空缓冲区
     *
     * @return
     */
    @Override
    public boolean clearRecvFlush() {
        if (this.fd < 0) {
            return false;
        }

        // 清除输入缓存
        int rtn = API.tcflush(this.fd, LinuxMacro.TCIFLUSH);
        return rtn == 0;
    }


    /**
     * 清空缓冲区
     *
     * @return
     */
    @Override
    public boolean clearSendFlush() {
        if (this.fd < 0) {
            return false;
        }

        // 清除输出缓存
        int rtn = API.tcflush(this.fd, LinuxMacro.TCOFLUSH);
        return rtn == 0;
    }

    /**
     * 关闭串口
     */
    @Override
    public boolean close() {
        if (this.fd <= 0) {
            return false;
        }

        // 恢复串口参数
        API.tcsetattr(this.fd, LinuxMacro.TCSADRAIN, this.otm);

        // 关闭串口
        API.close(this.fd);
        this.fd = -1;

        return true;
    }
}
