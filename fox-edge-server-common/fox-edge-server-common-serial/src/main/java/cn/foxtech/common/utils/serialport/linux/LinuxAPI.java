package cn.foxtech.common.utils.serialport.linux;

import cn.foxtech.common.utils.serialport.linux.entity.FD_SET;
import cn.foxtech.common.utils.serialport.linux.entity.TERMIOS;
import cn.foxtech.common.utils.serialport.linux.entity.TIMEVAL;
import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * LINUX的C语言系统函数
 * 通过JNA声明LINUX系统API，使得可以直接调用LINUX操作系统API
 */
public interface LinuxAPI extends com.sun.jna.platform.unix.LibCAPI, Library {
    String NAME = "c";
    LinuxAPI INSTANCE = (LinuxAPI) Native.load("c", LinuxAPI.class);

    int open(
            String path,
            int oflag
    );

    int close(
            int fd
    );

    int tcgetattr(
            int fd,
            TERMIOS termios
    );

    void bzero(
            TERMIOS termios,
            int size
    );

    void cfmakeraw(
            TERMIOS termios
    );

    int tcflush(
            int fd,
            int queue_selector
    );

    int tcsetattr(
            int fd,
            int optional_actions,
            TERMIOS termios
    );

    int write(
            int fd,
            byte[] buf,
            int size
    );

    int select(
            int __nfds,
            FD_SET readfds,
            FD_SET writefds,
            FD_SET exceptfds,
            TIMEVAL timeout
    );

    int
    read(
            int fd,
            byte[] buf,
            long nbytes
    );

}

