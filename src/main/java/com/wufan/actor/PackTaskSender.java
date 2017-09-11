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
                PackTaskHandler.priorityPackage(new PackTaskCallable(packInfo));
            } catch (Exception e) {
                LOGGER.warn(String.format("take packInfo error==%s", e.getMessage()));
            }
        }

    }

    private void run() {
        // monitor list
        Thread monitorThread = new Thread(() -> {
            while (true) {
                try {
                    if (runningList.size() > TASKSIZE ||
                            (System.currentTimeMillis() - startTime > WAITTASKTIME && runningList.size() > 0 && !runningList.isEmpty())) {
                        System.out.println("===================execute==================");
                        PackTaskHandler.handlePackTaskList(runningList);
                        runningList = new ArrayList<>();
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
                    LOGGER.warn(String.format("PackTaskSender send fail cause %s", e.getMessage()));
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
                throw new RuntimeException(String.format("exception %s", e.getMessage()));
            }
        }
    }

}
