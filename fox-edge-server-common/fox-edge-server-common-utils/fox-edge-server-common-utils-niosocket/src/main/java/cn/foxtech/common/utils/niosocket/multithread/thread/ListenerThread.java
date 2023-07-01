package cn.foxtech.common.utils.niosocket.multithread.thread;


import cn.foxtech.common.utils.niosocket.multithread.handler.ISocketL1Handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

/**
 * <1> 监听线程：整个socket服务端由这个监听线程入口，它在接到接入消息时候，会对外发出接入的通知。
 * handle在接收到通知后，会生成该接入对应的客户端channel，并指派一个负荷较轻的读线程 来处理该channel的读数据
 * 背景知识：
 * ServerSocketChannel：服务端的Channel
 * SocketChannel：客户端的Channel，可在客户端接入后，被服务端channel accpet后得到
 * Selector：用来查询是否有外部消息到来的对象，接收消息前先需要注册一下需要监听的消息，当有消息过来的时候，会产生一个key
 * SelectionKey：对应某个channel上监听的消息，它内部有SocketChannel成员，可以知道是哪个SocketChannel
 * 业务流程：
 * 1.用一个Selector向serverChannel订阅OP_ACCEPT，等待客户端的接入
 * 2.从serverChannel Accpet客户端SocketChannel，获得客户端的接入
 * 3.用一个Selector向客户端SocketChannel订阅OP_READ，等待接收数据
 * 4.Selector响应处理消息，进行接收数据的处理
 * 5.上层的应用线程，需要返回数据，则在客户端SocketChannel上注册OP_WRITE消息
 * 6.上层的应用线程，则循环进行“写入SocketChannel数据，响应OP_WRITE消息”，全部处理完成后，interestOps，close
 *
 * @author h00442047
 * @since 2019年12月4日
 */
public class ListenerThread extends Thread {
    /**
     * 选择器
     */
    private Selector selector;

    /**
     * 服务通道
     */
    private ServerSocketChannel serverChannel;

    /**
     * 外部接口
     */
    private ISocketL1Handler socketHandler;

    /**
     * 查询消息间隔
     */
    private int timeOut = 1 * 1000;

    /**
     * 构造函数
     *
     * @param socketHandler 处理消息的handler
     * @param port 服务端口
     * @throws IOException 异常信息
     */
    public ListenerThread(String name, ISocketL1Handler socketHandler, int port) throws IOException {
        super.setName(name);

        // 绑定handler
        this.socketHandler = socketHandler;
        // 打开一个ServerSocketChannel
        this.serverChannel = ServerSocketChannel.open();
        // 标识为非阻塞模式
        this.serverChannel.configureBlocking(false);
        // 绑定接口地址
        this.serverChannel.socket().bind(new InetSocketAddress(port), 150);
        // 打开选择器
        this.selector = Selector.open();
        // 注册监听OP_ACCEPT消息
        this.serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    /**
     * 线程函数：遍历监听到的消息
     */
    public void run() {
        while (this.socketHandler.isRunning()) {
            try {
                // 检查:是否有消息过来
                if (this.selector.select(this.timeOut) == 0) {
                    continue;
                }

                // 遍历消息SelectionKey
                Iterator<SelectionKey> it = this.selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    // 1.Accept消息
                    if (key.isValid() && key.isAcceptable()) {
                        this.socketHandler.handleAccept(key);
                    }
                }
            } catch (IOException e) {
                e.toString();
            } catch (Exception e) {
                e.toString();
            }
        }
    }
}
