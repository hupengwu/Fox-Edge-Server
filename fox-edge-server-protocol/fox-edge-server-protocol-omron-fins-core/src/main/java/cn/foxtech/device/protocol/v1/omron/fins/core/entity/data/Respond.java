/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.device.protocol.v1.omron.fins.core.entity.data;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter(value = AccessLevel.PUBLIC)
@Setter(value = AccessLevel.PUBLIC)
public class Respond {
    /**
     * 返回：2字节结束代码
     */
    private byte main = 0;
    private byte sub = 0;

    /**
     * 检查命令帧中的EndCode
     *
     * @param main 主码
     * @param sub  副码
     * @param info 错误信息
     * @return 指示程序是否可以继续进行
     */
    public static boolean checkEndCode(byte main, byte sub, StringBuilder info) {
        switch (main) {
            case 0x00:
                switch (sub) {
                    case 0x00:
                        return true;//the only situation of success
                    case 0x01:
                        info.append("service canceled");
                        return false;
                }
                break;
            case 0x01:
                switch (sub) {
                    case 0x01:
                        info.append("local node not in network");
                        return false;
                    case 0x02:
                        info.append("token timeout");
                        return false;
                    case 0x03:
                        info.append("retries failed");
                        return false;
                    case 0x04:
                        info.append("too many send frames");
                        return false;
                    case 0x05:
                        info.append("node address range error");
                        return false;
                    case 0x06:
                        info.append("node address duplication");
                        return false;
                }
                break;
            case 0x02:
                switch (sub) {
                    case 0x01:
                        info.append("destination node not in network");
                        return false;
                    case 0x02:
                        info.append("unit missing");
                        return false;
                    case 0x03:
                        info.append("third node missing");
                        return false;
                    case 0x04:
                        info.append("destination node busy");
                        return false;
                    case 0x05:
                        info.append("response timeout");
                        return false;
                }
                break;
            case 0x03:
                switch (sub) {
                    case 0x01:
                        info.append("communications controller error");
                        return false;
                    case 0x02:
                        info.append("CPU unit error");
                        return false;
                    case 0x03:
                        info.append("controller error");
                        return false;
                    case 0x04:
                        info.append("unit number error");
                        return false;
                }
                break;
            case 0x04:
                switch (sub) {
                    case 0x01:
                        info.append("undefined command");
                        return false;
                    case 0x02:
                        info.append("not supported by model/version");
                        return false;
                }
                break;
            case 0x05:
                switch (sub) {
                    case 0x01:
                        info.append("destination address setting error");
                        return false;
                    case 0x02:
                        info.append("no routing tables");
                        return false;
                    case 0x03:
                        info.append("routing table error");
                        return false;
                    case 0x04:
                        info.append("too many relays");
                        return false;
                }
                break;
            case 0x10:
                switch (sub) {
                    case 0x01:
                        info.append("command too long");
                        return false;
                    case 0x02:
                        info.append("command too short");
                        return false;
                    case 0x03:
                        info.append("elements/data don't match");
                        return false;
                    case 0x04:
                        info.append("command format error");
                        return false;
                    case 0x05:
                        info.append("header error");
                        return false;
                }
                break;
            case 0x11:
                switch (sub) {
                    case 0x01:
                        info.append("area classification missing");
                        return false;
                    case 0x02:
                        info.append("access size error");
                        return false;
                    case 0x03:
                        info.append("address range error");
                        return false;
                    case 0x04:
                        info.append("address range exceeded");
                        return false;
                    case 0x06:
                        info.append("program missing");
                        return false;
                    case 0x09:
                        info.append("relational error");
                        return false;
                    case 0x0a:
                        info.append("duplicate data access");
                        return false;
                    case 0x0b:
                        info.append("response too long");
                        return false;
                    case 0x0c:
                        info.append("parameter error");
                        return false;
                }
                break;
            case 0x20:
                switch (sub) {
                    case 0x02:
                        info.append("protected");
                        return false;
                    case 0x03:
                        info.append("table missing");
                        return false;
                    case 0x04:
                        info.append("data missing");
                        return false;
                    case 0x05:
                        info.append("program missing");
                        return false;
                    case 0x06:
                        info.append("file missing");
                        return false;
                    case 0x07:
                        info.append("data mismatch");
                        return false;
                }
                break;
            case 0x21:
                switch (sub) {
                    case 0x01:
                        info.append("read-only");
                        return false;
                    case 0x02:
                        info.append("protected , cannot write data link table");
                        return false;
                    case 0x03:
                        info.append("cannot register");
                        return false;
                    case 0x05:
                        info.append("program missing");
                        return false;
                    case 0x06:
                        info.append("file missing");
                        return false;
                    case 0x07:
                        info.append("file name already exists");
                        return false;
                    case 0x08:
                        info.append("cannot change");
                        return false;
                }
                break;
            case 0x22:
                switch (sub) {
                    case 0x01:
                        info.append("not possible during execution");
                        return false;
                    case 0x02:
                        info.append("not possible while running");
                        return false;
                    case 0x03:
                        info.append("wrong PLC mode");
                        return false;
                    case 0x04:
                        info.append("wrong PLC mode");
                        return false;
                    case 0x05:
                        info.append("wrong PLC mode");
                        return false;
                    case 0x06:
                        info.append("wrong PLC mode");
                        return false;
                    case 0x07:
                        info.append("specified node not polling node");
                        return false;
                    case 0x08:
                        info.append("step cannot be executed");
                        return false;
                }
                break;
            case 0x23:
                switch (sub) {
                    case 0x01:
                        info.append("file device missing");
                        return false;
                    case 0x02:
                        info.append("memory missing");
                        return false;
                    case 0x03:
                        info.append("clock missing");
                        return false;
                }
                break;
            case 0x24:
                switch (sub) {
                    case 0x01:
                        info.append("table missing");
                        return false;
                }
                break;
            case 0x25:
                switch (sub) {
                    case 0x02:
                        info.append("memory error");
                        return false;
                    case 0x03:
                        info.append("I/O setting error");
                        return false;
                    case 0x04:
                        info.append("too many I/O points");
                        return false;
                    case 0x05:
                        info.append("CPU bus error");
                        return false;
                    case 0x06:
                        info.append("I/O duplication");
                        return false;
                    case 0x07:
                        info.append("CPU bus error");
                        return false;
                    case 0x09:
                        info.append("SYSMAC BUS/2 error");
                        return false;
                    case 0x0a:
                        info.append("CPU bus unit error");
                        return false;
                    case 0x0d:
                        info.append("SYSMAC BUS No. duplication");
                        return false;
                    case 0x0f:
                        info.append("memory error");
                        return false;
                    case 0x10:
                        info.append("SYSMAC BUS terminator missing");
                        return false;
                }
                break;
            case 0x26:
                switch (sub) {
                    case 0x01:
                        info.append("no protection");
                        return false;
                    case 0x02:
                        info.append("incorrect password");
                        return false;
                    case 0x04:
                        info.append("protected");
                        return false;
                    case 0x05:
                        info.append("service already executing");
                        return false;
                    case 0x06:
                        info.append("service stopped");
                        return false;
                    case 0x07:
                        info.append("no execution right");
                        return false;
                    case 0x08:
                        info.append("settings required before execution");
                        return false;
                    case 0x09:
                        info.append("necessary items not set");
                        return false;
                    case 0x0a:
                        info.append("number already defined");
                        return false;
                    case 0x0b:
                        info.append("error will not clear");
                        return false;
                }
                break;
            case 0x30:
                switch (sub) {
                    case 0x01:
                        info.append("no access right");
                        return false;
                }
                break;
            case 0x40:
                switch (sub) {
                    case 0x01:
                        info.append("service aborted");
                        return false;
                }
                break;
        }
        info.append("unknown exception");
        return false;
    }

}
