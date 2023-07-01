package cn.foxtech.common.utils.niosocket.singlethread.impl;

import cn.foxtech.common.utils.niosocket.singlethread.handler.ISocketL2Handler;

import java.nio.channels.SelectionKey;



public class NioServerSocketL2HandlerImpl implements ISocketL2Handler {
    /**
     * 处理进入消息
     * @param key key
     */
    public void handleStart(SelectionKey key) {

    }

    /**
     * 处理退出消息
     * @param key key
     */
    public void handleFinish(SelectionKey key) {

    }

    /**
     * 处理异常消息
     * @param key key
     */
    public void handleException(SelectionKey key, Exception e) {

    }
}
