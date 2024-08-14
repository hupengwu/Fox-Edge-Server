/* ----------------------------------------------------------------------------
 * Copyright (c) Guangzhou Fox-Tech Co., Ltd. 2020-2024. All rights reserved.
 * --------------------------------------------------------------------------- */

package cn.foxtech.common.utils.serialport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 异步执行器：用于全双工模式的数据收发
 */
public class AsyncExecutor {
    /**
     * 发送缓存
     */
    private final List<byte[]> sendList = new CopyOnWriteArrayList<>();
    /**
     * 接收缓存
     */
    private final List<byte[]> recvList = new CopyOnWriteArrayList<>();
    /**
     * 线程退出标记
     */
    private final AtomicInteger exit = new AtomicInteger(0);


    private ReadableNotify readableNotify;

    /**
     * 数据抵达时的外部通知接口
     * 实现多线程之间的wait和notify
     *
     * @param readableNotify 通知接口
     */
    public void setReadableNotify(ReadableNotify readableNotify) {
        this.readableNotify = readableNotify;
    }

    /**
     * 关闭线程池
     */
    public void closeExecutor() {
        // 发出退出标记：1
        this.exit.incrementAndGet();

        while (true) {
            try {
                // 等待退出标记：发出信号，两个线程结束信号，总共3个信号
                if (this.exit.get() >= 3) {
                    return;
                }
                Thread.sleep(10);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 创建线程池
     *
     * @param serialPort
     */
    public void createExecutor(ISerialPort serialPort) {
        this.createSendExecutor(serialPort);
        this.createRecvExecutor(serialPort);
    }

    /**
     * 异步发送数据
     *
     * @param data 等待发送的数据
     */
    public synchronized void waitWriteable(byte[] data) {
        synchronized (this.sendList) {
            this.sendList.add(data);
            this.sendList.notify();
        }
    }

    /**
     * 是否有接收数据到达
     *
     * @return
     */
    public synchronized boolean isReadable() {
        return !this.recvList.isEmpty();
    }

    /**
     * 异步接收数据的模式1：单线程模式
     * 单线程中，该单循环waitWriteable()，来读取缓存中的数据
     * 本异步线程在有数据到达的时候，会发出一个notify，来触发waitWriteable()往下一步执行
     *
     * @param timeout 等待超时
     * @return 接收到的一批数据
     */
    public synchronized List<byte[]> waitReadable(long timeout) {
        try {
            synchronized (this.recvList) {
                this.recvList.wait(timeout);

                List<byte[]> result = new ArrayList<>();
                if (this.recvList.isEmpty()) {
                    return result;
                }

                // 弹出一个或者全部数据
                result.addAll(this.recvList);
                this.recvList.clear();

                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    /**
     * 异步接收数据的模式2：多程模式
     * 多线程中，setReadableNotify(readableNotify)后，外部线程等待readableNotify的wait()消息，然后来读取缓存中的数据
     *
     * 本异步线程在有数据到达的时候，会发出一个notify，来触发readableNotify上的wait后，可以用readRecvList进行读取数据
     *
     * @return 接收到的数据
     */
    public synchronized List<byte[]> readRecvList() {
        List<byte[]> result = new ArrayList<>();
        if (this.recvList.isEmpty()) {
            return result;
        }

        // 弹出一个或者全部数据
        result.addAll(this.recvList);
        this.recvList.clear();

        return result;

    }

    private void createSendExecutor(ISerialPort serialPort) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (exit.get() > 0) {
                            exit.incrementAndGet();
                            return;
                        }

                        List<byte[]> sendList = getSendList(3 * 1000);
                        if (sendList.isEmpty()) {
                            continue;
                        }

                        for (byte[] send : sendList) {
                            serialPort.sendData(send);
                            Thread.sleep(10);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, TimeUnit.MILLISECONDS);
        scheduledExecutorService.shutdown();
    }

    private List<byte[]> getSendList(int timeout) throws InterruptedException {
        synchronized (this.sendList) {
            // 等待消息别的线程的notify
            this.sendList.wait(timeout);

            List<byte[]> result = new ArrayList<>();
            if (this.sendList.isEmpty()) {
                return result;
            }

            // 弹出一个或者全部数据
            result.addAll(this.sendList);
            this.sendList.clear();

            return result;
        }
    }

    private void createRecvExecutor(ISerialPort serialPort) {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] recv = new byte[4096];

                    while (true) {
                        if (exit.get() > 0) {
                            exit.incrementAndGet();
                            return;
                        }

                        Thread.sleep(100);

                        // 读取串口数据
                        int count = serialPort.readData(recv);
                        if (count <= 0) {
                            continue;
                        }

                        // 复制数据
                        byte[] data = new byte[count];
                        System.arraycopy(recv, 0, data, 0, count);

                        // 数据可读的消息通知：单线程模式的通知
                        synchronized (recvList) {
                            // 保存数据
                            recvList.add(data);

                            // 通知外部有数据到达
                            recvList.notify();
                        }

                        // 数据可读的消息通知：多线程模式的通知
                        if (readableNotify != null && !recvList.isEmpty()) {
                            readableNotify.notifyReadable();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, TimeUnit.MILLISECONDS);
        scheduledExecutorService.shutdown();
    }

    /**
     * 有数据到达时的外部通知接口
     */
    public interface ReadableNotify {
        void notifyReadable();
    }

}
