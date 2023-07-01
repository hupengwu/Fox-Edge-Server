package cn.foxtech.common.utils.serialport.linux;

import cn.foxtech.common.utils.serialport.linux.entity.FD_SET;

/**
 *LINUX的宏定义
 */
public class LinuxMacro {
    public static final int B300 = 0000007;
    public static final int B600 = 0000010;
    public static final int B1200 = 0000011;
    public static final int B1800 = 0000012;
    public static final int B2400 = 0000013;
    public static final int B4800 = 0000014;
    public static final int B9600 = 0000015;
    public static final int B19200 = 0000016;
    public static final int B38400 = 0000017;
    public static final int CSIZE = 0000060;
    public static final int CS5 = 0000000;
    public static final int CS6 = 0000020;
    public static final int CS7 = 0000040;
    public static final int CS8 = 0000060;
    public static final int CSTOPB = 0000100;
    public static final int CREAD = 0000200;
    public static final int PARENB = 0000400;
    public static final int PARODD = 0001000;
    public static final int CLOCAL = 0004000;
    public static final int INPCK = 0000020;
    public static final int VTIME = 5;
    public static final int VMIN = 6;
    public static final int TCIFLUSH = 0;
    public static final int TCOFLUSH = 1;
    public static final int TCSANOW = 0;
    public static final int TCSADRAIN = 1;

    /**
     * linux中的long int的长度
     */
    private static int __fd_mask = 8;
    private static int __NFDBITS = 8 * __fd_mask;

    private static long __FD_MASK(long d) {
        return ((long) (1 << ((d) % __NFDBITS)));
    }

    private static long __FD_ELT(long d) {
        return ((d) / __NFDBITS);
    }

    private static long[] __FDS_BITS(FD_SET set) {
        return ((set).fds_bits);
    }

    public static boolean FD_ISSET(int d, FD_SET set) {
        long[] fds_bits = __FDS_BITS(set);
        int pos = (int) __FD_ELT(d);
        long bits = fds_bits[pos];
        long mask = __FD_MASK(d);
        return (bits & mask) != 0L;
    }

    public static void FD_SET(int d, FD_SET set) {
        long[] fds_bits = __FDS_BITS(set);
        int pos = (int) __FD_ELT(d);
        long mask = __FD_MASK(d);

        fds_bits[pos] |= mask;
    }

    public static void FD_ZERO(FD_SET set) {
        for (int i=0; i<set.fds_bits.length; i++){
            set.fds_bits[i] = 0;
        }
    }
}
