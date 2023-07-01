package cn.foxtech.common.utils.niosocket.multithread.thread;


import cn.foxtech.common.utils.niosocket.multithread.handler.ISocketL1Handler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * <3> 读数据线程：可以为整个socket服务端实例，实例化一个公共的写线程，来统一响应各个客户端的数据
 *
 * @author h00442047
 * @since 2019年12月3日
 */
public class WriteThread extends Thread {
    /**
     * 选择器
     */
    private Selector selector;

    /**
     * 外部接口
     */
    private ISocketL1Handler socketHandler;

    /**
     * 待注册写消息的Channel
     */
    private Map<SocketChannel, List<byte[]>> queue = new HashMap<SocketChannel, List<byte[]>>();

    /**
     * 构造函数
     *
     * @param socketHandler 外部响应接口的实现
     * @throws IOException 异常信息
     */
    public WriteThread(String name, ISocketL1Handler socketHandler) throws IOException {
        super.setName(name);

        this.socketHandler = socketHandler;
        this.selector = Selector.open();
    }

    /**
     * 提交异步发送的数据：线程安全
     *
     * @param channel 客户端对应的channel
     * @param data 待发送的数据
     */
    public synchronized void offerData(SocketChannel channel, byte[] data) {
        List<byte[]> list = this.queue.get(channel);
        if (list == null) {
            list = new LinkedList<byte[]>();
            this.queue.put(channel, list);
        }

        list.add(data);
    }

    /**
     * 从队列中取出待发送的数据：线程安全
     *
     * @return 待注册的channel和它的数据
     */
    private synchronized Map<SocketChannel, List<byte[]>> pollData() {
        Map<SocketChannel, List<byte[]>> result = new HashMap<SocketChannel, List<byte[]>>();
        result.putAll(this.queue);
        this.queue.clear();

        return result;
    }

    /**
     * 线程函数：根据发送数据请求，循环注册写消息；根据写消息，循环发送数据
     */
    public void run() {
        while (this.socketHandler.isRunning()) {
            try {
                // 注册:请求队列中的Channel
                this.registWriters();

                // select可以进入写的消息：100毫秒检查一下注册请求，对于可写的消息立即响应
                int count = selector.select(100);
                if (count == 0) {
                    continue;
                }

                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    if (key.isValid() && key.isWritable()) {
                        this.socketHandler.handleWrite(key);
                    }
                }
            } catch (IOException e) {
                e.toString();
            } catch (Exception e) {
                e.toString();
            }
        }
    }

    /**
     * 注册写消息：根据发送数据的需要，想channel注册OP_WRITE消息，同时将需要发送的数据追加到附件上，
     * 并发出OP_WRITE消息的通知，告知后续流程可以捕获OP_WRITE消息，然后真正发送附件上的数据
     */
    private void registWriters() {
        // 从客户端请求中，弹出请求数据
        Map<SocketChannel, List<byte[]>> sc2dat = this.pollData();

        for (Map.Entry<SocketChannel, List<byte[]>> entry : sc2dat.entrySet()) {
            SocketChannel socketChannel = entry.getKey();
            List<byte[]> datas = entry.getValue();

            // 检查：该channel是否注册过该selector
            SelectionKey key = socketChannel.keyFor(this.selector);

            try {
                if (key == null) {
                    // 如果不曾注册过，则channel注册selector，并标识对OP_WRITE感兴趣
                    try {
                        // 将数据追加到附件中（安全队列）
                        Queue<byte[]> att = new ConcurrentLinkedQueue<byte[]>();
                        att.addAll(datas);

                        key = socketChannel.register(this.selector, SelectionKey.OP_WRITE, att);
                    } catch (ClosedChannelException e) {
                        e.toString();
                    }
                } else {
                    // 将数据追加到附件中（安全队列）
                    Object attachment = key.attachment();
                    if (attachment != null && Queue.class.isInstance(attachment)) {
                        @SuppressWarnings("unchecked")
                        Queue<byte[]> att = Queue.class.cast(key.attachment());
                        att.addAll(datas);
                    }

                    // 如果已经注册过，则用注册的key设置为对OP_WRITE感兴趣
                    key.interestOps(SelectionKey.OP_WRITE);
                }

            } catch (CancelledKeyException e) {
                e.toString();
            }
        }
    }

    /**
     * 处理Write消息
     *
     * @param key SelectionKey
     * @throws IOException 异常
     */
    public static void writeChannel(SelectionKey key) {
        try {
            // 取出channel
            SocketChannel sc = (SocketChannel) key.channel();

            // 取出附件:缓存
            @SuppressWarnings("unchecked")
            Queue<byte[]> queue = (Queue<byte[]>) key.attachment();

            // 计算待发送的数据总量
            int sum = 0;
            int size = queue.size();
            Iterator<byte[]> it = queue.iterator();
            for (int i = 0; i < size; i++) {
                if (it.hasNext()) {
                    byte[] dat = it.next();
                    sum += dat.length;
                }
            }
            // 发送数据
            if (sum > 0) {
                ByteBuffer buff = ByteBuffer.allocate(sum);
                while (!queue.isEmpty()) {
                    buff.put(queue.poll());
                }
                buff.flip();

                sc.write(buff);

                buff.clear();
            }
        } catch (Exception e) {
            e.toString();
        }
    }
}
