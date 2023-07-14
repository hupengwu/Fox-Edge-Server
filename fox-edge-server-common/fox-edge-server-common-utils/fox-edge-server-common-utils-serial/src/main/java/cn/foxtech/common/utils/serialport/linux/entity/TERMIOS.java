package cn.foxtech.common.utils.serialport.linux.entity;

import com.sun.jna.Structure;


@Structure.FieldOrder({"c_iflag", "c_oflag", "c_cflag", "c_lflag", "c_line", "c_cc", "c_ispeed", "c_ospeed"})
public class TERMIOS extends Structure {
    public int c_iflag = 0;        /* input mode flags */
    public int c_oflag = 0;        /* output mode flags */
    public int c_cflag = 0;        /* control mode flags */
    public int c_lflag = 0;        /* local mode flags */
    public byte c_line = 0;            /* line discipline */
    public byte[] c_cc = new byte[32];        /* control characters */
    public int c_ispeed = 0;        /* input speed */
    public int c_ospeed = 0;        /* output speed */

    public TERMIOS() {
    }
}
