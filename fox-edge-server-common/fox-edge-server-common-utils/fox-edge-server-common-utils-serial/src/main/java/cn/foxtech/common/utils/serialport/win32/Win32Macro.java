/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.serialport.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

public interface Win32Macro extends WinDef, BaseTSD {
    WinNT.HANDLE INVALID_HANDLE_VALUE = new WinNT.HANDLE(Pointer.createConstant(Native.POINTER_SIZE == 8 ? -1L : 4294967295L));
    long PURGE_TXABORT = 0x0001;  // Kill the pending/current writes to the comm port.
    long PURGE_RXABORT = 0x0002;  // Kill the pending/current reads to the comm port.
    long PURGE_TXCLEAR = 0x0004;  // Kill the transmit queue if there.
    long PURGE_RXCLEAR = 0x0008; // Kill the typeahead buffer if there.

    long EV_RXCHAR = 0x0001;  // Any Character received
    long EV_RXFLAG = 0x0002;  // Received certain character
    long EV_TXEMPTY = 0x0004;  // Transmitt Queue Empty
    long EV_CTS = 0x0008;  // CTS changed state
    long EV_DSR = 0x0010;  // DSR changed state
    long EV_RLSD = 0x0020;  // RLSD changed state
    long EV_BREAK = 0x0040;  // BREAK received
    long EV_ERR = 0x0080;  // Line status error occurred
    long EV_RING = 0x0100;  // Ring signal detected
    long EV_PERR = 0x0200;  // Printer error occured
    long EV_RX80FULL = 0x0400;  // Receive buffer is 80 percent full
    long EV_EVENT1 = 0x0800;  // Provider specific event 1
    long EV_EVENT2 = 0x1000;  // Provider specific event 2

    @Structure.FieldOrder({"fCtsHold", "fDsrHold", "fRlsdHold", "fXoffHold", "fXoffSent", "fEof", "fTxim", "fReserved", "cbInQue", "cbOutQue"})
    public static class COMSTAT extends Structure {
        public DWORD fCtsHold = new DWORD(1);
        public DWORD fDsrHold = new DWORD(1);
        public DWORD fRlsdHold = new DWORD(1);
        public DWORD fXoffHold = new DWORD(1);
        public DWORD fXoffSent = new DWORD(1);
        public DWORD fEof = new DWORD(1);
        public DWORD fTxim = new DWORD(1);
        public DWORD fReserved = new DWORD(25);
        public DWORD cbInQue;
        public DWORD cbOutQue;

        public COMSTAT() {
        }
    }
}
