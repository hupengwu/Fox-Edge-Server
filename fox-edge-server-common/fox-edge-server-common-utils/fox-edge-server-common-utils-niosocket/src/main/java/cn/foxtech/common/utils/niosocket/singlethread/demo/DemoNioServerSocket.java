package cn.foxtech.common.utils.niosocket.singlethread.demo;

import cn.foxtech.common.utils.niosocket.singlethread.socket.NioServerSocket;

public class DemoNioServerSocket {
    public static void main(String[] args) {

        NioServerSocket nioServerSocket = new NioServerSocket();
        nioServerSocket.selector();
    }
}
