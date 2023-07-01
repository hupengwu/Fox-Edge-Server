package cn.foxtech.common.utils.serialport.win32;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.Wincon;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

/**
 * 声明即将调用的WIN32API：这些API格式的定义，查阅WIN32 C++的函数格式
 */
public interface Win32API extends StdCallLibrary, WinNT, Wincon {
    Win32API INSTANCE = (Win32API) Native.load("kernel32", Win32API.class, W32APIOptions.DEFAULT_OPTIONS);

    boolean
    SetupComm(
            HANDLE hFile,
            DWORD dwInQueue,
            DWORD dwOutQueue
    );

    boolean
    BuildCommDCB(
            String lpDef,
            DCB lpDCB
    );

    boolean
    SetCommMask(
            HANDLE hFile,
            DWORD dwEvtMask
    );

    boolean
    ClearCommError(
            HANDLE hFile,
            DWORDByReference lpErrors,
            Win32Macro.COMSTAT lpStat
    );


    boolean
    PurgeComm(
            HANDLE hFile,
            DWORD dwFlags
    );

}
