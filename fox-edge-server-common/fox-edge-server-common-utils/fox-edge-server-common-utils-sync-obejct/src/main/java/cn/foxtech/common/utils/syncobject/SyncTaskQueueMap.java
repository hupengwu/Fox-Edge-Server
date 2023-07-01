package cn.foxtech.common.utils.syncobject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 异步任务队列
 */
public class SyncTaskQueueMap {
    /**
     * 实例化对象
     */
    private static final SyncTaskQueueMap inst = new SyncTaskQueueMap();

    /**
     * list容器
     */
    private final Map<String, Map<Object, SyncTask>> map = new ConcurrentHashMap<>();

    /**
     * 进程空间内的唯一实例对象
     *
     * @return 进程空间类的唯一实例
     */
    public static SyncTaskQueueMap inst() {
        return inst;
    }

    /**
     * 创建任务
     * @param taskKey
     * @param taskRequest
     */
    public void createTask(Object taskKey, Object taskRequest) {
        this.createTask( taskKey, taskRequest, 1000);
    }

    public void createTask(Object taskKey, Object taskRequest, int max) {
        this.createTask("", taskKey, taskRequest, max);
    }

    public boolean createTask(String name, Object taskKey, Object taskRequest, int max) {
        if (taskKey == null || taskRequest == null) {
            return false;
        }

        Map<Object, SyncTask> queue = getQueue(name);

        synchronized (queue) {
            if (queue.size() < max) {
                SyncTask syncTask = new SyncTask();
                syncTask.taskKey = taskKey;
                syncTask.taskStatus = SyncStatus.WAITING;
                syncTask.taskRequest = taskRequest;
                syncTask.taskRespond = null;
                syncTask.taskTime = System.currentTimeMillis();
                queue.put(taskKey, syncTask);

                // 发出notify
                queue.notify();
                return true;
            }
        }

        return false;
    }

    private synchronized Map<Object, SyncTask> getQueue(String name) {
        Map<Object, SyncTask> queue = this.map.get(name);
        if (queue == null) {
            queue = new ConcurrentHashMap<>();
            this.map.put(name, queue);
        }

        return queue;
    }

    public Map<Object, Object> processTask() throws InterruptedException {
        return this.processTask("");
    }

    public Map<Object, Object> processTask(String name) throws InterruptedException {
        return this.processTask(name, false);
    }

    public Map<Object, Object> processTask(String name, boolean one) throws InterruptedException {
        return this.processTask(name, one, 100);
    }

    public Map<Object, Object> processTask(String name, boolean one, int timeout) throws InterruptedException {
        Map<Object, SyncTask> queue = getQueue(name);

        synchronized (queue) {
            // 等待消息别的线程的notify
            queue.wait(timeout);

            Map<Object, Object> result = new ConcurrentHashMap<>();
            if (queue.isEmpty()) {
                return result;
            }

            // 弹出一个或者全部数据
            if (one) {
                for (Map.Entry<Object, SyncTask> entry : queue.entrySet()) {
                    SyncTask syncTask = entry.getValue();
                    if (syncTask.taskStatus != SyncStatus.WAITING) {
                        continue;
                    }

                    syncTask.taskStatus = SyncStatus.PROCESS;
                    syncTask.taskTime = System.currentTimeMillis();
                    result.put(syncTask.taskKey, syncTask.taskRequest);
                    return result;
                }
            } else {
                for (Map.Entry<Object, SyncTask> entry : queue.entrySet()) {
                    SyncTask syncTask = entry.getValue();
                    if (syncTask.taskStatus != SyncStatus.WAITING) {
                        continue;
                    }

                    syncTask.taskStatus = SyncStatus.PROCESS;
                    result.put(syncTask.taskKey, syncTask.taskRequest);
                }
                return result;
            }

            return result;
        }
    }

    public boolean completeTask(String name, Object taskKey, Object taskRespond) {
        if (taskKey == null) {
            return false;
        }

        Map<Object, SyncTask> queue = getQueue(name);

        synchronized (queue) {
            SyncTask syncTask = queue.get(taskKey);
            if (syncTask == null) {
                return false;
            }

            syncTask.taskKey = taskKey;
            syncTask.taskStatus = SyncStatus.COMPLETE;
            syncTask.taskRespond = taskRespond;
            syncTask.taskTime = System.currentTimeMillis();

            // 发出notify
            queue.notify();
            return true;
        }
    }

    public Map<Object, SyncTask> waitingTask(String name, Set<Object> taskKeys, int timeout) throws InterruptedException {
        Map<Object, SyncTask> queue = getQueue(name);

        synchronized (queue) {
            // 等待消息别的线程的notify
            queue.wait(timeout);

            Map<Object, SyncTask> result = new ConcurrentHashMap<>();
            if (queue.isEmpty()) {
                return result;
            }

            for (Object taskKey : taskKeys) {
                SyncTask syncTask = queue.get(taskKey);
                if (syncTask == null) {
                    continue;
                }

                SyncTask clone = new SyncTask();
                clone.taskStatus = syncTask.taskStatus;
                clone.taskRespond = syncTask.taskRespond;
                clone.taskRequest = syncTask.taskRequest;
                clone.taskKey = syncTask.taskKey;

                result.put(clone.taskKey, clone);

                // 从队列中删除已经完成的任务
                if (syncTask.taskStatus == SyncStatus.COMPLETE) {
                    queue.remove(taskKey);
                }
            }

            return result;
        }
    }

    public void overdueTask(String name, int timeout) throws InterruptedException {
        Map<Object, SyncTask> queue = getQueue(name);

        synchronized (queue) {
            // 等待消息别的线程的notify
            queue.wait(timeout);

            if (queue.isEmpty()) {
                return;
            }


            // 检查过期的任务
            Set<Object> keys = new HashSet<>();
            long overdueTime = System.currentTimeMillis() - 60 * 1000;
            for (Object taskKey : queue.keySet()) {
                SyncTask syncTask = queue.get(taskKey);
                if (syncTask == null) {
                    continue;
                }

                // 任务完成，但是长期不来取任务
                if (syncTask.taskStatus == SyncStatus.COMPLETE && overdueTime > syncTask.taskTime) {
                    keys.add(taskKey);
                }
            }

            // 删除这些过期任务
            for (Object taskKey : keys) {
                queue.remove(taskKey);
            }
        }
    }


    private static class SyncTask {
        private Object taskKey;
        private int taskStatus;
        private Object taskRequest;
        private Object taskRespond;
        private Long taskTime;
    }

    private static class SyncStatus {
        public static final int WAITING = 0;
        public static final int PROCESS = 1;
        public static final int COMPLETE = 2;
    }
}
