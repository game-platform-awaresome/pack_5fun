package com.wufan.actor;

import com.wufan.handle.PackTaskCallable;
import com.wufan.handle.PackTaskHandler;
import com.wufan.model.entity.PackInfo;
import com.wufan.model.output.PackURL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.wufan.constant.ServiceConstant.TASKSIZE;
import static com.wufan.constant.ServiceConstant.WAITTASKTIME;

/**
 * Created by 7cc on 2017/9/7
 */
public class PackTaskSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(PackTaskSender.class);
    private final LinkedBlockingQueue<PackInfo> queue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<PackInfo> priorityQueue = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Future<PackURL>> priorityFutureQueue = new LinkedBlockingQueue<>();
    private static PackTaskSender sender = null;
    private Lock lockReceiver = new ReentrantLock();
    private Condition conditionReceiver = lockReceiver.newCondition();

    private PackTaskSender() {  }

    public static PackTaskSender getPackTaskSender() {
        if (sender == null) {
            synchronized (PackTaskSender.class) {
                if (sender == null) {
                    sender = new PackTaskSender();
                    // priority thread
                    Thread priorityThread = new Thread(() -> {
                        sender.priorityRun();
                    });
                    priorityThread.setDaemon(true);
                    priorityThread.start();
                    // normal thread
                    Thread sendThread = new Thread(() -> {
                        PackTaskSender.sender.run();
                    });
                    sendThread.setDaemon(true);
                    sendThread.start();
                    // priority future thread
                    Thread priorityFutureThread = new Thread(() -> {
                        PackTaskSender.sender.priorityFutureRun();
                    });
                    priorityFutureThread.setDaemon(true);
                    priorityFutureThread.start();
                }
            }
        }
        return sender;
    }

    public static void addPackInfoIntoQueue(PackInfo packInfo) throws InterruptedException {
        getPackTaskSender().queue.put(packInfo);
    }

    public static void addPackInfoIntoPriorityQueue(PackInfo packInfo) throws InterruptedException {
        getPackTaskSender().priorityQueue.put(packInfo);
    }

    public List<Callable<PackURL>> getRunningList() {
        return runningList;
    }

    public LinkedBlockingQueue<PackInfo> getQueue() {
        return queue;
    }

    private volatile List<Callable<PackURL>> runningList = new ArrayList<>();
    long startTime = System.currentTimeMillis();

    private void priorityRun() {
        while (true) {
            try {
                PackInfo packInfo = priorityQueue.take();
                if(packInfo == null) continue;
                Future<PackURL> future = PackTaskHandler.priorityExecutor.submit(new PackTaskCallable(packInfo));
                priorityFutureQueue.put(future);
            } catch (Exception e) {
                LOGGER.warn(String.format("take packInfo error==%s", e.getMessage()), e);
            }
        }

    }

    private void priorityFutureRun() {
        while (true) {
            try {
                Future<PackURL> future = priorityFutureQueue.take();
                if(future == null) continue;
                LOGGER.info("===================execute priority==================");
                PackURL futurePackURL = future.get();
                FutureResponse.receiveCallBack(futurePackURL);
            } catch (Exception e) {
                LOGGER.warn(String.format("take priority future error==%s", e.getMessage()), e);
            }
        }
    }

    private void run() {
        // monitor list
        Thread monitorThread = new Thread(() -> {
            List<Callable<PackURL>> trackList = new ArrayList<>();
            while (true) {
                try {
                    if (runningList.size() > TASKSIZE ||
                            (System.currentTimeMillis() - startTime > WAITTASKTIME && runningList.size() > 0 && !runningList.isEmpty())) {
                        LOGGER.info("===================execute batch==================");
                        // 记录执行的任务
                        trackList.addAll(runningList);
                        PackTaskHandler.handlePackTaskList(runningList);
                        // 执行完后 , 删除被记录的
                        runningList.removeAll(trackList);
                        // 清空记录数组
                        trackList.clear();
                        startTime = System.currentTimeMillis();
                        lockReceiver.lock();
                        try {
                            conditionReceiver.signal();
                        } finally {
                            lockReceiver.unlock();
                        }
                    }
                    Thread.sleep(100);
                } catch (Throwable e) {
                    LOGGER.warn(String.format("PackTaskSender send fail cause %s", e.getMessage()), e);
                }
            }
        });
        monitorThread.setDaemon(true);
        monitorThread.start();

        // receive into list
        while(true) {
            try {
                if(runningList.size() < TASKSIZE + 1) {
                    PackInfo packInfo = queue.take();
                    if(packInfo == null) continue;
                    runningList.add(new PackTaskCallable(packInfo));
                } else {
                    lockReceiver.lock();
                    try {
                        conditionReceiver.await();
                    } finally {
                        lockReceiver.unlock();
                    }
                }
            } catch (Throwable e) {
                LOGGER.error(e.getMessage(), e);
                throw new RuntimeException(String.format("exception %s", e.getMessage()));
            }
        }
    }

}
