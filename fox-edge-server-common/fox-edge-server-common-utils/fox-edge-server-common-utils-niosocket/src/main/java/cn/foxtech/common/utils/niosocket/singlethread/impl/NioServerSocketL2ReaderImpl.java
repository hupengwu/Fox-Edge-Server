package cn.foxtech.common.utils.niosocket.singlethread.impl;

import cn.foxtech.common.utils.niosocket.singlethread.handler.ISocketL2Reader;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;


public class NioServerSocketL2ReaderImpl implements ISocketL2Reader {
    /**
     * 读取数据
     *
     * @param key
     * @param buff
     */
    public void readData(SelectionKey key, ByteBuffer buff) {
        // 处理方法1：将数据写入到一个byte数组
        int len = buff.limit() - buff.position();
        byte[] dst = new byte[len];
        buff.get(dst);

        // 处理方法2：读取数据一个一个打印出来
        // while (buff.hasRemaining()) {
        // System.out.print((char) buff.get());
        // }
        // System.out.println();
    }

    /**
     * 关闭前的处理
     *
     * @param key
     */
    public void closeStart(SelectionKey key) {

    }

    /**
     * 关闭后的处理
     *
     * @param key
     */
    public void closeFinish(SelectionKey key) {

    }

    /**
     * 处理异常消息
     *
     * @param key
     * @param e
     */
    public void handleException(SelectionKey key, Exception e) {

    }
}
