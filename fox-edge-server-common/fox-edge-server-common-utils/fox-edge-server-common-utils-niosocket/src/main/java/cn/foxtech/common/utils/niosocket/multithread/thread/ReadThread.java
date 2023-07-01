package cn.foxtech.common.utils.niosocket.multithread.thread;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Vector;

import cn.foxtech.common.utils.niosocket.multithread.handler.ISocketL1Handler;

/**
 * <2> 读数据线程：可以为每一个客户端接入，实例化一个读取数据程
 *
 * @author h00442047
 * @since 2019年12月3日
 */
public class ReadThread extends Thread {
    /**
     * 选择器
     */
    private Selector selector;

    /**
     * 外部接口
     */
    private ISocketL1Handler socketHandler;

    /**
     * 当前处理线程处理key：Vector线程安全
     */
    private Vector<SelectionKey> keys = new Vector<SelectionKey>();

    /**
     * 是否初始化对象
     */
    private boolean initing = true;

    /**
     * 构造函数
     *
     * @param socketHandler 外部响应接口的实现
     * @param id 线程的ID号
     * @throws IOException 异常信息
     */
    public ReadThread(String name, ISocketL1Handler socketHandler) throws IOException {
        super.setName(name);

        this.socketHandler = socketHandler;
        this.selector = Selector.open();
    }

    /**
     * 客户端Key
     *
     * @return 客户端Key
     */
    public Vector<SelectionKey> getKeys() {
        return this.keys;
    }

    /**
     * 重置线程回初始化状态
     */
    public void restThread(SelectionKey key) {
        this.keys.remove(key);
        this.initing = true;
    }

    @Override
    public void run() {
        while (this.socketHandler.isRunning()) {
            try {
                // 等待线程状态就绪
                this.selector.select(100);
                while (this.initing) {
                    synchronized (this) {
                        this.wait(100);
                    }
                }

                // 初始化操作完成后，开始遍历消息
                Iterator<SelectionKey> it = this.selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    if (key.isValid() && key.isReadable()) {
                        this.socketHandler.handleRead(key);
                    }
                }
            } catch (IOException e) {
                e.toString();
            } catch (InterruptedException e) {
                e.toString();
            } catch (Exception e) {
                e.toString();
            }
        }
    }

    /**
     * 注册监听读消息，并保存读取该客户端Channel消息的key
     *
     * @param channel 通道
     * @param att 附件内容
     * @return 注册生成的key
     * @throws IOException 异常
     */
    public SelectionKey registerChannel(SocketChannel channel, Object att) throws IOException {
        SelectionKey key = channel.register(selector, SelectionKey.OP_READ, att);
        this.keys.add(key);
        return key;
    }

    /**
     * 设置为初始化阶段
     */
    public void startInit() {
        this.initing = true;
        this.selector.wakeup();
    }

    /**
     * 设置为结束初始化阶段
     */
    public synchronized void finishInit() {
        this.initing = false;
        this.notifyAll();
    }
}
