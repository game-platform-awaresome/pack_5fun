package com.wufan.actor;

import com.wufan.model.output.PackURL;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by 7cc on 2017/9/8
 */
public class FutureResponse {

    private volatile PackURL packURL = new PackURL();
    public final static Map<String, FutureResponse> FUTURES = new ConcurrentHashMap<>();
    private volatile Lock lock = new ReentrantLock();
    private volatile Condition condition = lock.newCondition();

    public FutureResponse() {   }

    public FutureResponse(String taskId) {
        packURL.setTaskId(taskId);
        FUTURES.put(taskId, this);
    }

    public PackURL get() {
        lock.lock();
        try {
            while (!hasURL()) {
                condition.await();
            }
        } catch (Throwable e){
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return packURL;
    }

    public static void receive(PackURL packURL) {
        FutureResponse future = FUTURES.remove(packURL.getTaskId());
        if (future == null) {
            return;
        }
        Lock lock = future.lock;
        lock.lock();
        try {
            future.packURL = packURL;
            Condition condition = future.condition;
            if(condition != null) {
                condition.signal();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private boolean hasURL() {
        return packURL.getCosUrl() != null ? true : false;
    }


}
