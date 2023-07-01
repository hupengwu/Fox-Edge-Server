package cn.foxtech.common.utils.niosocket.multithread.demo;

import cn.foxtech.common.utils.niosocket.multithread.handler.level1.SocketL1Handler;
import cn.foxtech.common.utils.niosocket.multithread.handler.level2.SocketAccpetHandler;
import cn.foxtech.common.utils.niosocket.multithread.handler.level2.SocketReadHandler;
import cn.foxtech.common.utils.niosocket.multithread.handler.level2.SocketWriteHandler;
import cn.foxtech.common.utils.niosocket.multithread.handler.level2.SocketCloseHandler;
import cn.foxtech.common.utils.niosocket.multithread.socket.NioServerSocket;

import java.io.IOException;

public class DemoServer {
    public static void main(String[] args) throws IOException {
        NioServerSocket server = new NioServerSocket();
        server.setPort(10000);
        server.setReadThreadCount(10);

        SocketL1Handler handler = (SocketL1Handler) server.getSocketHandler();
        handler.setAccpetHandler(new SocketAccpetHandler());
        handler.setCloseHandler(new SocketCloseHandler());
        handler.setReadHandler(new SocketReadHandler());
        handler.setWriteHandler(new SocketWriteHandler());

        server.start();
    }
}
